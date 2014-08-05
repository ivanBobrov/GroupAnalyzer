package analizer.utils;

/**
 * Created by user on 29.07.14.
 */
public class NGram {
    private final String base;

    public NGram(String base) {
        this.base = base;
    }

    public String getBase() {
        return base;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NGram && ((NGram) obj).base.equals(this.base);
    }

    @Override
    public int hashCode() {
        return base.hashCode();
    }

    @Override
    public String toString() {
        return base;
    }
}
