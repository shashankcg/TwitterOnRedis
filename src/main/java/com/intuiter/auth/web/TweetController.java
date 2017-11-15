package com.intuiter.auth.web;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.intuiter.auth.model.Tweet;
import com.intuiter.auth.redis.RedisUtility;
import com.intuiter.auth.service.SecurityService;

/**
 * Base controller for handling tweets
 * Currently allows:
 * 	1. Posting a tweet
 *  2. Getting all tweets for the user.
 *  
 * @author shashank
 *
 */
@RestController
public class TweetController {

	@Autowired
	RedisUtility redisUtility;

	/**
	 * This method is used to write the tweet to the tweet index.
	 * It does the following:
	 * 	1. Gets the current username
	 *  2. Writes (tweetcontent, tweet owner, to the global tweet index
	 *  3. Writes the tweetID to the user timelines index 
	 *  	for all the current list of followers of the tweet owner
	 *   
	 * @param tweetContent
	 */
    @RequestMapping(value = "/tweet", method = RequestMethod.POST)
    public void postTweet(@RequestBody String tweetContent) {

    	System.out.println("tweetcontent: " + tweetContent);
    	LdapUserDetailsImpl user = (LdapUserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = user.getUsername();

    	//Write tweet to global tweet index
    	String tweetID = UUID.randomUUID().toString();
    	redisUtility.addTweet(userName, tweetID, tweetContent);

    	Set<String> followerIDs = redisUtility.getAllFollowersIDs(userName);

    	if(followerIDs != null){
    		for(String followerID: followerIDs){
    			redisUtility.addTweetIDToUserTimeline(followerID, tweetID);
    		}
    	}
    }

    /**
     * 1. Gets the current logged in username
     * 2. Gets the list of timeline TweetIDs from the timeline index for the user
     * 3. Writes the content of all the timeline tweets to the json Array.
     * 4. Returns the array
     * 
     * @return the timeline tweets as a JSONArray
     */
    @RequestMapping(value = "/feed/{fromIndex}/{toIndex}", 
			method = RequestMethod.GET, produces = { "application/json" })
    public String getFeed(@PathVariable int fromIndex, @PathVariable int toIndex) throws Exception {

    	LdapUserDetailsImpl user = (LdapUserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = user.getUsername();

        List<String> timelineTweetIDs = redisUtility.getTimelineTweetsIDsForUser(userName, fromIndex, toIndex);
        System.out.println("Timeline tweets size: " + timelineTweetIDs.size());

        JSONArray arrayOfTweets = new JSONArray();
        for(int i=0; i<timelineTweetIDs.size(); i++){
        	Map<String, String> tweetAttributes = redisUtility.getTweetAttributes(timelineTweetIDs.get(i));
        	
        	JSONObject json = new JSONObject();
        	json.put(Tweet.usernameName, tweetAttributes.get(Tweet.usernameName));
        	json.put(Tweet.tweetContentName, tweetAttributes.get(Tweet.tweetContentName));
        	arrayOfTweets.put(json);
        }
        System.out.println(arrayOfTweets);
        return arrayOfTweets.toString();
    }
}