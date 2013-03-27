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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Dictionnaire {

    private static HashMap<Integer, String> dic;

    private Dictionnaire() {
    }

    public static Map<Integer, String> getDictionnaire() {
        //Dictionnaire d = new Dictionnaire();
        if (dic == null) {
            dic = new HashMap<Integer, String>();
            dic.put(0, "***************************");
            Dictionnaire.readDictionnaireFromFile();
        }
        
        return dic;
    }

    public static void writeDictionnaireIntoFile() throws IOException{
        try {
            URL outFile = Dictionnaire.class.getResource("/Dict.dat");
            if(outFile == null) {
                File f = new File(Dictionnaire.class.getResource("/").getPath()+"Dict.dat");
                f.createNewFile();
                outFile = Dictionnaire.class.getResource("/Dict.dat");
            } 
            FileOutputStream fos = new FileOutputStream(new File(outFile.toURI()));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            try {
                // sérialisation : écriture de l'objet dans le flux de sortie
                oos.writeObject(dic);
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
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unchecked")
    private static void readDictionnaireFromFile() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(Dictionnaire.class.getResource("/Dic.dat").toURI()));
            // création d'un "flux objet" avec le flux fichier
            ObjectInputStream ois = new ObjectInputStream(fis);
            // désérialisation : lecture de l'objet depuis le flux d'entrée
            dic = (HashMap<Integer, String>) ois.readObject();
        } catch (URISyntaxException ex) {
            Logger.getLogger(Dictionnaire.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } catch (java.lang.NullPointerException ex) {
            Logger.getLogger(Dictionnaire.class.getName()).log(Level.INFO, null, "Dict.dat file doesn't exist");
        }finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(Dictionnaire.class.getName()).log(Level.SEVERE, null, ex);
            } catch (java.lang.NullPointerException ex){
                //empty file
            }
            
        }

    }
}
