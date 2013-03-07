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
 * The TrainingParameters object combines the KernelParm and LearnParam objects.
 * 
 * @author Tom Crecelius & Martin Theobald
 */
public class TrainingParameters {

  private KernelParam m_kp;

  private LearnParam m_lp;

  /**
   * Initializes the training parameters with the default values for the kernel
   * and the learning parameters.
   */
  public TrainingParameters() {
    this(new LearnParam(), new KernelParam());
  }

  /**
   * Initializes the training parameters with customized values for the kernel
   * and the learning parameters.
   */
  public TrainingParameters(LearnParam lp, KernelParam kp) {
    m_lp = lp;
    m_kp = kp;
  }

  public LearnParam getLearningParameters() {
    return m_lp;
  }

  public KernelParam getKernelParameters() {
    return m_kp;
  }

  public void setLearningParameters(LearnParam lp) {
    this.m_lp = lp;
  }

  public void setKernelParameters(KernelParam kp) {
    this.m_kp = kp;
  }

  public TrainingParameters(String[] argv) {
    this();
    m_lp.argc = argv.length;
    if (argv.length > 1 && (argv.length % 2) == 0) {
      m_lp.argv = argv;
    } else {
      System.err.println("Wrong number of arguments!");
    }
  }
}
