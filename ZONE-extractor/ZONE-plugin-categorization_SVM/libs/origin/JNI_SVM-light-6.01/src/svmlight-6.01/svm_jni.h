# include <stdio.h>
# include <ctype.h>
# include <math.h>
# include <string.h>
# include <stdlib.h>
# include "jnisvmlight_SVMLightInterface.h"
# include "svm_common.h"
# include "svm_learn.h"

extern void print_help();
extern void wait_any_key();

static MODEL * _model;

typedef struct javaparamids {
	
	jsize tDataSize;
	jclass tDataCls;
	jfieldID ID_double_label;
	jfieldID ID_double_factor;
	jfieldID ID_intArray_dimensions;
	jfieldID ID_doubleArray_values;
	// jfieldID ID_int_size;
	jmethodID ConstructorID_tDataCls;
	
	jclass SVMLightModelCls;
	jfieldID ID_string_format;
	jfieldID ID_long_kType;
	jfieldID ID_long_dParam;
	jfieldID ID_double_gParam;
	jfieldID ID_double_sParam;
	jfieldID ID_double_rParam;
	jfieldID ID_string_uParam;
	jfieldID ID_long_highFeatIdx; 
	jfieldID ID_long_trainDocs;
	jfieldID ID_long_numSupVecs; 
	jfieldID ID_double_threshold;
	jfieldID ID_labeledFeatureVectorArray_docs;
	jfieldID ID_doubleArray_linWeights;
	jmethodID ConstructorID_SVMLightModelCls;
	
	
} JavaParamIDs;

typedef struct jtrainparams {
	JNIEnv * env;
	int argc;
	char **argv;
	jfieldID ID_LearnParam_lp;
	jobject lp;
	
	jfieldID ID_int_verbosity; 
	
	jfieldID ID_long_type;                 
	jfieldID ID_double_svm_c;                
	jfieldID ID_double_eps;                  
	jfieldID ID_double_svm_costratio;        
	jfieldID ID_double_transduction_posratio;
	
	jfieldID ID_long_biased_hyperplane;    
	jfieldID ID_long_sharedslack;          
	jfieldID ID_long_svm_maxqpsize;        
	jfieldID ID_long_svm_newvarsinqp;      
	jfieldID ID_long_kernel_cache_size;    
	jfieldID ID_double_epsilon_crit;         
	jfieldID ID_double_epsilon_shrink;       
	jfieldID ID_long_svm_iter_to_shrink;   
	jfieldID ID_long_maxiter;              
	jfieldID ID_long_remove_inconsistent;  
	jfieldID ID_long_skip_final_opt_check; 
	jfieldID ID_long_compute_loo;          
	jfieldID ID_double_rho;                  
	jfieldID ID_long_xa_depth;             
	
	jfieldID ID_string_predfile;          
	jfieldID ID_string_alphafile;         
	
	jfieldID ID_double_epsilon_const;        
	jfieldID ID_double_epsilon_a;            
	jfieldID ID_double_opt_precision;        
	
	jfieldID ID_long_svm_c_steps;          
	jfieldID ID_double_svm_c_factor;         
	jfieldID ID_double_svm_costratio_unlab;
	jfieldID ID_double_svm_unlabbound;
	jfieldID ID_double_svm_cost;            
	jfieldID ID_long_totwords;             
	
	
	jfieldID ID_KernelParam_kp;
	jobject kp;
	
	jfieldID ID_long_kernel_type;   
	jfieldID ID_long_poly_degree;
	jfieldID ID_double_rbf_gamma;
	jfieldID ID_double_coef_lin;
	jfieldID ID_double_coef_const;
	jfieldID ID_string_custom;    
	
} JTrainParams;

JavaParamIDs* GetJParamIDs(JNIEnv * env, jobjectArray *tdata); 
void createDOCs(JNIEnv * env,JavaParamIDs *JIDs,jobjectArray* tdata,DOC*** docs, double** target, long* totwords, long* totdoc, long* ndocuments);
void SVMparmInit(KERNEL_CACHE* kernel_cache,LEARN_PARM* learn_parm,KERNEL_PARM* kernel_parm, MODEL* model,JTrainParams* tparam);
jobject buildModelData(JNIEnv *env, jobject obj, MODEL * model,JavaParamIDs* ids);
void jinit_traindoc(double doc_label, DOC ***docs, double **label, long max_words_doc, long int *totwords, long int *totdoc, int* dims, double *vals, long* ndocuments, WORD* words, FILE* test);
int jparse_document(WORD *words, long *queryid, long *slackid, double *costfactor,  long int *numwords, long int max_words_doc, int *dims, double *vals);
SVECTOR *create_svector2(WORD *words, double factor);
