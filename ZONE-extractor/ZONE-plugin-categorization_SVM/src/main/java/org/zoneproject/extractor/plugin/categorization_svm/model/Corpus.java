package org.zoneproject.extractor.plugin.categorization_svm.model;

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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Corpus {

    private static List<Text> allTexts;

    private Corpus() {
    }

    public static List<Text> getCorpus() {
        //Corpus c = new Corpus();
        if (allTexts == null) {
            allTexts = new ArrayList<Text>();
            Corpus.readCorpusFromFile();
        }
        return allTexts;

    }

    public static void writeCorpusIntoFile(){
        try {
            URL outFile = Corpus.class.getResource("/Corpus.dat");
            if(outFile == null) {
                File f = new File(Corpus.class.getResource("/").getPath()+"Corpus.dat");
                f.createNewFile();
                outFile = Corpus.class.getResource("/Corpus.dat");
            } 
            FileOutputStream fos = new FileOutputStream(new File(outFile.toURI()));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            try {
                // sérialisation : écriture de l'objet dans le flux de sortie
                oos.writeObject(allTexts);
                // on vide le tampon
                oos.flush();

            } finally {
                //fermeture des flux
                try {
                    oos.close();
                } finally {
                    fos.close();
                }
            }
        } catch (URISyntaxException | IOException e) {
            Logger.getLogger(Corpus.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    @SuppressWarnings("unchecked")
    private static void readCorpusFromFile(){
        try {
            if(Corpus.class.getResource("/Corpus.dat") == null)
                return;
            // ouverture d'un flux d'entrée depuis le fichier "personne.serial"
            FileInputStream fis = new FileInputStream(new File(Corpus.class.getResource("/Corpus.dat").toURI()));
            // création d'un "flux objet" avec le flux fichier
            ObjectInputStream ois = new ObjectInputStream(fis);
            try {
                // désérialisation : lecture de l'objet depuis le flux d'entrée
                allTexts = (List<Text>) ois.readObject();
            } finally {
                // on ferme les flux
                try {
                    ois.close();
                } finally {
                    fis.close();
                }
            }
        } catch (IOException | URISyntaxException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }

    }
}
