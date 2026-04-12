/*
 * I attest that the code in this file is entirely my own except for the starter
 * code provided with the assignment and the following exceptions:
 * <Enter all external resources and collaborations here. Note external code may
 * reduce your score but appropriate citation is required to avoid academic
 * integrity violations. Please see the Course Syllabus as well as the
 * university code of academic integrity:
 *  https://catalog.upenn.edu/pennbook/code-of-academic-integrity/ >
 * Signed,
 * Author: Kaiqi Wang
 * Penn email: <kaiqiw2@seas.upenn.edu>
 * Date: 2026-04-07
 */

package edu.upenn.cit5940.logging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Singleton Logger that writes all log output to a file
 */
public class SingletonLogger {

    // The single instance of this class
    private static SingletonLogger instance;

    // Writer for the log file
    private PrintWriter writer;

    // Formatter for timestamps
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Private constructor to prevents external instantiation
     * @param logFilePath the path to the log file
     * @throws IOException if the log file cannot be created or opened
     */
    private SingletonLogger(String logFilePath) throws IOException {
        // Open the file in append mode so logs are not overwritten
        this.writer = new PrintWriter(
                new BufferedWriter(new FileWriter(logFilePath, true)), true);
    }

    /**
     * Initialize the Singleton logger with the specified log file path
     * @param logFilePath the path to the log file
     * @throws IOException if the log file cannot be created or opened
     */
    public static synchronized void initialize(String logFilePath) throws IOException {

        // If a previous instance exists, close it first
        if (instance != null) {
            instance.close();
        }

        instance = new SingletonLogger(logFilePath);
    }

    /**
     * Get the Singleton logger instance
     * @return the AppLogger instance
     * @throws IllegalStateException if initialize() has not been called yet
     */
    public static synchronized SingletonLogger getInstance() {

        if (instance == null) {
            throw new IllegalStateException();
        }

        return instance;
    }

    /**
     * Log an INFO message
     * @param message the message to log
     */
    public void info(String message) {
        log("INFO", message);
    }

    /**
     * Log a WARNING message
     * @param message the message to log
     */
    public void warn(String message) {
        log("WARN", message);
    }

    /**
     * Log an ERROR message
     * @param message the message to log
     */
    public void error(String message) {
        log("ERROR", message);
    }

    /**
     * Log an ERROR message with an exception stack trace
     * @param message the message to log
     * @param e the exception whose stack trace should be logged
     */
    public void error(String message, Exception e) {
        log("ERROR", message + " - " + e.getMessage());
    }

    /**
     * Core logging method. Writes a formatted log entry to the file
     * Format: [YYYY-MM-DD HH:MM:SS]
     * @param level the log level (INFO, WARN, ERROR)
     * @param message the message to log
     */
    private void log(String level, String message) {

        if (writer == null) {
            return;
        }

        // Build the log entry with timestamp and level
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logEntry = "[" + timestamp + "] " + level + " " + message;

        // Write to the log file (PrintWriter is auto-flushed)
        writer.println(logEntry);
    }

    /**
     * Close the logger and release the file resource
     */
    public void close() {
        if (writer != null) {
            writer.flush();
            writer.close();
            writer = null;
        }
    }
}