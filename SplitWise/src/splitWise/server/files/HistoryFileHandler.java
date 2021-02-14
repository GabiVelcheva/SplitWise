package splitWise.server.files;

import splitWise.server.user.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class HistoryFileHandler {
    private static final String HISTORY_FILE_COULD_NOT_OPEN = "File of history payments could not be opened.";
    private static final String HISTORY_FILE_COULD_NOT_CREATE = "File of history payments could not be created.";
    private static final String HISTORY_FILE_COULD_NOT_CLOSE = "File of history payments could not be closed.";
    private static final String PROBLEM_WRITING_TO_HISTORY_FILE = "Problem while trying to write in the history file.";
    private static final String HISTORY_FILES_DIRECTORY_PATH = "resources" + File.separator;
    private static final String HISTORY_FILE_SUFFIX_NAME = "-history.txt";


    public static String loadHistoryOfPayments(User user) {
        String filePath = HISTORY_FILES_DIRECTORY_PATH + user.getUsername() + HISTORY_FILE_SUFFIX_NAME;
        if (Files.exists(Paths.get(filePath))) {
            try (Reader reader = new FileReader(Paths.get(filePath).toAbsolutePath().toString())) {

                return loadHistoryOfPayments(reader);

            } catch (FileNotFoundException e) {
                System.out.println(HISTORY_FILE_COULD_NOT_OPEN);
                LogFileHandler.log(HISTORY_FILE_COULD_NOT_OPEN + e.getStackTrace());
            } catch (IOException e) {
                System.out.println(HISTORY_FILE_COULD_NOT_CLOSE);
                LogFileHandler.log(HISTORY_FILE_COULD_NOT_CLOSE + e.getStackTrace());
            }
        }
        return "";
    }

    public static String loadHistoryOfPayments(Reader reader) {
        StringBuilder stringBuilder = new StringBuilder();
        try (Scanner scanner = new Scanner(new BufferedReader(reader))) {
            while (scanner.hasNext()) {
                stringBuilder.append(scanner.nextLine());
                stringBuilder.append(System.lineSeparator());
            }
        }
        return stringBuilder.toString();
    }

    public static void addPayment(User user, String payedTo, Double amount) {
        Path path = Paths.get(HISTORY_FILES_DIRECTORY_PATH + user.getUsername() + HISTORY_FILE_SUFFIX_NAME);
        String payment = "You payed " + amount + " to " + payedTo + ".";
        if (Files.notExists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                System.out.println(HISTORY_FILE_COULD_NOT_CREATE);
                LogFileHandler.log(HISTORY_FILE_COULD_NOT_CREATE + e.getStackTrace());
                return;
            }
        }

        try (Writer writer = new FileWriter(path.toAbsolutePath().toString(), true)) {
            addPayment(payment, writer);
        } catch (IOException e) {
            System.out.println(HISTORY_FILE_COULD_NOT_CLOSE);
            LogFileHandler.log(HISTORY_FILE_COULD_NOT_CLOSE + e.getStackTrace());
        }
    }

    public static void addPayment(String payment, Writer writer) {
        try {
            writer.write(payment + System.lineSeparator());
            writer.flush();
        } catch (IOException e) {
            System.out.println(PROBLEM_WRITING_TO_HISTORY_FILE);
            LogFileHandler.log(PROBLEM_WRITING_TO_HISTORY_FILE + e.getStackTrace());
        }
    }
}
