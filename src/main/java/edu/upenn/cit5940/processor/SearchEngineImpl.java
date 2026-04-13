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

package edu.upenn.cit5940.processor;

import edu.upenn.cit5940.processor.datastructures.InvertedIndex;
import edu.upenn.cit5940.processor.datastructures.CustomTrie;
import edu.upenn.cit5940.processor.datastructures.DateIndex;
import edu.upenn.cit5940.processor.datastructures.WordFrequencyHeap;
import edu.upenn.cit5940.common.dto.Article;
import edu.upenn.cit5940.datamanagement.DataRepository;
import edu.upenn.cit5940.common.dto.ArticleDTO;

import java.util.*;

/**
 * This is the implementation of SearchEngineService, used for the Business Logic Tier
 */
public class SearchEngineImpl implements SearchEngineService{

    // Initialize dependencies and fields
    private final DataRepository repository;
    private InvertedIndex index;
    private CustomTrie trie;
    private DateIndex dateIndex;

    private Map<String, Article> articleDatabase;
    private Set<String> stopWords;
    private int totalArticlesLoaded;

    /**
     * Constructor method
     * @param repository
     */
    public SearchEngineImpl(DataRepository repository) {
        this.repository = repository;
        this.articleDatabase = new HashMap<>();
        this.stopWords = new HashSet<>();
        this.totalArticlesLoaded = 0;
    }

    /**
     * Initialize the search engine system
     * This is responsible for:
     * invoking the data layer to load articles and stop words followed by data cleaning and inverted index construction
     * @param articlesFilepath
     * @param stopWordsFilepath
     * @throws Exception if file reading fails, the exception is propagated to be handled by the UI layer for user notification
     */
    @Override
    public void initializeSystem(String articlesFilepath, String stopWordsFilepath) throws Exception {

        // Load stop words from the data layer
        this.stopWords = repository.loadStopWords(stopWordsFilepath);

        // Initialize data structures
        this.index = new InvertedIndex(this.stopWords);
        this.trie = new CustomTrie();
        this.dateIndex = new DateIndex();

        // Load all articles from the data layer
        this.articleDatabase = repository.loadAllArticles(articlesFilepath);
        this.totalArticlesLoaded = articleDatabase.size();

        // Iterate through all articles and populate all data structures
        for (Article article : articleDatabase.values()) {

            String uri = article.getUri();
            String date = article.getDate();
            String title = article.getTitle();
            String body = article.getBody();

            // Add to inverted index
            index.addDocument(article);

            // Add title words to trie for autocomplete
            if (title != null && !title.isEmpty()) {
                String[] titleWords = cleanText(title);
                for (String word : titleWords) {
                    if (!word.isEmpty() && word.length() > 1
                            && !stopWords.contains(word)) {
                        trie.insertWord(word);
                    }
                }
            }

            // Add to date index
            dateIndex.addArticle(uri, date, title, body);
        }

    }

    /**
     * Search for tech news based on user's input keyword
     * @param keyword entered by user
     * @return a List of ArticleDTO containing the searching results
     */
    @Override
    public List<ArticleDTO> searchByKeyword(String keyword) {

        // Initialize List variable
        List<ArticleDTO> searchResults = new ArrayList<>();

        // Check for the null or empty input
        if (keyword == null || keyword.trim().isEmpty()) {
            return searchResults;
        }

        // Trim the extra whitespaces and convert to lowercases
        String cleanKeyword = keyword.trim().toLowerCase();

        // Check if the input key word is a stop word
        if (stopWords.contains(cleanKeyword)) {
            return searchResults;
        }

        // Search the keywords for corresponding uris by calling search method
        Set<String> matchingUris = index.search(cleanKeyword);

        // Check if there is no matches found
        if (matchingUris == null || matchingUris.isEmpty()) {
            return searchResults;
        }

        // Iterates through all matched uris
        for(String uri : matchingUris) {
            // Get the current article object in the map
            Article article = articleDatabase.get(uri);

            // Check if current article object is valid
            if (article != null){

                // Create Data Transfer Object using corresponding article objects
                ArticleDTO dto = new ArticleDTO(article.getUri(), article.getDate(), article.getTitle(), article.getBody());

                // Add this article dto to results
                searchResults.add(dto);
            }

        }

        return searchResults;

    }

