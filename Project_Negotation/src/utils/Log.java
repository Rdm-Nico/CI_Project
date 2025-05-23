package utils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {
    private static final Logger logger = Logger.getLogger(Log.class.getName());

    public Logger InitLog() {
        try {
            SimpleFormatter formatter = new SimpleFormatter() {
                private static final String format = "%3$s"; // only show message w.o. new line

                @Override
                public synchronized String format(LogRecord lr) {
                    return String.format(format,
                            new Date(lr.getMillis()),
                            lr.getLevel().getLocalizedName(),
                            lr.getMessage());
                }
            };
            DateTimeFormatter date_format = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm");
            String filename = "Project_Negotation/logs/" + LocalDateTime.now().format(date_format) + ".log";
            FileHandler fileHandler = new FileHandler(filename, true);
            ConsoleHandler consoleHandler = new ConsoleHandler();

            fileHandler.setFormatter(formatter);
            consoleHandler.setFormatter(formatter);

            logger.addHandler(fileHandler);
            logger.addHandler(consoleHandler);
            logger.setUseParentHandlers(false); // disable default output on the console for not shitty output

            return logger;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return logger;
        }
    }
}
