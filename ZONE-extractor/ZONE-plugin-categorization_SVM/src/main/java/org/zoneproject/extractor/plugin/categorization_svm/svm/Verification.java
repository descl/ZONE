package org.zoneproject.extractor.plugin.categorization_svm.svm;

/*
 * #%L
 * ZONE-plugin-categorization_SVM
 * %%
 * Copyright (C) 2012 ZONE-project
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
import java.io.File;

public class Verification {

    public static int countTextInDirectory(File dir) {

        File[] filesToClass = dir.listFiles();

        int nbOfTexts = filesToClass.length;
        return nbOfTexts;

    }

    public static int countTextCorrectInDirectory(File dirRef, File dirClas) {
        File[] filesRef = dirRef.listFiles();
        File[] filesClas = dirClas.listFiles();
        int nbOfCorrectText = 0;
        for (File f : filesClas) {

            for (File f1 : filesRef) {

                if (f1.getName().equals(f.getName())) {

                    nbOfCorrectText++;
                    break;
                }

            }

        }
        return nbOfCorrectText;
    }

    public static double computeRecall(int nbTextCorrect, int nbTextTot) {

        return (double) nbTextCorrect / nbTextTot;

    }

    public static double computePrecision(int nbTextCorrect, int nbTextInCat) {

        return (double) nbTextCorrect / nbTextInCat;
    }
}