    /**
     * Get autocomplete suggestions for a prefix.
     * @param prefix the prefix to autocomplete
     * @return a list of up to 10 matching words
     */
    @Override
    public List<String> autocomplete(String prefix) {

        if (prefix == null || prefix.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // Clean the prefix: lowercase
        String cleanPrefix = prefix.trim().toLowerCase();

        // Call trie's autocomplete with max 10 results
        return trie.autocomplete(cleanPrefix, 10);
    }

    /**
     * Get the top 10 trending topics for a given month.
     * Steps:
     *   1. Filter articles by the period (YYYY-MM)
     *   2. Count word frequencies (excluding stop words and single chars)
     *   3. Use WordFrequencyHeap to extract top 10
     * @param period the month in YYYY-MM format
     * @return a list of (word, count) entries, sorted by frequency descending
     */
    @Override
    public List<Map.Entry<String, Integer>> getTopics(String period) {

        // Initialize the list to store the final results
        List<Map.Entry<String, Integer>> results = new ArrayList<>();

        // Validate the input
        if (period == null || period.trim().isEmpty()) {
            return results;
        }

        String cleanPeriod = period.trim();

        // Count word frequencies for articles in this period
        Map<String, Integer> wordCounts = new HashMap<>();

        // Iterate through all articles
        for (Article article : articleDatabase.values()) {

            String date = article.getDate();

            // Check if this article belongs to the specified period
            if (date != null && date.length() >= 7
                    && date.substring(0, 7).equals(cleanPeriod)) {

                // Combine title and body
                String text = (article.getTitle() != null ? article.getTitle() : "")
                        + " "
                        + (article.getBody() != null ? article.getBody() : "");

                // Clean and split the text
                String[] words = cleanText(text);

                // Count the frequency of each valid word
                for (String word : words) {

                    // Ignore empty strings, single characters, and common stop words
                    if (!word.isEmpty() && word.length() > 1
                            && !stopWords.contains(word)) {
                        wordCounts.put(word,
                                wordCounts.getOrDefault(word, 0) + 1);
                    }
                }
            }
        }

        // Build a max heap from word counts
        // Initialize a max heap to find the words with the highest counts
        WordFrequencyHeap heap = new WordFrequencyHeap(wordCounts.size());
        for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
            heap.add(entry.getKey(), entry.getValue());
        }

        // Extract top 10
        int count = 0;
        while (!heap.isEmpty() && count < 10) {
            Map.Entry<String, Integer> top = heap.extractMax();
            if (top != null) {
                results.add(top);
                count++;
            }
        }

        return results;
    }

    /**
     * Get the monthly frequency trend of a topic within a date range.
     * @param topic the word to track
     * @param start the start period in YYYY-MM format
     * @param end the end period in YYYY-MM format
     * @return a list of (month, count) entries in chronological order
     */
    @Override
    public List<Map.Entry<String, Integer>> getTrends(
            String topic, String start, String end) {

        List<Map.Entry<String, Integer>> results = new ArrayList<>();

        if (topic == null || start == null || end == null) {
            return results;
        }

        String cleanTopic = topic.trim().toLowerCase();

        // Use a TreeMap to keep months in chronological order
        TreeMap<String, Integer> monthlyCounts = new TreeMap<>();

        // Initialize all months in the range to 0
        String currentMonth = start;
        while (currentMonth.compareTo(end) <= 0) {
            monthlyCounts.put(currentMonth, 0);
            currentMonth = getNextMonth(currentMonth);
        }

        // Count occurrences of the topic word in each article
        for (Article article : articleDatabase.values()) {

            String date = article.getDate();

            // Extract YYYY-MM from the article date
            if (date != null && date.length() >= 7) {

                String articleMonth = date.substring(0, 7);

                // Check if this article falls within the range
                if (articleMonth.compareTo(start) >= 0
                        && articleMonth.compareTo(end) <= 0) {

                    // Combine title and body
                    String text = (article.getTitle() != null ? article.getTitle() : "")
                            + " "
                            + (article.getBody() != null ? article.getBody() : "");

                    // Count occurrences of the topic word
                    String[] words = cleanText(text);
                    int wordCount = 0;
                    for (String word : words) {
                        if (word.equals(cleanTopic)) {
                            wordCount++;
                        }
                    }

                    // Add to the monthly total
                    if (wordCount > 0) {
                        monthlyCounts.put(articleMonth,
                                monthlyCounts.getOrDefault(articleMonth, 0) + wordCount);
                    }
                }
            }
        }

        // Convert TreeMap to result list (already in chronological order)
        for (Map.Entry<String, Integer> entry : monthlyCounts.entrySet()) {
            results.add(new AbstractMap.SimpleEntry<>(
                    entry.getKey(), entry.getValue()));
        }

        return results;
    }

