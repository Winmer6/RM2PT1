package entities;

import services.impl.StandardOPs;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.time.LocalDate;
import java.io.Serializable;
import java.lang.reflect.Method;

public class Receipt implements Serializable {
	
	/* all primary attributes */
	private LocalDate Time;
	private float OperationCount;
	private OperatopnName Operation;
	private float BeforeAmount;
	private float AfterAmount;
	private int CardID;
	private int UserID;
	
	/* all references */
	private ATM ReceiptFromATM; 
	
	/* all get and set functions */
	public LocalDate getTime() {
		return Time;
	}	
	
	public void setTime(LocalDate time) {
		this.Time = time;
	}
	public float getOperationCount() {
		return OperationCount;
	}	
	
	public void setOperationCount(float operationcount) {
		this.OperationCount = operationcount;
	}
	public OperatopnName getOperation() {
		return Operation;
	}	
	
	public void setOperation(OperatopnName operation) {
		this.Operation = operation;
	}
	public float getBeforeAmount() {
		return BeforeAmount;
	}	
	
	public void setBeforeAmount(float beforeamount) {
		this.BeforeAmount = beforeamount;
	}
	public float getAfterAmount() {
		return AfterAmount;
	}	
	
	public void setAfterAmount(float afteramount) {
		this.AfterAmount = afteramount;
	}
	public int getCardID() {
		return CardID;
	}	
	
	public void setCardID(int cardid) {
		this.CardID = cardid;
	}
	public int getUserID() {
		return UserID;
	}	
	
	public void setUserID(int userid) {
		this.UserID = userid;
	}
	
	/* all functions for reference*/
	public ATM getReceiptFromATM() {
		return ReceiptFromATM;
	}	
	
	public void setReceiptFromATM(ATM atm) {
		this.ReceiptFromATM = atm;
	}			
	


}
