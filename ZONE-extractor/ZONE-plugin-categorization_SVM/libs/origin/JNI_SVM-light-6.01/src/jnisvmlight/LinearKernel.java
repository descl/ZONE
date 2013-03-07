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
 * A linear kernel. Computes the scalar (inner) product of two feature vectors
 * and is the default inner kernel of all the other kernel implementations.
 * 
 * @author Tom Crecelius & Martin Theobald
 */
public class LinearKernel extends Kernel {

  public double evaluate(FeatureVector fv1, FeatureVector fv2) {
     double result = 0.0;      
     int i = 0, j = 0;
     while (i < fv1.m_dims.length && j < fv2.m_dims.length) {
       if (fv1.m_dims[i]>fv2.m_dims[j]) j++;
       else if (fv1.m_dims[i]<fv2.m_dims[j]) i++;
       else result += (fv1.m_vals[i++] * fv2.m_vals[j++]);
     }
     return result;
   }
}
