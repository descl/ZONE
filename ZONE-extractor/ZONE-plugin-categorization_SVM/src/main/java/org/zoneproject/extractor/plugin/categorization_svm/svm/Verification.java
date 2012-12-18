package org.zoneproject.extractor.plugin.categorization_svm.svm;

/*
 * #%L
 * ZONE-plugin-categorization_SVM
 * %%
 * Copyright (C) 2012 ZONE-project
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import java.io.File;



public class Verification {
	
	public static int countTextInDirectory (File dir){
		 
		    File [] filesToClass = dir.listFiles();
		    
		  int nbOfTexts = filesToClass.length;
		return nbOfTexts;
		
	}
	public static int countTextCorrectInDirectory (File dirRef, File dirClas)
	{
		 File [] filesRef = dirRef.listFiles();
		 File [] filesClas = dirClas.listFiles();
		 int nbOfCorrectText = 0;
		 for(File f : filesClas){
			 
			 for(File f1 : filesRef){
				 
				 if(f1.getName().equals(f.getName())){
					 
					nbOfCorrectText++;
					break;
				 }
			    	
			    }
		    	
		    }
		return  nbOfCorrectText;
	}
	
	public static double computeRecall (int nbTextCorrect, int nbTextTot){
		
		return (double)nbTextCorrect/nbTextTot;
		
	}
	public static double computePrecision(int nbTextCorrect, int nbTextInCat){
		
		return (double)nbTextCorrect/nbTextInCat;
}
	

	
}
