package services;

import entities.*;  
import java.util.List;
import java.time.LocalDate;


public interface ManageTransactionCRUDService {

	/* all system operations of the use case*/
	boolean createTransaction(String withdrewnum, String balanceafterwithdraw) throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean queryTransaction(String withdrewNum) throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean modifyTransaction(String withdrewnum, String balanceafterwithdraw) throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean deleteTransaction(String withdrewNum) throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	
	/* all get and set functions for temp property*/
	
	/* all get and set functions for temp property*/
	
	/* invariant checking function */
}
