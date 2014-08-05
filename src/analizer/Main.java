package analizer;

import analizer.utils.dictionary.Dictionary;
import analizer.utils.io.GroupReader;
import analizer.utils.io.FileWriter;
import analizer.utils.group.Group;

import java.io.FileNotFoundException;

class Main {
    private static final String INPUT_FILE  = "train_content_big.csv";
    private static final String OUTPUT_FILE = "output.csv";

    public static void main (String[] args) {
        GroupReader groupReader = createGroupReader(INPUT_FILE);
        FileWriter fileWriter   = createFileWriter(OUTPUT_FILE);
        Dictionary.getInstance().setNGramLength(4); //TODO: magic
        if (!loadDictionary()) {
            buildDictionary(INPUT_FILE);
        }

        //From 0.0005 to 0.001
        Dictionary.getInstance().deleteHighProbabilities(0.0005); //TODO: magic
        //Is redundant
        //Dictionary.getInstance().deleteLowProbabilities(0.002);

        System.out.println("Process");
        while (groupReader.hasNext()) {
            Group group = groupReader.next();
            if (group == null || group.size() < 1000) { //TODO: magic number
                continue;
            }

            group.updateFrequencies();

            //TODO: magic number
            System.out.print("\r№: " + group.getGroupId());
            fileWriter.println("№: " + group.getGroupId());
            fileWriter.println("Group size: " + group.size());
            fileWriter.printMostFrequentlyNGrams(group, 0.0005);
            fileWriter.printMostDistinctMessages(group, 0.0005);
            //break;//Temporary
        }

        System.out.println("\nFree resources");
        groupReader.close();
        fileWriter.close();
        System.out.println("done");
    }

    private static boolean loadDictionary() {
        try {
            Dictionary.getInstance().load();
        } catch (FileNotFoundException exception) {
            System.out.println("Dictionary file not found. Continue.");
            return false;
        }

        return true;
    }

    private static void buildDictionary(String inputFilename) {
        try {
            Dictionary.getInstance().build(new CsvFileReader(), inputFilename);
        } catch (FileNotFoundException exception) {
            System.out.println("Dictionary building error. No such file. Exit.");
            System.exit(1);
        }
    }

    private static GroupReader createGroupReader(String inputFilename) {
        GroupReader reader = new GroupReader(new CsvFileReader());
        try {
            reader.open(inputFilename);
        } catch (FileNotFoundException exception) {
            System.out.println("No file \"" + inputFilename + "\" for input. Exit.");
            System.exit(1);
        }

        return reader;
    }

    private static FileWriter createFileWriter(String outputFilename) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(outputFilename);
        } catch (FileNotFoundException exception) {
            System.out.println("Output file opening error. Exit.");
            System.exit(1);
        }

        return writer;
    }

}
