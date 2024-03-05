package com.hirshi001.game.lwjgl3;

import com.badlogic.gdx.ApplicationLogger;
import logger.ConsoleColors;
import logger.DateStringFunction;
import logger.Logger;

public class CustomApplicationLogger implements ApplicationLogger {


    static String log_color = ConsoleColors.GREEN;
    static String error_color = ConsoleColors.RED;
    static String debug_color = ConsoleColors.YELLOW;

    public Logger logger;

    public CustomApplicationLogger(){
        logger = new Logger(System.out, System.err,
                new DateStringFunction(ConsoleColors.CYAN, "[", "]")
                );
        // logger.debug();
        logger.debugShort(true);
    }
    @Override
    public void log(String tag, String message) {
        logger.log(log_color + "[" + tag + "] "+ ConsoleColors.RESET + message, 2);
    }

    @Override
    public void log(String tag, String message, Throwable exception) {
        logger.log(log_color + "[" + tag + "] "+ ConsoleColors.RESET + message, 2);
        exception.printStackTrace(logger.getErr());
    }

    @Override
    public void error(String tag, String message) {
        boolean debug = logger.getDebug();
        logger.debug(true);
        logger.error(error_color + "[" + tag + "] "+ ConsoleColors.RESET + message, 2);
        logger.debug(debug);
    }

    @Override
    public void error(String tag, String message, Throwable exception) {
        boolean debug = logger.getDebug();
        logger.debug(true);
        logger.error(error_color + "[" + tag + "] "+ ConsoleColors.RESET + message, 2);
        logger.debug(debug);
        exception.printStackTrace(logger.getErr());
    }

    @Override
    public void debug(String tag, String message) {
        boolean debug = logger.getDebug();
        logger.debug(true);
        logger.log(debug_color + "[" + tag + "] "+ ConsoleColors.RESET + message, 2);
        logger.debug(debug);
    }

    @Override
    public void debug(String tag, String message, Throwable exception) {
        boolean debug = logger.getDebug();
        logger.debug(true);
        logger.log(debug_color + "[" + tag + "] "+ ConsoleColors.RESET + message, 2);
        logger.debug(debug);
        exception.printStackTrace(logger.getErr());

    }
}
