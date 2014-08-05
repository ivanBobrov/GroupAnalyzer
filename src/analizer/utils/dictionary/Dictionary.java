package analizer.utils.dictionary;

import analizer.utils.NGram;
import analizer.utils.Post;
import analizer.utils.io.AbstractFileReader;
import analizer.utils.io.FileWriter;
import analizer.utils.io.Reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public final class Dictionary {
    public static final String REGEX = "\\s|\\.|,|\"|-|\\s+|\\?|!|:|–|\\)|\\(|\\*|\\[|\\]|\\|/|;|»|«";
    public static final String DICTIONARY_FILENAME = "dictionary.csv";
    private static Dictionary instance;

    private HashMap<Integer, Entry> nGramIdMap = new HashMap<Integer, Entry>();
    private HashMap<String, Integer> indexMap = new HashMap<String, Integer>();

    private int nGramLength = 3; //TODO: Maybe build parameter
    private int readNGramCount = 0;

    public static synchronized Dictionary getInstance() {
        if (instance == null) {
            instance = new Dictionary();
        }

        return instance;
    }

    private Dictionary() {
    }

    public void build(Reader<Post> reader, String filename) throws FileNotFoundException {
        if (reader == null) {
            throw new IllegalArgumentException();
        }

        nGramIdMap.clear();
        indexMap.clear();

        reader.open(filename);
        System.out.println("Building dictionary");

        while (reader.hasNext()) {
            Post next = reader.next();
            addString(next.getMessage());
            System.out.print("\rgroup: " + next.getGroupId());
        }

        reader.close();

        //deleteHighProbabilities(0.0001);

        System.out.println("\nSaving dictionary to file");
        save(DICTIONARY_FILENAME);

        System.out.println("Building done");
    }

    public void save(String filename) {
        try {
            FileWriter writer = new FileWriter(filename);
            for (Entry entry : nGramIdMap.values()) {
                writer.println(entry.toString());
            }
            writer.close();
        } catch (FileNotFoundException exception) {
            System.out.println("Cannot load file for saving dictionary. Continue.");
        }
    }

    public void load(String filename) throws FileNotFoundException {
        nGramIdMap.clear();
        indexMap.clear();

        Reader<Entry> reader = new AbstractFileReader<Entry>() {
            @Override
            public Entry next() {
                scanner.useDelimiter("\\s+");
                Integer id = scanner.nextInt();
                Integer useCount = scanner.nextInt();
                String base = scanner.next();
                return new Entry(new NGram(base), id, useCount);
            }
        };

        reader.open(filename);
        System.out.println("Loading dictionary");

        while (reader.hasNext()) {
            Entry next = reader.next();
            addNewEntry(next);
            readNGramCount += next.useCount;
        }

        reader.close();
        System.out.println("Dictionary loaded");
    }

    public void load() throws FileNotFoundException{
        load(DICTIONARY_FILENAME);
    }

    public void setNGramLength(int newLength) {
        if (newLength > 0) {
            nGramLength = newLength;
        }
    }

    public NGram getNGramById(Integer id) {
        Entry entry = nGramIdMap.get(id);
        if (entry != null) {
            return entry.nGram;
        }

        return null;
    }

    public Integer getId(NGram nGram) {
        //TODO: implement getId(); Maybe rename.
        return null;
    }

    public ArrayList<Integer> stringToIndexes(String string) {
        String normalizedString = normalizeString(string);
        ArrayList<String> bases = divideToNGramBases(normalizedString);
        ArrayList<Integer> indexes = new ArrayList<Integer>();

        for (String base : bases) {
            Integer idx = indexMap.get(base);
            if (idx != null) {
                indexes.add(idx);
            }
        }

        return indexes;
    }

    public Double compare(String str1, String str2) {
        //TODO: Watch and check it out.
        if (str1 == null || str2 == null) {
            return null;
        }

        List<Integer> grams1 = stringToIndexes(str1);
        List<Integer> grams2 = stringToIndexes(str2);

        HashSet<Integer> set = new HashSet<Integer>(grams1);
        set.addAll(grams2);
        Iterator iterator = set.iterator();

        int matches = 0;
        while (iterator.hasNext()) {
            Integer id = (Integer) iterator.next();
            if (grams1.contains(id) && grams2.contains(id))
                matches++;
        }

        int length = Math.min(grams1.size(), grams2.size());
        return length != 0 ? ((double)length - (double)matches) / (double)length : new Double(1);
    }

    private void addString(String string) {
        String normalizedString = normalizeString(string);
        ArrayList<String> bases = divideToNGramBases(normalizedString);

        for (String base : bases) {
            add(new NGram(base));
        }
    }

    private void add(NGram nGram) {
        Integer index = indexMap.get(nGram.getBase());
        if (index == null) {
            Integer uniqueId = nGramIdMap.size(); // Will be always bigger than anyone from ids
            addNewEntry(new Entry(nGram, uniqueId));
        } else {
            nGramIdMap.get(index).incrementUseCount();
        }

        readNGramCount++;
    }

    private void addNewEntry(Entry entry) {
        //Maybe throw IllegalStateException if entry already exists?
        nGramIdMap.put(entry.id, entry);
        indexMap.put(entry.nGram.getBase(), entry.id);
    }

    private String normalizeString(String string) {
        return string.replaceAll(REGEX, " ").toLowerCase();
    }

    private ArrayList<String> divideToNGramBases(String string) {
        String words[] = string.split(" ");
        ArrayList<String> bases = new ArrayList<String>();

        for (String word : words) {
            if (word.length() < nGramLength) {
                //TODO: implement adding white spaces. (Algorithm)
                continue;
            }

            for (int i = 0; i < word.length() - (nGramLength - 1); i++) {
                bases.add(word.substring(i, i + nGramLength));
            }
        }

        return bases;
    }

    public void deleteHighProbabilities(double threshold) {
        //TODO: make private
        Iterator<Entry> iterator = nGramIdMap.values().iterator();

        //TODO: [debug] delete writing to file
        FileWriter writer = null;//extra
        try {
            writer = new FileWriter("smthg.csv");//extra

        } catch (FileNotFoundException e) {

        }

        int count = 0;//extra
        while (iterator.hasNext()) {
            Entry entry = iterator.next();

            if ((double) entry.useCount / (double) readNGramCount > threshold) {
                writer.println((double) entry.useCount / (double) readNGramCount + " " + entry.nGram.getBase());//extra
                indexMap.remove(entry.nGram.getBase());
                iterator.remove();
                count++;//extra
            }
        }
        System.out.println("deleted: " + count);//extra
    }

    public void deleteLowProbabilities(double threshold) {
        //TODO: make private
        Iterator<Entry> iterator = nGramIdMap.values().iterator();

        //TODO: [debug] delete counter
        int count = 0;//extra
        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            if ((double) entry.useCount / (double) readNGramCount < threshold) {
                indexMap.remove(entry.nGram.getBase());
                iterator.remove();
                count++;//extra
            }
        }
        System.out.println("deleted: " + count);//extra
    }

    public int findFreq(double thr1, double thr2 ) {
        //TODO: [debug] delete method
        Iterator<Entry> iterator = nGramIdMap.values().iterator();

        int count = 0;
        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            double freq = (double) entry.useCount / (double) readNGramCount;
            if (freq < thr1 && freq > thr2) {
                count++;
            }
        }

        return count;
    }

    private class Entry {
        public final NGram nGram;
        public final Integer id;
        public int useCount;

        public Entry(NGram nGram, Integer id, int useCount) {
            this.nGram = nGram;
            this.id = id;
            this.useCount = useCount;
        }

        public Entry(NGram nGram, Integer id) {
            this(nGram, id, 1);
        }

        public void incrementUseCount() {
            useCount++;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(id + "\t").append(useCount + "\t").append(nGram);
            return builder.toString();
        }
    }
}
