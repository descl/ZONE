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
 * Kernel parameters used by SVM-light.
 * 
 * @author Tom Crecelius & Martin Theobald
 */
public class KernelParam {

  /** Uses a linear kernel type. */
  public static final int LINEAR = 0;

  /** Uses a polynomial kernel type. */
  public static final int POLYNOMIAL = 1;

  /** Use a radial base kernel type. */
  public static final int RBF = 2;

  /** Uses as sigmoid kernel type. */
  public static final int SIGMOID = 3;

  /** Constant coefficient for extended kernels. */
  public double coef_const;

  /** Linear coefficient for extended kernels. */
  public double coef_lin;

  /** For user supplied kernel. */
  public String custom;

  /** Selects between LINEAR, POLYNOMIAL, RBF, or SIGMOID kernel type. */
  public long kernel_type;

  /** Degree of polynomial kernel. */
  public long poly_degree;

  /** Gamma constant for a radial base kernel. */
  public double rbf_gamma;

  /** Initializes the kernel parameters with the default SVM-light values. */
  public KernelParam() {
    this.kernel_type = LINEAR;
    this.poly_degree = 3;
    this.rbf_gamma = 1.0;
    this.coef_lin = 1.0;
    this.coef_const = 0.0;
    this.custom = new String("empty");
  }
}
