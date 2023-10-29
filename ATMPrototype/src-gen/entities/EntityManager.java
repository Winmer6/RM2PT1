package entities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.lang.reflect.Method;
import java.util.Map;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.File;

public class EntityManager {

	private static Map<String, List> AllInstance = new HashMap<String, List>();
	
	private static List<BankCard> BankCardInstances = new LinkedList<BankCard>();
	private static List<User> UserInstances = new LinkedList<User>();
	private static List<Transaction> TransactionInstances = new LinkedList<Transaction>();
	private static List<BankClerk> BankClerkInstances = new LinkedList<BankClerk>();
	private static List<Receipt> ReceiptInstances = new LinkedList<Receipt>();
	private static List<ATM> ATMInstances = new LinkedList<ATM>();
	private static List<Bank> BankInstances = new LinkedList<Bank>();

	
	/* Put instances list into Map */
	static {
		AllInstance.put("BankCard", BankCardInstances);
		AllInstance.put("User", UserInstances);
		AllInstance.put("Transaction", TransactionInstances);
		AllInstance.put("BankClerk", BankClerkInstances);
		AllInstance.put("Receipt", ReceiptInstances);
		AllInstance.put("ATM", ATMInstances);
		AllInstance.put("Bank", BankInstances);
	} 
		
	/* Save State */
	public static void save(File file) {
		
		try {
			
			ObjectOutputStream stateSave = new ObjectOutputStream(new FileOutputStream(file));
			
			stateSave.writeObject(BankCardInstances);
			stateSave.writeObject(UserInstances);
			stateSave.writeObject(TransactionInstances);
			stateSave.writeObject(BankClerkInstances);
			stateSave.writeObject(ReceiptInstances);
			stateSave.writeObject(ATMInstances);
			stateSave.writeObject(BankInstances);
			
			stateSave.close();
					
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/* Load State */
	public static void load(File file) {
		
		try {
			
			ObjectInputStream stateLoad = new ObjectInputStream(new FileInputStream(file));
			
			try {
				
				BankCardInstances =  (List<BankCard>) stateLoad.readObject();
				AllInstance.put("BankCard", BankCardInstances);
				UserInstances =  (List<User>) stateLoad.readObject();
				AllInstance.put("User", UserInstances);
				TransactionInstances =  (List<Transaction>) stateLoad.readObject();
				AllInstance.put("Transaction", TransactionInstances);
				BankClerkInstances =  (List<BankClerk>) stateLoad.readObject();
				AllInstance.put("BankClerk", BankClerkInstances);
				ReceiptInstances =  (List<Receipt>) stateLoad.readObject();
				AllInstance.put("Receipt", ReceiptInstances);
				ATMInstances =  (List<ATM>) stateLoad.readObject();
				AllInstance.put("ATM", ATMInstances);
				BankInstances =  (List<Bank>) stateLoad.readObject();
				AllInstance.put("Bank", BankInstances);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
		
	/* create object */  
	public static Object createObject(String Classifer) {
		try
		{
			Class c = Class.forName("entities.EntityManager");
			Method createObjectMethod = c.getDeclaredMethod("create" + Classifer + "Object");
			return createObjectMethod.invoke(c);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/* add object */  
	public static Object addObject(String Classifer, Object ob) {
		try
		{
			Class c = Class.forName("entities.EntityManager");
			Method addObjectMethod = c.getDeclaredMethod("add" + Classifer + "Object", Class.forName("entities." + Classifer));
			return  (boolean) addObjectMethod.invoke(c, ob);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}	
	
	/* add objects */  
	public static Object addObjects(String Classifer, List obs) {
		try
		{
			Class c = Class.forName("entities.EntityManager");
			Method addObjectsMethod = c.getDeclaredMethod("add" + Classifer + "Objects", Class.forName("java.util.List"));
			return  (boolean) addObjectsMethod.invoke(c, obs);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/* Release object */
	public static boolean deleteObject(String Classifer, Object ob) {
		try
		{
			Class c = Class.forName("entities.EntityManager");
			Method deleteObjectMethod = c.getDeclaredMethod("delete" + Classifer + "Object", Class.forName("entities." + Classifer));
			return  (boolean) deleteObjectMethod.invoke(c, ob);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/* Release objects */
	public static boolean deleteObjects(String Classifer, List obs) {
		try
		{
			Class c = Class.forName("entities.EntityManager");
			Method deleteObjectMethod = c.getDeclaredMethod("delete" + Classifer + "Objects", Class.forName("java.util.List"));
			return  (boolean) deleteObjectMethod.invoke(c, obs);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}		 	
	
	 /* Get all objects belongs to same class */
	public static List getAllInstancesOf(String ClassName) {
			 return AllInstance.get(ClassName);
	}	

   /* Sub-create object */
	public static BankCard createBankCardObject() {
		BankCard o = new BankCard();
		return o;
	}
	
	public static boolean addBankCardObject(BankCard o) {
		return BankCardInstances.add(o);
	}
	
	public static boolean addBankCardObjects(List<BankCard> os) {
		return BankCardInstances.addAll(os);
	}
	
	public static boolean deleteBankCardObject(BankCard o) {
		return BankCardInstances.remove(o);
	}
	
	public static boolean deleteBankCardObjects(List<BankCard> os) {
		return BankCardInstances.removeAll(os);
	}
	public static User createUserObject() {
		User o = new User();
		return o;
	}
	
	public static boolean addUserObject(User o) {
		return UserInstances.add(o);
	}
	
	public static boolean addUserObjects(List<User> os) {
		return UserInstances.addAll(os);
	}
	
	public static boolean deleteUserObject(User o) {
		return UserInstances.remove(o);
	}
	
	public static boolean deleteUserObjects(List<User> os) {
		return UserInstances.removeAll(os);
	}
	public static Transaction createTransactionObject() {
		Transaction o = new Transaction();
		return o;
	}
	
	public static boolean addTransactionObject(Transaction o) {
		return TransactionInstances.add(o);
	}
	
	public static boolean addTransactionObjects(List<Transaction> os) {
		return TransactionInstances.addAll(os);
	}
	
	public static boolean deleteTransactionObject(Transaction o) {
		return TransactionInstances.remove(o);
	}
	
	public static boolean deleteTransactionObjects(List<Transaction> os) {
		return TransactionInstances.removeAll(os);
	}
	public static BankClerk createBankClerkObject() {
		BankClerk o = new BankClerk();
		return o;
	}
	
	public static boolean addBankClerkObject(BankClerk o) {
		return BankClerkInstances.add(o);
	}
	
	public static boolean addBankClerkObjects(List<BankClerk> os) {
		return BankClerkInstances.addAll(os);
	}
	
	public static boolean deleteBankClerkObject(BankClerk o) {
		return BankClerkInstances.remove(o);
	}
	
	public static boolean deleteBankClerkObjects(List<BankClerk> os) {
		return BankClerkInstances.removeAll(os);
	}
	public static Receipt createReceiptObject() {
		Receipt o = new Receipt();
		return o;
	}
	
	public static boolean addReceiptObject(Receipt o) {
		return ReceiptInstances.add(o);
	}
	
	public static boolean addReceiptObjects(List<Receipt> os) {
		return ReceiptInstances.addAll(os);
	}
	
	public static boolean deleteReceiptObject(Receipt o) {
		return ReceiptInstances.remove(o);
	}
	
	public static boolean deleteReceiptObjects(List<Receipt> os) {
		return ReceiptInstances.removeAll(os);
	}
	public static ATM createATMObject() {
		ATM o = new ATM();
		return o;
	}
	
	public static boolean addATMObject(ATM o) {
		return ATMInstances.add(o);
	}
	
	public static boolean addATMObjects(List<ATM> os) {
		return ATMInstances.addAll(os);
	}
	
	public static boolean deleteATMObject(ATM o) {
		return ATMInstances.remove(o);
	}
	
	public static boolean deleteATMObjects(List<ATM> os) {
		return ATMInstances.removeAll(os);
	}
	public static Bank createBankObject() {
		Bank o = new Bank();
		return o;
	}
	
	public static boolean addBankObject(Bank o) {
		return BankInstances.add(o);
	}
	
	public static boolean addBankObjects(List<Bank> os) {
		return BankInstances.addAll(os);
	}
	
	public static boolean deleteBankObject(Bank o) {
		return BankInstances.remove(o);
	}
	
	public static boolean deleteBankObjects(List<Bank> os) {
		return BankInstances.removeAll(os);
	}
  
}

