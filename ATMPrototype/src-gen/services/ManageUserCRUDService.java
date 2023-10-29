package services;

import entities.*;  
import java.util.List;
import java.time.LocalDate;


public interface ManageUserCRUDService {

	/* all system operations of the use case*/
	boolean createUser(String userid, String name, String address) throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean queryUser(String userID) throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean modifyUser(String userid, String name, String address) throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean deleteUser(String userID) throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	
	/* all get and set functions for temp property*/
	
	/* all get and set functions for temp property*/
	
	/* invariant checking function */
}
