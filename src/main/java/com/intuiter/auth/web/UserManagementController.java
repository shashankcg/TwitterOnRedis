package com.intuiter.auth.web;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.intuiter.auth.redis.RedisUtility;
import com.intuiter.auth.service.SecurityService;
import com.intuiter.auth.util.UnboundilUtil;
import com.unboundid.ldap.sdk.LDAPException;

/**
 * 
 * Controller for performing user and user-user related operations
 * 
 * @author shashank
 *
 */
@RestController
public class UserManagementController {

	@Autowired
	RedisUtility redisUtility;

	@Autowired
	SecurityService securityService;

	/**
	 * Gets the list of all the users from LDAP.
	 * Built temporarily for 
	 * @return
	 * @throws LDAPException
	 */
    @RequestMapping(value = "/getAllUsers", 
			method = RequestMethod.GET, produces = { "application/json" })
    public List<String> getAllUsers() throws LDAPException {
    	UnboundilUtil unboundutil = new UnboundilUtil();
    	return unboundutil.getAll();
    }

    /**
     * This method handles the user-user follow operation.
     * Prechecks if the user exists on LDAP. 
     * 
     * @param userNameToFollow
     * @throws Exception
     */
    @RequestMapping(value = "/follow", method = RequestMethod.POST)
    public void followUser(@RequestBody String userNameToFollow) throws Exception {

    	LdapUserDetailsImpl user = (LdapUserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = user.getUsername();

        UnboundilUtil unboundilUtil = new UnboundilUtil();
    	if(unboundilUtil.getUser(userNameToFollow) == null){
    		System.out.println("No user with the username found");
    		throw new Exception("Bad user!");
    	}
    	else{
    		redisUtility.addFollower(userNameToFollow, userName);
    		System.out.println("User: " + userName + " now follows: " + userNameToFollow);
    	}
    }

    /**
     * Gets the list of all followers for the authorized user.
     * @return <List> of usernames.
     */
    @RequestMapping(value = "/getAllFollowers", 
			method = RequestMethod.GET, produces = { "application/json" })
    public List<String> getAllFollowers() {

    	LdapUserDetailsImpl user = (LdapUserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = user.getUsername();

        
        Set<String> followerSet = redisUtility.getAllFollowersIDs(userName);
        if(followerSet != null){
        	return new ArrayList<String>(followerSet);
        }
        else{
        	return new ArrayList<String>();
        }
    }
    
}