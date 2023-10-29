package entities;

import services.impl.StandardOPs;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.time.LocalDate;
import java.io.Serializable;
import java.lang.reflect.Method;

public class Bank implements Serializable {
	
	/* all primary attributes */
	private String BankName;
	private String Address;
	private int StuffNum;
	
	/* all references */
	private List<ATM> OwnedATM = new LinkedList<ATM>(); 
	private List<BankCard> OwnedBankCard = new LinkedList<BankCard>(); 
	
	/* all get and set functions */
	public String getBankName() {
		return BankName;
	}	
	
	public void setBankName(String bankname) {
		this.BankName = bankname;
	}
	public String getAddress() {
		return Address;
	}	
	
	public void setAddress(String address) {
		this.Address = address;
	}
	public int getStuffNum() {
		return StuffNum;
	}	
	
	public void setStuffNum(int stuffnum) {
		this.StuffNum = stuffnum;
	}
	
	/* all functions for reference*/
	public List<ATM> getOwnedATM() {
		return OwnedATM;
	}	
	
	public void addOwnedATM(ATM atm) {
		this.OwnedATM.add(atm);
	}
	
	public void deleteOwnedATM(ATM atm) {
		this.OwnedATM.remove(atm);
	}
	public List<BankCard> getOwnedBankCard() {
		return OwnedBankCard;
	}	
	
	public void addOwnedBankCard(BankCard bankcard) {
		this.OwnedBankCard.add(bankcard);
	}
	
	public void deleteOwnedBankCard(BankCard bankcard) {
		this.OwnedBankCard.remove(bankcard);
	}
	


}
