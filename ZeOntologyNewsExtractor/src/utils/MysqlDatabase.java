package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class MysqlDatabase {
    
    private static Statement st = null;
    private static Connection con = null;
    
    public static Statement getStatement(){
        try {
            if(st == null){
                String url = "jdbc:mysql://localhost:3306/ZONE";
                String user = "ZONE";
                String password = "ZONE";
                con = DriverManager.getConnection(url, user, password);
                st = con.createStatement();
            }
            return st;
        } catch (SQLException ex) {
            Logger.getLogger(MysqlDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    private static String query(String query) {
        ResultSet rs = null;
        try {
            rs = MysqlDatabase.getStatement().executeQuery(query);
            if (rs.next()) {
                return rs.getString(1);
            }

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(MysqlDatabase.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(MysqlDatabase.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        return null;
    }

    private static void Exec(String query) {
        try {
            MysqlDatabase.getStatement().execute(query);

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(MysqlDatabase.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        }
    }
    public static void close(){
        try {
            if (st != null) {
                st.close();
            }
            if (con != null) {
                con.close();
            }
            con = null;
            st = null;

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(MysqlDatabase.class.getName());
            lgr.log(Level.WARNING, ex.getMessage(), ex);
        }        
    }
    private static void createItem(String type, String value,int inc){
        MysqlDatabase.Exec("INSERT INTO  `ZONE`.`items` (`type` ,`value` ,`inc`)VALUES ('"+type+"',  '"+value+"',  '"+inc+"')");
    }
    private static void createItem(String type, String value){
        int nbOcc = MysqlDatabase.getNbOccurences(type, value);
        if(nbOcc < 0 ){
            MysqlDatabase.createItem(type, value,1);
        }else{
            nbOcc++;
            MysqlDatabase.Exec("UPDATE  `ZONE`.`items` SET  `inc` =  '"+nbOcc+"' WHERE  `items`.`type` =  '"+type+"' AND  `items`.`value` =  '"+value+"';");
        }
    }
    private static int getNbOccurences(String type, String value){
        String res = MysqlDatabase.query("SELECT inc FROM `items` WHERE type='"+type+"' AND value='"+value+"'");
        if(res == null)return -1;
        return Integer.parseInt(res);
    }
    
    public static void createItem(Prop prop){
        if(!prop.isLiteral()){
            MysqlDatabase.createItem(prop.getType(), prop.getValue());
        }
    }
}
