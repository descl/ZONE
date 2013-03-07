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
 * Learning parameters as denoted by SVM-light.
 * 
 * @author Tom Crecelius & Martin Theobald
 */
public class LearnParam {

  /** Trains a classification model. */
  public static final int CLASSIFICATION = 1;

  /** Trains on general set of constraints. */
  public static final int OPTIMIZATION = 4;

  /** Trains a ranking model. */
  public static final int RANKING = 3;

  /** Trains a regression model. */
  public static final int REGRESSION = 2;

  /**
   * File to store optimal alphas in. use empty string if alphas should not be
   * output.
   */
  public String alphafile;

  /** The cardinality of the command line parameters. */
  public int argc = 0;

  /**
   * Optionally simulates a simple command shell-like usage and transfers the
   * command line parameters to SVM-light.
   */
  public String[] argv;

  /** If nonzero, use hyperplane w*x+b=0 otherwise w*x=0. */
  public long biased_hyperplane;

  /** If nonzero, computes leave-one-outestimates. */
  public long compute_loo;

  /** Regression epsilon (eps=1.0 for classification). */
  public double eps;

  /** Tolerable error on alphas at bounds. */
  public double epsilon_a;

  /** Tolerable error on eq-constraint. */
  public double epsilon_const;

  /** Tolerable error for distances used in stopping criterion. */
  public double epsilon_crit;

  /** How much a multiplier should be above zero for shrinking. */
  public double epsilon_shrink;

  /** Size of kernel cache in megabytes. */
  public long kernel_cache_size;

  /**
   * Number of iterations after which the optimizer terminates, if there was no
   * progress in maxdiff.
   */
  public long maxiter;

  /** Precision of solver, set to e.g. 1e-21 if you get convergence problems. */
  public double opt_precision;

  /** File for predictions on unlabeled examples in transduction. */
  public String predfile;

  /** Exclude examples with alpha at C and retrain. */
  public long remove_inconsistent;

  /** Parameter in xi/alpha-estimates and for pruning leave-one-out range [1..2]. */
  public double rho;

  /**
   * If nonzero, it will use the shared slack variable mode. In
   * svm_learn_optimization it requires that the slack-id is set for every
   * training example.
   */
  public long sharedslack;

  /**
   * Do not check KT-Conditions at the end of optimization for examples removed
   * by shrinking. WARNING: This might lead to sub-optimal solutions!
   */
  public long skip_final_opt_check;

  /** Upper bound C on alphas. */
  public double svm_c;

  /** Increase C by this factor every step. */
  public double svm_c_factor;

  /** Do so many steps for finding optimal C. */
  public long svm_c_steps;

  /** Individual upper bounds for each var. */
  public double svm_cost;

  /** Factor to multiply C for positive examples. */
  public double svm_costratio;

  public double svm_costratio_unlab;

  /* You probably do not want to touch the following: */

  /** Iterations h after which an example can be removed by shrinking. */
  public long svm_iter_to_shrink;

  /** Size q of working set. */
  public long svm_maxqpsize;

  /** New variables to enter the working set in each iteration. */
  public long svm_newvarsinqp;

  /* The following are only for internal use: */

  public double svm_unlabbound;

  /** Total amount of features. */
  public long totwords;

  /** Fraction of unlabeled examples to be classified as positives. */
  public double transduction_posratio;

  /** Selects between CLASSIFICATION, REGRESSION, RANKING, or OPTIMIZATION mode. */
  public long type;

  /** The level of SVM-light debugging infos. */
  public int verbosity;

  /**
   * Parameter in xi/alpha-estimates upper bounding the number of SV the current
   * alpha_t is distributed over.
   */
  public long xa_depth;

  /** Initializes the learning parameters with the default SVM-light values. */
  public LearnParam() {
    this.verbosity = 0;
    this.type = CLASSIFICATION;
    this.predfile = new String("trans_predictions");
    this.alphafile = new String("");
    this.biased_hyperplane = 1;
    this.sharedslack = 0;
    this.remove_inconsistent = 0;
    this.skip_final_opt_check = 0;
    this.svm_maxqpsize = 10;
    this.svm_newvarsinqp = 0;
    this.svm_iter_to_shrink = -9999;
    this.maxiter = 100000;
    this.kernel_cache_size = 40;
    this.svm_c = 0.0;
    this.eps = 0.1;
    this.transduction_posratio = -1.0;
    this.svm_costratio = 1.0;
    this.svm_costratio_unlab = 1.0;
    this.svm_unlabbound = 1E-5;
    this.epsilon_crit = 0.001;
    this.epsilon_a = 1E-15;
    this.compute_loo = 0;
    this.rho = 1.0;
    this.xa_depth = 0;
  }
}
