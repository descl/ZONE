// vim:fdm=marker:foldmarker={%{,}%}:
# include <jni.h>
# include "svm_jni.h"


JavaParamIDs* GetJParamIDs(JNIEnv * env, jobjectArray* tdata) {

	JavaParamIDs *ids = my_malloc(sizeof(struct javaparamids));

	// Finde und Bestimme Klassentyp von SVMLightModel
	ids->SVMLightModelCls = (*env)->FindClass(env,"jnisvmlight/SVMLightModel");
	if (ids->SVMLightModelCls == 0) {
		perror("Class 'SVMLightModel' can't be found!: perror()");
		exit(1);
	}

	// Bestimme IDs der Membervariablen aus der Klasse 'SVMLightModel'
	ids->ID_string_format = (*env)->GetFieldID(env, ids->SVMLightModelCls, "m_format", "Ljava/lang/String;");
	ids->ID_long_kType = (*env)->GetFieldID(env, ids->SVMLightModelCls, "m_kType", "J");
	ids->ID_long_dParam = (*env)->GetFieldID(env, ids->SVMLightModelCls, "m_dParam", "J");
	ids->ID_double_gParam = (*env)->GetFieldID(env, ids->SVMLightModelCls, "m_gParam", "D");
	ids->ID_double_sParam = (*env)->GetFieldID(env, ids->SVMLightModelCls, "m_sParam", "D");
	ids->ID_double_rParam = (*env)->GetFieldID(env, ids->SVMLightModelCls, "m_rParam", "D");
	ids->ID_string_uParam = (*env)->GetFieldID(env, ids->SVMLightModelCls, "m_uParam", "Ljava/lang/String;");
	ids->ID_long_highFeatIdx = (*env)->GetFieldID(env, ids->SVMLightModelCls, "m_highFeatIdx", "J");
	ids->ID_long_trainDocs = (*env)->GetFieldID(env, ids->SVMLightModelCls, "m_trainDocs", "J");
	ids->ID_long_numSupVecs = (*env)->GetFieldID(env, ids->SVMLightModelCls, "m_numSupVecs", "J");
	ids->ID_double_threshold = (*env)->GetFieldID(env, ids->SVMLightModelCls, "m_threshold", "D");
	ids->ID_doubleArray_linWeights = (*env)->GetFieldID(env, ids->SVMLightModelCls, "m_linWeights", "[D");
	ids->ID_labeledFeatureVectorArray_docs = 
		(*env)->GetFieldID(env, ids->SVMLightModelCls, "m_docs", "[Ljnisvmlight/LabeledFeatureVector;");


	if ((ids->ID_string_format && ids->ID_long_kType && ids->ID_long_dParam 
		&& ids->ID_double_sParam && ids->ID_double_rParam && ids->ID_string_uParam && 
		ids->ID_long_highFeatIdx && ids->ID_long_trainDocs && ids->ID_long_numSupVecs 
		&& ids->ID_double_threshold && ids->ID_labeledFeatureVectorArray_docs) == 0) {

			perror("Can't access JFieldIDs: perror()");
			exit(1);
		}

		// Bestimme ID des Konstruktors der Klasse SVMLightModel
		ids->ConstructorID_SVMLightModelCls = 
			(*env)->GetMethodID( env, ids->SVMLightModelCls, 
			"<init>", 
			"(Ljava/lang/String;JJDDDLjava/lang/String;JJJD[Ljnisvmlight/LabeledFeatureVector;)V"
			);


		if ( ids->ConstructorID_SVMLightModelCls == 0) {
			perror("Can't determine the constructor-method of SVMLightModel: perror()");
			exit(1);
		}

		// Bestimmen der Groesse des uebergebenen Arrays (mit Trainingsdokumenten) tdata
		ids->tDataSize = (*env)->GetArrayLength(env, *tdata);
		if (ids->tDataSize<1) {
			perror("\nArray is containing no training documents!\n");
		}

		// Abgreifen des erstes Trainingsdokuments
		jobject traindoc = (*env)->GetObjectArrayElement(env, *tdata, 0);	
		if (traindoc == NULL) {
			perror("\ntraining document is null!\n");
		}

		// Klassentyp des Trainingsdokuments bestimmen
		ids->tDataCls = (*env)->GetObjectClass(env, traindoc);
		if (ids->tDataCls == 0) {
			perror("Can't determine the class of training documents: perror()");
			exit(1);
		}

		// Die IDs der Membervaribalen aus der Klasse des Trainingsdokuments bestimmen
		ids->ID_double_label = (*env)->GetFieldID(env, ids->tDataCls, "m_label", "D");
		ids->ID_double_factor = (*env)->GetFieldID(env, ids->tDataCls, "m_factor", "D");
		ids->ID_intArray_dimensions = (*env)->GetFieldID(env, ids->tDataCls, "m_dims", "[I");
		ids->ID_doubleArray_values = (*env)->GetFieldID(env, ids->tDataCls, "m_vals", "[D");
		// ids->MemVarID_size = (*env)->GetFieldID(env, ids->tDataCls, "m_size", "I");
		if (((ids->ID_double_label) && (ids->ID_intArray_dimensions) && (ids->ID_doubleArray_values)) == 0) {
			perror("Can't determine jfieldIDs (training documents): perror()");
			exit(1);
		}

		// Die ID des Konstruktors fuer die Klasse eines Trainingsdokuments bestimmen
		ids->ConstructorID_tDataCls = (*env)->GetMethodID(env, ids->tDataCls, "<init>", "()V");
		if ( ids->ConstructorID_tDataCls == 0) {
			perror("Can't determine the constructor-method of a training document: perror()");
			exit(1);
		}

		return ids;

}

