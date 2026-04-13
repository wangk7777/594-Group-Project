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

package edu.upenn.cit5940.processor.datastructures;

//import any classes you will need
import java.util.*;
import edu.upenn.cit5940.common.dto.Article;


public class InvertedIndex {

    // Initialize Hashmap
    private Map<String, Set<String>> indexMap;

    private Set<String> stopWords;

    // Constructor
    public InvertedIndex(Set<String> stopWords) {
        this.stopWords = stopWords;
        this.indexMap = new HashMap<>();
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
            if (!word.isEmpty() && word.length() > 1 && !stopWords.contains(word)){

                // Check if the keyword already exist
                if (!indexMap.containsKey(word)) {

                    // Create a new hashset and store it into map
                    indexMap.put(word, new HashSet<>());
                }

                // Get the set of the current word
                Set<String> uris = indexMap.get(word);

                // Add the uri of current article
                uris.add(uri);


            }
        }
    }

    /*
    This method returns a set of document IDs based on the query
    @param String query
    @return Set<String>
    */
    //search
    public Set<String> search(String query) {

        // Check if indexMap or query is null, return a new hashset
        if (indexMap.isEmpty() || query == null || query.trim().isEmpty()) {
            return new HashSet<>();
        }

        // Clean the text of query by calling cleanText helper method
        String[] words = cleanText(query);

        // Initialize the result set
        Set<String> finalResult = null;

        // Iterate every word in query
        for (String word : words) {

            // If current word is not empty and is not in STOP WORD
            if (!word.isEmpty() && word.length() > 1 && !stopWords.contains(word)){

                // Get this word from map
                Set<String> uris = indexMap.get(word);

                // Return a hashset if it is null
                if (uris == null || uris.isEmpty()) {
                    return new HashSet<>();
                }

                // If this is the first word we searched so far
                if (finalResult == null) {

                    // Initialize a hashset and copy the uri into it
                    finalResult = new HashSet<>(uris);

                } else {
                    // Find the intersection of uri
                    finalResult.retainAll(uris);

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
     @param String uri
     @return void
     */
    // remove the uri
    public void removeDocument(String uri) {

        if (uri == null || uri.isEmpty()) {
            return;
        }

        // Initialize a list to track words that no longer have any associated uris
        List<String> emptyWordsToRemove = new ArrayList<>();

        // Iterate through all words in the map
        for (String word : indexMap.keySet()) {

            // Get the set of uris associated with the current word
            Set<String> uris = indexMap.get(word);

            // Remove this uri
            uris.remove(uri);

            // Check if the set is empty now
            if (uris.isEmpty()) {
                // Mark this word for removal
                emptyWordsToRemove.add(word);
            }
        }

        // Clean up the index map by removing words with no remaining uris
        for (String emptyWord : emptyWordsToRemove) {
            indexMap.remove(emptyWord);
        }

    }

    /*
     This method get the map of inverted index
     can be used for testing purposes
     @param none
     @return Map<String, Set<String>>
     */
    // returns the map of the inverted index
    public Map<String, Set<String>> getIndex() {

        // Initialize a linked hashmap
        Map<String, Set<String>> copyMap = new LinkedHashMap<>();

        // Iterate through each entry in the source indexMap
        for (Map.Entry<String, Set<String>> entry : indexMap.entrySet()) {

            // Create a new HashSet containing all elements of the current set and put it into the new map
            copyMap.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        return copyMap;
    }


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

}
