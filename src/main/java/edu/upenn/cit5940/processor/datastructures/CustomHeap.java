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

public class CustomHeap {

    private int[] numArray;

    private int size = 0;

    // TODO constructor
    public CustomHeap(int capacity) {
        this.numArray = new int[capacity];
    }

    // TODO
    public boolean addNum(int number) {

        // Check if the heap is full
        if (size == numArray.length) {
            return false;
        }

        // Add the input number to the end of array
        numArray[size] = number;

        // Call bubbleUp method to rearrange heap
        bubbleUp(size);

        // Increment the size
        size++;

        return true;
    }

    // this implementation is given to students in the starter code
    public boolean addList(int[] myList) {
        for (int i = 0; i < myList.length; i++) {
            if (!addNum(myList[i]))
                return false;
        }
        return true;
    }

    // TODO
    public int getParentIndex(int index) {

        int parentIndex = (index-1) / 2;
        return parentIndex;
    }

    // TODO
    public void swap(int i1, int i2) {

        // Initialize a temp variable store value of i1
        int temp = numArray[i1];

        // Assign the value of i2 to i1
        numArray[i1] = numArray[i2];

        // Assign the value of i2 to temp(which is i1)
        numArray[i2] = temp;
    }

    // TODO
    public void bubbleUp(int index) {

        if (index == 0){
            return;
        }

        // Get the index of its parent node
        int parentIndex = getParentIndex(index);

        // Compare the value to its parent
        if (numArray[parentIndex] > numArray[index]){

            // Swap their value if parent is larger
            swap(parentIndex, index);

            // Recursively call bubbleUp method
            bubbleUp(parentIndex);
        }

    }

    // this implementation is given to students in the starter code
    public int[] getArray() {
        return this.numArray;
    }

}