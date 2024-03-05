package com.hirshi001.game.gwt;

import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.backends.gwt.GwtApplicationLogger;

public class GWTCustomLogger implements ApplicationLogger {
    @Override
    public void log(String tag, String message) {
        GwtApplicationLogger.consoleLog(tag + ": " + message);
    }

    @Override
    public void log(String tag, String message, Throwable exception) {

    }

    @Override
    public void error(String tag, String message) {

    }

    @Override
    public void error(String tag, String message, Throwable exception) {

    }

    @Override
    public void debug(String tag, String message) {

    }

    @Override
    public void debug(String tag, String message, Throwable exception) {

    }
}
