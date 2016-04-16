package com.google.engedu.puzzle8;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

class MinPQ<PuzzleBoard> implements Iterable<PuzzleBoard> {
    private PuzzleBoard[] pq;
    private int N;
    private Comparator<PuzzleBoard> comparator;

    public MinPQ(int initCapacity) {
        pq = (PuzzleBoard[]) new Object[initCapacity + 1];
        N = 0;
    }

    public MinPQ(int initCapacity, Comparator<PuzzleBoard> comparator) {
        this.comparator = comparator;
        pq = (PuzzleBoard[]) new Object[initCapacity + 1];
        N = 0;
    }

    public boolean isEmpty() {
        return N == 0;
    }

    public int size() {
        return N;
    }

    public PuzzleBoard min() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
        return pq[1];
    }

    private void resize(int capacity) {
        assert capacity > N;
        PuzzleBoard[] temp = (PuzzleBoard[]) new Object[capacity];
        for (int i = 1; i <= N; i++) {
            temp[i] = pq[i];
        }
        pq = temp;
    }

    public void insert(PuzzleBoard x) {
        // double size of array if necessary
        if (N == pq.length - 1) resize(2 * pq.length);

        // add x, and percolate it up to maintain heap invariant
        pq[++N] = x;
        swim(N);
        assert isMinHeap();
    }

    public PuzzleBoard delMin() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
        exch(1, N);
        PuzzleBoard min = pq[N--];
        sink(1);
        pq[N + 1] = null;         // avoid loitering and help with garbage collection
        if ((N > 0) && (N == (pq.length - 1) / 4)) resize(pq.length / 2);
        assert isMinHeap();
        return min;
    }

    private void swim(int k) {
        while (k > 1 && greater(k / 2, k)) {
            exch(k, k / 2);
            k = k / 2;
        }
    }

    private void sink(int k) {
        while (2 * k <= N) {
            int j = 2 * k;
            if (j < N && greater(j, j + 1)) j++;
            if (!greater(k, j)) break;
            exch(k, j);
            k = j;
        }
    }

    private boolean greater(int i, int j) {
        if (comparator == null) {
            return ((Comparable<PuzzleBoard>) pq[i]).compareTo(pq[j]) > 0;
        } else {
            return comparator.compare(pq[i], pq[j]) > 0;
        }
    }

    private void exch(int i, int j) {
        PuzzleBoard swap = pq[i];
        pq[i] = pq[j];
        pq[j] = swap;
    }

    private boolean isMinHeap() {
        return isMinHeap(1);
    }

    private boolean isMinHeap(int k) {
        if (k > N) return true;
        int left = 2 * k, right = 2 * k + 1;
        if (left <= N && greater(k, left)) return false;
        if (right <= N && greater(k, right)) return false;
        return isMinHeap(left) && isMinHeap(right);
    }

    public Iterator<PuzzleBoard> iterator() {
        return new HeapIterator();
    }

    private class HeapIterator implements Iterator<PuzzleBoard> {
        private MinPQ<PuzzleBoard> copy;

        public HeapIterator() {
            if (comparator == null) copy = new MinPQ<PuzzleBoard>(size());
            else copy = new MinPQ<PuzzleBoard>(size(), comparator);
            for (int i = 1; i <= N; i++)
                copy.insert(pq[i]);
        }

        public boolean hasNext() {
            return !copy.isEmpty();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public PuzzleBoard next() {
            if (!hasNext()) throw new NoSuchElementException();
            return copy.delMin();
        }
    }


}