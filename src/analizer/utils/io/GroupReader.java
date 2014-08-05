package analizer.utils.io;

import analizer.utils.Post;
import analizer.utils.group.Group;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GroupReader implements Reader<Group> {
    private String filename;
    private Reader<Post> fileReader;
    private Post currentPost = null;

    public GroupReader(Reader<Post> fileReader) {
        if (fileReader == null) {
            throw new IllegalArgumentException();
        }

        this.fileReader = fileReader;
    }

    @Override
    public void open(String filename) throws FileNotFoundException{
        this.filename = filename;
        this.fileReader.open(filename);
    }

    @Override
    public void close() {
        fileReader.close();
    }

    @Override
    public Group next() {
        if (fileReader == null) {
            throw new UnsupportedOperationException();
        }

        if (currentPost == null) {
            if (!hasNext()) {
                return null;
            }
            readNextEntry();
        }

        Integer currentGroupId = currentPost.getGroupId();
        Group group = new Group(currentGroupId);
        while (readNextEntry() != null && currentPost.getGroupId() == currentGroupId) {
            group.add(currentPost);
        }

        if (group.isEmpty()) {
            return null;
        }

        return group;
    }

    @Override
    public boolean hasNext() {
        return fileReader.hasNext();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private Post readNextEntry() {
        return currentPost = fileReader.next();
    }
}
