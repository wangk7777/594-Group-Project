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

import edu.upenn.cit5940.common.dto.ArticleDTO;
import java.util.List;
import java.util.Map;

/**
 * This is an interface for Business Logic Tier
 */
public interface SearchEngineService {

    /**
     * Initialize the search engine system
     * This is responsible for:
     * invoking the data layer to load articles and stop words followed by data cleaning and inverted index construction
     * @param articlesFilepath
     * @param stopWordsFilepath
     * @throws Exception if file reading fails, the exception is propagated to be handled by the UI layer for user notification
     */
    void initializeSystem(String articlesFilepath, String stopWordsFilepath) throws Exception;

    /**
     * Search for tech news based on user's input keyword
     * @param keyword entered by user
     * @return a List of ArticleDTO containing the searching results
     */
    List<ArticleDTO> searchByKeyword(String keyword);

    /**
     * Get autocomplete suggestions for a given prefix
     * @param prefix the prefix to autocomplete
     * @return a list of suggested words (max 10)
     */
    List<String> autocomplete(String prefix);

    /**
     * Get the top 10 trending topics for a given period
     * @param period the month in YYYY-MM format
     * @return a list of (word, count) entries sorted by frequency descending
     */
    List<Map.Entry<String, Integer>> getTopics(String period);

    /**
     * Get the monthly frequency trend of a topic within a date range
     * @param topic the word to track
     * @param start the start period in YYYY-MM format
     * @param end the end period in YYYY-MM format
     * @return a list of (month, count) entries in chronological order
     */
    List<Map.Entry<String, Integer>> getTrends(String topic, String start, String end);

    /**
     * Get all articles published within a date range
     * Results are sorted chronologically.
     * @param startDate the start date in YYYY-MM-DD format
     * @param endDate the end date in YYYY-MM-DD format
     * @return a list of ArticleDTOs within the date range
     */
    List<ArticleDTO> getArticlesByDateRange(String startDate, String endDate);

    /**
     * Retrieve a specific article by its URI.
     * @param uri the unique article identifier
     * @return the ArticleDTO if found, or null if not found
     */
    ArticleDTO getArticleById(String uri);

    /**
     * Get statistics about the loaded data.
     * @return a string containing the statistics summary
     */
    String getStats();
}