    /**
     * Get articles within a date range and sorted chronologically.
     * @param startDate start date in YYYY-MM-DD format
     * @param endDate end date in YYYY-MM-DD format
     * @return a list of ArticleDTOs sorted by date
     */
    @Override
    public List<ArticleDTO> getArticlesByDateRange(String startDate, String endDate) {

        if (startDate == null || endDate == null) {
            return new ArrayList<>();
        }

        return dateIndex.getArticlesByDateRange(startDate, endDate);
    }

    /**
     * Retrieve a specific article by its URI.
     * @param uri the unique article identifier
     * @return the ArticleDTO if found, or null if not found
     */
    @Override
    public ArticleDTO getArticleById(String uri) {

        // Validate that the uri is not null and contains non-whitespace characters
        if (uri == null || uri.trim().isEmpty()) {
            return null;
        }

        Article article = articleDatabase.get(uri.trim());

        if (article == null) {
            return null;
        }

        // Map the internal Article entity to a public ArticleDTO for the response
        return new ArticleDTO(
                article.getUri(), article.getDate(),
                article.getTitle(), article.getBody()
        );
    }

    /**
     * Get statistics about the loaded data.
     * @return a string summary of the statistics
     */
    @Override
    public String getStats() {
        return "Total articles loaded: " + totalArticlesLoaded;
    }

    /**
     * Get the total number of articles loaded (for startup message).
     * @return the article count
     */
    public int getTotalArticlesLoaded() {
        return totalArticlesLoaded;
    }

    /**
     * Helper method to clean text: lowercase, remove non-alphanumeric,
     * split by whitespace, and trim hyphens.
     * @param text the raw text to clean
     * @return an array of cleaned words
     */
    private String[] cleanText(String text) {

        if (text == null || text.isEmpty()) {
            return new String[0];
        }

        // Convert to lowercase
        text = text.toLowerCase();

        // Remove non-alphabet, non-hyphens, non-numbers
        text = text.replaceAll("[^a-z0-9\\s-]", " ");

        // Split by whitespace
        String[] rawWords = text.split("\\s+");

        // Trim hyphens and collect clean words
        List<String> cleanWords = new ArrayList<>();
        for (String word : rawWords) {
            word = word.replaceAll("^-+|-+$", "");
            if (!word.isEmpty()) {
                cleanWords.add(word);
            }
        }

        return cleanWords.toArray(new String[0]);
    }

    /**
     * Helper method to compute the next month in YYYY-MM format.
     * @param yearMonth current month in YYYY-MM format
     * @return the next month in YYYY-MM format
     */
    private String getNextMonth(String yearMonth) {

        // Parse the year and month from the input string
        int year = Integer.parseInt(yearMonth.substring(0, 4));
        int month = Integer.parseInt(yearMonth.substring(5, 7));

        // Increment the month and handle the year rollover if it exceeds December
        month++;
        if (month > 12) {
            month = 1;
            year++;
        }

        return String.format("%04d-%02d", year, month);
    }
}
