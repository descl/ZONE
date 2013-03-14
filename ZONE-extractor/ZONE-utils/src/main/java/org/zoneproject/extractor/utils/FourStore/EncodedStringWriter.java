package org.zoneproject.extractor.utils.FourStore;

/*
 * #%L
 * ZONE-utils
 * %%
 * Copyright (C) 2012 ZONE-project
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
