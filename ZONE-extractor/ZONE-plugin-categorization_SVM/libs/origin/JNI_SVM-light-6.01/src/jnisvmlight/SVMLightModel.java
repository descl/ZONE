/*
 * JNI_SVM-light - A Java Native Interface for SVM-light
 * 
 * Copyright (C) 2005 
 * Tom Crecelius & Martin Theobald 
 * Max-Planck Institute for Computer Science
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package jnisvmlight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.text.ParseException;

/**
 * SVM classifier model returned by SVM-light.
 * 
 * @author Tom Crecelius & Martin Theobald
 */
public class SVMLightModel implements Serializable {

  /**
   * Reads an SVM-light model from a URL and creates an SVMLightModel object in
   * Java. The format is compatible to the SVM-light model files.
   */
  public static SVMLightModel readSVMLightModelFromURL(URL file)
      throws ParseException {

    LabeledFeatureVector[] lfv;

    String format;
    long kType;
    long dParam;
    double gParam;
    double sParam;
    double rParam;
    String uParam;
    long highFeatIdx;
    long trainDocs;
    long numSupVecs;
    double threshold;

    int linecnt = 0;
    try {

      BufferedReader bi = new BufferedReader(new InputStreamReader(file
          .openStream()));
      String line = null;
      linecnt++;
      line = bi.readLine();
      format = line.toString();
      linecnt++;
      line = bi.readLine();
      kType = new Long((line.substring(0, line.indexOf('#'))).trim())
          .longValue();
      linecnt++;
      line = bi.readLine();
      dParam = new Long((line.substring(0, line.indexOf('#'))).trim())
          .longValue();
      linecnt++;
      line = bi.readLine();
      gParam = new Double((line.substring(0, line.indexOf('#'))).trim())
          .doubleValue();
      linecnt++;
      line = bi.readLine();
      sParam = new Double((line.substring(0, line.indexOf('#'))).trim())
          .doubleValue();
      linecnt++;
      line = bi.readLine();
      rParam = new Double((line.substring(0, line.indexOf('#'))).trim())
          .doubleValue();
      linecnt++;
      line = bi.readLine();
      uParam = (line.substring(0, line.indexOf('#'))).trim();
      linecnt++;
      line = bi.readLine();
      highFeatIdx = new Long((line.substring(0, line.indexOf('#'))).trim())
          .longValue();
      linecnt++;
      line = bi.readLine();
      trainDocs = new Long((line.substring(0, line.indexOf('#'))).trim())
          .longValue();
      linecnt++;
      line = bi.readLine();
      numSupVecs = new Long((line.substring(0, line.indexOf('#'))).trim())
          .longValue();
      linecnt++;
      line = bi.readLine();
      threshold = new Double((line.substring(0, line.indexOf('#'))).trim())
          .doubleValue();

      bi.close();

    } catch (IOException ioe) {
      ioe.printStackTrace();
      throw new ParseException("Parse error in header at line " + linecnt
          + ": " + ioe.getMessage() + " of file: '" + file.toString()
          + "'. Not an svmlight-model file ?!", 0);
    } catch (NullPointerException npe) {
      throw new ParseException("Parse error in header at line " + linecnt
          + ": " + npe.getMessage() + " of file: '" + file.toString()
          + "'. Not an svmlight-model file ?!", 0);
    } catch (IndexOutOfBoundsException ibe) {
      throw new ParseException("Parse error in header at line " + linecnt
          + ": " + ibe.getMessage() + " of file: '" + file.toString()
          + "'. Not an svmlight-model file ?!", 0);
    } catch (NumberFormatException nfe) {
      throw new ParseException("Parse error in header at line " + linecnt
          + ": " + nfe.getMessage() + " of file: '" + file.toString()
          + "'. Not an svmlight-model file ?!", 0);
    }

    lfv = SVMLightInterface.getLabeledFeatureVectorsFromURL(file, linecnt);

    SVMLightModel model = new SVMLightModel(format, kType, dParam, gParam,
        sParam, rParam, uParam, highFeatIdx, trainDocs, numSupVecs, threshold,
        lfv);

    if (kType == 0)
      model.addWeightVectorToLinearModel();

    return model;
  }

  private LabeledFeatureVector[] m_docs;

  private long m_dParam;

  private String m_format;

  private double m_gParam;

  private long m_highFeatIdx;

  private Kernel m_kernel;

  private long m_kType;

  protected double[] m_linWeights;

  private long m_numSupVecs;

  private double m_rParam;

  private double m_sParam;

  private double m_threshold;

  private long m_trainDocs;

  private String m_uParam;

