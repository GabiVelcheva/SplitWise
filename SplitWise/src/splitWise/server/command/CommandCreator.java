package splitWise.server.command;

public class CommandCreator {
    public static Command newCommand(String clientInput) {
        if (clientInput.endsWith(System.lineSeparator())) {
            clientInput = clientInput.replaceAll(System.lineSeparator(), "");
        }
        String[] message = clientInput.split(" ");
        String command = message[0];
        if (clientInput.endsWith(System.lineSeparator())) {
            command = command.replaceAll(System.lineSeparator(), "");
        }
        return new Command(command, message);
    }
}

