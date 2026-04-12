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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomTrie {

    // inner class
    private class Node {
        private HashMap<Character, Node> children = new HashMap<>();

        // TODO (provide starting value)
        private boolean endOfWord = false;

    }

    // root node (has no value)
    private Node root = new Node();

    // TODO
    public void insertWord(String word) {

        // Initialize from root
        Node current = root;

        // Iterate through all characters in words
        for (int i = 0; i < word.length(); i++) {

            // Get the current character
            char c = word.charAt(i);

            // Check if the current hashmap has no this character
            if (!current.children.containsKey(c)) {

                // Create a new key
                current.children.put(c, new Node());
            }

            // Move to the next node
            current = current.children.get(c);
        }

        // Set the flag to true indicating the end of this word
        current.endOfWord = true;
    }

    // this implementation is given to students in the starter code
    public void insertList(String[] wordList) {
        for (String string : wordList) {
            insertWord(string);
        }
    }

    // TODO
    public boolean findWord(String word) {

        // Check for null or empty input
        if (word == null || word.isEmpty()) {
            return false;
        }

        // Start from root
        Node current = root;

        // Iterate through all characters in words
        for (int i = 0; i < word.length(); i++) {

            // Get the current character
            char c = word.charAt(i);

            // Check if the current hashmap has no this character
            if (!current.children.containsKey(c)) {

                // Return false if not find
                return false;
            }

            // Continue to check its children node
            current = current.children.get(c);

        }

        // Get and return the flag when for loop ends, which is the result
        return current.endOfWord;
    }

    // TODO
    public void deleteWord(String word) {

        // Check if this word exist in the trie
        if (findWord(word)) {
            // Proceed the deletion by calling helper method
            deleteWordHelper(word, 0, root);
        }
    }

    // TODO
    public boolean deleteWordHelper(String word, int index, Node curNode) {

        // Base case: if the current character reaches the end of the word
        if (index == word.length()) {
            // Set the flag back to false
            curNode.endOfWord = false;

            // Check if there are no keys stored in the current node's hashmap
            if (curNode.children.isEmpty()) {
                return true;
            } // If it contains keys, then this node is not deletable
            else {
                return false;
            }
        }

        // Get the current character in this word
        char c = word.charAt(index);

        // Get the next node
        Node nextNode = curNode.children.get(c);

        // Recursively call this method itself to find if the next node is deletable
        boolean isDeletable = deleteWordHelper(word, index + 1, nextNode);

        // If the next node is deletable then delete this node
        if (isDeletable) {
            curNode.children.remove(c);
        }

        // Check if the current node is also deletable
        if (curNode.endOfWord == false && curNode.children.isEmpty()) {
            return true;
        } else {
            return false;
        }

    }

    // TODO
    public List<String> allWords() {

        // Create an empty list to store all the words we find
        List<String> wordList = new ArrayList<>();

        // Create an empty StringBuilder
        StringBuilder notebook = new StringBuilder();

        // Start word search by calling the helper method
        allWordsHelper(root, notebook, wordList);

        return wordList;
    }

    // TODO
    public void allWordsHelper(Node node, StringBuilder accumulated, List<String> myList) {

        // Check the current node's flag status
        if (node.endOfWord == true) {
            // Convert the accumulated characters to a string and add it to the list
            myList.add(accumulated.toString());
        }

        // Iterate through all the characters in the current node's hashmap
        for (Character c : node.children.keySet()) {

            // Append the current character to the string builder
            accumulated.append(c);

            // Get the next node associated with the current character
            Node nextNode = node.children.get(c);

            // Recursively call this method itself to traverse the next node
            allWordsHelper(nextNode, accumulated, myList);

            // Remove the last character from the string builder to backtrack
            accumulated.deleteCharAt(accumulated.length() - 1);
        }


    }

    /**
     * Return a list of words that start with the given prefix
     * @param prefix the prefix to search for
     * @param maxResults the maximum number of suggestions to return
     * @return
     */
    public List<String> autocomplete(String prefix, int maxResults) {

        List<String> results = new ArrayList<>();

        // Check for null or empty input
        if (prefix == null || prefix.isEmpty() || maxResults <= 0) {
            return results;
        }

        // Navigate to the node corresponding to the last character of the prefix
        Node current = root;

        for (int i = 0; i < prefix.length(); i++) {

            char c = prefix.charAt(i);

            // If the character does not exist, no words match this prefix
            if (!current.children.containsKey(c)) {
                return results;
            }

            // Move to the next node
            current = current.children.get(c);
        }

        // Collect all words from this node, with the prefix prepended
        StringBuilder accumulated = new StringBuilder(prefix);
        autocompleteHelper(current, accumulated, results, maxResults);

        return results;
    }

    /**
     * Helper method for autocomplete. Collects words starting from the given node.
     * @param node: the current node to search from
     * @param accumulated: the characters accumulated so far
     * @param results: the list to add complete words to
     * @param maxResults: the maximum number of results to collect
     */
    private void autocompleteHelper(Node node, StringBuilder accumulated,
                                    List<String> results, int maxResults) {

        // Check if we have collected enough results
        if (results.size() >= maxResults) {
            return;
        }

        // If this node marks the end of a word, add it to the results
        if (node.endOfWord) {
            results.add(accumulated.toString());
        }

        // Iterate through all children characters
        for (Character c : node.children.keySet()) {

            // Stop early if we have collected enough results
            if (results.size() >= maxResults) {
                return;
            }

            // Append the current character
            accumulated.append(c);

            // Recursively search the child node
            autocompleteHelper(node.children.get(c), accumulated, results, maxResults);

            // Backtrack by removing the last character
            accumulated.deleteCharAt(accumulated.length() - 1);
        }
    }

}
