package log;

import java.util.logging.*;

/**
 * Created by Timbo on 16.06.17.
 * @version 06.2017
 */
public class Log
{
    /**
     * The Class that empoweres the logger to write to the console
     */
    private ConsoleHandler _consoleHandler;

    /**
     * A Simple Format for the ConsoleHandler to format its output
     */
    private Formatter _simpleFormatter;

    /**
     * The logger instance that creates the messages
     */
    private static Logger _logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Constructor that builds a new Log instance
     */
    private Log()
    {
        _consoleHandler = new ConsoleHandler();
        _simpleFormatter = new SimpleFormatter();

    }

    /**
     * The factory method to create a log
     * @return A new Log instance
     */
    public Log getLogger()
    {
        return new Log();
    }

    /**
     * Logs an info message to the console
     * @param info
     */
    public static void info(String info)
    {
        _logger.log(Level.INFO, info);
    }

    public static void warning(String warning)
    {
        _logger.log(Level.WARNING, warning);
    }


}
