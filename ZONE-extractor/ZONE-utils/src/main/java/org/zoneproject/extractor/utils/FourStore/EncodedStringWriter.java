package org.zoneproject.extractor.utils.FourStore;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Arrays;

class EncodedStringWriter extends Writer {

    private final OutputStream os;

    public EncodedStringWriter(OutputStream os) {
        this.os = os;
    }

    @Override
    public void write(char[] chars, int i, int i1) throws IOException {
        
        String string = new String(Arrays.copyOfRange(chars, i, i1));

        String encoded = URLEncoder.encode(string, "UTF-8");

        os.write(encoded.getBytes("UTF-8"));
    }

    @Override
    public void flush() throws IOException {
        os.flush();
    }

    @Override
    public void close() throws IOException {
        os.close();
    }
}