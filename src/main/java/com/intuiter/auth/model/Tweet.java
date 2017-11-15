package com.intuiter.auth.model;

public class Tweet {

	private String userName;
	private String tweetContent;

	public static final String usernameName = "userName";
	public static final String tweetContentName = "tweetContent";
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getTweetContent() {
		return tweetContent;
	}
	public void setTweetContent(String tweetContent) {
		this.tweetContent = tweetContent;
	}
}
