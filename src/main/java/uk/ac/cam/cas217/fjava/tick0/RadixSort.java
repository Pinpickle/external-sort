package uk.ac.cam.cas217.fjava.tick0;

import java.util.Arrays;

public class RadixSort {
    public static int[] radixSort(int[] input){

        int positiveCount = positiveCountOfArray(input);
        int negativeCount = input.length - positiveCount;

        int[] positives = new int[positiveCount];
        int[] negatives = new int[negativeCount];
        int positiveIndex = 0;
        int negativeIndex = 0;

        for (int index = 0; index < input.length; index ++) {
            int value = input[index];

            if (value >= 0) {
                positives[positiveIndex] = value;
                positiveIndex ++;
            } else {
                negatives[negativeIndex] = value;
                negativeIndex ++;
            }
        }


        positives = partialRadixSort(positives);
        negatives = partialRadixSort(negatives);

        int[] result = new int[input.length];


        for (int index = negativeCount - 1; index >= 0; index --) {
            result[negativeCount - index - 1] = negatives[index];
        }

        for (int index = 0; index < positiveCount; index ++) {
            result[index + negativeCount] = positives[index];
        }

        return result;
    }

    private static int[] partialRadixSort(int[] input) {
        // Largest place for a 32-bit int is the 1 billion's place
        for(int place=1; place <= 1000000000; place *= 10){
            // Use counting sort at each digit's place
            input = countingSort(input, place);
        }

        return input;
    }

    private static int positiveCountOfArray(int[] array) {
        int positiveCount = 0;
        for(int index = 0; index < array.length; index ++) {
            if (array[index] >= 0) {
                positiveCount ++;
            }
        }

        return positiveCount;
    }

    private static int[] countingSort(int[] input, int place){
        int[] out = new int[input.length];

        int[] count = new int[10];

        for(int i=0; i < input.length; i++){
            int digit = getDigit(input[i], place);
            count[digit] += 1;
        }

        for(int i=1; i < count.length; i++){
            count[i] += count[i-1];
        }

        for(int i = input.length-1; i >= 0; i--){
            int digit = getDigit(input[i], place);

            out[count[digit]-1] = input[i];
            count[digit]--;
        }

        return out;

    }

    private static int getDigit(int value, int digitPlace){
        return Math.abs((value/digitPlace ) % 10);
    }

}
