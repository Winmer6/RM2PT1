package services;

import entities.*;  
import java.util.List;
import java.time.LocalDate;


public interface ManageBankCardCRUDService {

	/* all system operations of the use case*/
	boolean createBankCard(String cardid, String password, String balance, String cardstatus, String catalog) throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean queryBankCard(String cardID) throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean modifyBankCard(String cardid, String password, String balance, String cardstatus, String catalog) throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean deleteBankCard(String cardID) throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	
	/* all get and set functions for temp property*/
	
	/* all get and set functions for temp property*/
	
	/* invariant checking function */
}
