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
 * A kernel with sigmoid smoothing.
 * 
 * @author Tom Crecelius & Martin Theobald
 */
public class SigmoidKernel extends ExtendedKernel {

  protected SigmoidKernel() {
    super(new LinearKernel(), 1.0, 0.0);
  }

  public SigmoidKernel(Kernel nestedKernel, double a, double c) {
    super(nestedKernel, a, c);
  }

  public double evaluate(FeatureVector v1, FeatureVector v2) {
    return tanh(m_a * m_kernel.evaluate(v1, v2) + m_c);
  }

  private double tanh(double a) {
    double x = Math.exp(a);
    double y = Math.exp(-a);
    return (x - y) / (x + y);
  }

  public String toString() {
    return "Sigmoid kernel K(x, k) = tanh(" + m_a + ".k(x) + " + m_c + ")"
        + ". k = " + m_kernel.toString();
  }
}
