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
import java.util.List;
import java.util.TreeMap;

public class Scoreboard {

    private TreeMap<Integer, List<String>> scoreboard;

    public TreeMap<Integer, List<String>> getScoreboard() {
        return scoreboard;
    }

    // TODO
    public Scoreboard() {
        // Sort the score from high to low
        scoreboard = new TreeMap<>(java.util.Collections.reverseOrder());
    }

    // TODO
    public void update(String name, Integer score) {

        if (score < 0){
            return;
        }

        // Remove any existing record of this person to ensure no duplicates
        remove(name);

        // Check if the new score already exists in the scoreboard
        if (scoreboard.containsKey(score) == false) {
            // If it does not exist, create a new empty list for this score
            scoreboard.put(score, new ArrayList<>());
        }

        // Get the list for this score and add the person's name to it
        scoreboard.get(score).add(name);
    }

    // TODO
    public void remove(String name) {

        // Initialize a variable to write the score we need to remove, avoiding ConcurrentModificationException
        Integer scoreToRemove = null;

        // Iterate through all the existing scores (keys) on the scoreboard
        for (Integer currentScore : scoreboard.keySet()) {

            // Get the list of names associated with the current score
            List<String> namesList = scoreboard.get(currentScore);

            // Check if this list contains the target name
            if (namesList.contains(name)) {

                // Remove the name from the list
                namesList.remove(name);

                // Mark this value to scoreToRemove if it becomes empty
                if (namesList.isEmpty()) {
                    scoreToRemove = currentScore;
                }
                break;
            }
        }

        // Check if we marked a score
        if (scoreToRemove != null) {
            // Remove the empty score key from the scoreboard
            scoreboard.remove(scoreToRemove);
        }


    }

}