package analizer.utils.io;

import analizer.utils.Post;
import analizer.utils.dictionary.Dictionary;
import analizer.utils.group.Group;
import analizer.utils.Pair;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Iterator;


public class FileWriter {
    public static final String SPACER = "--------------------------------------------------";
    private PrintStream printStream = null;

    public FileWriter(String outputFilename) throws FileNotFoundException {
        printStream = new PrintStream(new FileOutputStream(outputFilename));
    }

    public void printMostFrequentlyNGrams(Group group, double threshold) {
        if (group.isEmpty()) {
            return;
        }

        print("Frequently used: ");
        print(group.getThemeMessage(threshold));
        print("\n");
    }

    public void printMostFrequentlyNGrams(Group group, int count) {
        if (group.isEmpty()) {
            return;
        }

        int nGramsCount = group.uniqueNGramsCount();
        if (nGramsCount < count) {
            count = nGramsCount;
        }

        print("Frequently used: ");
        //TODO: replace with group.getThemeMessage();
        for (int i = 1; i < count + 1; i++) {
            Pair<Double, Integer> pair = group.getFrequency(nGramsCount - i);
            print(Dictionary.getInstance().getNGramById(pair.second).getBase() + " ");
        }

        print("\n");
    }

    public void printMostDistinctMessages(Group group, double frequencyThreshold) {
        if (group == null || group.isEmpty()) {
            return;
        }

        String themeMessage = group.getThemeMessage(frequencyThreshold);
        printMostDistinctMessages(group, themeMessage);
    }

    public void printMostDistinctMessages(Group group, int themeMessageLength) {
        if (group == null || group.isEmpty()) {
            return;
        }

        String themeMessage = group.getThemeMessage(themeMessageLength);
        printMostDistinctMessages(group, themeMessage);
    }

    private void printMostDistinctMessages(Group group, String themeMessage) {
        if (group == null || group.isEmpty() || themeMessage == null || themeMessage.isEmpty()) {
            return;
        }

        Iterator<Post> iterator = group.iterator();

        while (iterator.hasNext()) {
            Post post = iterator.next();
            Double difference = Dictionary.getInstance().compare(post.getMessage(), themeMessage);
            //TODO: magic number
            if (difference > 0.95) {
                println(String.format("%.2f\t%s", difference, post.getMessage()));
            }
        }

        println(SPACER);
    }

    public void close() {
        //TODO: implement close()
    }

    public void print(Object obj) {
        printStream.print(obj.toString());
    }

    public void println(Object obj) {
        printStream.println(obj.toString());
    }

    private class Entry {

        public Entry() {

        }
    }
}
