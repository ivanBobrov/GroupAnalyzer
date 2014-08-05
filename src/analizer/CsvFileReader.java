package analizer;

import analizer.utils.Post;
import analizer.utils.dictionary.Dictionary;
import analizer.utils.io.AbstractFileReader;

import java.util.ArrayList;

class CsvFileReader extends AbstractFileReader<Post> {

    @Override
    public Post next() {
        if (hasNext()) {
            Entry entry = readNextEntry();
            ArrayList<Integer> nGramIds = Dictionary.getInstance().stringToIndexes(entry.message);
            for (Integer a : nGramIds) {
                if (a == 447) {
                    int b = 0;
                    int c = b+a;
                }
            }
            return new Post(entry.message, entry.groupId, nGramIds);
        }

        return null;
    }

    private Entry readNextEntry() {
        scanner.useDelimiter("\\s");
        int groupId = scanner.nextInt();
        int messageId = scanner.nextInt();
        long timeStamp = scanner.nextLong();

        scanner.useDelimiter("\n|\t");
        String message = scanner.next();

        return new Entry(groupId, messageId, timeStamp, message);
    }

    private class Entry {
        public final int groupId;
        public final int messageId;
        public final long timeStamp;
        public final String message;

        public Entry(int groupId, int messageId, long timeStamp, String message) {
            this.groupId = groupId;
            this.messageId = messageId;
            this.timeStamp = timeStamp;
            this.message = message;
        }
    }
}