  protected SVMLightModel(String format, long ktype, long dparm, double gparm,
      double sparm, double rparm, String uparm, long hfi, long tdocs, long nsv,
      double threshold, LabeledFeatureVector[] docs) {
  
    m_format = format;
    m_kType = ktype;
    m_dParam = dparm;
    m_gParam = gparm;
    m_sParam = sparm;
    m_rParam = rparm;
    m_uParam = uparm;
    m_highFeatIdx = hfi;
    m_trainDocs = tdocs;
    m_numSupVecs = nsv;
    m_threshold = threshold;
    m_docs = docs;
    m_linWeights = null;

    switch ((int) m_kType) {
      case 0:
        m_kernel = new LinearKernel();
        break;
      case 1:
        m_kernel = new PolynomialKernel(new LinearKernel(), m_dParam, m_sParam,
            m_rParam);
        break;
      case 2:
        m_kernel = new RadialBaseKernel(new LinearKernel(), m_gParam);
        break;
      case 3: 
	  m_kernel = new SigmoidKernel(new LinearKernel(), m_sParam, m_rParam);
	  break;
      default:
        throw new RuntimeException("This type of kernel is not supported!");
    }
  }

  private void addWeightVectorToLinearModel() {
    this.m_linWeights = new double[(int) m_highFeatIdx + 1];
    for (int i = 0; i < m_docs.length; i++) {
      for (int j = 0; j < m_docs[i].m_dims.length; j++) {
        m_linWeights[m_docs[i].m_dims[j]] += m_docs[i].m_factor
            * m_docs[i].m_label * m_docs[i].m_vals[j];
      }
    }
  }

  public double classify(FeatureVector v) {
    double delta = 0;
    if (m_kType == 0) {
      for (int i = 0; i < v.m_dims.length; i++)
        delta += v.m_factor * m_linWeights[v.m_dims[i]] * v.m_vals[i];
    } else {
      for (int i = 0; i < m_docs.length; i++) {
        double alpha = m_docs[i].getLabel();
        if (alpha != 0){
          delta += v.m_factor * alpha * m_kernel.evaluate(m_docs[i], v);
        }
      }
    }
    return delta - m_threshold;
  }

  /**
   * Returns a vector with the weights of all features for linear kernels. For
   * non-linear kernels this is null.
   */
  public double[] getLinearWeights() {
    return m_linWeights;
  }

  public void setThreshold(double threshold) {
    m_threshold = threshold;
  }

  public String toString() {
    String s = "\n(| Format     : "
        + m_format
        + "|\n"
        + "| KType      : "
        + m_kType
        + "\n"
        + "| dParam     : "
        + m_dParam
        + "\n"
        + "| gParam     : "
        + m_gParam
        + "\n"
        + "| sParam     : "
        + m_sParam
        + "\n"
        + "| rParam     : "
        + m_rParam
        + "\n"
        + "| uParam     : "
        + m_uParam
        + "\n"
        + "| HighFeatIdx   : "
        + m_highFeatIdx
        + "\n"
        + "| trainDocs  : "
        + m_trainDocs
        + "\n"
        + "| numSupVecs : "
        + m_numSupVecs
        + "\n"
        + "| threshold  : "
        + m_threshold
        + "\n"
        + (m_docs != null ? ("#SupportVectors:" + m_docs.length + "\n|[0]"
            + m_docs[0] + "|\n" + (0 < m_docs.length ? ("...\n" + "|["
            + (m_docs.length - 1) + "]" + m_docs[m_docs.length - 1] + "|\n")
            : "")) : "");
    return s;
  }

  /**
   * Writes this SVMLightModel to a file. The format is compatible to the
   * SVM-light model files.
   */
  public void writeModelToFile(String path) {
    File dump = new File(path);
    if (m_docs != null) {
      String header = m_format
          + "\n"
          + m_kType
          + " # kernel type\n"
          + m_dParam
          + " # kernel parameter -d\n"
          + m_gParam
          + " # kernel parameter -g\n"
          + m_sParam
          + " # kernel parameter -s\n"
          + m_rParam
          + " # kernel parameter -r\n"
          + m_uParam
          + "# kernel parameter -u\n"
          + m_highFeatIdx
          + " # highest feature index\n"
          + m_trainDocs
          + " # number of training documents\n"
          + m_numSupVecs
          + " # number of support vectors plus 1\n"
          + m_threshold
          + " # threshold b, each following line is a SV (starting with alpha*y)\n";

      try {
        BufferedWriter bd = new BufferedWriter(new FileWriter(dump));
        bd.write(header);
        for (int i = 0; i < m_docs.length; i++) {
          bd.write(m_docs[i].toString());
        }
        bd.close();
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }

    } else {
      System.out.println("Cannot write model file..");
    }
  }
}
