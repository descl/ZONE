package org.zoneproject.extractor.utils;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.ResultSet;
import java.util.concurrent.Callable;

public class ThreadExec implements Callable<ResultSet> {
   QueryExecution q;

   ThreadExec(QueryExecution q) {
      this.q = q;
   }

   public ResultSet call() throws Exception {
       return q.execSelect();
   }

}