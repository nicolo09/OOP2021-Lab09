package it.unibo.oop.lab.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nThreads;

    MultiThreadedSumMatrix(final int threads) {
        nThreads = threads;
    }

    @Override
    public double sum(final double[][] matrix) {
        final int nElem = matrix.length * matrix[0].length;
        final int size = nElem % nThreads + nElem / nThreads;
        double result = 0;
        final List<Worker> threadList = new ArrayList<>();
        for (int i = 0; i < nElem; i += size) {
            threadList.add(new Worker(matrix, i, size));
        }

        for (final Worker thread : threadList) {
            thread.run();
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

    public class Worker extends Thread {
        final private int start;
        final private int nelem;
        final private int rowLength;
        final private double[][] matrix;
        private double result;

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

        public double getResult() {
            return result;
        }
    }

}
