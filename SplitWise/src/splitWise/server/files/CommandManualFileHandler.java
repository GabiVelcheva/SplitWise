package splitWise.server.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class CommandManualFileHandler {
    private static final String COMMAND_MANUAL_FILE_COULD_NOT_OPEN = "File of command manual could not be opened.";
    private static final String COMMAND_MANUAL_FILE_COULD_NOT_CLOSE = "File of command manual could not be closed.";
    private static final String COMMAND_MANUAL_FILES_DIRECTORY_PATH = "resources" + File.separator;
    private static final String COMMAND_MANUAL_FILE_NAME = "CommandManual.txt";


    public static String loadCommandManual() {
        String filePath = COMMAND_MANUAL_FILES_DIRECTORY_PATH + COMMAND_MANUAL_FILE_NAME;
        if (Files.exists(Paths.get(filePath))) {
            try (Reader reader = new FileReader(Paths.get(filePath).toAbsolutePath().toString())) {

                return loadCommandManual(reader);

            } catch (FileNotFoundException e) {
                System.out.println(COMMAND_MANUAL_FILE_COULD_NOT_OPEN);
                LogFileHandler.log(COMMAND_MANUAL_FILE_COULD_NOT_OPEN + e.getStackTrace());
            } catch (IOException e) {
                System.out.println(COMMAND_MANUAL_FILE_COULD_NOT_CLOSE);
                LogFileHandler.log(COMMAND_MANUAL_FILE_COULD_NOT_CLOSE + e.getStackTrace());
            }
        }
        return "";
    }

    public static String loadCommandManual(Reader reader) {
        StringBuilder stringBuilder = new StringBuilder();
        try (Scanner scanner = new Scanner(new BufferedReader(reader))) {
            while (scanner.hasNext()) {
                stringBuilder.append(scanner.nextLine());
                stringBuilder.append(System.lineSeparator());
            }
        }
        return stringBuilder.toString();
    }
}