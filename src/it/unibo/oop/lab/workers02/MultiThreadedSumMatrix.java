package it.unibo.oop.lab.workers02;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that can sum the elements of a matrix in multi-thread.
 * 
 */
public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nThreads;

    MultiThreadedSumMatrix(final int threads) {
        nThreads = threads;
    }

    @Override
    public double sum(final double[][] matrix) {
        // Thinks the matrix as a simple array, it can balance quite efficiently even if
        // it's not a square matrix, each thread receive the matrix, a starting point
        // and the number of element it has to work on. It has to calculate the elements
        // positions though
        final int nElem = matrix.length * matrix[0].length;
        final int size = nElem % nThreads + nElem / nThreads;
        double result = 0;
        final List<Worker> threadList = new ArrayList<>();
        for (int i = 0; i < nElem; i += size) {
            threadList.add(new Worker(matrix, i, size));
        }

        for (final Worker thread : threadList) {
            thread.start();
        }

        for (final Worker thread : threadList) {
            try {
                thread.join();
                result += thread.getResult();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 
     * Represents a thread which sums matrix elements.
     */
    public class Worker extends Thread {
        private final int start;
        private final int nelem;
        private final int rowLength;
        private final double[][] matrix;
        private double result;

        /**
         * 
         * @param matrix
         * @param start
         *                   element the sum will start from
         * @param nelem
         *                   number of element to work on
         */
        public Worker(final double[][] matrix, final int start, final int nelem) {
            this.start = start;
            this.nelem = nelem;
            this.matrix = matrix;
            rowLength = matrix[0].length;
        }

        @Override
        public void run() {
            for (int i = start; i < start + nelem && i < rowLength * matrix.length; i++) {
                result += matrix[i / rowLength][i % rowLength];
            }
        }

        /**
         * 
         * @return thread's computation result
         */
        public double getResult() {
            return result;
        }
    }

}
