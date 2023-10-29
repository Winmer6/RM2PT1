package entities;

import services.impl.StandardOPs;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.time.LocalDate;
import java.io.Serializable;
import java.lang.reflect.Method;

public class ATM implements Serializable {
	
	/* all primary attributes */
	private int ATMid;
	private String Address;
	
	/* all references */
	private Bank BelongedtoBank; 
	private Receipt ATMproduceReceipt; 
	
	/* all get and set functions */
	public int getATMid() {
		return ATMid;
	}	
	
	public void setATMid(int atmid) {
		this.ATMid = atmid;
	}
	public String getAddress() {
		return Address;
	}	
	
	public void setAddress(String address) {
		this.Address = address;
	}
	
	/* all functions for reference*/
	public Bank getBelongedtoBank() {
		return BelongedtoBank;
	}	
	
	public void setBelongedtoBank(Bank bank) {
		this.BelongedtoBank = bank;
	}			
	public Receipt getATMproduceReceipt() {
		return ATMproduceReceipt;
	}	
	
	public void setATMproduceReceipt(Receipt receipt) {
		this.ATMproduceReceipt = receipt;
	}			
	


}
