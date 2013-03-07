package org.zoneproject.extractor.twitterreader;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zoneproject.extractor.utils.Database;
import org.zoneproject.extractor.utils.Item;
import org.zoneproject.extractor.utils.ZoneOntology;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class TwitterApi {
    /**
     * Get all the items from twitter for an user timeline list
     * @param sources the list of sources URIs
     * @return the list of items
     */
    public static ArrayList<Item> getFlux(String[] sources) {
        ArrayList<Item> result = new ArrayList<Item>();
        TwitterFactory factory = new TwitterFactory();
        
        for(String sourceUri : sources){
            AccessToken userToken = TwitterApi.getAccessToken(sourceUri);
            Twitter twitter = factory.getInstance();
            twitter.setOAuthConsumer("gUJnCSOMPcgk8QEAekEqA", "RVNIXZx7fzgUr6tMEFh1tRtNtGUezCYhuKHZ3dnbc");
            twitter.setOAuthAccessToken(userToken);
            ResponseList<Status> status;
            try {
                status = twitter.getHomeTimeline();
                for(Status r :status){
                    result.add(TwitterApi.getItemFromStatus(r,sourceUri));
                }
            } catch (TwitterException ex) {
                Logger.getLogger(TwitterApi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }
    
    /**
     * create an item by his twitter Status
     * @param s the twitter Status
     * @param source the Uri of the source
     * @return the item created
     */
    private static Item getItemFromStatus(Status s, String source){
        return new Item(source, "https://twitter.com/"+s.getUser().getScreenName()+"/status/"+Long.toString(s.getId()), s.getText(), s.getText(), s.getCreatedAt());
    }
    
    /**
     * 
     * @param uri
     * @return 
     */
    private static AccessToken getAccessToken(String uri){
        ResultSet results = Database.getRelationsForURI(uri, ZoneOntology.GRAPH_SOURCES);
        String token=null, secret=null;
        while (results.hasNext()) {
            QuerySolution result = results.nextSolution();
            if (result.get("?relation").toString().equals(ZoneOntology.SOURCES_TWITTER_TOKEN)){
                token = result.get("?object").toString();
            }else if (result.get("?relation").toString().equals(ZoneOntology.SOURCES_TWITTER_TOKEN_SECRET)){
                secret = result.get("?object").toString();
            }
        }
        if(token == null || secret == null) {
            return null;
        }
        return new AccessToken(token, secret);
    }
    
    public static String [] getSources(){
        String query = "SELECT *  WHERE {?uri rdf:type <"+ZoneOntology.SOURCES_TWITTER_TYPE+">.}";
        System.out.println(query);
        ResultSet res = Database.runSPARQLRequest(query, ZoneOntology.GRAPH_SOURCES);
        ArrayList<String> sources = new ArrayList<String>();
        while (res.hasNext()) {
            QuerySolution r = res.nextSolution();
            sources.add(r.get("?uri").toString());
        }
        return sources.toArray(new String[sources.size()]);
    }
}
