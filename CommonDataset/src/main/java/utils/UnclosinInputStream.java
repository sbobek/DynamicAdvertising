package utils;

import java.io.FilterInputStream;
import java.io.InputStream;

public class UnclosinInputStream extends FilterInputStream {
    public UnclosinInputStream(InputStream in) {
        super(in);
    }

    @Override
    public void close() {
        // do nothing
    }
}