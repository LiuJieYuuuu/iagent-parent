package com.iagent.logging;

import com.iagent.exception.LogException;
import com.iagent.logging.commons.JakartaCommonsLoggingImpl;
import com.iagent.logging.console.ConsoleImpl;
import com.iagent.logging.jdk14.Jdk14LoggingImpl;
import com.iagent.logging.log4j.Log4jImpl;
import com.iagent.logging.log4j2.Log4j2Impl;
import com.iagent.logging.nologging.NoLoggingImpl;
import com.iagent.logging.slf4j.Slf4jImpl;

import java.lang.reflect.Constructor;

public final class LogFactory {

    public static final String MARKER = "IAGENT";

    private static Constructor<? extends Logger> constructor = null;

    static {
        useSlf4jLogging();
        useCommonsLogging();
        useLog4JLogging();
        useLog4J2Logging();
        useJdkLogging();
        useConsoleLogging();
        useNoLogging();
    }

    private static void useSlf4jLogging(){
        tryUseLogging(Slf4jImpl.class);
    }
    public static synchronized void useCommonsLogging() {
        tryUseLogging(JakartaCommonsLoggingImpl.class);
    }

    public static synchronized void useLog4JLogging() {
        tryUseLogging(Log4jImpl.class);
    }

    public static synchronized void useLog4J2Logging() {
        tryUseLogging(Log4j2Impl.class);
    }

    public static synchronized void useJdkLogging() {
        tryUseLogging(Jdk14LoggingImpl.class);
    }

    public static synchronized void useConsoleLogging() {
        tryUseLogging(ConsoleImpl.class);
    }

    public static synchronized void useNoLogging() {
        tryUseLogging(NoLoggingImpl.class);
    }

    public static Logger getLogger(String clazzName) {
        try {
            return constructor.newInstance(clazzName);
        } catch (Throwable t) {
            throw new LogException("Error creating logger for logger " + clazzName + ".  Cause: " + t, t);
        }
    }

    public static Logger getLogger(Class clazz) {
        try {
            return constructor.newInstance(clazz.getName());
        } catch (Throwable t) {
            throw new LogException("Error creating logger for logger " + clazz.getName() + ".  Cause: " + t, t);
        }
    }

    public static void tryUseLogging(Class<? extends Logger> clazz) {
        if (constructor == null) {
            useLogging(clazz);
        }
    }

    public static void useLogging(Class<? extends Logger> clazz){
        try {
            Constructor<? extends Logger> candidate = clazz.getConstructor(String.class);
            Logger logger = candidate.newInstance(LogFactory.class.getName());
            if (logger.isDebugEnabled()) {
                logger.debug("Logging initialized using '" + clazz + "' adapter.");
            }
            constructor = candidate;
        } catch (Throwable e) {
            //adapter log frame work
        }
    }
}
