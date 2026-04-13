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



public class CommandModeUI {
	 
    private final SearchEngineService service;
    private final Scanner scanner;
 
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter PERIOD_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM");
 
    public CommandModeUI(SearchEngineService service, Scanner scanner) {
        this.service = service;
        this.scanner = scanner;
    }
    
    /**
     * Enters the command mode loop.
     * Displays header and ">" prompt, reads commands until user types "menu".
     */
    public void start() {
        printCommandModeHeader();
 
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
 
            if (input.equalsIgnoreCase("menu")) {
                return; // return to main menu
            }
 
            if (!input.isEmpty()) {
                handleCommand(input);
            }
        }
    }
    
    /**
     * Parses the raw input line and dispatches to the correct handler.
     */
    private void handleCommand(String input) {
        String[] parts = input.split("\\s+");
        String cmd = parts[0].toLowerCase();
 
        switch (cmd) {
            case "search"       -> handleSearch(parts);
            case "autocomplete" -> handleAutocomplete(parts);
            case "topics"       -> handleTopics(parts);
            case "trends"       -> handleTrends(parts);
            case "articles"     -> handleArticles(parts);
            case "article"      -> handleArticle(parts);
            case "stats"        -> handleStats();
            case "help"         -> printCommandHelp();
            default             -> System.out.println(
                    "Unknown command. Type 'help' for available commands.");
        }
    }
    
    
    
    
    /**
     * search <keyword(s)> - AND search across all keywords.
     */
    private void handleSearch(String[] parts) {
        if (parts.length < 2) {
        	System.out.println("Usage: search <keyword>");
            return;
        }
        String keyword = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        List<ArticleDTO> results = service.searchByKeyword(keyword);
 
        if (results.isEmpty()) {
            System.out.println("No articles found.");
        } else {
        	for (ArticleDTO article : results) {
        	    System.out.println(article.getTitle());
        	}
        }
    }
    
    
    
    
    
    
    /**
     * autocomplete <prefix> - returns up to 10 word suggestions from titles.
     */
    private void handleAutocomplete(String[] parts) {
        if (parts.length < 2) {
        	System.out.println("Usage: autocomplete <prefix>");
            return;
        }
        
        List<String> suggestions = service.autocomplete(parts[1]);
 
        if (suggestions.isEmpty()) {
            System.out.println("No suggestions found.");
        } else {
        	 for (String suggestion : suggestions) {
                 System.out.println(suggestion);
        	 }
        }
    }
    
    
    
    
    /**
     * topics <YYYY-MM> - top 10 trending words for the given month.
     */
    private void handleTopics(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: topics <YYYY-MM>");
            return;
        }
        
        if (!isValidPeriod(parts[1])) {
            System.out.println("Invalid date format. Please use YYYY-MM (e.g., 2023-12).");
            return;
        }
        
        List<Map.Entry<String, Integer>> topics = service.getTopics(parts[1]);
 
        if (topics.isEmpty()) {
            System.out.println("No topics found for this period.");
        } else {
        	for (Map.Entry<String, Integer> entry : topics) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
    }
    
    
    
    /**
     * trends <topic> <YYYY-MM> <YYYY-MM> - monthly frequency of a word in a date range.
     */
    private void handleTrends(String[] parts) {
        if (parts.length < 4) {
            System.out.println("Usage: trends <topic> <YYYY-MM> <YYYY-MM>");
            return;
        }
        String topic = parts[1];
        String start = parts[2];
        String end   = parts[3];
 
        if (!isValidPeriod(start) || !isValidPeriod(end)) {
            System.out.println("Invalid date format. Please use YYYY-MM (e.g., 2023-01).");
            return;
        }
        if (start.compareTo(end) > 0) {
            System.out.println("Invalid date range: start period cannot be after end period.");
            return;
        }
 
        List<Map.Entry<String, Integer>> trends = service.getTrends(topic, start, end);
        if (trends.isEmpty()) {
            System.out.println("No trends found.");
        } else {
        	for (Map.Entry<String, Integer> entry : trends) {
        	    System.out.println(entry.getKey() + ": " + entry.getValue());
        	}
        }
    }
    
    
    /**
     * articles <YYYY-MM-DD> <YYYY-MM-DD> - list articles published within date range.
     */
    private void handleArticles(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Usage: articles <YYYY-MM-DD> <YYYY-MM-DD>");
            return;
        }
        String startStr = parts[1];
        String endStr   = parts[2];
 
        if (!isValidDate(startStr) || !isValidDate(endStr)) {
            System.out.println("Invalid date. Please use YYYY-MM-DD format with valid values.");
            return;
        }
        if (startStr.compareTo(endStr) > 0) {
            System.out.println("Invalid date range: start date cannot be after end date.");
            return;
        }
 
        List<ArticleDTO> results = service.getArticlesByDateRange(startStr, endStr);
 
        if (results.isEmpty()) {
            System.out.println("No articles found.");
        } else {
            for (ArticleDTO article : results) {
                System.out.println(article.getTitle());
            }
        }
    }
 
    
    
    
    
    /**
     * article <id> - display full details of a single article.
     */
    private void handleArticle(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: article <id>");
            return;
        }
        String id = parts[1];
        ArticleDTO article = service.getArticleById(id);
 
        if (article == null) {
            System.out.println("No article found with ID: " + id);
        } else {
            System.out.println("ID    : " + article.getUri());
            System.out.println("Title : " + article.getTitle());
            System.out.println("Date  : " + article.getDate());
            System.out.println("Body  : " + article.getBody());
        }
    }
 
    
    
    
    
    
    /**
     * stats - display total article count.
     */
    private void handleStats() {
        String total = service.getStats();
        System.out.println("Total articles: " + total);
    }
    
    
    
    
    
    //
    // Validation helpers
    
    /**
     * Validates a YYYY-MM period string.
     */
    private boolean isValidPeriod(String period) {
        try {
            YearMonth.parse(period, PERIOD_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    
 
    /**
     * Validates a YYYY-MM-DD date string including logical validity.
     */
    private boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
 

    
    
    

    // Print helpers

    private void printCommandModeHeader() {
        System.out.println("==================================================");
        System.out.println("                  COMMAND MODE");
        System.out.println("==================================================");
        System.out.println("Enter commands directly. Type 'help' for available commands.");
        System.out.println("Type 'menu' to return to the main menu.");
        System.out.println();
    }
 
    private void printCommandHelp() {
        System.out.println("--------------------------------------------------");
        System.out.println("Available Commands:");
        System.out.println("  search <keyword(s)>          - Search articles by keyword (AND)");
        System.out.println("  autocomplete <prefix>        - Get word suggestions from titles");
        System.out.println("  topics <YYYY-MM>             - Top 10 trending words for a month");
        System.out.println("  trends <topic> <start> <end> - Monthly frequency of a word");
        System.out.println("  articles <start> <end>       - Articles in a date range (YYYY-MM-DD)");
        System.out.println("  article <id>                 - View a specific article by ID");
        System.out.println("  stats                        - Show total article count");
        System.out.println("  help                         - Show this command list");
        System.out.println("  menu                         - Return to main menu");
        System.out.println("--------------------------------------------------");
    }
    
    
    
    
}