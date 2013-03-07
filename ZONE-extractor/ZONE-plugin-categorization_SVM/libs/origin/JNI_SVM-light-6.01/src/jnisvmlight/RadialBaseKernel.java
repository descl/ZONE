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
 * A radial base kernel.
 * 
 * @author Tom Crecelius & Martin Theobald
 */
public class RadialBaseKernel extends Kernel {

  private double m_width;

  protected RadialBaseKernel() {
    this(new LinearKernel(), 1.0);
  }

  public RadialBaseKernel(Kernel nestedKernel, double width) {
    super(nestedKernel);
    this.m_width = width;
  }

  public double evaluate(FeatureVector v1, FeatureVector v2) {
    return Math.exp(-m_width * (m_kernel.evaluate(v1, v1) - (2 * m_kernel.evaluate(v1, v2)) + m_kernel.evaluate(v2, v2))); 
  }

  public double getWidth() {
    return m_width;
  }

  public void setWidth(double width) {
    this.m_width = width;
  }

  public String toString() {
    return "Radial base kernel K(x, y, k) = exp(-" + m_width + " k(x,x) -2 k(x,y) + k(y,y)); k = " + m_kernel.toString();
  }
}
