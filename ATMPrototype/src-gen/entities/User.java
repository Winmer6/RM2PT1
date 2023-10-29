package entities;

import services.impl.StandardOPs;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.time.LocalDate;
import java.io.Serializable;
import java.lang.reflect.Method;

public class User implements Serializable {
	
	/* all primary attributes */
	private int UserID;
	private String Name;
	private String Address;
	
	/* all references */
	private List<BankCard> OwnedCard = new LinkedList<BankCard>(); 
	
	/* all get and set functions */
	public int getUserID() {
		return UserID;
	}	
	
	public void setUserID(int userid) {
		this.UserID = userid;
	}
	public String getName() {
		return Name;
	}	
	
	public void setName(String name) {
		this.Name = name;
	}
	public String getAddress() {
		return Address;
	}	
	
	public void setAddress(String address) {
		this.Address = address;
	}
	
	/* all functions for reference*/
	public List<BankCard> getOwnedCard() {
		return OwnedCard;
	}	
	
	public void addOwnedCard(BankCard bankcard) {
		this.OwnedCard.add(bankcard);
	}
	
	public void deleteOwnedCard(BankCard bankcard) {
		this.OwnedCard.remove(bankcard);
	}
	


}
