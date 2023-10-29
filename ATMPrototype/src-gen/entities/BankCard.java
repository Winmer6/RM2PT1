package entities;

import services.impl.StandardOPs;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.time.LocalDate;
import java.io.Serializable;
import java.lang.reflect.Method;

public class BankCard implements Serializable {
	
	/* all primary attributes */
	private int CardID;
	private int Password;
	private float Balance;
	private CardStatus CardStatus;
	private CardCatalog Catalog;
	
	/* all references */
	private User BelongedUser; 
	private List<Transaction> Has = new LinkedList<Transaction>(); 
	private Bank BelongedToBank; 
	
	/* all get and set functions */
	public int getCardID() {
		return CardID;
	}	
	
	public void setCardID(int cardid) {
		this.CardID = cardid;
	}
	public int getPassword() {
		return Password;
	}	
	
	public void setPassword(int password) {
		this.Password = password;
	}
	public float getBalance() {
		return Balance;
	}	
	
	public void setBalance(float balance) {
		this.Balance = balance;
	}
	public CardStatus getCardStatus() {
		return CardStatus;
	}	
	
	public void setCardStatus(CardStatus cardstatus) {
		this.CardStatus = cardstatus;
	}
	public CardCatalog getCatalog() {
		return Catalog;
	}	
	
	public void setCatalog(CardCatalog catalog) {
		this.Catalog = catalog;
	}
	
	/* all functions for reference*/
	public User getBelongedUser() {
		return BelongedUser;
	}	
	
	public void setBelongedUser(User user) {
		this.BelongedUser = user;
	}			
	public List<Transaction> getHas() {
		return Has;
	}	
	
	public void addHas(Transaction transaction) {
		this.Has.add(transaction);
	}
	
	public void deleteHas(Transaction transaction) {
		this.Has.remove(transaction);
	}
	public Bank getBelongedToBank() {
		return BelongedToBank;
	}	
	
	public void setBelongedToBank(Bank bank) {
		this.BelongedToBank = bank;
	}			
	


}
