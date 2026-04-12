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

import dto.ArticleDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * A date based index that stores articles organized by their publication date.
 */
public class DateIndex {

    // Initialize a treemap
    private TreeMap<String, List<ArticleDTO>> dateMap;

    /**
     * Constructor
     */
    public DateIndex() {
        this.dateMap = new TreeMap<>();
    }

    /**
     * Add an article to the date index.
     * @param uri the article's URI
     * @param date the article's publication date
     * @param title the article's title
     * @param body the article's body
     */
    public void addArticle(String uri, String date, String title, String body) {

        // Check for invalid articles
        if (date == null || date.trim().isEmpty()) {
            return;
        }

        // Extract YYYY-MM-DD portion
        String dateKey = date.trim();
        if (dateKey.length() >= 10) {
            dateKey = dateKey.substring(0, 10);
        }

        // If this date does not yet exist in the map, create a new list
        if (!dateMap.containsKey(dateKey)) {
            dateMap.put(dateKey, new ArrayList<>());
        }

        // Create an ArticleDTO and add it to the list for this date
        ArticleDTO dto = new ArticleDTO(uri, date, title, body);
        dateMap.get(dateKey).add(dto);
    }

    /**
     * Retrieve all articles within a date range
     * @param startDate the start date in YYYY-MM-DD format
     * @param endDate the end date in YYYY-MM-DD format
     * @return a list of ArticleDTOs within the date range, sorted chronologically
     */
    public List<ArticleDTO> getArticlesByDateRange(String startDate, String endDate) {

        List<ArticleDTO> results = new ArrayList<>();

        // Validate input
        if (startDate == null || endDate == null) {
            return results;
        }

        // Use subMap to get all entries within the range
        NavigableMap<String, List<ArticleDTO>> subMap =
                dateMap.subMap(startDate, true, endDate, true);

        // Collect all articles from each date in the range
        for (List<ArticleDTO> articlesOnDate : subMap.values()) {
            results.addAll(articlesOnDate);
        }

        return results;
    }

    /**
     * Get the underlying TreeMap for testing or direct access purposes
     * @return the date-indexed TreeMap
     */
    public TreeMap<String, List<ArticleDTO>> getDateMap() {
        return dateMap;
    }
}