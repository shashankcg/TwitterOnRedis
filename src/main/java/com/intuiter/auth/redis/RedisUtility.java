package com.intuiter.auth.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.intuiter.auth.model.Tweet;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

/**
 * 
 * Utility to handle operations on REDIS
 * 
 * @author shashank
 *
 */
@Component
public class RedisUtility {

	private static final int MAX_TIMEOUT_MILLIS = 5000;
	private static final int MAX_TWEETS_PER_TIMELINE = 99;
	private JedisPool pool;
	private static final String host = "127.0.0.1";
	private static final String LRU_SET = "LeastRecentlyUsed";
	private static final int USER_INDEX = 5;
	private static final int USER_FOLLOWER_INDEX = USER_INDEX + 1;
	private static final int GLOBAL_TWEET_INDEX = USER_FOLLOWER_INDEX + 1;
	private static final int USER_TIMELINE_INDEX = GLOBAL_TWEET_INDEX + 1;

	/**
	 * Prepares the RedisUtility bean
	 * 1. Creates a pool config()
	 * 2. Creates a jedis resrouce pool
	 * 3. Flushes all the elements.
	 */
	@PostConstruct
	public void init() {

		System.out.println("Trying to initialize jedis pool");
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxWait(MAX_TIMEOUT_MILLIS);
		pool = new JedisPool(config, host);
		System.out.println("Created jedis pool.. ");
		Jedis j = null;
		try{
			j = pool.getResource();
			j.flushAll();
		}
		finally{
			if(j!=null){
				pool.returnResource(j);
			}
		}
		System.out.println("Cleared redis db");
	}

	/**
	 * Adds a userID-follower mapping to the user-follower index.
	 * 
	 * @param userID
	 * @param follower
	 */
	public void addFollower(String userID, String follower){
		Jedis j = pool.getResource();
		try{
			j.select(USER_FOLLOWER_INDEX);
			j.sadd(userID, follower);
		}
		finally{
			if(j!=null){
				pool.returnResource(j); 
			}
		}
	}

	/**
	 * Returns the HASHSET of all the followers for a given user
	 * 
	 * @param userID
	 * @return followers
	 */
	public Set<String> getAllFollowersIDs(String userID){
		Jedis j = pool.getResource();
		try{
			j.select(USER_FOLLOWER_INDEX);
			Set<String> followers = j.smembers(userID);
			return followers;
		}
		finally{
			if(j!=null){
				pool.returnResource(j); 
			}
		}
	}

	/**
	 * Writes tweet to the global tweet index.
	 * 
	 * @param ownerID
	 * @param tweetID
	 * @param content
	 */
	public void addTweet(String ownerID, String tweetID, String content){
		Jedis j = pool.getResource();
		try{
			j.select(GLOBAL_TWEET_INDEX);
			Map<String, String> attributes = new HashMap<>();
			attributes.put(Tweet.usernameName, ownerID);
			attributes.put(Tweet.tweetContentName, content);


			Transaction multi = j.multi();
			multi.hmset(tweetID, attributes);
			multi.zadd(LRU_SET, System.currentTimeMillis(), tweetID);
			multi.exec();
		}
		finally{
			if(j!=null){
				pool.returnResource(j); 
			}
		}
	}

	/**
	 * This is used to post the tweet ID to the user_timeline of userID
	 * Performs a trim on the MAX tweets allowed per timeline.
	 * 
	 * @param userID
	 * @param tweetID
	 */
	public void addTweetIDToUserTimeline(String userID, String tweetID){
		Jedis j = pool.getResource();
		try{
			j.select(USER_TIMELINE_INDEX);

			j.lpush(userID, tweetID);
			j.ltrim(userID, 0, MAX_TWEETS_PER_TIMELINE);
		}
		finally{
			if(j!=null){
				pool.returnResource(j);
			}
		}
	}

	/**
	 * Returns the list of tweetIDs for a given users.
	 * Allows pagination by allowing the start and end indices. 
	 * 
	 * @param userID
	 * @param startIndex
	 * @param endIndex
	 * @return list of tweet IDs
	 */
	public List<String> getTimelineTweetsIDsForUser(String userID, int startIndex, int endIndex){
		Jedis j = pool.getResource();
		try{
			j.select(USER_TIMELINE_INDEX);
			List<String> tweetIDs = j.lrange(userID, 0, 99);
			return tweetIDs;
		}
		finally{
			if(j!=null){
				pool.returnResource(j);
			}
		}
	}

	/**
	 * Gets all the stored tweet attributes from 
	 * the global tweet index for a given tweetID
	 * 
	 * @param tweetID
	 * @returns a map of all the tweetAttribute:tweetValue combinations
	 */
	public Map<String, String> getTweetAttributes(String tweetID){
		Jedis j = pool.getResource();
		try{
			j.select(GLOBAL_TWEET_INDEX);
			Map<String, String> attributes = j.hgetAll(tweetID);
			return attributes;
		}
		finally{
			if(j!=null){
				pool.returnResource(j);
			}
		}
	}
}