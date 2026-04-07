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

import business.datastructures.InvertedIndex;
import data.Article;
import data.DataRepository;
import dto.ArticleDTO;

import java.util.*;

/**
 * This is the implementation of SearchEngineService, used for the Business Logic Tier
 */
public class SearchEngineImpl implements SearchEngineService{

    // Initialize dependencies and fields
    private final DataRepository repository;
    private InvertedIndex index;

    private Map<String, Article> articleDatabase;
    private Set<String> stopWords;

    /**
     * Constructor method
     * @param repository
     */
    public SearchEngineImpl(DataRepository repository) {
        this.repository = repository;
        this.articleDatabase = new HashMap<>();
        this.stopWords = new HashSet<>();
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

        // Load stop words and article data
        this.stopWords = repository.loadStopWords(stopWordsFilepath);
        this.index = new InvertedIndex(this.stopWords);
        this.articleDatabase = repository.loadAllArticles(articlesFilepath);

        // Iterate through all articles and populated the inverted index
        for (Article article : this.articleDatabase.values()) {
            index.addDocument(article);
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
}
