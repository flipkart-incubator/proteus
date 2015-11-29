package com.flipkart.layoutengine.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;

/**
 * LogbackConfigureHelper
 * This helper method configures logback for logging.
 * We are doing it using code since config using logback.xml caused issues of not logging sometimes.
 *
 * @author aditya.sharat
 */
public class LogbackConfigureHelper {

    public static void configure() {
        // reset the default context (which may already have been initialized)
        // since we want to reconfigure it
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.reset();

        //File file = new File(context.getFilesDir(), "main.log");

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(lc);
        encoder.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger - %msg%n");
        encoder.start();


        /*FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
        fileAppender.setContext(lc);
        fileAppender.setFile(file.getAbsolutePath());
        fileAppender.setEncoder(encoder);
        fileAppender.start();*/

        /*AsyncAppender asyncAppender = new AsyncAppender();
        asyncAppender.setContext(lc);
        asyncAppender.addAppender(fileAppender);
        asyncAppender.start();*/

        LogcatAppender logcatAppender = new LogcatAppender();
        logcatAppender.setContext(lc);
        logcatAppender.setEncoder(encoder);
        logcatAppender.start();

        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.ALL); // level.OFF can be used for muting all logs
        //root.addAppender(asyncAppender); //for logging to a file
        root.addAppender(logcatAppender); //for logging to logcat

    }
}
