package pq;

import java.util.*;

public class ArrayHeapMinPQ<T> {
    private ArrayList<T> items;
    private HashMap<T, Double> priorityMap;
    private HashMap<Integer, T> indexMap;
    private HashMap<T, Integer> itemMap;

    public ArrayHeapMinPQ() {
        items = new ArrayList<>();
        items.add(null);

        priorityMap = new HashMap<>();
        indexMap = new HashMap<>();
        itemMap = new HashMap<>();
    }

    /**
     * A helper method for swapping the items at two indices of the array heap.
     */
    private void swap(int a, int b) {
        Collections.swap(items, a, b);  // swap for arraylist
        // swap for itemMap
        itemMap.put(indexMap.get(a), b);
        itemMap.put(indexMap.get(b), a);

        // swap for indexMap
        T tempT = indexMap.get(b);
        indexMap.put(b, indexMap.get(a));
        indexMap.put(a, tempT);


    }

    /**
     * Adds an item with the given priority value.
     * Assumes that item is never null.
     * Runs in O(log N) time (except when resizing).
     * @throws IllegalArgumentException if item is already present in the PQ
     */
    @Override
    public void add(T item, double priority) {
        if (contains(item)) {
            throw new IllegalArgumentException();
        }
        items.add(item);
        priorityMap.put(item, priority);
        indexMap.put(size(), item);
        itemMap.put(item, size());
        int childIndex = size();
        int parentIndex = childIndex / 2;
        if (size() > 1) {
            while (parentIndex != 0 && indexToPriority(parentIndex) > indexToPriority(childIndex)) {
                swap(childIndex, parentIndex);
                childIndex = parentIndex;
                parentIndex = childIndex / 2;
            }
        }

    }

    /**
     * Returns true if the PQ contains the given item; false otherwise.
     * Runs in O(log N) time.
     */
    @Override
    public boolean contains(T item) {
        return priorityMap.containsKey(item);
    }

    private boolean contains(int index) {
        return indexMap.containsKey(index);
    }

    /**
     * Returns the item with the smallest priority.
     * Runs in O(log N) time.
     *
     * @throws NoSuchElementException if the PQ is empty
     */
    @Override
    public T getSmallest() {
        if (items.size() == 0) {
            throw new NoSuchElementException();
        }
        return indexMap.get(1);
    }

    /**
     * Removes and returns the item with the smallest priority.
     * Runs in O(log N) time (except when resizing).
     * @throws NoSuchElementException if the PQ is empty
     */
    @Override
    public T removeSmallest() {
        if (size() == 0) {
            throw new NoSuchElementException();
        }
        T smallestValue = this.getSmallest();   // store smallest value, root, to return
        swap(1, size());    // swap root with last item
        removeLast();

        // sinking time
        if (size() > 1) {
            int parent = 1;
            int leftChild = 2;
            int rightChild = 3;
            int child = 2;
            if (contains(rightChild) && indexToPriority(leftChild) > indexToPriority(rightChild)) {
                child = rightChild;
            }
            while (child != 0 && indexToPriority(parent) > indexToPriority(child)) {
                swap(child, parent);
                parent = child;
                leftChild = parent * 2;
                rightChild = parent * 2 + 1;
                if (contains(leftChild)) {
                    if (contains(rightChild)) {
                        if (indexToPriority(leftChild) > indexToPriority(rightChild)) {
                            child = rightChild;
                        } else {
                            child = leftChild;
                        }
                    } else {
                        child = leftChild;
                    }
                } else {
                    child = 0;
                }
            }
        }
        return smallestValue;
    }

    /**
     * Changes the priority of the given item.
     * Runs in O(log N) time.
     * @throws NoSuchElementException if the item is not present in the PQ
     */
    @Override
    public void changePriority(T item, double priority) {
        if (!contains(item)) {
            throw new NoSuchElementException();
        }
        int index = itemMap.get(item);
        if (index == size()) {
            removeLast();
            add(item, priority);
        }
//        else if (contains(index * 2) && indexToPriority(index * 2) > priority) {
//
//        }
        else {
            swap(index, size());    // swap with last item
            removeLast();

            // sinking time
            if (contains(index * 2)) { // if it has left child
                int parent = index;
                // determine smaller child
                int leftChild = index * 2;
                int rightChild = index * 2 + 1;
                int child = leftChild;
                if (contains(rightChild) && indexToPriority(leftChild) > indexToPriority(rightChild)) {
                    child = rightChild;
                }
                // sink
                while (child != 0 && indexToPriority(parent) > indexToPriority(child)) {
                    swap(child, parent);
                    parent = child;
                    leftChild = parent * 2;
                    rightChild = parent * 2 + 1;
                    if (contains(leftChild)) {
                        child = leftChild;
                        if (contains(rightChild) && indexToPriority(leftChild) > indexToPriority(rightChild)) {
                            child = rightChild;
                        }
                    } else {
                        child = 0;
                    }
                }
            }
            add(item, priority); // add new item, automatically swims
        }
    }

    /**
     * Returns the number of items in the PQ.
     * Runs in O(log N) time.
     */
    @Override
    public int size() {
        return items.size() - 1;
    }

    private double indexToPriority(int index) {
        return priorityMap.get(indexMap.get(index));
    }

    private void removeLast() {
        priorityMap.remove(indexMap.get(size()));
        itemMap.remove(indexMap.get(size()));
        indexMap.remove(size());
        items.remove(size());

    }

}
