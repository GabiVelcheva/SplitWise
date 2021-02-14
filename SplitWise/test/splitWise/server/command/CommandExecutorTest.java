package splitWise.server.command;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import splitWise.server.SplitWiseServer;

import static org.junit.Assert.assertEquals;
import static splitWise.server.command.CommandConstants.ADDING_YOURSELF_GROUP;
import static splitWise.server.command.CommandConstants.BETWEEN_YOU;
import static splitWise.server.command.CommandConstants.FRIEND_TOTALS_COMMAND;
import static splitWise.server.command.CommandConstants.GROUP_MEMBERS;
import static splitWise.server.command.CommandConstants.GROUP_TOTALS_COMMAND;
import static splitWise.server.command.CommandConstants.SPLIT;
import static splitWise.server.command.CommandConstants.SPLIT_ARGUMENTS_MESSAGE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CommandExecutorTest {
    private static final String REGISTER_COMMAND = "register";
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
            String username = USERNAME_FIRST_USER;
            String password = PASSWORD_FIRST_USER;

            String reply = registerUser(username, password);
            String expecting = USERNAME + username + ALREADY_TAKEN_USERNAME + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testRegisterWhileLoggedIn() {
        try {
            loginUser(USERNAME_FIRST_USER, PASSWORD_FIRST_USER);
            String username = USERNAME_GABI;
            String password = PASSWORD_GABI;

            String reply = registerUser(username, password);
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
            loginUser(USERNAME_FIRST_USER, PASSWORD_FIRST_USER);
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
            loginUser(USERNAME_FIRST_USER, PASSWORD_FIRST_USER);
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
            String user = USERNAME_GABI;
            String reply = addFriend(user);
            String expecting = NOT_LOGGED_IN + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAddFriendWithNotRegisteredFriend() {
        try {
            loginUser(USERNAME_FIRST_USER, PASSWORD_FIRST_USER);
            String user = WRONG_USERNAME;

            String reply = addFriend(user);
            String expecting = NOT_REGISTERED_USER + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAddFriendSuccessfully() {
        try {
            loginUser(USERNAME_FIRST_USER, PASSWORD_FIRST_USER);
            String user = USERNAME_SECOND_USER;

            String reply = addFriend(user);
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
            loginUser(USERNAME_FIRST_USER, PASSWORD_FIRST_USER);

            String reply = addFriend(USERNAME_FIRST_USER);
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
            registerUser(user, user);
            loginUser(USERNAME_FIRST_USER, PASSWORD_FIRST_USER);
            addFriend(user);

            String reply = addFriend(user);
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
            String groupName = GROUP_NAME;
            String user1 = USERNAME_FIRST_USER;
            String user2 = USERNAME_SECOND_USER;

            String reply = createGroup(groupName, user1, user2);
            String expecting = NOT_LOGGED_IN + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateGroupWithNotRegisteredFriend() {
        try {
            loginUser(USERNAME_FIRST_USER, PASSWORD_FIRST_USER);
            String user = WRONG_USERNAME;

            String reply = createGroup(GROUP_NAME, user, USERNAME_SECOND_USER);
            String expecting = NOT_REGISTERED_USER + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateGroupWithNotFriends() {
        try {
            registerUser(USERNAME_GABI, PASSWORD_GABI);
            loginUser(USERNAME_FIRST_USER, PASSWORD_FIRST_USER);

            String user = USERNAME_GABI;
            String reply = createGroup(GROUP_NAME, USERNAME_SECOND_USER, user);
            String expecting = SOMEONE + USER_NOT_FRIEND_SECOND + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateGroupSuccessfully() {
        try {
            registerUser(USERNAME_GROUP1, PASSWORD_GROUP1);
            registerUser(USERNAME_GROUP2, PASSWORD_GROUP2);
            loginUser(USERNAME_FIRST_USER, PASSWORD_FIRST_USER);
            addFriend(USERNAME_GROUP1);
            addFriend(USERNAME_GROUP2);

            String reply = createGroup(GROUP_NAME, USERNAME_GROUP1, USERNAME_GROUP2);
            String expecting = GROUP + GROUP_NAME + CREATED_SUCCESSFULLY + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateGroupAddingYou() {
        try {
            registerUser(USERNAME_GROUP1, PASSWORD_GROUP1);
            loginUser(USERNAME_FIRST_USER, PASSWORD_FIRST_USER);

            String reply = createGroup(GROUP_NAME, USERNAME_FIRST_USER, USERNAME_GROUP1);
            String expecting = ADDING_YOURSELF_GROUP + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateGroupWithExistingGroup() {
        try {
            registerUser(USERNAME_GROUP1, PASSWORD_GROUP1);
            registerUser(USERNAME_GROUP2, PASSWORD_GROUP2);
            loginUser(USERNAME_FIRST_USER, PASSWORD_FIRST_USER);
            addFriend(USERNAME_GROUP1);
            addFriend(USERNAME_GROUP2);
            createGroup("group", USERNAME_GROUP1, USERNAME_GROUP2);

            String reply = createGroup("group", USERNAME_GROUP1, USERNAME_GROUP2);
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
            String user1 = USERNAME_FIRST_USER;

            String reply = split(10.0, user1);
            String expecting = NOT_LOGGED_IN + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSplitWithNotRegisteredFriend() {
        try {
            loginUser(USERNAME_FIRST_USER, PASSWORD_FIRST_USER);
            String user = WRONG_USERNAME;

            String reply = split(10.0, user);
            String expecting = NOT_REGISTERED_USER + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSplitWithNotAddedFriend() {
        try {
            registerUser("NoFriend", "NoFriend");
            loginUser(USERNAME_FIRST_USER, PASSWORD_FIRST_USER);
            String user = "NoFriend";

            String reply = split(10.0, user);
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
            registerUser(user, user);
            loginUser(USERNAME_FIRST_USER, PASSWORD_FIRST_USER);
            addFriend(user);

            String reply = split(10.0, user);
            String expecting = SPLIT + 10.0 + BETWEEN_YOU + user + System.lineSeparator();
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

    private String splitGroup(Double amount, String groupName) throws IOException {
        writer.println(SPLIT_GROUP_COMMAND + SPACE + amount + SPACE + groupName + SPACE + DESCRIPTION +
                System.lineSeparator());
        return reader.readLine() + System.lineSeparator();
    }

    @Test
    public void testSplitGroupWhileNotLoggedIn() {
        try {
            String groupName = "groupp";

            String reply = splitGroup(10.0, groupName);
            String expecting = NOT_LOGGED_IN + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSplitGroupWithNoSuchGroup() {
        try {
            loginUser(USERNAME_FIRST_USER, PASSWORD_FIRST_USER);
            String groupName = "groupp";

            String reply = splitGroup(10.0, groupName);
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
            registerUser(user1, user1);
            registerUser(user2, user2);
            loginUser(USERNAME_FIRST_USER, PASSWORD_FIRST_USER);
            addFriend(user1);
            addFriend(user2);
            String groupName = "grouppy";
            createGroup(groupName, user1, user2);

            String reply = splitGroup(10.0, groupName);
            String expecting = SPLIT + 10.0 + BETWEEN_YOU + GROUP_MEMBERS + groupName + System.lineSeparator();
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
            String user1 = USERNAME_FIRST_USER;

            String reply = payed(10.0, user1);
            String expecting = NOT_LOGGED_IN + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPayedWithNotRegisteredFriend() {
        try {
            loginUser(USERNAME_FIRST_USER, PASSWORD_FIRST_USER);
            String user = WRONG_USERNAME;

            String reply = payed(10.0, user);
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
            registerUser(user, user);
            loginUser(user, user);

            String reply = payed(10.0, user);
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
            registerUser(user, user);
            registerUser(username, username);
            loginUser(username, username);
            addFriend(user);
            Double amount = 10.0;
            split(amount, user);
            amount = amount + 10.0;

            String reply = payed(amount, user);
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
            registerUser(user, user);
            registerUser(username, username);
            loginUser(username, username);
            addFriend(user);

            String reply = payed(10.0, user);
            String expecting = "[ " + user + " doesn't owe you money ]" + System.lineSeparator();
            assertEquals(expecting, reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String registerUser(String username, String password) throws IOException {
        writer.println(REGISTER_COMMAND + SPACE + username + SPACE + password + System.lineSeparator());
        return reader.readLine() + System.lineSeparator();
    }

    private void loginUser(String username, String password) throws IOException {
        writer.println(
                LOGIN_COMMAND + SPACE + username + SPACE + password + System.lineSeparator());
        reader.readLine();
        reader.readLine();
    }

    private String addFriend(String username) throws IOException {
        writer.println(
                ADD_FRIEND_COMMAND + SPACE + username + System.lineSeparator());
        return reader.readLine() + System.lineSeparator();
    }

    private String createGroup(String groupName, String username1, String username2) throws IOException {
        writer.println(CREATE_GROUP_COMMAND + SPACE + groupName + SPACE + username1 + SPACE + username2 +
                System.lineSeparator());
        return reader.readLine() + System.lineSeparator();
    }

    private String payed(Double amount, String username) throws IOException {
        writer.println(ADD_PAYMENT_COMMAND + SPACE + amount + SPACE + username + SPACE + System.lineSeparator());
        return reader.readLine() + System.lineSeparator();
    }

    private String split(Double amount, String user) throws IOException {
        writer.println(SPLIT_COMMAND + SPACE + amount + SPACE + user + SPACE + DESCRIPTION + System.lineSeparator());
        return reader.readLine() + System.lineSeparator();
    }

    @Test
    public void testPayedFriendSuccessfully() {
        try {
            String user = "Ffrriend";
            String username = "gga";
            registerUser(user, user);
            registerUser(username, username);
            loginUser(username, username);
            addFriend(user);
            Double amount = 10.0;
            split(amount, user);
            amount = (amount / 2);

            String reply = payed(amount, user);
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
