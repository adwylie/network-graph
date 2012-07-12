package logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2011-09-10
 */
public class FileLogger {

    private static FileHandler fileTxt;
    private final static SimpleFormatter formatterTxt = new SimpleFormatter();

    private static Logger logger = null;

    /**
     * Constructor, private as we'll use the singleton pattern.
     */
    private FileLogger() {
    }

    /**
     * Set up the logger & assign it to write to a log file.
     * 
     * @throws IOException
     * @throws SecurityException
     */
    private static void setup() throws IOException, SecurityException {
        // Only allow setup to occur once, so log files can't be overwritten
        // while within a single execution of the program.
        if (logger == null) {
            setupLogger(Level.INFO);
            fileTxt = new FileHandler("log.txt");
            fileTxt.setFormatter(formatterTxt);
            logger.addHandler(fileTxt);
        }
    }

    /**
     * Set up our logger to be the global logger, along with setting its level.
     * 
     * @param level a level of logging. ie. Level.INFO, Level.WARNING, &c.
     */
    static private void setupLogger(Level level) {
        logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        logger.setLevel(level);
    }

    /**
     * Log a message. First initialize the logger if need be, then log the
     * message at the given severity. If there is an error creating a log file
     * to write to, the logger is disabled by setting its level to Level.OFF.
     * 
     * @param level the level of logging to do. ie. Level.INFO, &c.
     * @param message the logged message.
     */
    static public void log(Level level, String message) {
        try {
            setup();
            logger.log(level, message);
        } catch (Exception e) {
            // If we can't create an output file for the info, then don't
            // bother even logging the info.
            logger.setLevel(Level.OFF);
        }
    }

    /**
     * Disable logging.
     */
    static public void disableLogging() {
        setupLogger(Level.OFF);
    }

}
