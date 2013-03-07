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
 * A polynomial kernel.
 * 
 * @author Tom Crecelius & Martin Theobald
 */
public class PolynomialKernel extends ExtendedKernel {

  private double m_order;

  protected PolynomialKernel() {
    this(new LinearKernel(), 3.0, 1.0, 1.0);
  }

  public PolynomialKernel(Kernel nestedKernel, double order, double a, double c) {
    super(nestedKernel, a, c);
    this.m_order = order;
  }

  public double evaluate(FeatureVector v1, FeatureVector v2) {
    return Math.pow(m_a * m_kernel.evaluate(v1, v2) + m_c, m_order);
  }

  public double getOrder() {
    return m_order;
  }

  public void setOrder(double order) {
    this.m_order = order;
  }

  public String toString() {
    return "Polynomial kernel K(x, y | k) = (" + m_a + " * k(x, y) + " + m_c
        + ")^" + m_order + ". k = " + m_kernel.toString();
  }
}
