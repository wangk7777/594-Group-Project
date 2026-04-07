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

package business;

import dto.ArticleDTO;
import java.util.List;

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
}
