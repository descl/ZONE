/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zoneproject.extractor;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author cdesclau
 */
public class ThreadApp extends TimerTask{
    String app;
    public ThreadApp(String app) {
        this.app = app;
    }
    public void run(){
        System.out.println("Starting annotation process for "+app);
        try {
            Class.forName(app).newInstance();
        } catch (InstantiationException ex) {
            Logger.getLogger(ThreadApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ThreadApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ThreadApp.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
}
