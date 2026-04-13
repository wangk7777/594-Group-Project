package edu.upenn.cit5940.ui;
import edu.upenn.cit5940.processor.SearchEngineService;
import java.util.Scanner;


public class ConsoleApp {
	private final SearchEngineService service;
	private final Scanner scanner;
	
	private final MainMenuUI mainMenuUI;
	private final InteractiveModeUI interactiveModeUI;
	private final CommandModeUI commandModeUI;
	
	

    public ConsoleApp(SearchEngineService service, String articlesFilepath) {
        this.service = service;
        this.scanner = new Scanner(System.in);
 
        this.mainMenuUI = new MainMenuUI(scanner);
        this.interactiveModeUI = new InteractiveModeUI(service, scanner);
        this.commandModeUI = new CommandModeUI(service, scanner);
    }
    
    
    /**
     * Starts the application: prints startup messages, then enters the main loop.
     * @param articlesFilepath path shown in the loading message
     * @param articleCount number of articles successfully loaded
     */
    public void start(String articlesFilepath, int articleCount) {
        printStartupMessages(articlesFilepath, articleCount);
        runMainLoop();
    }
	
    
    
     // Prints all startup messages 
     
    private void printStartupMessages(String articlesFilepath, int articleCount) {
        System.out.println("=== Tech News Search Engine ===");
        System.out.println("Initializing n-tier architecture...");
        System.out.println("Loading articles from: " + articlesFilepath);
        System.out.println(articleCount + " articles loaded");
        System.out.println("Architecture initialization complete!");
        System.out.println();
    }
    
    
     // Main navigation loop that assigns to sub-UIs based on user's choice
     
    private void runMainLoop() {
        while (true) {
            String choice = mainMenuUI.show();
 
            switch (choice) {
                case "1" -> interactiveModeUI.start();
                case "2" -> commandModeUI.start();
                case "3" -> mainMenuUI.displayHelp();
                case "4" -> {
                    printExitMessages();
                    return;
                }
            }
        }
    }
    
    
     // Prints exit messages
     
    private void printExitMessages() {
        System.out.println();
        System.out.println("Thank you for using the Tech News Search Engine!");
        System.out.println("Goodbye!");
    }
 
	
}
