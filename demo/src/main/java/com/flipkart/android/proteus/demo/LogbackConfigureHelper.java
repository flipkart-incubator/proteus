package com.flipkart.android.proteus.demo;

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
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.reset();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(lc);
        encoder.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger - %msg%n");
        encoder.start();

        LogcatAppender logcatAppender = new LogcatAppender();
        logcatAppender.setContext(lc);
        logcatAppender.setEncoder(encoder);
        logcatAppender.start();

        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.OFF);
        root.addAppender(logcatAppender);
    }
}
