package splitWise.server.files;

import splitWise.server.user.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


public class RegisteredUsersFileHandler {
    private static final String REGISTERED_USERS_FILE_COULD_NOT_CLOSE = "File of registered users could not be closed.";
    private static final String REGISTERED_USERS_FILE_COULD_NOT_OPEN = "File of registered users could not be opened.";
    private static final String PROBLEM_WHILE_TRYING_TO_WRITE =
            "Problem while trying to write in the registered users file.";
    private final static String REGISTERED_USERS_FILE_PATH = "resources" + File.separator + "Registered.ser";

    public static Map<String, User> loadRegisteredUsers() {
        if (Files.exists(Paths.get(REGISTERED_USERS_FILE_PATH))) {
            try (FileInputStream file = new FileInputStream(REGISTERED_USERS_FILE_PATH);
                    ObjectInputStream in = new ObjectInputStream(file)) {
                return (HashMap<String, User>) in.readObject();
            } catch (FileNotFoundException e) {
                System.out.println(REGISTERED_USERS_FILE_COULD_NOT_OPEN);
                LogFileHandler.log(REGISTERED_USERS_FILE_COULD_NOT_OPEN + e.getStackTrace());
            } catch (IOException e) {
                System.out.println(REGISTERED_USERS_FILE_COULD_NOT_CLOSE);
                LogFileHandler.log(REGISTERED_USERS_FILE_COULD_NOT_CLOSE + e.getStackTrace());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>();
    }

    public static void saveRegisteredUsersToFile(Map<String, User> registeredUsers) {
        try (FileOutputStream file = new FileOutputStream(REGISTERED_USERS_FILE_PATH);
                ObjectOutputStream out = new ObjectOutputStream(file)) {
            out.writeObject(registeredUsers);
        } catch (IOException e) {
            System.out.println(PROBLEM_WHILE_TRYING_TO_WRITE);
            LogFileHandler.log(PROBLEM_WHILE_TRYING_TO_WRITE + e.getStackTrace());
        }
    }
}