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
 * Abstract class for an extended kernel.
 * 
 * @author Tom Crecelius & Martin Theobald
 */
public abstract class ExtendedKernel extends Kernel {

  protected double m_a;

  protected double m_c;

  protected ExtendedKernel() {
    super();
    this.m_a = 1.0;
    this.m_c = 0.0;
  }

  protected ExtendedKernel(Kernel nestedKernel, double multiplier,
      double constant) {
    super(nestedKernel);
    this.m_a = multiplier;
    this.m_c = constant;
  }

  public double getConstant() {
    return m_c;
  }

  public double getMultiplier() {
    return m_a;
  }

  public void setConstant(double c) {
    this.m_c = c;
  }

  public void setMultiplier(double m) {
    this.m_a = m;
  }
}
