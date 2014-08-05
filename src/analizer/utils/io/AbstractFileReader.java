package analizer.utils.io;

import analizer.utils.Post;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by user on 01.08.14.
 */
public abstract class AbstractFileReader<C> implements Reader<C> {
    protected String filename;
    protected Scanner scanner = null;

    @Override
    public void open(String filename) throws FileNotFoundException {
        if (scanner == null) {
            this.filename = filename;
            this.scanner = new Scanner(new FileInputStream(this.filename), "UTF-8");
        }
    }

    @Override
    public void close() {
        if (scanner != null) {
            scanner.close();
            scanner = null;
        }
    }

    @Override
    public boolean hasNext() {
        if (scanner == null) {
            throw new IllegalStateException();
        }

        return scanner.hasNext();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
