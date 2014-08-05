package analizer.utils;

import java.util.*;


public class SortedPairs<F extends Comparable, S> implements Iterable<Pair<F, S>> {
    private List<Entry<F, S>> list;

    public SortedPairs() {
        this.list = new ArrayList<Entry<F, S>>();
    }

    public void add(F first, S second) {
        list.add(new Entry<F, S>(first, second));
    }

    public Pair<F, S> getPair(int index) {
        Entry entry = list.get(index);
        return new Pair<F, S>((F) entry.first, (S) entry.second);
    }

    public void remove(int index) {
        list.remove(index);
    }

    public void sort() {
        Collections.sort(list);
    }

    public int size() {
        return list.size();
    }

    @Override
    public Iterator<Pair<F, S>> iterator() {
        return new Iterator<Pair<F, S>>() {

            private Iterator<Entry<F, S>> iterator = list.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Pair<F, S> next() {
                Entry entry = iterator.next();
                return new Pair<F, S>((F) entry.first, (S) entry.second);
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }

    private class Entry<F extends Comparable, S> implements Comparable<Entry> {
        public F first;
        public S second;

        public Entry(F first, S second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public int compareTo(Entry entry) {
            return this.first.compareTo(entry.first);
        }
    }
}