JTrainParams* GetJTrainParamIDs(JNIEnv * env, jobject* tparam) {

	JTrainParams *tids = my_malloc(sizeof(struct jtrainparams));		

	tids->env=env;

	jclass tparamCls = (*env)->FindClass(env,"jnisvmlight/TrainingParameters");
	if (tparamCls == 0) {
		perror("Can't determine the class of 'TrainingParameters': perror()");
		exit(1);
	}

	tids->ID_LearnParam_lp = (*env)->GetFieldID(env, tparamCls, "m_lp", "Ljnisvmlight/LearnParam;");
	tids->ID_KernelParam_kp = (*env)->GetFieldID(env, tparamCls, "m_kp", "Ljnisvmlight/KernelParam;");

	if ((tids->ID_LearnParam_lp && tids->ID_KernelParam_kp) == 0) {
		perror("Can't find member variable 'm_lp' or 'm_kp': perror()");
		exit(1);
	}

	tids->lp = (*env)->GetObjectField(env, *tparam, tids->ID_LearnParam_lp);	
	tids->kp = (*env)->GetObjectField(env, *tparam, tids->ID_KernelParam_kp);	
	if (tids->lp == NULL || tids->kp == NULL) {
		perror("Can't access 'm_lp' or 'm_kp': perror()");
		exit(1);
	}


	jclass lpCls = (*env)->GetObjectClass(env,tids->lp);
	jclass kpCls = (*env)->GetObjectClass(env,tids->kp);
	if ((lpCls && kpCls) == 0) {
		perror("Can't determine the class of 'm_lp' or 'm_kp': perror()");
		exit(1);
	}

	tids->ID_int_verbosity = (*env)->GetFieldID(env, lpCls, "verbosity", "I");
	tids->ID_long_type = (*env)->GetFieldID(env, lpCls, "type", "J");
	tids->ID_double_svm_c = (*env)->GetFieldID(env, lpCls, "svm_c", "D");
	tids->ID_double_eps = (*env)->GetFieldID(env, lpCls, "eps", "D");
	tids->ID_double_svm_costratio = (*env)->GetFieldID(env, lpCls, "svm_costratio", "D");
	tids->ID_double_transduction_posratio = (*env)->GetFieldID(env, lpCls, "transduction_posratio", "D");
	tids->ID_long_biased_hyperplane = (*env)->GetFieldID(env, lpCls, "biased_hyperplane", "J");
	tids->ID_long_sharedslack = (*env)->GetFieldID(env, lpCls, "sharedslack", "J");
	tids->ID_long_svm_maxqpsize = (*env)->GetFieldID(env, lpCls, "svm_maxqpsize", "J");
	tids->ID_long_svm_newvarsinqp = (*env)->GetFieldID(env, lpCls, "svm_newvarsinqp", "J");
	tids->ID_long_kernel_cache_size = (*env)->GetFieldID(env, lpCls, "kernel_cache_size", "J");
	tids->ID_double_epsilon_crit = (*env)->GetFieldID(env, lpCls, "epsilon_crit", "D");
	tids->ID_double_epsilon_shrink = (*env)->GetFieldID(env, lpCls, "epsilon_shrink", "D");
	tids->ID_long_svm_iter_to_shrink = (*env)->GetFieldID(env, lpCls, "svm_iter_to_shrink", "J");
	tids->ID_long_maxiter = (*env)->GetFieldID(env, lpCls, "maxiter", "J");
	tids->ID_long_remove_inconsistent = (*env)->GetFieldID(env, lpCls, "remove_inconsistent", "J");
	tids->ID_long_skip_final_opt_check = (*env)->GetFieldID(env, lpCls, "skip_final_opt_check", "J");
	tids->ID_long_compute_loo = (*env)->GetFieldID(env, lpCls, "compute_loo", "J");
	tids->ID_double_rho = (*env)->GetFieldID(env, lpCls, "rho", "D");
	tids->ID_long_xa_depth = (*env)->GetFieldID(env, lpCls, "xa_depth", "J");
	tids->ID_string_predfile = (*env)->GetFieldID(env, lpCls, "predfile", "Ljava/lang/String;");
	tids->ID_string_alphafile = (*env)->GetFieldID(env, lpCls, "alphafile", "Ljava/lang/String;");
	tids->ID_double_epsilon_const = (*env)->GetFieldID(env, lpCls, "epsilon_const", "D");
	tids->ID_double_epsilon_a = (*env)->GetFieldID(env, lpCls, "epsilon_a", "D");
	tids->ID_double_opt_precision = (*env)->GetFieldID(env, lpCls, "opt_precision", "D");
	tids->ID_long_svm_c_steps = (*env)->GetFieldID(env, lpCls, "svm_c_steps", "J");
	tids->ID_double_svm_c_factor = (*env)->GetFieldID(env, lpCls, "svm_c_factor", "D");
	tids->ID_double_svm_costratio_unlab = (*env)->GetFieldID(env, lpCls, "svm_costratio_unlab", "D");
	tids->ID_double_svm_unlabbound = (*env)->GetFieldID(env, lpCls, "svm_unlabbound", "D");
	tids->ID_double_svm_cost = (*env)->GetFieldID(env, lpCls, "svm_cost", "D");
	tids->ID_long_totwords = (*env)->GetFieldID(env, lpCls, "totwords", "J");

	if ((tids->ID_int_verbosity && tids->ID_long_type && tids->ID_double_svm_c && tids->ID_double_eps && tids->ID_double_svm_costratio &&
		tids->ID_double_transduction_posratio && tids->ID_long_biased_hyperplane && tids->ID_long_sharedslack && tids->ID_long_svm_maxqpsize &&
		tids->ID_long_svm_newvarsinqp && tids->ID_long_kernel_cache_size && tids->ID_double_epsilon_crit && tids->ID_double_epsilon_shrink && 
		tids->ID_long_svm_iter_to_shrink && tids->ID_long_maxiter && tids->ID_long_remove_inconsistent && tids->ID_long_skip_final_opt_check &&
		tids->ID_long_compute_loo && tids->ID_double_rho && tids->ID_long_xa_depth && tids->ID_string_predfile && tids->ID_string_alphafile &&
		tids->ID_double_epsilon_const && tids->ID_double_epsilon_a && tids->ID_double_opt_precision && tids->ID_long_svm_c_steps && 
		tids->ID_double_svm_c_factor && tids->ID_double_svm_costratio_unlab && tids->ID_double_svm_unlabbound && tids->ID_double_svm_cost &&
		tids->ID_long_totwords) == 0) {

			perror("Can't determine the jfieldIDs of class 'LearnParam': perror()");
			exit(1);

		}

		tids->ID_long_kernel_type = (*env)->GetFieldID(env, kpCls, "kernel_type", "J");
		tids->ID_long_poly_degree = (*env)->GetFieldID(env, kpCls, "poly_degree", "J");
		tids->ID_double_rbf_gamma = (*env)->GetFieldID(env, kpCls, "rbf_gamma", "D");
		tids->ID_double_coef_lin = (*env)->GetFieldID(env, kpCls, "coef_lin", "D");
		tids->ID_double_coef_const = (*env)->GetFieldID(env, kpCls, "coef_const", "D");
		tids->ID_string_custom = (*env)->GetFieldID(env, kpCls, "custom", "Ljava/lang/String;");

		if ((tids->ID_long_kernel_type && tids->ID_long_poly_degree && tids->ID_double_rbf_gamma && tids->ID_double_coef_lin &&
			tids->ID_double_coef_const && tids->ID_string_custom) == 0) {
				perror("Can't determine the jfieldIDs of class 'KernelParam': perror()");
				exit(1);
			}

			jfieldID argcID = (*env)->GetFieldID(env, lpCls, "argc", "I");
			jfieldID argvID = (*env)->GetFieldID(env, lpCls, "argv", "[Ljava/lang/String;");

			if ((argcID && argvID) == 0) {
				perror("Can't find jfieldIDs of 'argc/argv'");
				exit(1);
			}

			tids->argc = (*env)->GetIntField(env,tids->lp,argcID);

			jobjectArray sfield = (*env)->GetObjectField(env,tids->lp,argvID);
			if (tids->argc > 0) {
				tids->argv = (char**) my_malloc(sizeof(char*) * tids->argc);
				int j;
				for (j=0;j<tids->argc;j++) {
					jstring jstr = (*env)->GetObjectArrayElement(env, sfield, j);
					const char* str = (*env)->GetStringUTFChars(env, jstr, 0 );
					(tids->argv)[j] = (char*) my_malloc(sizeof(char) * strlen(str)+1);
					strcpy((tids->argv)[j],str);
					(*env)->ReleaseStringUTFChars(env,jstr,str);
				}
			}

			return tids;

}

