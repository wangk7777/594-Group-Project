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
 * Date: 2026-04-12
 */

package edu.upenn.cit5940;

import edu.upenn.cit5940.datamanagement.CSVRepositoryImpl;
import edu.upenn.cit5940.datamanagement.DataRepository;
import edu.upenn.cit5940.datamanagement.JsonRepositoryImpl;
import edu.upenn.cit5940.logging.SingletonLogger;
import edu.upenn.cit5940.processor.SearchEngineImpl;
import edu.upenn.cit5940.ui.ConsoleApp;

/**
 * Main entry point for the Tech News Search Engine
 * Responsible for the following:
 *   1. Parse command-line arguments
 *   2. Initialize the Singleton Logger
 *   3. Select data loading strategy based on file extension (Strategy Pattern)
 *   4. Initialize all tiers via dependency injection
 *   5. Launch the UI
 */
public class Main {

    // Set file paths
    private static final String DEFAULT_DATA_FILE = "data/articles.csv";
    private static final String DEFAULT_LOG_FILE = "tech_news_search.log";
    private static final String DEFAULT_STOP_WORDS_FILE = "data/stop_words.txt";

    public static void main(String[] args) {

        // Parse command-line arguments
        String dataFilePath = DEFAULT_DATA_FILE;
        String logFilePath = DEFAULT_LOG_FILE;

        if (args.length >= 1) {
            dataFilePath = args[0];
        }
        if (args.length >= 2) {
            logFilePath = args[1];
        }

        // Initialize the Logger
        try {
            SingletonLogger.initialize(logFilePath);
        } catch (Exception e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
            return;
        }

        SingletonLogger logger = SingletonLogger.getInstance();
        logger.info("Application starting");
        logger.info("Data file: " + dataFilePath);
        logger.info("Log file: " + logFilePath);

        // Select data repository strategy
        // Choose CSV or JSON parser based on file extension
        DataRepository repository;

        // Check the file extension
        if (dataFilePath.endsWith(".json")) {
            repository = new JsonRepositoryImpl();
            logger.info("Using JSON parsing strategy");
        } else {
            repository = new CSVRepositoryImpl();
            logger.info("Using CSV parsing strategy");
        }

        // Initialize the Processor tier
        SearchEngineImpl searchEngine = new SearchEngineImpl(repository);

        try {
            searchEngine.initializeSystem(dataFilePath, DEFAULT_STOP_WORDS_FILE);

            int articleCount = searchEngine.getTotalArticlesLoaded();
            logger.info(articleCount + " articles loaded successfully");

            // Check for error
            if (articleCount == 0) {
                System.err.println("No valid articles found in " + dataFilePath);
                logger.error("Fatal error: No valid articles loaded");
                logger.close();
                return;
            }

            // Launch the UI tier
            // Print welcome message
            ConsoleApp ui = new ConsoleApp(searchEngine, dataFilePath);
            ui.start(dataFilePath, articleCount);

        } catch (Exception e) {
            // Fatal error: cannot proceed without data
            System.err.println("Unable to load data file - " + e.getMessage());
            logger.error("Fatal error! during initialization", e);
        }

        // Cleanup on exit
        logger.info("Application shutting down");
        logger.close();
    }
}