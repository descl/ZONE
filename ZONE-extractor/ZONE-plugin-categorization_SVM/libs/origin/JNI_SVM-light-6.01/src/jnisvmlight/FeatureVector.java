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

/**
 * A feature vector. Features are dimension-value pairs. This class implements a
 * simple dictionary data structure to map dimensions onto their values. Note
 * that for convenience, features do not have be sorted according to their
 * dimensions at this point. The SVMLightTrainer class has an option for sorting
 * input vectors prior to training.
 * 
 * @author Tom Crecelius & Martin Theobald
 */
public class FeatureVector implements java.io.Serializable {

  protected int[] m_dims;

  protected double m_factor;

  protected double[] m_vals;

  public FeatureVector(double factor, int[] dims, double[] vals) {
    this.m_factor = factor;
    this.m_dims = dims;
    this.m_vals = vals;
  }

  public FeatureVector(int size) {
    this.m_factor = 1.0;
    this.m_dims = new int[size];
    this.m_vals = new double[size];
  }

  public FeatureVector(int[] dims, double[] vals) {
    this.m_factor = 1.0;
    this.m_dims = dims;
    this.m_vals = vals;
  }

  /**
   * Returns the cosine similarity between two feature vectors.
   */
  public double getCosine(FeatureVector v) {
    double cosine = 0.0;
    int dim;
    double q_i, d_i;
    for (int i = 0; i < Math.min(this.size(), v.size()); i++) {
      dim = v.getDimAt(i);
      q_i = v.getValueAt(dim);
      d_i = this.getValueAt(dim);
      cosine += q_i * d_i;
    }
    return cosine / (this.getL2Norm() * v.getL2Norm());
  }

  public int getDimAt(int index) {
    return m_dims[index];
  }

  public double getFactor() {
    return m_factor;
  }

  /**
   * Returns the linear norm factor of this vector's values (i.e., the sum of
   * it's values).
   */
  public double getL1Norm() {
    double sum = 0.0;
    for (int i = 0; i < m_vals.length; i++) {
      sum += m_vals[i];
    }
    return sum;
  }

  /**
   * Returns the L2 norm factor of this vector's values.
   */
  public double getL2Norm() {
    double square_sum = 0.0;
    for (int i = 0; i < m_vals.length; i++) {
      square_sum += (m_vals[i] * m_vals[i]);
    }
    return Math.sqrt(square_sum);
  }

  public double getValueAt(int index) {
    return m_vals[index];
  }

  /**
   * Performs a linear normalization to the value 1.
   */
  public void normalizeL1() {
    normalizeL1(getL1Norm());
  }

  /**
   * Performs a linear normalization to the given norm value.
   */
  public void normalizeL1(double norm) {
    for (int i = 0; i < m_vals.length; i++) {
      if (m_vals[i] > 0) {
        m_vals[i] /= norm;
      }
    }
  }

  /**
   * Performs an L2 normalization to the value 1.
   */
  public void normalizeL2() {
    double norm = Math.pow(getL2Norm(), 2);
    for (int i = 0; i < m_vals.length; i++) {
      m_vals[i] = Math.pow(m_vals[i], 2) / norm;
    }
  }

  public void setFactor(double factor) {
    this.m_factor = factor;
  }

  public void setFeatures(int[] dims, double[] vals) {
    this.m_dims = dims;
    this.m_vals = vals;
  }

  public int size() {
    return m_dims.length;
  }

  public String toString() {
    String s = "";
    for (int i = 0; i < m_vals.length; i++) {
      s += "" + m_dims[i] + ":" + m_vals[i] + ""
          + (i < m_vals.length - 1 ? " " : "");
    }
    return s;
  }
}
