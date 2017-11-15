package com.intuiter.auth.util;

import java.util.List;
import java.util.ArrayList;
import com.intuiter.auth.model.User;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

/**
 * Manages connections and search requests to/from the LDAP server
 * 
 * @author shashank
 *
 */
public class UnboundilUtil {

	private static final String DOMAIN_NAME = "dc=springframework,dc=org";
	private static final int LDAP_PORT_NUMBER = 8389;
	private static final String HOSTNAME = "localhost";
	private static final String CN = "cn";
	private static final String UID = "uid";

	private static Filter filterAll = Filter
			.createEqualityFilter("objectclass", "person")
			.createEqualityFilter("objectclass", "top")
			.createPresenceFilter(UID);

	/**
	 * Gets the full list of users from LDAP server 
	 * 
	 * @return List of usernames
	 * @throws LDAPException
	 */
	public List<String> getAll() throws LDAPException {

		List<String> userIDs = null;


		SearchRequest searchRequest = new SearchRequest(
				DOMAIN_NAME, SearchScope.SUB, filterAll, UID);
		SearchResult searchResult;
		LDAPConnection connection = null;
		try{
			searchResult = getLDAPConnection().search(searchRequest);
			userIDs = new ArrayList<String>();
			for (SearchResultEntry entry : searchResult.getSearchEntries()) {
				String userid = entry.getAttributeValue(UID);
				userIDs.add(userid);
			}
		}
		catch (LDAPSearchException lse) {
			String errorMessageFromServer = lse.getDiagnosticMessage();
			System.out.println(errorMessageFromServer);
		}
		finally{
			closeLDAPConnection(connection);
		}
		return userIDs;
	}

	/**
	 * Retrieves the user from the LDAP server based on the userID
	 * 
	 * @param userID
	 * @return User object
	 * 
	 * @throws LDAPException
	 */
	public User getUser(String userID) throws LDAPException{

		User user = null;
		Filter filter = Filter.createEqualityFilter("objectclass", "person")
				.createEqualityFilter("objectclass", "top")
				.createPresenceFilter(UID).createEqualityFilter(UID, userID);

		SearchRequest searchRequest = new SearchRequest(
				DOMAIN_NAME, SearchScope.SUB, filter , UID, CN);
		SearchResult searchResult;

		LDAPConnection connection = null;
		try{
			connection =  getLDAPConnection();
			searchResult = connection.search(searchRequest);

			if(searchResult != null && searchResult.getSearchEntries().size() > 0){
				user = new User();
				user.setUsername(searchResult.getSearchEntries().get(0).getAttributeValue(UID));
				user.setCName(searchResult.getSearchEntries().get(0).getAttributeValue(CN));
			}
		} catch (LDAPSearchException lse) {
			String errorMessageFromServer = lse.getDiagnosticMessage();
			System.out.println(errorMessageFromServer);
		}
		finally{
			closeLDAPConnection(connection);
		}
		return user;
	}

	/**
	 * Gets a connection to the LDAP server
	 * 
	 * @return connection
	 * @throws LDAPException
	 */
	private LDAPConnection getLDAPConnection() throws LDAPException{
		return new LDAPConnection(HOSTNAME, LDAP_PORT_NUMBER);
	}

	/**
	 * Closes the LDAP connection
	 * 
	 * @param connection
	 */
	private void closeLDAPConnection(LDAPConnection connection) {
		if(connection != null){
			connection.close();
		}
	}

}
