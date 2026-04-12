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

import java.io.*;
import java.util.*;

public class ArticleCSVParser {
    private final CharacterReader reader;
    private int iLine = 1;
    private int iRecord = 1;

    public ArticleCSVParser(CharacterReader reader) {
        this.reader = reader;
    }

    // The states for the Finite State Machine (FSM).
    private enum STATES {
        // Add states here

        // Start reading a new field
        STARTING,

        // Reading normal text without quotes
        READING_PLAIN,

        // Reading text inside double quotes
        READING_QUOTED,

        // Check if it is escaped or end of field
        VERIFYING_ESCAPE,

        // Expect a line feed next
        WAITING_FOR_NEWLINE
    }

    /**
     * Reads the entire CSV stream and parses it into a map of Articles.
     *
     * @return A map where the key is the article's URI (String) and the value
     * is the fully populated Article object.
     * @throws IOException when the underlying reader encounters an error.
     * @throws CSVFormatException when the CSV file is formatted incorrectly.
     */
    public Map<String, Article> readAllArticles() throws IOException, CSVFormatException {
        // TODO: Add code here
        // Create a map to store the final result
        Map<String, Article> articles = new HashMap<>();

        // Create a list to hold fields of the current row
        List<String> currentRecord = new ArrayList<>();

        // Create a StringBuilder to build the current field
        StringBuilder currentField = new StringBuilder();

        // Initialize the state
        STATES state = STATES.STARTING;

        // Initialize a variable to store each character code
        int charCode;

        while (true){
            // Read the next character ASCII code from the file
            charCode = reader.read();

            // Check if we have reached the end of file
            if (charCode == -1){
                break;
            }

            // Convert the ASCII code back to character
            char c = (char)charCode;


            switch (state){
                // STARTING state
                case STARTING:
                    // If current char is " we change the state to READING_QUOTED
                    if (c =='"'){
                        state = STATES.READING_QUOTED;
                    }
                    // If current char is , we are encounter an empty field
                    else if (c == ','){
                        // Add the empty string to the record list
                        currentRecord.add(currentField.toString());
                        currentField.setLength(0);
                    }
                    // If current char is \r, we are expecting for another \n to process the new line
                    else if (c == '\r'){
                        state = STATES.WAITING_FOR_NEWLINE;
                    }
                    // If current char is \n, this means the row ends with an empty field
                    else if (c == '\n'){
                        // Add the empty string to the record list
                        currentRecord.add(currentField.toString());
                        currentField.setLength(0);

                        // Process this row
                        processRecord(currentRecord,articles);

                        // Clear the record list
                        currentRecord.clear();
                    }
                    else {
                        // Append this normal character into our builder
                        currentField.append(c);

                        // Change the state to READING_PLAIN
                        state = STATES.READING_PLAIN;
                    }
                    break;

                case READING_PLAIN:
                    // If current char is "," this means that this field is end
                    if (c == ','){
                        // Save this field and reset the builder
                        currentRecord.add(currentField.toString());
                        currentField.setLength(0);

                        // Change the state to STARTING
                        state = STATES.STARTING;
                    }
                    // If current char is \r, we are expecting for another \n to process the new line
                    else if (c == '\r'){
                        state = STATES.WAITING_FOR_NEWLINE;
                    }
                    // If current char is \n, this means the row ends with an empty field
                    else if (c == '\n'){
                        // Add the empty string to the record list
                        currentRecord.add(currentField.toString());
                        currentField.setLength(0);

                        // Process this row
                        processRecord(currentRecord,articles);

                        // Clear the record list
                        currentRecord.clear();

                        // Change the state to STARTING
                        state = STATES.STARTING;
                    }
                    // If current char is " it is invalid
                    else if (c == '"'){
                        throw new CSVFormatException();
                    }
                    else {
                        currentField.append(c);
                    }
                    break;

                case READING_QUOTED:
                    // If the current char is " we need to check if this is the actual end of field
                    if (c == '"'){
                        // Change state to VERIFYING_ESCAPE to verify it
                        state = STATES.VERIFYING_ESCAPE;
                    }
                    // We can append everything else
                    else{
                        currentField.append(c);
                    }
                    break;

                case VERIFYING_ESCAPE:
                    // If the current char is another " this is an escaped quote
                    if (c == '"'){
                        // Append this single quote to builder
                        currentField.append('"');

                        // Change the state back to READING_QUOTED
                        state = STATES.READING_QUOTED;
                    }
                    // if the current char is a comma, this means this field is end
                    else if (c == ','){
                        // Add field to record list and clear the builder
                        currentRecord.add(currentField.toString());
                        currentField.setLength(0);

                        // Change the state to starting
                        state = STATES.STARTING;
                    }
                    // If current char is \r, this means that this field is end
                    else if (c == '\r'){
                        // Change the state to WAITING_FOR_NEWLINE
                        state = STATES.WAITING_FOR_NEWLINE;
                    }
                    // If current char is \n, this means this row is end
                    else if (c == '\n'){
                        currentRecord.add(currentField.toString());
                        currentField.setLength(0);

                        // Process the complete row.
                        processRecord(currentRecord, articles);
                        currentRecord.clear();

                        // Change the state to STARTING
                        state = STATES.STARTING;
                    }
                    // If the current char is a regular text, it is invalid
                    else {
                        throw new CSVFormatException();
                    }
                    break;

                case WAITING_FOR_NEWLINE:
                    if (c == '\n'){
                        // Add the current field to record list and clear the builder
                        currentRecord.add(currentField.toString());
                        currentField.setLength(0);

                        // Call processRecord to process the current list
                        processRecord(currentRecord, articles);
                        currentRecord.clear();

                        // Change the state to STARTING
                        state = STATES.STARTING;
                    }
                    // If \r is not followed by \n, this is invalid
                    else{
                        throw new CSVFormatException();
                    }
                    break;
            }
        }

        // Check if there is a missing quote after we are finish reading the file
        if (state == STATES.READING_QUOTED){
            throw new CSVFormatException();
        }

        // Check if the file ended abruptly after \r without the required newline \n
        if (state == STATES.WAITING_FOR_NEWLINE) {
            throw new CSVFormatException();
        }

        // Check if there is a missing \n at the end of the file
        if (!currentRecord.isEmpty() || currentField.length() > 0){
            // Process the unfinish data
            currentRecord.add(currentField.toString());
            processRecord(currentRecord, articles);
        }

        return articles;
    }

    /**
     * Helper method to convert a parsed record (list of strings) into an Article
     * and add it to the map.
     */
    private void processRecord(List<String> rec, Map<String, Article> articles) throws CSVFormatException {
        // TODO: Add code here

        // Check for empty input
        if (rec == null || rec.isEmpty()){
            return;
        }

        // Check for the header by using equalsIgnoreCase
        if (rec.get(0).equalsIgnoreCase("uri")){
            return;
        }

        // Check for invalid size
        if (rec.size() != Article.EXPECTED_FIELD_COUNT){
            throw new CSVFormatException();
        }

        // Create new article object
        Article article = new Article(rec);

        // Put this article into the map
        articles.put(article.getUri(), article);
    }
}
