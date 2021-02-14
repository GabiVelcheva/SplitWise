package splitWise.server;

import splitWise.server.command.CommandCreator;
import splitWise.server.command.CommandExecutor;
import splitWise.server.files.LogFileHandler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SplitWiseServer {
    private static final String PROBLEM_SERVER_START = "Problem to start server.";
    private static final String PROBLEM_CLIENT_REQUEST = "Error occurred while processing client request: ";
    private static final String COULD_NOT_SEND_MESSAGE = "Could not send message to client";
    private static final String ERROR_ACCEPT_MESSAGE = "Problem with accepting connection";
    private static final String COULD_CREATED_SERVER_MESSAGE = "Problem with starting the server";
    private static final String PROBLEM_NIO_OBJECTS_MESSAGE = "Problem with opening the nio objects";


    private static final String DISCONNECTED_FROM_SERVER_MESSAGE = "[ Disconnected from server ]";

    private static final String SERVER_HOST = "localhost";

    public static final int SERVER_PORT = 7777;
    private static final int BUFFER_SIZE = 2048;
    private static final int ZERO = 0;

    private ByteBuffer buffer;
    private Selector selector;
    private boolean isServerWorking;

    private ServerSocketChannel serverSocketChannel;
    private final CommandExecutor commandExecutor;

    public SplitWiseServer() {
        this.commandExecutor = new CommandExecutor();
    }

    private void openResources() throws IOException {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            serverSocketChannel.configureBlocking(false);

            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            System.out.println(COULD_CREATED_SERVER_MESSAGE);
            LogFileHandler.log(PROBLEM_NIO_OBJECTS_MESSAGE + e.getStackTrace());
            throw new RuntimeException(PROBLEM_NIO_OBJECTS_MESSAGE, e);
        }
    }

    public boolean hasUser(String username) {
        return commandExecutor.hasUser(username);
    }

    public void startSplitWiseServer() {
        try {
            openResources();
            this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
            isServerWorking = true;
            commandExecutor.loadRegisteredUsers();

            while (isServerWorking) {
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == ZERO) {
                        continue;
                    }
                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        if (key.isReadable()) {
                            readFromKey(key);
                        } else if (key.isAcceptable()) {
                            acceptFromKey(key);
                        }
                        keyIterator.remove();
                    }
                } catch (IOException e) {
                    System.out.println(PROBLEM_CLIENT_REQUEST + e.getMessage());
                    LogFileHandler.log(PROBLEM_CLIENT_REQUEST + e.getStackTrace());
                    throw new RuntimeException(PROBLEM_CLIENT_REQUEST, e);
                }
            }
        } catch (IOException e) {
            LogFileHandler.log(PROBLEM_SERVER_START + e.getStackTrace());
            throw new UncheckedIOException(PROBLEM_SERVER_START, e);
        }
    }

    private void readFromKey(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        buffer.clear();
        int readBytes = socketChannel.read(buffer);
        if (readBytes < ZERO) {
            socketChannel.close();
            return;
        }
        buffer.flip();
        String readMessage = new String(buffer.array(), ZERO, buffer.limit());


        String[] message = readMessage.split(" ");
        String command = message[0];
        if (message[message.length - 1].endsWith(System.lineSeparator())) {
            message[message.length - 1] = message[message.length - 1].replaceAll(System.lineSeparator(), "");
        }
        if (readMessage.endsWith(System.lineSeparator())) {
            command = command.replaceAll(System.lineSeparator(), "");
        }
        String output = commandExecutor.execute(CommandCreator.newCommand(readMessage), socketChannel);
        writeClientOutput(socketChannel, output);
        if (output.equals(DISCONNECTED_FROM_SERVER_MESSAGE)) {
            socketChannel.close();
        }
    }

    private void writeClientOutput(SocketChannel socketChannel, String output) {
        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();
        while (buffer.hasRemaining()) {
            try {
                socketChannel.write(buffer);
            } catch (IOException e) {
                System.out.println(COULD_NOT_SEND_MESSAGE);
                LogFileHandler.log(COULD_NOT_SEND_MESSAGE + e.getStackTrace());
            }
        }
    }

    private void acceptFromKey(SelectionKey key) {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        try {
            SocketChannel accept = sockChannel.accept();
            accept.configureBlocking(false);
            accept.register(selector, SelectionKey.OP_READ);
            commandExecutor.addToConnected(accept);
        } catch (IOException e) {
            System.out.println(ERROR_ACCEPT_MESSAGE);
            LogFileHandler.log(ERROR_ACCEPT_MESSAGE + e.getStackTrace());
        }
    }

    public void stop() {
        this.isServerWorking = false;
        commandExecutor.saveToFile();
        if (selector.isOpen()) {
            selector.wakeup();
        }
        try {
            commandExecutor.saveToFile();
            commandExecutor.disconnectEveryone();
            serverSocketChannel.close();
            selector.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