void SVMparmInit(KERNEL_CACHE* kernel_cache,LEARN_PARM* learn_parm,KERNEL_PARM* kernel_parm, MODEL* model, JTrainParams* tparm) {

	char type[100] = " ";
	jstring test;
	const char *str;
	JNIEnv* env = tparm->env;
	int argc = tparm->argc;
	char **argv = tparm->argv;

	verbosity = (*env)->GetIntField(env,tparm->lp,tparm->ID_int_verbosity);

	learn_parm->type = (*env)->GetLongField(env,tparm->lp,tparm->ID_long_type);
	/* learn_parm->svm_c=0.0; */
	learn_parm->svm_c = (*env)->GetDoubleField(env,tparm->lp,tparm->ID_double_svm_c);                
	/* learn_parm->eps=0.1; */
	learn_parm->eps = (*env)->GetDoubleField(env,tparm->lp,tparm->ID_double_eps);                  
	/* learn_parm->svm_costratio=1.0; */
	learn_parm->svm_costratio = (*env)->GetDoubleField(env,tparm->lp,tparm->ID_double_svm_costratio);        
	/* learn_parm->transduction_posratio=-1.0; */
	learn_parm->transduction_posratio = (*env)->GetDoubleField(env,tparm->lp,tparm->ID_double_transduction_posratio);
	/* learn_parm->biased_hyperplane=1; */
	learn_parm->biased_hyperplane = (*env)->GetLongField(env,tparm->lp,tparm->ID_long_biased_hyperplane);    
	/* learn_parm->sharedslack=0; */
	learn_parm->sharedslack = (*env)->GetLongField(env,tparm->lp,tparm->ID_long_sharedslack);          
	/* learn_parm->svm_maxqpsize=10; */
	learn_parm->svm_maxqpsize = (*env)->GetLongField(env,tparm->lp,tparm->ID_long_svm_maxqpsize);        
	/* learn_parm->svm_newvarsinqp=0; */
	learn_parm->svm_newvarsinqp = (*env)->GetLongField(env,tparm->lp,tparm->ID_long_svm_newvarsinqp);      
	/* learn_parm->kernel_cache_size=40; */
	learn_parm->kernel_cache_size = (*env)->GetLongField(env,tparm->lp,tparm->ID_long_kernel_cache_size);    
	/* learn_parm->epsilon_crit=0.001; */
	learn_parm->epsilon_crit = (*env)->GetDoubleField(env,tparm->lp,tparm->ID_double_epsilon_crit);         

	learn_parm->epsilon_shrink = (*env)->GetDoubleField(env,tparm->lp,tparm->ID_double_epsilon_shrink);       
	/* learn_parm->svm_iter_to_shrink=-9999; */
	learn_parm->svm_iter_to_shrink = (*env)->GetLongField(env,tparm->lp,tparm->ID_long_svm_iter_to_shrink);   
	/* learn_parm->maxiter=100000; */
	learn_parm->maxiter = (*env)->GetLongField(env,tparm->lp,tparm->ID_long_maxiter);              
	/* learn_parm->remove_inconsistent=0; */
	learn_parm->remove_inconsistent = (*env)->GetLongField(env,tparm->lp,tparm->ID_long_remove_inconsistent);  
	/* learn_parm->skip_final_opt_check=0; */
	learn_parm->skip_final_opt_check = (*env)->GetLongField(env,tparm->lp,tparm->ID_long_skip_final_opt_check); 
	/* learn_parm->compute_loo=0; */
	learn_parm->compute_loo = (*env)->GetLongField(env,tparm->lp,tparm->ID_long_compute_loo);          
	/* learn_parm->rho=1.0; */
	learn_parm->rho = (*env)->GetDoubleField(env,tparm->lp,tparm->ID_double_rho);                  
	/* learn_parm->xa_depth=0; */
	learn_parm->xa_depth = (*env)->GetLongField(env,tparm->lp,tparm->ID_long_xa_depth);             


	/* strcpy (learn_parm->predfile, "trans_predictions"); */
	test = (*env)->GetObjectField(env, tparm->lp, tparm->ID_string_predfile);
	str = (*env)->GetStringUTFChars(env, test, 0 );
	strcpy (learn_parm->predfile, str);
	(*env)->ReleaseStringUTFChars(env,test,str);
	/* strcpy (learn_parm->alphafile, ""); */
	test = (*env)->GetObjectField(env, tparm->lp, tparm->ID_string_alphafile);
	str = (*env)->GetStringUTFChars(env, test, 0 );
	strcpy (learn_parm->alphafile, str);
	(*env)->ReleaseStringUTFChars(env,test,str);


	learn_parm->epsilon_const = (*env)->GetDoubleField(env,tparm->lp,tparm->ID_double_epsilon_const);        
	/* learn_parm->epsilon_a=1E-15; */
	learn_parm->epsilon_a = (*env)->GetDoubleField(env,tparm->lp,tparm->ID_double_epsilon_a);            

	learn_parm->opt_precision = (*env)->GetDoubleField(env,tparm->lp,tparm->ID_double_opt_precision);        

	learn_parm->svm_c_steps = (*env)->GetLongField(env,tparm->lp,tparm->ID_long_svm_c_steps);          

	learn_parm->svm_c_factor = (*env)->GetDoubleField(env,tparm->lp,tparm->ID_double_svm_c_factor);         
	/* learn_parm->svm_costratio_unlab=1.0; */
	learn_parm->svm_costratio_unlab = (*env)->GetDoubleField(env,tparm->lp,tparm->ID_double_svm_costratio_unlab);
	/* learn_parm->svm_unlabbound=1E-5; */
	learn_parm->svm_unlabbound = (*env)->GetDoubleField(env,tparm->lp,tparm->ID_double_svm_unlabbound);

	learn_parm->svm_cost = (double *) my_malloc(sizeof(double));            
	*(learn_parm->svm_cost) =	(double) (*env)->GetDoubleField(env,tparm->lp,tparm->ID_double_svm_cost);

	learn_parm->totwords = (*env)->GetLongField(env,tparm->lp,tparm->ID_long_svm_c_steps);             

	/* kernel_parm->kernel_type=0; */
	kernel_parm->kernel_type = (*env)->GetLongField(env,tparm->kp,tparm->ID_long_kernel_type); 
	/* kernel_parm->poly_degree=3; */
	kernel_parm->poly_degree = (*env)->GetLongField(env,tparm->kp,tparm->ID_long_poly_degree);
	/* kernel_parm->rbf_gamma=1.0; */
	kernel_parm->rbf_gamma = (*env)->GetDoubleField(env,tparm->kp,tparm->ID_double_rbf_gamma);
	/* kernel_parm->coef_lin=1; */
	kernel_parm->coef_lin = (*env)->GetDoubleField(env,tparm->kp,tparm->ID_double_coef_lin);

	/* kernel_parm->coef_const=1; */
	kernel_parm->coef_const = (*env)->GetDoubleField(env,tparm->kp,tparm->ID_double_coef_const);

	/* strcpy(kernel_parm->custom,"empty"); */
	test = (*env)->GetObjectField(env, tparm->kp, tparm->ID_string_custom);
	str = (*env)->GetStringUTFChars(env, test, 0 );
	strcpy(kernel_parm->custom, str);
	(*env)->ReleaseStringUTFChars(env,test,str);


	if (argc>0) {	
		int i=0;	
		for(i=0;(i<argc) && ((argv[i])[0] == '-');i++) {
			switch ((argv[i])[1]) 
			{ 
			case '?': print_help(); exit(0);
			case 'z': i++; strcpy(type,argv[i]); break;
			case 'v': i++; verbosity=atol(argv[i]); break;
			case 'b': i++; learn_parm->biased_hyperplane=atol(argv[i]); break;
			case 'i': i++; learn_parm->remove_inconsistent=atol(argv[i]); break;
			case 'f': i++; learn_parm->skip_final_opt_check=!atol(argv[i]); break;
			case 'q': i++; learn_parm->svm_maxqpsize=atol(argv[i]); break;
			case 'n': i++; learn_parm->svm_newvarsinqp=atol(argv[i]); break;
			case '#': i++; learn_parm->maxiter=atol(argv[i]); break;
			case 'h': i++; learn_parm->svm_iter_to_shrink=atol(argv[i]); break;
			case 'm': i++; learn_parm->kernel_cache_size=atol(argv[i]); break;
			case 'c': i++; learn_parm->svm_c=atof(argv[i]); break;
			case 'w': i++; learn_parm->eps=atof(argv[i]); break;
			case 'p': i++; learn_parm->transduction_posratio=atof(argv[i]); break;
			case 'j': i++; learn_parm->svm_costratio=atof(argv[i]); break;
			case 'e': i++; learn_parm->epsilon_crit=atof(argv[i]); break;
			case 'o': i++; learn_parm->rho=atof(argv[i]); break;
			case 'k': i++; learn_parm->xa_depth=atol(argv[i]); break;
			case 'x': i++; learn_parm->compute_loo=atol(argv[i]); break;
			case 't': i++; kernel_parm->kernel_type=atol(argv[i]); break;
			case 'd': i++; kernel_parm->poly_degree=atol(argv[i]); break;
			case 'g': i++; kernel_parm->rbf_gamma=atof(argv[i]); break;
			case 's': i++; kernel_parm->coef_lin=atof(argv[i]); break;
			case 'r': i++; kernel_parm->coef_const=atof(argv[i]); break;
			case 'u': i++; strcpy(kernel_parm->custom,argv[i]); break;
			case 'l': i++; strcpy(learn_parm->predfile,argv[i]); break;
			case 'a': i++; strcpy(learn_parm->alphafile,argv[i]); break;
			case 'y': i++; printf("Option \"-y\" is not supported in this Version of the JNI-SVMLight-interface!\n"); fflush(stdout); break;
			default: printf("\nUnrecognized option %s!\n\n",argv[i]);
				print_help();
				exit(0);
			}
		}


		if(strcmp(type,"c")==0) {
			learn_parm->type=CLASSIFICATION;
		}
		else if(strcmp(type,"r")==0) {
			learn_parm->type=REGRESSION;
		}
		else if(strcmp(type,"p")==0) {
			learn_parm->type=RANKING;
		}
		else if(strcmp(type,"o")==0) {
			learn_parm->type=OPTIMIZATION;
		}
		else if(strcmp(type,"s")==0) {
			learn_parm->type=OPTIMIZATION;
			learn_parm->sharedslack=1;
		}
		else if (strcmp(type," ") != 0 || ((learn_parm->type & (CLASSIFICATION | REGRESSION | RANKING | OPTIMIZATION))==0)) {
			printf("\n\nUnknown type '%s': Valid types are 'c' (classification), 'r' regession, and 'p' preference ranking.\n",type);
			fflush(stdout);
			printf("\n\nPress Return for help\n\n");
			fflush(stdout);
			wait_any_key();
			print_help();
			exit(0);
		}    


	}

	if(learn_parm->svm_iter_to_shrink == -9999) {
		if(kernel_parm->kernel_type == LINEAR) 
			learn_parm->svm_iter_to_shrink=2;
		else
			learn_parm->svm_iter_to_shrink=100;
	}


	if((learn_parm->skip_final_opt_check) 
		&& (kernel_parm->kernel_type == LINEAR)) {
			printf("\nIt does not make sense to skip the final optimality check for linear kernels.\n\n");
			learn_parm->skip_final_opt_check=0;
		}    
		if((learn_parm->skip_final_opt_check) 
			&& (learn_parm->remove_inconsistent)) {
				printf("\nIt is necessary to do the final optimality check when removing inconsistent \nexamples.\n");
				fflush(stdout);
				printf("\n\nPress Return for help\n\n");
				fflush(stdout);
				wait_any_key();
				print_help();
				exit(0);
			}    
			if((learn_parm->svm_maxqpsize<2)) {
				printf("\nMaximum size of QP-subproblems not in valid range: %ld [2..]\n",learn_parm->svm_maxqpsize);
				fflush(stdout);
				printf("\n\nPress Return for help\n\n");
				fflush(stdout);
				wait_any_key();
				print_help();
				exit(0);
			}
			if((learn_parm->svm_maxqpsize<learn_parm->svm_newvarsinqp)) {
				printf("\nMaximum size of QP-subproblems [%ld] must be larger than the number of\n",learn_parm->svm_maxqpsize); 
				printf("new variables [%ld] entering the working set in each iteration.\n",learn_parm->svm_newvarsinqp);
				fflush(stdout);
				printf("\n\nPress Return for help\n\n");
				fflush(stdout);
				wait_any_key();
				print_help();
				exit(0);
			}
			if(learn_parm->svm_iter_to_shrink<1) {
				printf("\nMaximum number of iterations for shrinking not in valid range: %ld [1,..]\n",learn_parm->svm_iter_to_shrink); 
				fflush(stdout);
				printf("\n\nPress Return for help\n\n");
				fflush(stdout);
				wait_any_key();
				print_help();
				exit(0);
			}
			if(learn_parm->svm_c<0) {
				printf("\nThe C parameter must be greater than zero!\n\n");
				fflush(stdout);
				printf("\n\nPress Return for help\n\n");
				fflush(stdout);
				wait_any_key();
				print_help();
				exit(0);
			}
			if(learn_parm->transduction_posratio>1) {
				printf("\nThe fraction of unlabeled examples to classify as positives must\n");
				printf("be less than 1.0 !!!\n\n");
				fflush(stdout);
				printf("\n\nPress Return for help\n\n");
				fflush(stdout);
				wait_any_key();
				print_help();
				exit(0);
			}
			if(learn_parm->svm_costratio<=0) {
				printf("\nThe COSTRATIO parameter must be greater than zero!\n\n");
				fflush(stdout);
				printf("\n\nPress Return for help\n\n");
				fflush(stdout);
				wait_any_key();
				print_help();
				exit(0);
			}
			if(learn_parm->epsilon_crit<=0) {
				printf("\nThe epsilon parameter must be greater than zero!\n\n");
				fflush(stdout);
				printf("\n\nPress Return for help\n\n");
				fflush(stdout);
				wait_any_key();
				print_help();
				exit(0);
			}
			if(learn_parm->rho<0) {
				printf("\nThe parameter rho for xi/alpha-estimates and leave-one-out pruning must\n");
				printf("be greater than zero (typically 1.0 or 2.0, see T. Joachims, Estimating the\n");
				printf("Generalization Performance of an SVM Efficiently, ICML, 2000.)!\n\n");
				fflush(stdout);
				printf("\n\nPress Return for help\n\n");
				fflush(stdout);
				wait_any_key();
				print_help();
				exit(0);
			}
			if((learn_parm->xa_depth<0) || (learn_parm->xa_depth>100)) {
				printf("\nThe parameter depth for ext. xi/alpha-estimates must be in [0..100] (zero\n");
				printf("for switching to the conventional xa/estimates described in T. Joachims,\n");
				printf("Estimating the Generalization Performance of an SVM Efficiently, ICML, 2000.)\n");
				fflush(stdout);
				printf("\n\nPress Return for help\n\n");
				fflush(stdout);
				wait_any_key();
				print_help();
				exit(0);
			}

}


