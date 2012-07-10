package logging;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class NetworkLogger {

    private static FileHandler fileTxt;
    private static SimpleFormatter formatterTxt;
    
    public static Logger LOGGER = null;
    
    private NetworkLogger() {}
    
    static public void setup() throws IOException {
        // Only allow setup to occur once, so log files can't be overwritten
        // while within a single execution of the program. Like some kind of
        // messed up singleton.
        if (LOGGER == null) {
            LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
            LOGGER.setLevel(Level.INFO);
            
            // Create text formatter.
            fileTxt = new FileHandler("NetLog.txt");
            formatterTxt = new SimpleFormatter();
            
            fileTxt.setFormatter(formatterTxt);
            LOGGER.addHandler(fileTxt);
        }
    }
    
}
