package org.zoneproject.extractor.utils.FourStore;

/*
 * #%L
 * ZONE-utils
 * %%
 * Copyright (C) 2012 ZONE-project
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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