JNIEXPORT jdouble JNICALL Java_jnisvmlight_SVMLightInterface_classifyNative(JNIEnv * env, jobject jo1, jobject testdoc) {

	DOC *doc;   /* test example */
	WORD *words;
	SVECTOR *sv; 
	long j,wpos,queryid,slackid,max_words_doc;
	double dist,costfactor;

	jclass classid = (*env)->GetObjectClass(env, testdoc);

	jintArray dim = (*env)->GetObjectField(env, testdoc, (*env)->GetFieldID(env, classid, "m_dims", "[I")); 
	jdoubleArray val = (*env)->GetObjectField(env, testdoc, (*env)->GetFieldID(env, classid, "m_vals", "[D"));

	jsize dimLen = (*env)->GetArrayLength(env, dim);
	jsize valLen = (*env)->GetArrayLength(env, val);

	jint *dimEl = (*env)->GetIntArrayElements(env, dim, 0);
	jdouble *valEl = (*env)->GetDoubleArrayElements(env, val, 0);


	int* ds;
	double *vs;

	if (sizeof(int) == sizeof(jint)) {
		ds = (int*) dimEl;
	} else {
		int fi=0;
		printf("!!!!!!!!!!!!!!! Warning: java datatype \"jint\" isn't of the same size as C datatype \"int\"\n");
		ds = (int*) my_malloc(sizeof(int)*dimLen); 
		for(fi=0;fi<dimLen;fi++) {
			ds[fi] = (int) dimEl[fi];
		}
	}

	if (sizeof(double) == sizeof(jdouble)) {
		vs = (double*) valEl;
	} else {
		int fi=0;
		printf("!!!!!!!!!!!!!!! Warning: Java-Datatype (jdouble) isn't of the same size as C datatype");
		vs = (double*) my_malloc(sizeof(double)*valLen); 
		for(fi=0;fi<valLen;fi++) {
			vs[fi] = (double) valEl[fi];
		}
	}
	//fprintf("totalwords: %ld \n",(int)dimLen);

	max_words_doc=dimLen;
	words = (WORD *)my_malloc(sizeof(WORD)*(dimLen+10));
	jparse_document(words,&queryid,&slackid,&costfactor,&wpos,max_words_doc,ds,vs);

	(*env)->ReleaseIntArrayElements(env,dim,dimEl,0);
	(*env)->ReleaseDoubleArrayElements(env,val,valEl,0);

	
	if(_model->kernel_parm.kernel_type == 0) {    /* linear kernel */
		for(j=0;(words[j]).wnum != 0;j++) {      /* Check if feature numbers   */
			if((words[j]).wnum>_model->totwords)  /* are not larger than in     */
				(words[j]).wnum=0;               /* model. Remove feature if   */
		}                                        /* necessary.                 */
		sv = create_svector2(words,1.0);
		doc = create_example(-1,0,0,0.0,sv);
		dist=classify_example_linear(_model,doc);
	} else {									/* non-linear kernel */
		sv = create_svector2(words,1.0);
		doc = create_example(-1,0,0,0.0,sv);
		dist=classify_example(_model,doc);
	}

	free(words);
	free(doc);

	return (jdouble)dist;
}

