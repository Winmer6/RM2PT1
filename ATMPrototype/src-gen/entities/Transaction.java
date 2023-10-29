package entities;

import services.impl.StandardOPs;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.time.LocalDate;
import java.io.Serializable;
import java.lang.reflect.Method;

public class Transaction implements Serializable {
	
	/* all primary attributes */
	private int WithdrewNum;
	private float BalanceAfterWithdraw;
	
	/* all references */
	private BankCard InvolvedCaed; 
	
	/* all get and set functions */
	public int getWithdrewNum() {
		return WithdrewNum;
	}	
	
	public void setWithdrewNum(int withdrewnum) {
		this.WithdrewNum = withdrewnum;
	}
	public float getBalanceAfterWithdraw() {
		return BalanceAfterWithdraw;
	}	
	
	public void setBalanceAfterWithdraw(float balanceafterwithdraw) {
		this.BalanceAfterWithdraw = balanceafterwithdraw;
	}
	
	/* all functions for reference*/
	public BankCard getInvolvedCaed() {
		return InvolvedCaed;
	}	
	
	public void setInvolvedCaed(BankCard bankcard) {
		this.InvolvedCaed = bankcard;
	}			
	


}
