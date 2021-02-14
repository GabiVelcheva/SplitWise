package splitWise.server.command;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import splitWise.server.SplitWiseServer;

import static org.junit.Assert.assertEquals;
import static splitWise.server.command.CommandConstants.BETWEEN_YOU;
import static splitWise.server.command.CommandConstants.FRIEND_TOTALS_COMMAND;
import static splitWise.server.command.CommandConstants.GROUP_MEMBERS;
import static splitWise.server.command.CommandConstants.GROUP_TOTALS_COMMAND;
import static splitWise.server.command.CommandConstants.SPLITTED;
import static splitWise.server.command.CommandConstants.SPLIT_ARGUMENTS_MESSAGE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CommandExecutorTest {
    private static final String REGISTER_COMMAND = "register";
    private static final String DISCONNECT_COMMAND = "disconnect";
    private static final String LOGIN_COMMAND = "login";
    private static final String LOGOUT_COMMAND = "logout";
    private static final String ADD_FRIEND_COMMAND = "add-friend";
    private static final String CREATE_GROUP_COMMAND = "create-group";
    private static final String SPLIT_COMMAND = "split";
    private static final String SPLIT_GROUP_COMMAND = "split-group";
    private static final String ADD_PAYMENT_COMMAND = "payed";

    static final String ALREADY_TAKEN_USERNAME = " is already taken, select another one ]";
    static final String CREATED_SUCCESSFULLY = " is created successfully ]";
    static final String GROUP = "[ group: ";
    static final String INVALID_USERNAME_PASSWORD = "[ Invalid username/password combination ]";
    static final String LOGGED_IN_ALREADY_AS = "[ You are logged in already as ";
    static final String NOT_LOGGED_IN = "[ You are not logged in ]";
    static final String NOT_REGISTERED_USER = "[ This user is not registered ]";
    static final String OPENING_BRACKET = "[ ";
    static final String USER_ALREADY_FRIEND = "[ This user is already in you friend list ]";
    static final String FRIEND_ADDED = "added successfully as a friend ]";
    static final String GROUP_EXISTS = "[ A group with the same name and participants already exists ]";
    static final String SEPARATOR = " ";
    static final String SUCCESSFULLY_LOGGED_IN = " successfully logged in ]";
    static final String SOMEONE = "[ Someone";
    static final String USER = "[ User ";
    static final String USERNAME = "[ Username ";
    static final String USER_NOT_FRIEND_FIRST = "[ This user: ";
    static final String USER_NOT_FRIEND_SECOND = " is not in your friend list, please add him first ]";
    static final String NO_SUCH_GROUP = "[ You are not in a group with that name ]";
    static final String TO_REGISTER_OTHER = " , to register other user, first logout ]";
    static final String TO_LOGIN_ANOTHER = " , to login as other user, first logout ]";

    static final String NO_NOTIFICATIONS_MESSAGE = "No notifications to show.";

    private static final String USERNAME_GABI = "Gabi";
    private static final String PASSWORD_GABI = "@2ws-g";
    private static final String USERNAME_FIRST_USER = "firstUser";
    private static final String PASSWORD_FIRST_USER = "passwordForTheFirst";
    private static final String USERNAME_SECOND_USER = "secondUser";
    private static final String PASSWORD_SECOND_USER = "passwordForTheSecond";
    private static final String USERNAME_GROUP1 = "us";
    private static final String PASSWORD_GROUP1 = "er";
    private static final String USERNAME_GROUP2 = "usi";
    private static final String PASSWORD_GROUP2 = "eri";
    private static final String WRONG_USERNAME = "brr";
    private static final String WRONG_PASSWORD = "brr";
    private static final String GROUP_NAME = "family";
    private static final String DESCRIPTION = "description";
    private static final String SPACE = " ";
    private static final String SERVER_HOST = "localhost";

    private static final String INVALID_NUMBER_ARGUMENTS_MESSAGE =
            "The number of arguments for the current command is invalid";

    private static final int SERVER_PORT = 7777;

    private static SplitWiseServer splitWiseServer;
    private static PrintWriter writer;
    private static BufferedReader reader;
    private static Thread thread;

    @BeforeClass
    public static void setup() throws InterruptedException {
        thread = new Thread(() -> {
            splitWiseServer = new SplitWiseServer();
            splitWiseServer.startSplitWiseServer();
        });
        thread.start();
        Thread.sleep(300);
        try {
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            writer = new PrintWriter(socket.getOutputStream(), true); // autoflush on
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Connected to the server.");
            String command = REGISTER_COMMAND;
            String username = USERNAME_FIRST_USER;
            String password = PASSWORD_FIRST_USER;
            writer.println(command + SPACE + username + SPACE + password + System.lineSeparator());
            reader.readLine();
            writer.println(
                    command + SPACE + USERNAME_SECOND_USER + SPACE + PASSWORD_SECOND_USER + System.lineSeparator());
            reader.readLine();

        } catch (IOException e) {
            System.out.println("There is a problem with the network communication");
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void close() throws InterruptedException {
        splitWiseServer.stop();
        thread.interrupt();
        Thread.sleep(300);
    }

    @After
    public void logout() throws IOException {
        String command = LOGOUT_COMMAND;
        writer.println(command + System.lineSeparator());
        reader.readLine();
    }

    @Test
    public void testRegisterWithInvalidArguments() {
        try {
            String command = REGISTER_COMMAND;
            writer.println(command + SPACE + "username" + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = INVALID_NUMBER_ARGUMENTS_MESSAGE + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testRegisterWithTakenUsername() {
        try {
            String command = REGISTER_COMMAND;
            String username = USERNAME_FIRST_USER;
            String password = PASSWORD_FIRST_USER;
            writer.println(command + SPACE + username + SPACE + password + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = USERNAME + username + ALREADY_TAKEN_USERNAME + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testRegisterWhileLoggedIn() {
        try {
            writer.println(
                    LOGIN_COMMAND + SPACE + USERNAME_FIRST_USER + SPACE + PASSWORD_FIRST_USER + System.lineSeparator());
            reader.readLine();
            reader.readLine();
            String command = REGISTER_COMMAND;
            String username = USERNAME_GABI;
            String password = PASSWORD_GABI;
            writer.println(command + SPACE + username + SPACE + password + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = LOGGED_IN_ALREADY_AS + USERNAME_FIRST_USER + TO_REGISTER_OTHER + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSuccessfullyLoggedIn() {
        try {
            String command = LOGIN_COMMAND;
            String username = USERNAME_FIRST_USER;
            String password = PASSWORD_FIRST_USER;
            writer.println(command + SPACE + username + SPACE + password + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            reply = reply.concat(reader.readLine() + System.lineSeparator());
            String expecting =
                    USER + username + SUCCESSFULLY_LOGGED_IN + System.lineSeparator() + NO_NOTIFICATIONS_MESSAGE +
                            System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLogInWhileLoggedIn() {
        try {
            writer.println(
                    LOGIN_COMMAND + SPACE + USERNAME_FIRST_USER + SPACE + PASSWORD_FIRST_USER + System.lineSeparator());
            reader.readLine();
            reader.readLine();
            String command = LOGIN_COMMAND;
            String username = USERNAME_GABI;
            String password = PASSWORD_GABI;
            writer.println(command + SPACE + username + SPACE + password + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = LOGGED_IN_ALREADY_AS + USERNAME_FIRST_USER + TO_LOGIN_ANOTHER + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLogInWithInvalidArguments() {
        try {
            String command = LOGIN_COMMAND;
            writer.println(command + SPACE + "username" + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = INVALID_NUMBER_ARGUMENTS_MESSAGE + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLogInWithNoSuchUser() {
        try {
            String command = LOGIN_COMMAND;
            String username = WRONG_USERNAME;
            String password = PASSWORD_GABI;
            writer.println(command + SPACE + username + SPACE + password + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = INVALID_USERNAME_PASSWORD + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLogInWithWrongPassword() {
        try {
            String command = LOGIN_COMMAND;
            String username = USERNAME_FIRST_USER;
            String password = WRONG_PASSWORD;
            writer.println(command + SPACE + username + SPACE + password + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = INVALID_USERNAME_PASSWORD + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLogoutWithInvalidArguments() {
        try {
            writer.println(
                    LOGIN_COMMAND + SPACE + USERNAME_FIRST_USER + SPACE + PASSWORD_FIRST_USER);
            reader.readLine();
            reader.readLine();
            String command = LOGOUT_COMMAND;
            writer.println(command + SPACE + "username" + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = INVALID_NUMBER_ARGUMENTS_MESSAGE + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAddFriendWithInvalidArguments() {
        try {
            String command = ADD_FRIEND_COMMAND;
            writer.println(command + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = INVALID_NUMBER_ARGUMENTS_MESSAGE + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAddFriendWhileNotLoggedIn() {
        try {
            String command = ADD_FRIEND_COMMAND;
            String user = USERNAME_GABI;
            writer.println(command + SPACE + user + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = NOT_LOGGED_IN + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAddFriendWithNotRegisteredFriend() {
        try {
            writer.println(
                    LOGIN_COMMAND + SPACE + USERNAME_FIRST_USER + SPACE + PASSWORD_FIRST_USER);
            reader.readLine();
            reader.readLine();
            String command = ADD_FRIEND_COMMAND;
            String user = WRONG_USERNAME;
            writer.println(command + SPACE + user + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = NOT_REGISTERED_USER + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAddFriendSuccessfully() {
        try {
            writer.println(
                    LOGIN_COMMAND + SPACE + USERNAME_FIRST_USER + SPACE + PASSWORD_FIRST_USER + System.lineSeparator());
            reader.readLine();
            reader.readLine();
            String command = ADD_FRIEND_COMMAND;
            String user = USERNAME_SECOND_USER;
            writer.println(command + SPACE + user + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting =
                    OPENING_BRACKET + USERNAME_SECOND_USER + SEPARATOR + FRIEND_ADDED + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAddFriendYourself() {
        try {
            writer.println(
                    LOGIN_COMMAND + SPACE + USERNAME_FIRST_USER + SPACE + PASSWORD_FIRST_USER + System.lineSeparator());
            reader.readLine();
            reader.readLine();
            String command = ADD_FRIEND_COMMAND;
            writer.println(command + SPACE + USERNAME_FIRST_USER + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = "You can't add yourself as friend." + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAddFriendTwice() {
        try {
            String user = "someone";
            writer.println(
                    REGISTER_COMMAND + SPACE + user + SPACE + user + System.lineSeparator());
            reader.readLine();
            writer.println(
                    LOGIN_COMMAND + SPACE + USERNAME_FIRST_USER + SPACE + PASSWORD_FIRST_USER + System.lineSeparator());
            reader.readLine();
            reader.readLine();
            String command = ADD_FRIEND_COMMAND;
            writer.println(command + SPACE + user + System.lineSeparator());
            reader.readLine();
            writer.println(command + SPACE + user + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = USER_ALREADY_FRIEND + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateGroupWithInvalidArguments() {
        try {
            String command = CREATE_GROUP_COMMAND;
            writer.println(command + SPACE + "username" + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = INVALID_NUMBER_ARGUMENTS_MESSAGE + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateGroupWhileNotLoggedIn() {
        try {
            String command = CREATE_GROUP_COMMAND;
            String groupName = GROUP_NAME;
            String user1 = USERNAME_FIRST_USER;
            String user2 = USERNAME_SECOND_USER;
            writer.println(command + SPACE + groupName + SPACE + user1 + SPACE + user2 + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = NOT_LOGGED_IN + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateGroupWithNotRegisteredFriend() {
        try {
            writer.println(
                    LOGIN_COMMAND + SPACE + USERNAME_FIRST_USER + SPACE + PASSWORD_FIRST_USER);
            reader.readLine();
            reader.readLine();
            String command = CREATE_GROUP_COMMAND;
            String user = WRONG_USERNAME;
            writer.println(command + SPACE + GROUP_NAME + SPACE + user + SPACE + USERNAME_SECOND_USER +
                    System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = NOT_REGISTERED_USER + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateGroupWithNotFriends() {
        try {
            writer.println(REGISTER_COMMAND + SPACE + USERNAME_GABI + SPACE + PASSWORD_GABI + System.lineSeparator());
            reader.readLine();
            writer.println(
                    LOGIN_COMMAND + SPACE + USERNAME_FIRST_USER + SPACE + PASSWORD_FIRST_USER + System.lineSeparator());
            reader.readLine();
            reader.readLine();

            String command = CREATE_GROUP_COMMAND;
            String user = USERNAME_GABI;
            writer.println(command + SPACE + GROUP_NAME + SPACE + USERNAME_SECOND_USER + SPACE + user +
                    System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = SOMEONE + USER_NOT_FRIEND_SECOND + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateGroupSuccessfully() {
        try {
            writer.println(
                    REGISTER_COMMAND + SPACE + USERNAME_GROUP1 + SPACE + PASSWORD_GROUP1 + System.lineSeparator());
            reader.readLine();
            writer.println(
                    REGISTER_COMMAND + SPACE + USERNAME_GROUP2 + SPACE + PASSWORD_GROUP2 + System.lineSeparator());
            reader.readLine();
            writer.println(
                    LOGIN_COMMAND + SPACE + USERNAME_FIRST_USER + SPACE + PASSWORD_FIRST_USER + System.lineSeparator());
            reader.readLine();
            reader.readLine();
            writer.println(ADD_FRIEND_COMMAND + SPACE + USERNAME_GROUP1 + System.lineSeparator());
            reader.readLine();
            writer.println(ADD_FRIEND_COMMAND + SPACE + USERNAME_GROUP2 + System.lineSeparator());
            reader.readLine();

            String command = CREATE_GROUP_COMMAND;
            writer.println(command + SPACE + GROUP_NAME + SPACE + USERNAME_GROUP1 + SPACE + USERNAME_GROUP2 +
                    System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = GROUP + GROUP_NAME + CREATED_SUCCESSFULLY + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateGroupAddingYou() {
        try {
            writer.println(
                    REGISTER_COMMAND + SPACE + USERNAME_GROUP1 + SPACE + PASSWORD_GROUP1 + System.lineSeparator());
            reader.readLine();
            writer.println(
                    LOGIN_COMMAND + SPACE + USERNAME_FIRST_USER + SPACE + PASSWORD_FIRST_USER + System.lineSeparator());
            reader.readLine();
            reader.readLine();
            String command = CREATE_GROUP_COMMAND;
            writer.println(
                    command + SPACE + GROUP_NAME + SPACE + USERNAME_FIRST_USER + SPACE + USERNAME_GROUP1 + SPACE +
                            System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = "You don't need to add you in the list of friends" + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateGroupWithExistingGroup() {
        try {
            writer.println(
                    REGISTER_COMMAND + SPACE + USERNAME_GROUP1 + SPACE + PASSWORD_GROUP1 + System.lineSeparator());
            reader.readLine();
            writer.println(
                    REGISTER_COMMAND + SPACE + USERNAME_GROUP2 + SPACE + PASSWORD_GROUP2 + System.lineSeparator());
            reader.readLine();
            writer.println(
                    LOGIN_COMMAND + SPACE + USERNAME_FIRST_USER + SPACE + PASSWORD_FIRST_USER + System.lineSeparator());
            reader.readLine();
            reader.readLine();
            writer.println(ADD_FRIEND_COMMAND + SPACE + USERNAME_GROUP1 + System.lineSeparator());
            reader.readLine();
            writer.println(ADD_FRIEND_COMMAND + SPACE + USERNAME_GROUP2 + System.lineSeparator());
            reader.readLine();

            String command = CREATE_GROUP_COMMAND;
            writer.println(command + SPACE + "group" + SPACE + USERNAME_GROUP1 + SPACE + USERNAME_GROUP2 +
                    System.lineSeparator());
            reader.readLine();
            writer.println(command + SPACE + "group" + SPACE + USERNAME_GROUP1 + SPACE + USERNAME_GROUP2 +
                    System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = GROUP_EXISTS + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSplitWithInvalidArguments() {
        try {
            String command = SPLIT_COMMAND;
            writer.println(command + SPACE + "username" + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = SPLIT_ARGUMENTS_MESSAGE + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSplitWhileNotLoggedIn() {
        try {
            String command = SPLIT_COMMAND;
            String user1 = USERNAME_FIRST_USER;
            Double amount = 10.0;
            String description = DESCRIPTION;
            writer.println(command + SPACE + amount + SPACE + user1 + SPACE + description + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = NOT_LOGGED_IN + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSplitWithNotRegisteredFriend() {
        try {
            writer.println(
                    LOGIN_COMMAND + SPACE + USERNAME_FIRST_USER + SPACE + PASSWORD_FIRST_USER);
            reader.readLine();
            reader.readLine();
            String command = SPLIT_COMMAND;
            String user = WRONG_USERNAME;
            Double amount = 10.0;
            String description = DESCRIPTION;
            writer.println(command + SPACE + amount + SPACE + user + SPACE + description +
                    System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = NOT_REGISTERED_USER + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSplitWithNotAddedFriend() {
        try {
            writer.println(REGISTER_COMMAND + SPACE + "NoFriend" + SPACE + "NoFriend" + System.lineSeparator());
            reader.readLine();
            writer.println(
                    LOGIN_COMMAND + SPACE + USERNAME_FIRST_USER + SPACE + PASSWORD_FIRST_USER + System.lineSeparator());
            reader.readLine();
            reader.readLine();
            String command = SPLIT_COMMAND;
            String user = "NoFriend";
            Double amount = 10.0;
            String description = DESCRIPTION;
            writer.println(command + SPACE + amount + SPACE + user + SPACE + description + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = USER_NOT_FRIEND_FIRST + "NoFriend" + USER_NOT_FRIEND_SECOND + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSplitSuccessfully() {
        try {
            String user = "Friend";
            writer.println(REGISTER_COMMAND + SPACE + user + SPACE + user + System.lineSeparator());
            reader.readLine();
            writer.println(
                    LOGIN_COMMAND + SPACE + USERNAME_FIRST_USER + SPACE + PASSWORD_FIRST_USER + System.lineSeparator());
            reader.readLine();
            reader.readLine();
            writer.println(
                    ADD_FRIEND_COMMAND + SPACE + user + System.lineSeparator());
            reader.readLine();
            String command = SPLIT_COMMAND;
            Double amount = 10.0;
            String description = DESCRIPTION;
            writer.println(command + SPACE + amount + SPACE + user + SPACE + description + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = SPLITTED + amount + BETWEEN_YOU + user + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSplitGroupWithInvalidArguments() {
        try {
            String command = SPLIT_GROUP_COMMAND;
            writer.println(command + SPACE + "username" + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = INVALID_NUMBER_ARGUMENTS_MESSAGE + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSplitGroupWhileNotLoggedIn() {
        try {
            String command = SPLIT_GROUP_COMMAND;
            String groupName = "groupp";
            Double amount = 10.0;
            String description = DESCRIPTION;
            writer.println(command + SPACE + amount + SPACE + groupName + SPACE + description + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = NOT_LOGGED_IN + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSplitGroupWithNoSuchGroup() {
        try {
            writer.println(
                    LOGIN_COMMAND + SPACE + USERNAME_FIRST_USER + SPACE + PASSWORD_FIRST_USER);
            reader.readLine();
            reader.readLine();
            String command = SPLIT_GROUP_COMMAND;
            String groupName = "groupp";
            Double amount = 10.0;
            String description = DESCRIPTION;
            writer.println(command + SPACE + amount + SPACE + groupName + SPACE + description + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = NO_SUCH_GROUP + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSplitGroupSuccessfully() {
        try {
            String user1 = "friendo";
            String user2 = "friendo2";
            writer.println(REGISTER_COMMAND + SPACE + user1 + SPACE + user1 + System.lineSeparator());
            reader.readLine();
            writer.println(REGISTER_COMMAND + SPACE + user2 + SPACE + user2 + System.lineSeparator());
            reader.readLine();
            writer.println(
                    LOGIN_COMMAND + SPACE + USERNAME_FIRST_USER + SPACE + PASSWORD_FIRST_USER);
            reader.readLine();
            reader.readLine();
            writer.println(
                    ADD_FRIEND_COMMAND + SPACE + user1 + System.lineSeparator());
            reader.readLine();
            writer.println(
                    ADD_FRIEND_COMMAND + SPACE + user2 + System.lineSeparator());
            reader.readLine();
            String groupName = "grouppy";
            writer.println(CREATE_GROUP_COMMAND + SPACE + groupName + SPACE + user1 + SPACE + user2 +
                    System.lineSeparator());
            reader.readLine();
            String command = SPLIT_GROUP_COMMAND;

            Double amount = 10.0;
            String description = DESCRIPTION;
            writer.println(command + SPACE + amount + SPACE + groupName + SPACE + description + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = SPLITTED + amount + BETWEEN_YOU + GROUP_MEMBERS + groupName + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPayedWithInvalidArguments() {
        try {
            String command = ADD_PAYMENT_COMMAND;
            writer.println(command + SPACE + "username" + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = INVALID_NUMBER_ARGUMENTS_MESSAGE + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPayedWhileNotLoggedIn() {
        try {
            String command = ADD_PAYMENT_COMMAND;
            String user1 = USERNAME_FIRST_USER;
            Double amount = 10.0;
            writer.println(command + SPACE + amount + SPACE + user1 + SPACE + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = NOT_LOGGED_IN + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPayedWithNotRegisteredFriend() {
        try {
            writer.println(
                    LOGIN_COMMAND + SPACE + USERNAME_FIRST_USER + SPACE + PASSWORD_FIRST_USER);
            reader.readLine();
            reader.readLine();
            String command = ADD_PAYMENT_COMMAND;
            String user = WRONG_USERNAME;
            Double amount = 10.0;
            writer.println(command + SPACE + amount + SPACE + user + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = NOT_REGISTERED_USER + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPayedWithNotAddedFriend() {
        try {
            String user = "NoFriendo";
            writer.println(REGISTER_COMMAND + SPACE + user + SPACE + user + System.lineSeparator());
            reader.readLine();
            writer.println(
                    LOGIN_COMMAND + SPACE + USERNAME_FIRST_USER + SPACE + PASSWORD_FIRST_USER + System.lineSeparator());
            reader.readLine();
            reader.readLine();
            String command = ADD_PAYMENT_COMMAND;
            Double amount = 10.0;
            writer.println(command + SPACE + amount + SPACE + user + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = USER_NOT_FRIEND_FIRST + user + USER_NOT_FRIEND_SECOND + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPayedFriendWhoNotOwesYpuSoMuch() {
        try {
            String user = "Ffrriend";
            String username = "gga";

            writer.println(REGISTER_COMMAND + SPACE + user + SPACE + user + System.lineSeparator());
            reader.readLine();
            writer.println(REGISTER_COMMAND + SPACE + username + SPACE + username + System.lineSeparator());
            reader.readLine();
            writer.println(
                    LOGIN_COMMAND + SPACE + username + SPACE + username + System.lineSeparator());
            reader.readLine();
            reader.readLine();
            writer.println(
                    ADD_FRIEND_COMMAND + SPACE + user + System.lineSeparator());
            reader.readLine();
            Double amount = 10.0;
            String description = DESCRIPTION;
            writer.println(
                    SPLIT_COMMAND + SPACE + amount + SPACE + user + SPACE + description + System.lineSeparator());
            reader.readLine();
            String command = ADD_PAYMENT_COMMAND;
            amount = amount + 10.0;
            writer.println(command + SPACE + amount + SPACE + user + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = "[ " + user + " doesn't owe you so much money ]" + System.lineSeparator();
            assertEquals(expecting, reply);
            writer.println(LOGOUT_COMMAND + System.lineSeparator());
            reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPayedFriendWhoNOtOwe() {
        try {
            String user = "Frriend";
            String username = "gg";

            writer.println(REGISTER_COMMAND + SPACE + user + SPACE + user + System.lineSeparator());
            reader.readLine();
            writer.println(REGISTER_COMMAND + SPACE + username + SPACE + username + System.lineSeparator());
            reader.readLine();
            writer.println(
                    LOGIN_COMMAND + SPACE + username + SPACE + username + System.lineSeparator());
            reader.readLine();
            reader.readLine();
            writer.println(
                    ADD_FRIEND_COMMAND + SPACE + user + System.lineSeparator());
            reader.readLine();
            String command = ADD_PAYMENT_COMMAND;
            Double amount = 10.0;
            writer.println(command + SPACE + amount + SPACE + user + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = "[ " + user + " doesn't owe you money ]" + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPayedFriendSuccessfully() {
        try {
            String user = "Ffrriend";
            String username = "gga";

            writer.println(REGISTER_COMMAND + SPACE + user + SPACE + user + System.lineSeparator());
            reader.readLine();
            writer.println(REGISTER_COMMAND + SPACE + username + SPACE + username + System.lineSeparator());
            reader.readLine();
            writer.println(
                    LOGIN_COMMAND + SPACE + username + SPACE + username + System.lineSeparator());
            reader.readLine();
            reader.readLine();
            writer.println(
                    ADD_FRIEND_COMMAND + SPACE + user + System.lineSeparator());
            reader.readLine();
            Double amount = 10.0;
            String description = DESCRIPTION;
            writer.println(
                    SPLIT_COMMAND + SPACE + amount + SPACE + user + SPACE + description + System.lineSeparator());
            reader.readLine();
            String command = ADD_PAYMENT_COMMAND;
            amount = (amount / 2);
            writer.println(command + SPACE + amount + SPACE + user + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = "[ " + user + " payed you " + amount + " ]" + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGroupTotalsWithInvalidArguments() {
        try {
            String command = GROUP_TOTALS_COMMAND;
            writer.println(command + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = INVALID_NUMBER_ARGUMENTS_MESSAGE + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testFriendTotalsWithInvalidArguments() {
        try {
            String command = FRIEND_TOTALS_COMMAND;
            writer.println(command + System.lineSeparator());
            String reply = reader.readLine() + System.lineSeparator();
            String expecting = INVALID_NUMBER_ARGUMENTS_MESSAGE + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