SVECTOR *create_svector2(WORD *words, double factor)
{
	SVECTOR *vec;
	long    fnum,i;

	fnum=0;
	while(words[fnum].wnum) {
		fnum++;
	}
	fnum++;

	vec = (SVECTOR *)my_malloc(sizeof(SVECTOR));
	vec->words = (WORD *)my_malloc(sizeof(WORD)*(fnum));
	for(i=0;i<fnum;i++) { 
		vec->words[i]=words[i];
	}
	vec->twonorm_sq=sprod_ss(vec,vec);
	vec->kernel_id=0;
	vec->next=NULL;
	vec->factor=factor;
	return(vec);
}

JNIEXPORT jobject JNICALL Java_jnisvmlight_SVMLightInterface_trainmodel
(JNIEnv * env, jobject obj, jobjectArray tdata, jobject tparm)
{
	DOC **docs;  /* training examples */
	long i;
	long* totdoc = (long*) my_malloc(sizeof(long));
	long* totwords = (long*) my_malloc(sizeof(long));
	long* ndocuments = (long*) my_malloc(sizeof(long));
	double *target=NULL;
	double *alpha_in=NULL;
	KERNEL_CACHE *kernel_cache;
	LEARN_PARM learn_parm;
	KERNEL_PARM kernel_parm;
	_model=(MODEL *)my_malloc(sizeof(MODEL));


	// --------------------- init stuff  ----------------------------

	JavaParamIDs *JIDs = GetJParamIDs(env, &tdata);
	JTrainParams* targs = GetJTrainParamIDs(env,&tparm);
	SVMparmInit(kernel_cache,&learn_parm,&kernel_parm,_model,targs);

	if(verbosity>=1) {
		printf("\n --- Native C function: scanning examples, now .. (JNI Interface)\n"); fflush(stdout);
	}


	// --------------------- create DOCs ---------------------------

	// allocate memory for all training documents

	createDOCs(env,JIDs,&tdata,&docs,&target,totwords,totdoc,ndocuments);

	if(verbosity>=1)
		printf(" --- Native C function: documents allocated successully.\n"); fflush(stdout);

	learn_parm.totwords = *totwords;		 

	// --------------------- create kernel -------------------------

	FILE * dump = NULL;
	long int z = 0;
	long int y = 0;
	if (verbosity>10) {

		if ((dump = fopen("jni-traindump.dat","w")) == NULL) {
			perror("Writing to \"traindump.txt\" doesn't work!\n");
			exit(1);
		}

		printf("\n|||||||||||||||||||||||||||||||||| dumping ..\n");
		fprintf(dump,"totaldocuments: %ld \n",*totdoc);
		while(z<(*totdoc)) {
			fprintf(dump,"(%ld) (QID: %ld) (CF: %.16g) (SID: %ld) ",docs[z]->docnum,docs[z]->queryid,docs[z]->costfactor,docs[z]->slackid);
			SVECTOR *v = docs[z]->fvec;
			fprintf(dump,"(NORM:%.32g) (UD:%s) (KID:%ld) (VL:%p) (F:%.32g) %.32g ",v->twonorm_sq,(v->userdefined == NULL ? "" : v->userdefined),v->kernel_id,v->next,v->factor,target[z]);
			if (v != NULL && v->words != NULL) {
				while ((v->words[y]).wnum) {
					fprintf(dump,"%ld:%.32g ",(v->words[y]).wnum, (v->words[y]).weight);
					y++;
				}
			} else 
				fprintf(dump, "NULL WORTE\n");
			fprintf(dump,"\n");
			y=0;
			z++;
		}


		fprintf(dump,"---------------------------------------------------\n");
		fprintf(dump,"kernel_type: %ld\n",kernel_parm.kernel_type);
		fprintf(dump,"poly_degree: %ld\n",kernel_parm.poly_degree);
		fprintf(dump,"rbf_gamma: %.32g\n",kernel_parm.rbf_gamma);
		fprintf(dump,"coef_lin: %.32g\n",kernel_parm.coef_lin);
		fprintf(dump,"coef_const: %.32g\n",kernel_parm.coef_const);
		fprintf(dump,"custom: %s\n",kernel_parm.custom);

		fprintf(dump,"type: %ld\n",learn_parm.type);
		fprintf(dump,"svm_c: %.32g\n",learn_parm.svm_c);
		fprintf(dump,"eps: %.32g\n",learn_parm.eps);
		fprintf(dump,"svm_costratio: %.32g\n",learn_parm.svm_costratio);
		fprintf(dump,"transduction_posratio: %.32g\n",learn_parm.transduction_posratio);
		fprintf(dump,"biased_hyperplane: %ld\n",learn_parm.biased_hyperplane);
		fprintf(dump,"svm_maxqpsize: %ld\n",learn_parm.svm_maxqpsize);
		fprintf(dump,"svm_newvarsinqp: %ld\n",learn_parm.svm_newvarsinqp);
		fprintf(dump,"epsilon_crit: %.32g\n",learn_parm.epsilon_crit);
		fprintf(dump,"epsilon_shrink: %.32g\n",learn_parm.epsilon_shrink);
		fprintf(dump,"svm_iter_to_shrink: %ld\n",learn_parm.svm_iter_to_shrink);
		fprintf(dump,"remove_inconsistent: %ld\n",learn_parm.remove_inconsistent);
		fprintf(dump,"skip_final_opt_check: %ld\n",learn_parm.skip_final_opt_check);
		fprintf(dump,"compute_loo: %ld\n",learn_parm.compute_loo);
		fprintf(dump,"rho: %.32g\n",learn_parm.rho);
		fprintf(dump,"xa_depth: %ld\n",learn_parm.xa_depth);
		fprintf(dump,"predfile: %s\n",learn_parm.predfile);
		fprintf(dump,"alphafile: %s\n",learn_parm.alphafile);
		fprintf(dump,"epsilon_const: %.32g\n",learn_parm.epsilon_const);
		fprintf(dump,"epsilon_a: %.32g\n",learn_parm.epsilon_a);
		fprintf(dump,"opt_precision: %.32g\n",learn_parm.opt_precision);
		fprintf(dump,"svm_c_steps: %ld\n",learn_parm.svm_c_steps);
		fprintf(dump,"svm_c_factor: %.32g\n",learn_parm.svm_c_factor);
		fprintf(dump,"svm_costratio_unlab: %.32g\n",learn_parm.svm_costratio_unlab);
		fprintf(dump,"svm_unlabbound: %.32g\n",learn_parm.svm_unlabbound);
	}


	if (*ndocuments > 0) {

		if(kernel_parm.kernel_type == LINEAR) { /* don't need the cache */
			kernel_cache=NULL;
		}
		else {
			/* Always get a new kernel cache. It is not possible to use the
			same cache for two different training runs */
			kernel_cache=kernel_cache_init(*totdoc,learn_parm.kernel_cache_size);
		}

		if(verbosity>=1)
			printf(" --- Native C function: engaging the training process.\n"); fflush(stdout);

		if(learn_parm.type == CLASSIFICATION) {
			svm_learn_classification(docs,target,*totdoc,*totwords,&learn_parm,&kernel_parm,kernel_cache,_model,alpha_in);
		}
		else if(learn_parm.type == REGRESSION) {
			svm_learn_regression(docs,target,*totdoc,*totwords,&learn_parm,&kernel_parm,&kernel_cache,_model);
		}
		else if(learn_parm.type == RANKING) {
			svm_learn_ranking(docs,target,*totdoc,*totwords,&learn_parm,&kernel_parm,&kernel_cache,_model);
		}
		else if(learn_parm.type == OPTIMIZATION) {
			svm_learn_ranking(docs,target,*totdoc,*totwords,&learn_parm,&kernel_parm,&kernel_cache,_model);
		}
		if(verbosity>=1)
			printf(" --- Native C function: training has been done.\n"); fflush(stdout);

		if(_model->kernel_parm.kernel_type == 0) { /* linear kernel */
			/* compute weight vector */
			add_weight_vector_to_linear_model(_model);
		}

	} else {
		_model->supvec = (DOC **)my_malloc(sizeof(DOC *)*2);
		_model->alpha = (double *)my_malloc(sizeof(double)*2);
		_model->index = (long *)my_malloc(sizeof(long)*2);
		_model->at_upper_bound=0;
		_model->b=0;	       
		_model->supvec[0]=0;  /* element 0 reserved and empty for now */
		_model->alpha[0]=0;
		_model->lin_weights=NULL;
		_model->totwords=0;
		_model->totdoc=0;
		_model->kernel_parm=(kernel_parm);
		_model->sv_num=1;
		_model->loo_error=-1;
		_model->loo_recall=-1;
		_model->loo_precision=-1;
		_model->xa_error=-1;
		_model->xa_recall=-1;
		_model->xa_precision=-1;
	}

	if (verbosity>10) {
		fprintf(dump,"totwords: %ld\n",learn_parm.totwords);
		printf("|||||||||||||||||||||||||||||||||| z: %ld, totdoc: %ld\n",z,*totdoc);
	}

	// ---------------------- build the model -----------------------

	if (verbosity>10)
		write_model("model-jnisvmlib.dat",_model);

	// baue C-Struktur des SVMLight-Models in Java-Objekt um.
	if(verbosity>=1)
		printf(" --- Native C function: creating Java return type.\n"); fflush(stdout);
	jobject ret = buildModelData(env,obj,_model,JIDs); 
	if(verbosity>=1)
		printf(" --- Native C function: creating Java object has been done.\n"); fflush(stdout);


	// Uncomment the following when using Java-side classification only!
	// For native classification we need to remember all model-related parameters.

	//free(alpha_in);
	//free_model(_model,0);
	//for(i=0;i<*(totdoc);i++) 
		//free_example(docs[i],1);
	//free(docs);
	//free(target);
	//free(totdoc);
	//free(totwords);
	//free(ndocuments);

	return ret;
}

