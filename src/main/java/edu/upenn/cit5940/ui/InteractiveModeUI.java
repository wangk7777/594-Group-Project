package edu.upenn.cit5940.ui;
 
import edu.upenn.cit5940.processor.SearchEngineService;
import edu.upenn.cit5940.common.dto.ArticleDTO;
 
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class InteractiveModeUI {
	
	    private final SearchEngineService service;
	    private final Scanner scanner;
	 
	    private static final DateTimeFormatter DATE_FORMATTER =
	            DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    private static final DateTimeFormatter PERIOD_FORMATTER =
	            DateTimeFormatter.ofPattern("yyyy-MM");
	 
	    public InteractiveModeUI(SearchEngineService service, Scanner scanner) {
	        this.service = service;
	        this.scanner = scanner;
	    }
	
	    /**
	     * Enters the interactive mode loop.
	     * Shows menu, handles a service, prompts Enter, repeats until user picks "Back".
	     */
	    public void start() {
	        while (true) {
	            printInteractiveModeMenu();
	            String input = scanner.nextLine().trim();

	            // Empty input
	            if (input.isEmpty()) {
	                System.out.println("Please enter a choice (1-8):");
	                continue;
	            }

	            int choice;
	            try {
	                choice = Integer.parseInt(input);
	            } catch (NumberFormatException e) {
	                System.out.println("Please enter a valid number (1-8):");
	                continue;
	            }

	            if (choice == 8) {
	                return; // back to main menu
	            }

	            if (choice < 1 || choice > 8) {
	                System.out.println("Invalid choice. Please enter 1-8.");
	                continue;
	            }

	            // Run the selected service
	            runService(choice);

	            // Spec requires: prompt Enter after each result before returning to menu
	            System.out.println();
	            System.out.println("Press Enter to return to the Interactive Mode menu...");
	            scanner.nextLine();
	        }
	    }
	    
	    
	    // Service switch
	 
	    private void runService(int choice) {
	        switch (choice) {
	            case 1 -> handleSearch();
	            case 2 -> handleAutocomplete();
	            case 3 -> handleTopics();
	            case 4 -> handleTrends();
	            case 5 -> handleArticles();
	            case 6 -> handleArticle();
	            case 7 -> handleStats();
	        }
	    }
	    
	    
	    
	     // Search: prompts for keyword, displays search results.
	     
	    private void handleSearch() {
	        System.out.println("--- Search Articles ---");
	        System.out.print("Enter search keyword(s) (space-separated for AND search): ");
	        String input = scanner.nextLine().trim();

	        if (input.isEmpty()) {
	            System.out.println("No keyword entered.");
	            return;
	        }

	        List<String> keywords = Arrays.asList(input.split("\\s+"));
	        List<ArticleDTO> results = service.searchByKeyword(keywords);

	        System.out.println();
	        System.out.println("Search results:");
	        if (results.isEmpty()) {
	            System.out.println("No articles found.");
	        } else {
	        	  for (ArticleDTO a : results) {
	                  System.out.println("  - " + a.getTitle());
	              }
	        }
	    }
	    
	    
	    
	     // Autocomplete: prompts for prefix, displays up to 10 suggestions.
	     
	    private void handleAutocomplete() {
	        System.out.println("--- Autocomplete Suggestions ---");
	        System.out.print("Enter a prefix to autocomplete: ");
	        String prefix = scanner.nextLine().trim();

	        if (prefix.isEmpty()) {
	            System.out.println("No prefix entered.");
	            return;
	        }

	        List<String> suggestions = service.autocomplete(prefix);

	        System.out.println();
	        if (suggestions.isEmpty()) {
	            System.out.println("No suggestions found.");
	        } else {
	            for (String s : suggestions) {
	                System.out.println("  " + s);
	            }
	        }
	    }
	    
	    
	    
	    
	     // Top topics: prompts for YYYY-MM period, displays top 10 trending words.
	     
	    private void handleTopics() {
	        System.out.println("--- Top Topics ---");
	        System.out.print("Enter a period (in the format of YYYY-MM): ");
	        String period = scanner.nextLine().trim();

	        if (!isValidPeriod(period)) {
	            System.out.println("Invalid date format. Please use YYYY-MM.");
	            return;
	        }

	        List<Map.Entry<String, Integer>> topics = service.getTopics(period);

	        System.out.println();
	        System.out.println("Top topics for " + period + ":");
	        if (topics.isEmpty()) {
	            System.out.println("No topics found for this period.");
	        } else {
	            for (Map.Entry<String, Integer> e : topics) {
	                System.out.println("  " + e.getKey() + ": " + e.getValue());
	            }
	        }
	    }
	    
	    
	    
	    
	    
	     // Trends: prompts for topic and date range.
	     
	    private void handleTrends() {
	        System.out.println("--- Topic Trends ---");
	        System.out.print("Enter a topic word: ");
	        String topic = scanner.nextLine().trim();

	        if (topic.isEmpty()) {
	            System.out.println("No topic entered.");
	            return;
	        }

	        System.out.print("Enter start period (YYYY-MM): ");
	        String start = scanner.nextLine().trim();

	        System.out.print("Enter end period (YYYY-MM): ");
	        String end = scanner.nextLine().trim();

	        if (!isValidPeriod(start) || !isValidPeriod(end)) {
	            System.out.println("Invalid date format. Please use YYYY-MM.");
	            return;
	        }
	        if (start.compareTo(end) > 0) {
	            System.out.println("Invalid date range: start period cannot be after end period.");
	            return;
	        }

	        List<Map.Entry<String, Integer>> trends = service.getTrends(topic, start, end);

	        System.out.println();
	        System.out.println("Monthly frequency of \"" + topic + "\":");
	        for (Map.Entry<String, Integer> entry : trends) {
        	    System.out.println(entry.getKey() + ": " + entry.getValue());
        	}
	    }
	    
	    
	    
	     // Articles: prompts for date range, displays matching article titles.
	     
	    private void handleArticles() {
	        System.out.println("--- Browse Articles by Date ---");
	        System.out.print("Enter start date (YYYY-MM-DD): ");
	        String startStr = scanner.nextLine().trim();

	        System.out.print("Enter end date (YYYY-MM-DD): ");
	        String endStr = scanner.nextLine().trim();

	        if (!isValidDate(startStr) || !isValidDate(endStr)) {
	            System.out.println("Invalid date. Please use YYYY-MM-DD format with valid values.");
	            return;
	        }
	        if (startStr.compareTo(endStr) > 0) {
	            System.out.println("Invalid date range: start date cannot be after end date.");
	            return;
	        }

	        List<ArticleDTO> results = service.getArticlesByDateRange(startStr, endStr);

	        System.out.println();
	        System.out.println("Articles from " + startStr + " to " + endStr + ":");
	        if (results.isEmpty()) {
	            System.out.println("No articles found.");
	        } else {
	            for (ArticleDTO a : results) {
	                System.out.println("  - " + a.getTitle());
	            }
	        }
	    }
	    
	    
	     // Article lookup by IDs: prompts for article ID, displays full article details.
	     
	    private void handleArticle() {
	        System.out.println("--- View Article by ID ---");
	        System.out.print("Enter article ID (URI): ");
	        String id = scanner.nextLine().trim();

	        if (id.isEmpty()) {
	            System.out.println("No ID entered.");
	            return;
	        }

	        ArticleDTO article = service.getArticleById(id);

	        System.out.println();
	        if (article == null) {
	            System.out.println("No article found with ID: " + id);
	        } else {
	            System.out.println("ID    : " + article.getUri());
	            System.out.println("Title : " + article.getTitle());
	            System.out.println("Date  : " + article.getDate());
	            System.out.println("Body  : " + article.getBody());
	        }
	    }
	    
	    
	    
	    // Displays database statistics.
	     
	    private void handleStats() {
	        System.out.println("--- Statistics ---");
	        String total = service.getStats();
	        System.out.println("Total articles in database: " + total);
	    }

	    
	    
	    
	    
	    
	    // Validation helpers
	    private boolean isValidDate(String dateStr) {
	        try {
	            LocalDate.parse(dateStr, DATE_FORMATTER);
	            return true;
	        } catch (DateTimeParseException e) {
	            return false;
	        }
	    }

	    private boolean isValidPeriod(String periodStr) {
	        try {
	            YearMonth.parse(periodStr, PERIOD_FORMATTER);
	            return true;
	        } catch (DateTimeParseException e) {
	            return false;
	        }
	    }
	    
	    
	    
	    
	    
	    // Print helpers
	    

	    private void printInteractiveModeMenu() {
	        System.out.println("==================================================");
	        System.out.println("               INTERACTIVE MODE");
	        System.out.println("==================================================");
	        System.out.println("This mode will guide you through each operation step by step.");
	        System.out.println("----------------------------------------");
	        System.out.println("             AVAILABLE SERVICES");
	        System.out.println("----------------------------------------");
	        System.out.println("1. Search Articles");
	        System.out.println("2. Get Autocomplete Suggestions");
	        System.out.println("3. View Top Topics");
	        System.out.println("4. Analyze Topic Trends");
	        System.out.println("5. Browse Articles by Date");
	        System.out.println("6. View Specific Article by ID");
	        System.out.println("7. Show Statistics");
	        System.out.println("8. Back to Main Menu");
	        System.out.println("----------------------------------------");
	        System.out.print("Select a service (1-8): ");
	    }
	    
	    
	    

}