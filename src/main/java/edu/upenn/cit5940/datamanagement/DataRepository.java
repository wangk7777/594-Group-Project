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

import data.exceptions.CSVFormatException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * This is the interface for data access tier
 */
public interface DataRepository {

    /**
     * Read and load JSON file and convert data into Article object
     * @param filepath
     * @return a Map, with key of article's URI and value of Article object
     * @throws IOException
     */
    Map<String,Article> loadAllArticles(String filepath) throws IOException, CSVFormatException;

    /**
     * Read and load stop words
     * @param stopWordsFilepath
     * @return a Set containing all stop words
     * @throws IOException
     */
    Set<String> loadStopWords(String stopWordsFilepath) throws IOException;
}
