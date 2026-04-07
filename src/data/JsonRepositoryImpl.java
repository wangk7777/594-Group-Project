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

package data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class JsonRepositoryImpl implements DataRepository {

    // Initialize JSON processing engine
    private final Gson gson = new Gson();

    /**
     * Read and load file and convert data into Article object
     * @param filepath
     * @return a Map, with key of article's URI and value of Article object
     * @throws IOException
     */
    @Override
    public Map<String,Article> loadAllArticles(String filepath) throws IOException{
        Map<String,Article> articles = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {

            // Use TypeToken to capture the generic type of Gson
            Type articleListType = new TypeToken<ArrayList<Article>>(){}.getType();

            // Deserialize JSON array into a list of article objects
            List<Article> articleList = gson.fromJson(br, articleListType);

            // Check if article list is not null
            if (articleList != null) {

                // Iterate through all articles
                for (Article article : articleList) {

                    // Check if the uri is not null
                    if (article.getUri() != null) {

                        // Put current article into map
                        articles.put(article.getUri(), article);
                    }
                }
            }

        }
        return articles;
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
