package splitWise.server.files;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LogFileHandler {
    private static final String LOG_FILE_COULD_NOT_CREATE = "File of log errors could not be created.";
    private static final String LOG_FILE_COULD_NOT_CLOSE = "File of log errors could not be closed.";
    private static final String PROBLEM_WRITING_TO_LOGFILE = "Problem while trying to write in the log file.";
    private final static String LOG_FILES_DIRECTORY_PATH = "resources" + File.separator + "server-logs.txt";

    public static void log(String message) {
        if (message.isEmpty()) {
            return;
        }
        Path path = Paths.get(LOG_FILES_DIRECTORY_PATH);
        if (Files.notExists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                System.out.println(LOG_FILE_COULD_NOT_CREATE);
                //throw new LoggingInServerException(LOG_FILE_NOT_CREATED, e);
            }
        }

        try (Writer writer = new FileWriter(path.toAbsolutePath().toString(), true)) {
            log(message, writer);
        } catch (IOException e) {
            System.out.println(LOG_FILE_COULD_NOT_CLOSE);
            //throw new LoggingInServerException(LOG_FILE_NOT_CLOSED, e);
        }
    }

    public static void log(String message, Writer writer) {
        try {
            writer.write(message + System.lineSeparator());
            writer.flush();
        } catch (IOException e) {
            System.out.println(PROBLEM_WRITING_TO_LOGFILE);
            //throw new LoggingInServerException(PROBLEM_WHILE_TRYING_TO_WRITE, e);
        }
    }
}
