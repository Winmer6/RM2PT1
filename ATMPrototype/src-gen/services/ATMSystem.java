package services;

import entities.*;  
import java.util.List;
import java.time.LocalDate;


public interface ATMSystem {

	/* all system operations of the use case*/
	boolean inputPassword(String password) throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean withdrawCash(String quantity) throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean printReceipt() throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean ejectCard() throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean inputCard(String cardID) throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean depositFunds(String quantity) throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean changePassword() throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean checkBalance() throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean cardIdentification() throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean transferMoney(String cardID, String quantity) throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean checkBankStatement() throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean closeAccount() throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean clerkAuthorization(String clerkID, String authorizationCode) throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean checkLog(String fromTime, String toTime) throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean printLog() throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean checkCash() throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean printDetails() throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean clerkExit() throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean changeMode(String pin) throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean checkUser(String userID) throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	boolean checkCard(String cardID) throws PreconditionException, PostconditionException, ThirdPartyServiceException;
	
	/* all get and set functions for temp property*/
	
	
	/* invariant checking function */
}