jobject buildModelData(JNIEnv *env, jobject obj, MODEL* model,JavaParamIDs* ids) {

	SVECTOR *v;
	long NUM_DOCS, NUM_FEAT, j;
	NUM_DOCS = model->sv_num;
	jobjectArray doks = NULL;

	if (NUM_DOCS > 1) {
		// Erstelle ein Java-Array vom Typ jnisvmlight/LabeledFeatureVector 
		doks = (*env)->NewObjectArray(env,(jsize) NUM_DOCS-1, ids->tDataCls, NULL);	
		if (doks == 0) {
			perror("perror: Can't create Java array of type jnisvmlight/LabeledFeatureVector!");
			(*env)->ExceptionDescribe(env); 
			exit(1);
		}

		long u;
		for (u=1; u<NUM_DOCS; u++) {
			// erzeuge fuer alle Dokumente ein neues Objekt vom Typ jnisvmlight/LabeledFeatureVector 
			jobject Data =(*env)->NewObject(env, ids->tDataCls, ids->ConstructorID_tDataCls);

			if (Data == NULL) {
				perror("perror: Can't create object of type  LabeledFeatureVector!");
				(*env)->ExceptionDescribe(env); 
			}

			// fuelle die lable-Membervariable mit zugehoerigem Wert
			jdouble label = (jdouble) model->alpha[u];
			(*env)->SetDoubleField(env, Data, ids->ID_double_label, label);

			// fuelle die factor-Membervariable mit zugehoerigem Wert
			jdouble factor = (jdouble) ((model->supvec[u])->fvec)->factor;
			//(*env)->SetDoubleField(env, Data, ids->ID_double_label, label);

			v = (model->supvec[u])->fvec;
			for (j=0; (v->words[j]).wnum; j++);
			NUM_FEAT = j; 

			//XXX: goes wrong, if NUM_FEAT is too big!!!!
			//jint size = (jint) NUM_FEAT;
			//(*env)->SetIntField(env, Data, ids->MemVarID_size, size);

			// Reserviere Speicherplatz fuer int/double Array zum Aufnehmen der Dokumentenfeatures
			int* intar = (int*) my_malloc(NUM_FEAT*sizeof(int));
			double* doublear = (double*) my_malloc(NUM_FEAT*sizeof(double));
			// Erstelle korrespondierende Java-Arrays
			jintArray dim = (*env)->NewIntArray(env,(jsize) NUM_FEAT);
			jdoubleArray val = (*env)->NewDoubleArray(env,(jsize) NUM_FEAT);

			if (dim && val == NULL) {
				perror("perror: Can't create jint- or jdoubleArrray! :");
				(*env)->ExceptionDescribe(env);
				exit(1);
			}

			for (j=0;j<NUM_FEAT;j++) {
				// Fuelle int/double Arrays mit zugehoerigen Werten
				intar[j] = (int)((v->words[j]).wnum);
				doublear[j] = (double) ((v->words[j]).weight);
				// Lege Referenz auf int/double Werte in Java-Array ab
				(*env)->SetIntArrayRegion(env, dim, (jsize) j, (jsize) 1, (jint*) &intar[j]);
				(*env)->SetDoubleArrayRegion(env, val, (jsize) j, (jsize) 1, (jdouble*) &doublear[j]);
			}

			// Lege Java-Arrays in Objekt des Typs jnisvmlight/LabeledFeatureVector ab
			(*env)->SetObjectField(env, Data, ids->ID_intArray_dimensions, dim);
			(*env)->SetObjectField(env, Data, ids->ID_doubleArray_values, val);
			// lege Objekt vom Type jnisvmlight/LabeledFeatureVector in jnisvmlight/LabeledFeatureVector-Array ab (an Position u-1)
			(*env)->SetObjectArrayElement(env, doks, (jsize) u-1, Data); 

			// ------------------------------------------------------------
			// XXX: TODO: check if i am allowed to free these arrays!!!
			free(intar);
			free(doublear);
			// ------------------------------------------------------------

		} 

		// Lege jnisvmlight/LabeledFeatureVector-Array in zugehoeriger Membervariable des Objekts vom Type SVMLightModel ab
		//  	(*env)->SetObjectField(env, SVMLightModel, ids->ID_labeledFeatureVectorArray_docs, doks);
	} else {
		fprintf(stderr,"-------------------------------------------------------------------------------\nThe number of suppert vecors (model->sv_num: %ld) is less than 2!\nThere must be at least 2 support vectors. Model can't be built.\n",model->sv_num);
		// 	(*env)->SetObjectField(env, SVMLightModel, ids->ID_labeledFeatureVectorArray_docs, NULL);

	}

	char* text = "SVM-light Version ";
	char* dummy = (char*) my_malloc(((int)strlen(VERSION)+strlen(text)+1)*sizeof(char));
	sprintf(dummy,"%s%s",text,VERSION); 

	// kreiere ein neues Objekt vom Typ SVMLightModel 
	jobject SVMLightModel =(*env)->NewObject(env, ids->SVMLightModelCls, ids->ConstructorID_SVMLightModelCls, 
		(*env)->NewStringUTF(env, dummy),
		(jlong) (model->kernel_parm.kernel_type),
		(jlong) (model->kernel_parm.poly_degree),
		(jdouble) (model->kernel_parm.rbf_gamma),
		(jdouble) (model->kernel_parm.coef_lin),
		(jdouble) (model->kernel_parm.coef_const),
		(*env)->NewStringUTF(env, (model->kernel_parm.custom)),
		(jlong) (model->totwords),
		(jlong) (model->totdoc),
		(jlong) (model->sv_num),
		(jdouble) (model->b),
		((model->sv_num > 1) ? doks : (jobjectArray) NULL)
		);

	if ( SVMLightModel == 0) {
		perror("perror: Can't create a new SVMLightModel-Object :");
		(*env)->ExceptionDescribe(env);
		exit(1);
	}

	if ( (model->kernel_parm.kernel_type) == 0 ) {
		jdoubleArray val = (*env)->NewDoubleArray(env,(jsize) model->totwords +1);
		for (j=0;j<model->totwords +1;j++) {
			(*env)->SetDoubleArrayRegion(env, val, (jsize) j, (jsize) 1, (jdouble*) &(model->lin_weights)[j]);
		}
		(*env)->SetObjectField(env, SVMLightModel, ids->ID_doubleArray_linWeights, val);
	} else {
		double nullpointer = 0;
		(*env)->SetObjectField(env, SVMLightModel, ids->ID_doubleArray_linWeights, &nullpointer);
	}

	// Belege restliche Membervariablen des Objekts vom Typ SVMLightModel mit zugehoerigen Werten
	//(*env)->SetObjectField(env, SVMLightModel, ids->ID_string_format, ((*env)->NewStringUTF(env, dummy)));
	free(dummy);

	//(*env)->SetLongField(env, SVMLightModel, ids->ID_long_kType, (jlong) (model->kernel_parm.kernel_type));
	//(*env)->SetLongField(env, SVMLightModel, ids->ID_long_dParam, (jlong) (model->kernel_parm.poly_degree));
	//(*env)->SetDoubleField(env, SVMLightModel, ids->ID_double_gParam, (jdouble) (model->kernel_parm.rbf_gamma));
	//(*env)->SetDoubleField(env, SVMLightModel, ids->ID_double_sParam, (jdouble)  (model->kernel_parm.coef_lin));
	//(*env)->SetDoubleField(env, SVMLightModel, ids->ID_double_rParam, (jdouble) (model->kernel_parm.coef_const) );
	//(*env)->SetObjectField(env, SVMLightModel, ids->ID_string_uParam, (*env)->NewStringUTF(env, (model->kernel_parm.custom)));
	//(*env)->SetLongField(env, SVMLightModel, ids->ID_long_highFeatIdx, (jlong) (model->totwords) );
	//(*env)->SetLongField(env, SVMLightModel, ids->ID_long_trainDocs, (jlong) (model->totdoc));
	//(*env)->SetLongField(env, SVMLightModel, ids->ID_long_numSupVecs, (jlong) (model->sv_num));
	//(*env)->SetDoubleField(env, SVMLightModel, ids->ID_double_threshold, (jdouble) (model->b));

	if(verbosity>=1)
		printf(" --- Native C function: classifier model created successfully.\n"); fflush(stdout);
	return SVMLightModel;
}

