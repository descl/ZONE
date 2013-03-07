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

import java.io.Serializable;

/**
 * Abstract kernel class.
 * 
 * @author Tom Crecelius & Martin Theobald
 */
public abstract class Kernel implements Serializable {

  protected Kernel m_kernel;

  protected Kernel() {
    this.m_kernel = null;
  }

  protected Kernel(Kernel nestedKernel) {
    this.m_kernel = nestedKernel;
  }

  abstract public double evaluate(FeatureVector v1, FeatureVector v2);

  public Kernel getNestedKernel() {
    return m_kernel;
  }

  public void setNestedKernel(Kernel nestedKernel) {
    m_kernel = nestedKernel;
  }
}
