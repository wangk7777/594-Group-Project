package edu.upenn.cit5940.ui;
import java.util.Scanner;

/**
 * Handles the display and input of the main menu.
 */
public class MainMenuUI {
	
		private final Scanner scanner;
		 
		public MainMenuUI(Scanner scanner) {
		        this.scanner = scanner;
		 }
	 
	 
	    public String show() {
	        printMenu();
	        while (true) {
	            String input = scanner.nextLine().trim();
	 
	            // Empty input
	            if (input.isEmpty()) {
	                System.out.println("Please enter a choice (1-4):");
	                continue;
	            }
	 
	            // Non-numeric input
	            int choice;
	            try {
	                choice = Integer.parseInt(input);
	            } catch (NumberFormatException e) {
	                System.out.println("Please enter a valid number (1-4):");
	                continue;
	            }
	 
	            // Out-of-range input
	            if (choice < 1 || choice > 4) {
	                System.out.println("Invalid choice. Please enter 1-4:");
	                continue;
	            }
	 
	            return String.valueOf(choice);
	        }
	    }
	    
	    
	    /**
	     * Displays the Help & Documentation screen.
	     */
	    
	    void displayHelp() {
	        System.out.println("=== HELP & DOCUMENTATION ===");
	        System.out.println();
	        System.out.println("INTERACTIVE MODE: Guided step-by-step interface.");
	        System.out.println("COMMAND MODE: Direct command entry. Type 'help' for available commands.");
	        System.out.println();
	        System.out.println("AVAILABLE SERVICES:");
	        System.out.println("  1. Search Articles");
	        System.out.println("  2. Autocomplete");
	        System.out.println("  3. Top Topics");
	        System.out.println("  4. Topic Trends");
	        System.out.println("  5. Browse Articles by Date");
	        System.out.println("  6. View Article by ID");
	        System.out.println("  7. Statistics");
	        System.out.println();
	        System.out.println("Press Enter to return to the main menu...");
	        scanner.nextLine();
	    }
	    
	    // Private helper that print the main menu
	    private void printMenu() {
	        System.out.println("==================================================");
	        System.out.println("                    MAIN MENU");
	        System.out.println("==================================================");
	        System.out.println("1. Interactive Mode (Guided Menu)");
	        System.out.println("2. Command Mode (Direct Commands)");
	        System.out.println("3. Help & Documentation");
	        System.out.println("4. Exit");
	        System.out.println("==================================================");
	        System.out.print("Please select an option (1-4): ");
	    }
	 
	 
	
	
}