void createDOCs(JNIEnv * env,JavaParamIDs *JIDs,jobjectArray* tdata,DOC*** docs, double** target, long* totwords, long* totdoc, long* ndocuments) {

	jboolean ex = 0;
	*ndocuments = 0;
	*totwords = 0;
	*totdoc = (long) JIDs->tDataSize;

	long max_docs=(long) ((JIDs->tDataSize)+3);
	(*docs) = (DOC **)my_malloc(sizeof(DOC *)*max_docs);    /* feature vectors */
	(*target) = (double *)my_malloc(sizeof(double)*max_docs); /* target values */
	long max_words_doc = 10;
	WORD *words = (WORD *)my_malloc(sizeof(WORD)*(max_words_doc+10));


	int k=0;

	FILE *test = NULL;
	int doTest=0;

	if(verbosity>10) {
		doTest=1;
		if ((test = fopen ("jni-train.dat", "w")) == NULL) { perror ("Writing to \"jni-train.dat\" doesn't work.\n"); exit (1); }
	}

	for (k=0; k<JIDs->tDataSize; k++) {

		// Bestimme Referenz auf k-tes Trainingsdokument
		jobject traindoc = (*env)->GetObjectArrayElement(env, *tdata, k);
		if (traindoc == NULL) { 
			if (verbosity>2) {	
				// nexus: debugging ..
				printf("\n\n Debugging: ----------------------------------------------- empty document %d! \n\n",k);
			}
			(*totdoc)--;
			ex = (*env)->ExceptionCheck(env);
			if (ex) { 
				(*env)->ExceptionDescribe(env);
				(*env)->ExceptionClear(env);
			}
			continue; 
		}

		// Lese das Label des k-ten Trainingsdokumentes ein
		jdouble label = (*env)->GetDoubleField(env, traindoc, JIDs->ID_double_label); 
		//if ((label != 1.0) && (label != -1.0)) {
		//	perror("\n\nTraining data with wrong label!\n\n");
		//	exit(1);
		//}

		//if (doTest)
		//	fprintf(test,"%s%lf ",label); fflush(stdout); 

		// Bestimme Speicheradresse der Dimension/Wert-Arrays aus der 
		// Java-Klasse des k-ten Trainingsdokumentes
		jintArray dim = (*env)->GetObjectField(env, traindoc, JIDs->ID_intArray_dimensions); 
		jdoubleArray val = (*env)->GetObjectField(env, traindoc, JIDs->ID_doubleArray_values);
		if ( (dim && val)== 0) {
			perror("---------------------------------------------------------- Can't access Dim/Val-Arrays \n");
			exit(1);
		}

		// Bestimme die Groesse der Dimension/Wert-Arrays aus der 
		// Java-Klasse des k-ten Trainingsdokumentes
		jsize dimLen = (*env)->GetArrayLength(env, dim);
		jsize valLen = (*env)->GetArrayLength(env, val);
		if ((dimLen != valLen) || (dimLen == 0)) {
			perror("---------------------------------------------------------- array length is zero or arrays are of different size!\n");
			exit(1);
		}

		// Referenziere Elemente aus den Java-Arrays aus
		jint *dimEl = (*env)->GetIntArrayElements(env, dim, 0);
		jdouble *valEl = (*env)->GetDoubleArrayElements(env, val, 0);

		int* ds;
		double *vs;

		if (sizeof(int) == sizeof(jint)) {
			ds = (int*) dimEl;
		} else {
			int fi=0;
			printf("!!!!!!!!!!!!!!! Warning: java datatype \"jint\" isn't of the same size as C datatype \"int\"\n");
			ds = (int*) my_malloc(sizeof(int)*dimLen); 
			for(fi=0;fi<dimLen;fi++) {
				ds[fi] = (int) dimEl[fi];
			}
		}

		if (sizeof(double) == sizeof(jdouble)) {
			vs = (double*) valEl;
		} else {
			int fi=0;
			printf("!!!!!!!!!!!!!!! Warning: Java-Datatype (jdouble) isn't of the same size as C datatype");
			vs = (double*) my_malloc(sizeof(double)*valLen); 
			for(fi=0;fi<valLen;fi++) {
				vs[fi] = (double) valEl[fi];
			}
		}

		/*			int g=0;
		printf("%lf",label);
		for (g=0;g<dimLen;g++){
		printf(" %ld:%.16lf ",ds[g],vs[g]);
		}
		printf("\n%lf",label);
		for (g=0;g<dimLen;g++){
		printf(" %ld:%.16lf ",dimEl[g],valEl[g]);
		}

		exit(0); */

		// ------------------------------- fill DOCs --------------------------------------------

		if (dimLen>max_words_doc) {
			free(words);
			max_words_doc=dimLen;
			words = (WORD *)my_malloc(sizeof(WORD)*(dimLen+10));
		}

		// erstelle anhand der Werte aus label,Dimension- und Wert-Array eine Datenstruktur vom Typ DOC
		jinit_traindoc((double)label,docs,target,dimLen,totwords,totdoc, ds, vs, ndocuments, words, test);
		//jinit_traindoc((double)label,docs,target,dimLen,totwords,totdoc, ds, vs, ndocuments, words,NULL);
		(*ndocuments)++;

		// --------------------------------------------------------------------------------------

		// gebe Speicherplatz der Java-Arrays frei
		(*env)->ReleaseIntArrayElements(env,dim,dimEl,0);
		(*env)->ReleaseDoubleArrayElements(env,val,valEl,0);

	}

	free(words);
	if(verbosity>=1) {
		fprintf(stdout, "OK. (%ld examples read)\n", *ndocuments); fflush(stdout);
	}


	if (doTest)
		fclose(test);

}

