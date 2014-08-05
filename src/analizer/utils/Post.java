package analizer.utils;

import java.util.ArrayList;
import java.util.Collection;


public class Post {
    private final String message;
    private final int groupId;
    private final ArrayList<Integer> nGramIds;

    public Post(String message, int groupId, Collection<Integer> collection) {
        this.message = message;
        this.groupId = groupId;
        this.nGramIds = new ArrayList<Integer>(collection);
    }

    public String getMessage() {
        return message;
    }

    public int getGroupId() {
        return groupId;
    }

    public ArrayList<Integer> getNGramIds() {
        return nGramIds;
    }
}
