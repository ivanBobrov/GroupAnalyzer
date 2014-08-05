package analizer.utils.io;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.util.Iterator;

public interface Reader<C> extends Iterator<C>, Closeable {

    void open(String filename) throws FileNotFoundException;

    void close();
}
