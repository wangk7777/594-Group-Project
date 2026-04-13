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

package edu.upenn.cit5940.datamanagement;

import edu.upenn.cit5940.common.dto.Article;
import edu.upenn.cit5940.datamanagement.exceptions.CSVFormatException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class implements DataRepository
 */
public class CSVRepositoryImpl implements DataRepository{

    /**
     * Read and load file and convert CSV data into Article object
     * @param filepath
     * @return a Map, with key of article's URI and value of Article object
     * @throws IOException
     * @throws CSVFormatException
     */
    @Override
    public Map<String,Article> loadAllArticles(String filepath) throws IOException, CSVFormatException {

        try (CharacterReader reader = new CharacterReader(filepath)) {
            // Call readAllArticles method
            ArticleCSVParser parser = new ArticleCSVParser(reader);
            return parser.readAllArticles();
        }
    }

    /**
     * Read and load stop words
     * @param stopWordsFilepath
     * @return a Set containing all stop words
     * @throws IOException
     */
    @Override
    public Set<String> loadStopWords(String stopWordsFilepath) throws IOException {

        // Initialize the Set
        Set<String> stopWords = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(stopWordsFilepath))) {
            String line;

            // Continue to read the next line until reach null
            while ((line = br.readLine()) != null) {

                // Trim the extra whitespace and convert to lowercase
                String word = line.trim().toLowerCase();

                if (!word.isEmpty()) {
                    stopWords.add(word);
                }
            }
        }
        return stopWords;
    }
}
