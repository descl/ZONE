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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * The main interface class that transfers the training data to the SVM-light
 * library by a native call. Optionally takes as input an individually modified
 * set of training parameters or an array of string parameters that exactly
 * simulate the command line input parameters used by the SVM-light binaries.
 * This class can also be used for native classification calls.
 * 
 * @author Tom Crecelius & Martin Theobald, including a bug fix by George Shaw (MIT)
 */
public class SVMLightInterface {

	/**
	 * Apply an in-place quicksort prior to each native training call to
	 * SVM-light. SVM-light requires each input feature vector to be sorted in
	 * ascending order of dimensions. Disable this option if you are sure to
	 * provide sorted vectors already.
	 */
	public static boolean SORT_INPUT_VECTORS = true;

	static {
		System.loadLibrary("svmlight");
	}

	/**
	 * Reads a set of labeled training vectors from a URL. The format is
	 * compatible to the SVM-light training files.
	 */
	public static LabeledFeatureVector[] getLabeledFeatureVectorsFromURL(
	    URL file, int numOfLinesToSkip) throws ParseException {

		ArrayList<LabeledFeatureVector> data = new ArrayList<LabeledFeatureVector>();
		LabeledFeatureVector[] traindata = null;
		BufferedReader bi = null;

		try {

			bi = new BufferedReader(new InputStreamReader(file.openStream()));

			String line = null;
			ArrayList<String> dimlist, vallist;
			String label, dimval, dim, val;
			String[] tokens;

			int idx, cnt = 0;
			while ((line = bi.readLine()) != null) {
				cnt++;
				if (cnt <= numOfLinesToSkip) {
					continue;
				}
				label = null;
				tokens = line.trim().split("[ \\t\\n\\x0B\\f\\r]");
				if (tokens.length > 1) {
					label = tokens[0];
					dimlist = new ArrayList<String>();
					vallist = new ArrayList<String>();
					for (int tokencnt = 1; tokencnt < tokens.length; tokencnt++) {
						dimval = tokens[tokencnt];
						if (dimval.trim().startsWith("#"))
							break;

						idx = dimval.indexOf(':');
						if (idx >= 0) {
							dim = dimval.substring(0, idx);
							val = dimval.substring(idx + 1, dimval.length());
							dimlist.add(dim);
							vallist.add(val);
						} else {
							throw new ParseException("Parse error in FeatureVector of file '"
							    + file.toString() + "' at line: " + cnt + ", token: "
							    + tokencnt + ". Could not estimate a \"int:double\" pair ?! "
							    + file.toString()
							    + " contains a wrongly defined feature vector!", 0);
						}
					}
					if (dimlist.size() > 0) {
						double labelvalue = new Double(label).doubleValue();
						int[] dimarray = new int[dimlist.size()];
						double[] valarray = new double[vallist.size()];
						for (int i = 0; i < dimlist.size(); i++) {
							dimarray[i] = new Integer((String) dimlist.get(i)).intValue();
						}
						for (int i = 0; i < vallist.size(); i++) {
							valarray[i] = new Double((String) vallist.get(i)).doubleValue();
						}
						LabeledFeatureVector lfv = new LabeledFeatureVector(labelvalue,
						    dimarray, valarray);
						data.add(lfv);
					}
				} else {
					throw new ParseException("Parse error in FeatureVector of file '"
					    + file.toString() + "' at line: " + cnt + ". "
					    + " Wrong format of the labeled feature vector?", 0);
				}
			}
			if (data.size() > 0) {
				traindata = new LabeledFeatureVector[data.size()];
				for (int i = 0; i < data.size(); i++) {
					traindata[i] = (LabeledFeatureVector) data.get(i);
				}
			} else {
				throw new ParseException("No labeled features found within " + cnt
				    + "lines of file '" + file.toString() + "'.", 0);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (bi != null) {
				try {
					bi.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return traindata;
	}

	protected TrainingParameters m_tp;

	/**
	 * Performs a classification step as a native call to SVM-light. If this
	 * method is used exclusively, no additional SVMLightModel object has to be
	 * kept in the Java runtime process.
	 */
	public native double classifyNative(FeatureVector doc);

	public TrainingParameters getTrainingParameters() {
		return m_tp;
	}

	private void quicksort(int[] dims, double[] vals, int low, int high) {
		if (low >= high)
			return;

		int leftIdx = low;
		int pivot = low;
		int rightIdx = high;
		pivot = (low + high) / 2;
		while (leftIdx <= pivot && rightIdx >= pivot) {
			while (dims[leftIdx] < dims[pivot] && leftIdx <= pivot)
				leftIdx++;
			while (dims[rightIdx] > dims[pivot] && rightIdx >= pivot)
				rightIdx--;
			int tmp = dims[leftIdx];
			dims[leftIdx] = dims[rightIdx];
			dims[rightIdx] = tmp;
			double tmpd = vals[leftIdx];
			vals[leftIdx] = vals[rightIdx];
			vals[rightIdx] = tmpd;
			leftIdx++;
			rightIdx--;
			if (leftIdx - 1 == pivot)
				pivot = rightIdx = rightIdx + 1;
			else if (rightIdx + 1 == pivot)
				pivot = leftIdx = leftIdx - 1;
			quicksort(dims, vals, low, pivot - 1);
			quicksort(dims, vals, pivot + 1, high);
		}
	}

	private void sort(FeatureVector[] trainingData) {
		for (int i = 0; i < trainingData.length; i++) {
			if (trainingData[i] != null) {
				quicksort(trainingData[i].m_dims, trainingData[i].m_vals, 0,
				    trainingData[i].size() - 1);
				// verifyIsSorted(trainingData[i].m_dims);
			}
		}
	}

	private native SVMLightModel trainmodel(LabeledFeatureVector[] traindata,
	    TrainingParameters p);

	public SVMLightModel trainModel(LabeledFeatureVector[] trainingData) {
		this.m_tp = new TrainingParameters();
		if (SORT_INPUT_VECTORS) {
			sort(trainingData);
		}
		return trainmodel(trainingData, m_tp);
	}

	public SVMLightModel trainModel(LabeledFeatureVector[] trainingData,
	    String[] argv) {
		this.m_tp = new TrainingParameters(argv);
		if (SORT_INPUT_VECTORS) {
			sort(trainingData);
		}
		return trainmodel(trainingData, m_tp);
	}

	public SVMLightModel trainModel(LabeledFeatureVector[] trainingData,
	    TrainingParameters tp) {
		this.m_tp = tp;
		if (SORT_INPUT_VECTORS) {
			sort(trainingData);
		}
		return trainmodel(trainingData, m_tp);
	}
}
