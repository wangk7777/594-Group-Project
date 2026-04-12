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

import java.util.AbstractMap;
import java.util.Map;

/**
 * A max heap that stores word, frequency pairs.
 * Elements are ordered by frequency in descending order.
 */
public class WordFrequencyHeap {

    // Array to store the heap entries
    private Map.Entry<String, Integer>[] heapArray;

    // Current number of elements in the heap
    private int size;

    /**
     * Constructor
     * @param capacity the maximum number of elements this heap can hold
     */
    public WordFrequencyHeap(int capacity) {
        this.heapArray = new Map.Entry[capacity];
        this.size = 0;
    }

    /**
     * Add a word-frequency pair to the heap
     * @param word the word
     * @param frequency the frequency count of the word
     * @return true if successfully added, false if the heap is full
     */
    public boolean add(String word, int frequency) {

        // Check if the heap is full
        if (size == heapArray.length) {
            return false;
        }

        // Create a new entry and add it to the end of the array
        heapArray[size] = new AbstractMap.SimpleEntry<>(word, frequency);

        // Call bubbleUp to restore heap order
        bubbleUp(size);

        // Increment the size
        size++;

        return true;
    }

    /**
     * Remove and return the entry with the highest frequency
     * @return the entry with the highest frequency, or null if the heap is empty
     */
    public Map.Entry<String, Integer> extractMax() {

        // Check if the heap is empty
        if (size == 0) {
            return null;
        }

        // Save the root element
        Map.Entry<String, Integer> max = heapArray[0];

        // Move the last element to the root
        size--;
        heapArray[0] = heapArray[size];
        heapArray[size] = null;

        // Call bubbleDown to restore heap order
        if (size > 0) {
            bubbleDown(0);
        }

        return max;
    }

    /**
     * Get the current number of elements in the heap
     * @return the size of the heap
     */
    public int size() {
        return size;
    }

    /**
     * Check if the heap is empty
     * @return true if the heap contains no elements
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Get the parent index of a given index
     * @param index the child index
     * @return the parent index
     */
    private int getParentIndex(int index) {
        return (index - 1) / 2;
    }

    /**
     * Get the left child index of a given index
     * @param index the parent index
     * @return the left child index
     */
    private int getLeftChildIndex(int index) {
        return 2 * index + 1;
    }

    /**
     * Get the right child index of a given index
     * @param index the parent index
     * @return the right child index
     */
    private int getRightChildIndex(int index) {
        return 2 * index + 2;
    }

    /**
     * Swap two elements in the heap array
     * @param i1 index of the first element
     * @param i2 index of the second element
     */
    private void swap(int i1, int i2) {
        Map.Entry<String, Integer> temp = heapArray[i1];
        heapArray[i1] = heapArray[i2];
        heapArray[i2] = temp;
    }

    /**
     * Bubble up the element at the given index to restore max heap order.
     * A parent should have a frequency greater than or equal to its children.
     * @param index the index of the element to bubble up
     */
    private void bubbleUp(int index) {

        // Base case: already at the root
        if (index == 0) {
            return;
        }

        int parentIndex = getParentIndex(index);

        // If the current element has a higher frequency than its parent, swap them
        if (heapArray[index].getValue() > heapArray[parentIndex].getValue()) {
            swap(index, parentIndex);
            bubbleUp(parentIndex);
        }
    }

    /**
     * Bubble down the element at the given index to restore max heap order.
     * @param index the index of the element to bubble down
     */
    private void bubbleDown(int index) {

        int largest = index;
        int leftChild = getLeftChildIndex(index);
        int rightChild = getRightChildIndex(index);

        // Check if the left child has a higher frequency
        if (leftChild < size
                && heapArray[leftChild].getValue() > heapArray[largest].getValue()) {
            largest = leftChild;
        }

        // Check if the right child has a higher frequency
        if (rightChild < size
                && heapArray[rightChild].getValue() > heapArray[largest].getValue()) {
            largest = rightChild;
        }

        // If the largest is not the current node, swap and continue
        if (largest != index) {
            swap(index, largest);
            bubbleDown(largest);
        }
    }
}