void jinit_traindoc(double doc_label, DOC ***docs, double **label,
					long max_words_doc, long int *totwords, long int *totdoc, int* dims, 
					double *vals, long* ndocuments, WORD* words, FILE* test) 
{

	char comment[1] = {'\0'}; 
	long dnum=0,wpos,dpos=0,dneg=0,dunlab=0,queryid,slackid;
	double costfactor;

	dnum = *ndocuments;


	if(!jparse_document(words,&queryid,&slackid,&costfactor,
		&wpos,max_words_doc,dims,vals)) {
			perror("\nParsing error in line !\n");
			exit(1);
		}


		int iw=0;
		while(iw<max_words_doc) {
			if (test != NULL) fprintf(test,"%ld:%.32g ",words[iw].wnum,words[iw].weight); fflush(stdout);
			iw++;
		}
		if (test != NULL) fprintf(test,"\n"); fflush(stdout);

		(*label)[dnum]=doc_label;
		/* printf("docnum=%ld: Class=%f ",dnum,doc_label); fflush(stdout); */
		if(doc_label > 0) dpos++;
		if (doc_label < 0) dneg++;
		if (doc_label == 0) dunlab++;

		if((wpos>1) && ((words[wpos-2]).wnum>(*totwords))) 
			(*totwords)=(words[wpos-2]).wnum;
		if((*totwords) > MAXFEATNUM) {
			printf("\nMaximum feature number exceeds limit defined in MAXFEATNUM! (%ld>MAXFEATNUM:%ld)\n",*totwords,(long int)MAXFEATNUM);
			exit(1);
		}

		(*docs)[dnum] = create_example(dnum,queryid,slackid,costfactor,
			create_svector(words,comment,1.0));


		/* printf("\nNorm=%f\n",((*docs)[dnum]->fvec)->twonorm_sq); fflush(stdout);  */
		if(verbosity>=1) {
			if((dnum % 20) == 0) {
				printf("%ld..",dnum); fflush(stdout);
			}
			if (dnum == (*totdoc)-1) {
				printf("%ld\n",dnum); fflush(stdout);
			}
		}
}

int jparse_document(WORD* words, long *queryid, long *slackid, double *costfactor,  long int *numwords, long int max_words_doc, int *dims, double *vals) {
	register long wpos;
	long wnum;
	double weight;

	(*queryid)=0;
	(*slackid)=0;
	(*costfactor)=1;


	wpos=0;
	while(wpos<max_words_doc) {

		wnum = dims[wpos];
		weight = vals[wpos]; //(FVAL) vals[wpos];

		if(wnum<=0) { 
			perror ("Feature numbers must be larger or equal to 1!!!\n"); 
			exit (1); 
		}

		if((wpos>0) && ((words[wpos-1]).wnum >= wnum)) { 
			perror ("Features must be in increasing order!!!\n"); 
			exit(1);
		}

		(words[wpos]).wnum=wnum;
		(words[wpos]).weight=(FVAL)weight; 

		wpos++;
	}

	(words[wpos]).wnum=0;
	(*numwords)=wpos+1;

	return(1);
}

