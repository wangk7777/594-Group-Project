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

package business.datastructures;

//import any classes you will need
import java.util.*;
import data.Article;


public class InvertedIndex {

    // Root of the BST
    private BSTNode root;

    private Set<String> stopWords;

    // define a private static inner class that represents a node in the BST
    private static class BSTNode{
        // keyWord that is indexed
        String keyWord;
        // set of uri where the keyWord appears
        Set<String> uris;
        // the left node stores keywords less than this node's keyword
        // the right node stores keywords greater than this node's keyword
        BSTNode left, right;

        // constructor to initialize each node
        BSTNode(String keyWord, String uri){
            this.keyWord = keyWord;
            this.uris = new HashSet<>();
            this.uris.add(uri);
        }
    }

    // Constructor
    public InvertedIndex(Set<String> stopWords) {
        this.stopWords = stopWords;
    }


    /*
     This method adds a document
     @param Article object
     @return no return
     */
    // addDocument
    public void addDocument(Article article) {

        // Check the null case
        if (article == null) {
            return;
        }

        String uri = article.getUri();

        // Concatenate the title and body
        String textToProcess = article.getTitle() + " " + article.getBody();

        // Clean the input text by calling cleanText helper method
        String[] words = cleanText(textToProcess);

        // Iterate through each word
        for (String word : words) {

            // Check for empty strings and stop word
            if (!word.isEmpty() && !stopWords.contains(word)) {

                // Insert this word into BST by calling insertText helper method
                root = insertText(root, word, uri);
            }
        }
    }

    /*
    This method returns a set of document IDs based on the query
    @param String query
    @return Set<Integer>
    */
    //search
    public Set<String> search(String query) {

        // Check if root or query is null, return a new hashset
        if (root == null || query == null || query.trim().isEmpty()) {
            return new HashSet<>();
        }

        // Clean the text of query by calling cleanText helper method
        String[] words = cleanText(query);

        // Initialize the result set
        Set<String> finalResult = null;

        // Iterate every word in query
        for (String word : words) {

            // If current word is not empty and is not in STOP WORD
            if (!word.isEmpty() && !stopWords.contains(word)) {

                // Search this word by calling helper method searchNode
                BSTNode node = searchNode(root, word);

                // Return a hashset if node is null
                if (node == null) {
                    return new HashSet<>();
                }

                // If this is the first word we searched so far
                if (finalResult == null) {

                    // Initialize a hashset and copy the uri into it
                    finalResult = new HashSet<>(node.uris);
                } else {
                    // Find the intersection of uri
                    finalResult.retainAll(node.uris);

                    // If the intersection is empty, return new hashset
                    if (finalResult.isEmpty()) {
                        return new HashSet<>();
                    }

                }
            }
        }

        // If our result set is still null after loop ends, return new hashset
        if (finalResult == null) {
            return new HashSet<>();
        }

        return finalResult;
    }



    /*
     This method removes a document based on the uri
     @param int uri
     @return void
     */
    // to remove a document traverse the entire tree and remove the given uri from the node's set
    // remove the uri
    public void removeDocument(String uri) {

        // Remove the given uri by calling removeID helper method
        removeID(root, uri);
    }

    /*
     This method get the map of inverted index
     can be used for testing purposes
     @param none
     @return Map<String, Set<Integer>>
     */
    // returns the map of the inverted index
    public Map<String, Set<String>> getIndex() {

        // Initialize a linked hashmap
        Map<String, Set<String>> indexMap = new LinkedHashMap<>();

        // Check if root is null
        if (root == null) {
            return indexMap;
        }

        // Build the map by calling buildIndexMap helper method
        buildIndexMap(root, indexMap);

        return indexMap;
    }

    /*
     * TODO: Implement helper methods below
     */

    /*
This is a helper method cleaning the input text by lowercasing, handling punctuation, and splitting whitespaces
 */
    private String[] cleanText(String text) {

        // Handle the null case
        if (text == null || text.isEmpty()) {
            return new String[0];
        }

        // Convert input text to lowercase
        text = text.toLowerCase();

        // Remove all non-alphabet, non-hyphens, and non-numbers to whitespace
        text = text.replaceAll("[^a-z0-9\\s-]", " ");

        // Split whitespaces
        String[] rawWords = text.split("\\s+");

        // Initialize a list to hold clean words
        List<String> cleanWords = new ArrayList<>();

        // Iterate through each token and trim hyphens
        for (String word : rawWords) {

            // Trim hyphens at the beginning and end for the current word
            word = word.replaceAll("^-+|-+$", "");

            // If the word is not empty after trimming, add to the clean list
            if (!word.isEmpty()) {
                cleanWords.add(word);
            }
        }

        // Convert the list back to an array
        return cleanWords.toArray(new String[0]);

    }
    /*
    This is a helper method to recursively insert text into BST
     */
    private BSTNode insertText(BSTNode current,String word,String uri) {

        // Base case: Create a new node if reaches empty
        if (current == null) {
            return new BSTNode(word, uri);
        }

        // Set a comparing variable for current node
        int compare = word.compareTo(current.keyWord);

        // Compare the words
        // If the word the smaller, then goes to left
        if (compare < 0) {
            current.left = insertText(current.left, word, uri);
        }
        // If the word is larger, then goes to right
        else if (compare > 0) {
            current.right = insertText(current.right, word, uri);
        }
        // If the word already exist in the BST, just add its uri
        else {
            current.uris.add(uri);
        }

        return current;
    }

    /*
    This helper method search given word in BST
     */
    private BSTNode searchNode(BSTNode current, String word) {

        // Base case: return current node if we found the word, return null if not found
        if (current == null || current.keyWord.equals(word)) {
            return current;
        }
        // Initialize a comparing variable
        int compare = word.compareTo(current.keyWord);

        // If the word is smaller, search left subtree
        if (compare < 0) {
            return searchNode(current.left, word);
        }
        // If the word is greater, search right subtree
        else{
            return searchNode(current.right, word);
        }
    }

    /*
    This helper method use pre-order traversal to traverse the BST to remove the uri
     */
    private void removeID(BSTNode current, String uri) {

        // Base case: return at null node
        if (current == null) {
            return;
        }

        // The pre-order traversal follows: root-> left -> right
        // Remove uri
        current.uris.remove(uri);

        // Visit the left subtree
        removeID(current.left, uri);

        // Visit the right subtree
        removeID(current.right, uri);

    }

    /*
    This helper method use in-order traversal traverse the BST to build the map
     */
    private void buildIndexMap(BSTNode current,Map<String, Set<String>> map) {

        // Base case: return at null node
        if (current == null) {
            return;
        }

        // The in-order traversal follows: left -> root -> right
        // Traverse the left subtree
        buildIndexMap(current.left, map);

        // Add keyword as key and uri as value
        map.put(current.keyWord, new HashSet<>(current.uris));

        // Traverse the right subtree
        buildIndexMap(current.right, map);

    }





}
