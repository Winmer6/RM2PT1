package entities;

import services.impl.StandardOPs;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.time.LocalDate;
import java.io.Serializable;
import java.lang.reflect.Method;

public class BankClerk implements Serializable {
	
	/* all primary attributes */
	private int ClerkID;
	private String Name;
	private PostName Post;
	
	/* all references */
	private List<User> ManageUser = new LinkedList<User>(); 
	private List<BankCard> ManageBankCard = new LinkedList<BankCard>(); 
	
	/* all get and set functions */
	public int getClerkID() {
		return ClerkID;
	}	
	
	public void setClerkID(int clerkid) {
		this.ClerkID = clerkid;
	}
	public String getName() {
		return Name;
	}	
	
	public void setName(String name) {
		this.Name = name;
	}
	public PostName getPost() {
		return Post;
	}	
	
	public void setPost(PostName post) {
		this.Post = post;
	}
	
	/* all functions for reference*/
	public List<User> getManageUser() {
		return ManageUser;
	}	
	
	public void addManageUser(User user) {
		this.ManageUser.add(user);
	}
	
	public void deleteManageUser(User user) {
		this.ManageUser.remove(user);
	}
	public List<BankCard> getManageBankCard() {
		return ManageBankCard;
	}	
	
	public void addManageBankCard(BankCard bankcard) {
		this.ManageBankCard.add(bankcard);
	}
	
	public void deleteManageBankCard(BankCard bankcard) {
		this.ManageBankCard.remove(bankcard);
	}
	


}
