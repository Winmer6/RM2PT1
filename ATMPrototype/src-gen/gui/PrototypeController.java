package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TabPane;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.Tooltip;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import java.io.File;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;
import java.time.LocalDate;
import java.util.LinkedList;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import gui.supportclass.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.util.Callback;
import services.*;
import services.impl.*;
import java.time.format.DateTimeFormatter;
import java.lang.reflect.Method;

import entities.*;

public class PrototypeController implements Initializable {


	DateTimeFormatter dateformatter;
	 
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	
		atmsystem_service = ServiceManager.createATMSystem();
		thirdpartyservices_service = ServiceManager.createThirdPartyServices();
		managebankcardcrudservice_service = ServiceManager.createManageBankCardCRUDService();
		manageusercrudservice_service = ServiceManager.createManageUserCRUDService();
		managetransactioncrudservice_service = ServiceManager.createManageTransactionCRUDService();
				
		this.dateformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
	   	 //prepare data for contract
	   	 prepareData();
	   	 
	   	 //generate invariant panel
	   	 genereateInvairantPanel();
	   	 
		 //Actor Threeview Binding
		 actorTreeViewBinding();
		 
		 //Generate
		 generatOperationPane();
		 genereateOpInvariantPanel();
		 
		 //prilimariry data
		 try {
			DataFitService.fit();
		 } catch (PreconditionException e) {
			// TODO Auto-generated catch block
		 	e.printStackTrace();
		 }
		 
		 //generate class statistic
		 classStatisicBingding();
		 
		 //generate object statistic
		 generateObjectTable();
		 
		 //genereate association statistic
		 associationStatisicBingding();

		 //set listener 
		 setListeners();
	}
	
	/**
	 * deepCopyforTreeItem (Actor Generation)
	 */
	TreeItem<String> deepCopyTree(TreeItem<String> item) {
		    TreeItem<String> copy = new TreeItem<String>(item.getValue());
		    for (TreeItem<String> child : item.getChildren()) {
		        copy.getChildren().add(deepCopyTree(child));
		    }
		    return copy;
	}
	
	/**
	 * check all invariant and update invariant panel
	 */
	public void invairantPanelUpdate() {
		
		try {
			
			for (Entry<String, Label> inv : entity_invariants_label_map.entrySet()) {
				String invname = inv.getKey();
				String[] invt = invname.split("_");
				String entityName = invt[0];
				for (Object o : EntityManager.getAllInstancesOf(entityName)) {				
					 Method m = o.getClass().getMethod(invname);
					 if ((boolean)m.invoke(o) == false) {
						 inv.getValue().setStyle("-fx-max-width: Infinity;" + 
									"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #af0c27 100%);" +
								    "-fx-padding: 6px;" +
								    "-fx-border-color: black;");
						 break;
					 }
				}				
			}
			
			for (Entry<String, Label> inv : service_invariants_label_map.entrySet()) {
				String invname = inv.getKey();
				String[] invt = invname.split("_");
				String serviceName = invt[0];
				for (Object o : ServiceManager.getAllInstancesOf(serviceName)) {				
					 Method m = o.getClass().getMethod(invname);
					 if ((boolean)m.invoke(o) == false) {
						 inv.getValue().setStyle("-fx-max-width: Infinity;" + 
									"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #af0c27 100%);" +
								    "-fx-padding: 6px;" +
								    "-fx-border-color: black;");
						 break;
					 }
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

	/**
	 * check op invariant and update op invariant panel
	 */		
	public void opInvairantPanelUpdate() {
		
		try {
			
			for (Entry<String, Label> inv : op_entity_invariants_label_map.entrySet()) {
				String invname = inv.getKey();
				String[] invt = invname.split("_");
				String entityName = invt[0];
				for (Object o : EntityManager.getAllInstancesOf(entityName)) {
					 Method m = o.getClass().getMethod(invname);
					 if ((boolean)m.invoke(o) == false) {
						 inv.getValue().setStyle("-fx-max-width: Infinity;" + 
									"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #af0c27 100%);" +
								    "-fx-padding: 6px;" +
								    "-fx-border-color: black;");
						 break;
					 }
				}
			}
			
			for (Entry<String, Label> inv : op_service_invariants_label_map.entrySet()) {
				String invname = inv.getKey();
				String[] invt = invname.split("_");
				String serviceName = invt[0];
				for (Object o : ServiceManager.getAllInstancesOf(serviceName)) {
					 Method m = o.getClass().getMethod(invname);
					 if ((boolean)m.invoke(o) == false) {
						 inv.getValue().setStyle("-fx-max-width: Infinity;" + 
									"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #af0c27 100%);" +
								    "-fx-padding: 6px;" +
								    "-fx-border-color: black;");
						 break;
					 }
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* 
	*	generate op invariant panel 
	*/
	public void genereateOpInvariantPanel() {
		
		opInvariantPanel = new HashMap<String, VBox>();
		op_entity_invariants_label_map = new LinkedHashMap<String, Label>();
		op_service_invariants_label_map = new LinkedHashMap<String, Label>();
		
		VBox v;
		List<String> entities;
		v = new VBox();
		
		//entities invariants
		entities = ATMSystemImpl.opINVRelatedEntity.get("ejectCard");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("ejectCard" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ATMSystem")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("ejectCard", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ATMSystemImpl.opINVRelatedEntity.get("printReceipt");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("printReceipt" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ATMSystem")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("printReceipt", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ATMSystemImpl.opINVRelatedEntity.get("withdrawCash");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("withdrawCash" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ATMSystem")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("withdrawCash", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ATMSystemImpl.opINVRelatedEntity.get("inputPassword");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("inputPassword" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ATMSystem")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("inputPassword", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ATMSystemImpl.opINVRelatedEntity.get("inputCard");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("inputCard" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ATMSystem")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("inputCard", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ATMSystemImpl.opINVRelatedEntity.get("depositFunds");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("depositFunds" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ATMSystem")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("depositFunds", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ATMSystemImpl.opINVRelatedEntity.get("changePassword");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("changePassword" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ATMSystem")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("changePassword", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ATMSystemImpl.opINVRelatedEntity.get("checkBalance");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("checkBalance" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ATMSystem")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("checkBalance", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ATMSystemImpl.opINVRelatedEntity.get("cardIdentification");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("cardIdentification" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ATMSystem")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("cardIdentification", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ManageTransactionCRUDServiceImpl.opINVRelatedEntity.get("createTransaction");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("createTransaction" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ManageTransactionCRUDService")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("createTransaction", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ManageTransactionCRUDServiceImpl.opINVRelatedEntity.get("queryTransaction");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("queryTransaction" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ManageTransactionCRUDService")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("queryTransaction", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ManageTransactionCRUDServiceImpl.opINVRelatedEntity.get("modifyTransaction");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("modifyTransaction" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ManageTransactionCRUDService")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("modifyTransaction", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ManageTransactionCRUDServiceImpl.opINVRelatedEntity.get("deleteTransaction");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("deleteTransaction" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ManageTransactionCRUDService")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("deleteTransaction", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ManageUserCRUDServiceImpl.opINVRelatedEntity.get("createUser");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("createUser" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ManageUserCRUDService")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("createUser", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ManageUserCRUDServiceImpl.opINVRelatedEntity.get("queryUser");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("queryUser" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ManageUserCRUDService")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("queryUser", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ManageUserCRUDServiceImpl.opINVRelatedEntity.get("modifyUser");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("modifyUser" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ManageUserCRUDService")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("modifyUser", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ManageUserCRUDServiceImpl.opINVRelatedEntity.get("deleteUser");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("deleteUser" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ManageUserCRUDService")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("deleteUser", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ManageBankCardCRUDServiceImpl.opINVRelatedEntity.get("createBankCard");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("createBankCard" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ManageBankCardCRUDService")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("createBankCard", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ManageBankCardCRUDServiceImpl.opINVRelatedEntity.get("queryBankCard");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("queryBankCard" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ManageBankCardCRUDService")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("queryBankCard", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ManageBankCardCRUDServiceImpl.opINVRelatedEntity.get("modifyBankCard");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("modifyBankCard" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ManageBankCardCRUDService")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("modifyBankCard", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ManageBankCardCRUDServiceImpl.opINVRelatedEntity.get("deleteBankCard");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("deleteBankCard" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ManageBankCardCRUDService")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("deleteBankCard", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ATMSystemImpl.opINVRelatedEntity.get("transferMoney");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("transferMoney" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ATMSystem")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("transferMoney", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ATMSystemImpl.opINVRelatedEntity.get("checkBankStatement");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("checkBankStatement" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ATMSystem")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("checkBankStatement", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ATMSystemImpl.opINVRelatedEntity.get("closeAccount");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("closeAccount" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ATMSystem")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("closeAccount", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ATMSystemImpl.opINVRelatedEntity.get("clerkAuthorization");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("clerkAuthorization" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ATMSystem")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("clerkAuthorization", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ATMSystemImpl.opINVRelatedEntity.get("checkLog");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("checkLog" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ATMSystem")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("checkLog", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ATMSystemImpl.opINVRelatedEntity.get("printLog");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("printLog" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ATMSystem")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("printLog", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ATMSystemImpl.opINVRelatedEntity.get("checkCash");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("checkCash" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ATMSystem")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("checkCash", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ATMSystemImpl.opINVRelatedEntity.get("printDetails");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("printDetails" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ATMSystem")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("printDetails", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ATMSystemImpl.opINVRelatedEntity.get("clerkExit");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("clerkExit" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ATMSystem")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("clerkExit", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ATMSystemImpl.opINVRelatedEntity.get("changeMode");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("changeMode" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ATMSystem")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("changeMode", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ATMSystemImpl.opINVRelatedEntity.get("checkUser");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("checkUser" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ATMSystem")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("checkUser", v);
		
		v = new VBox();
		
		//entities invariants
		entities = ATMSystemImpl.opINVRelatedEntity.get("checkCard");
		if (entities != null) {
			for (String opRelatedEntities : entities) {
				for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
					
					String invname = inv.getKey();
					String[] invt = invname.split("_");
					String entityName = invt[0];
		
					if (opRelatedEntities.equals(entityName)) {
						Label l = new Label(invname);
						l.setStyle("-fx-max-width: Infinity;" + 
								"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
							    "-fx-padding: 6px;" +
							    "-fx-border-color: black;");
						Tooltip tp = new Tooltip();
						tp.setText(inv.getValue());
						l.setTooltip(tp);
						
						op_entity_invariants_label_map.put(invname, l);
						
						v.getChildren().add(l);
					}
				}
			}
		} else {
			Label l = new Label("checkCard" + " is no related invariants");
			l.setPadding(new Insets(8, 8, 8, 8));
			v.getChildren().add(l);
		}
		
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			String invname = inv.getKey();
			String[] invt = invname.split("_");
			String serviceName = invt[0];
			
			if (serviceName.equals("ATMSystem")) {
				Label l = new Label(invname);
				l.setStyle("-fx-max-width: Infinity;" + 
						   "-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
						   "-fx-padding: 6px;" +
						   "-fx-border-color: black;");
				Tooltip tp = new Tooltip();
				tp.setText(inv.getValue());
				l.setTooltip(tp);
				
				op_entity_invariants_label_map.put(invname, l);
				
				v.getChildren().add(l);
			}
		}
		opInvariantPanel.put("checkCard", v);
		
		
	}
	
	
	/*
	*  generate invariant panel
	*/
	public void genereateInvairantPanel() {
		
		service_invariants_label_map = new LinkedHashMap<String, Label>();
		entity_invariants_label_map = new LinkedHashMap<String, Label>();
		
		//entity_invariants_map
		VBox v = new VBox();
		//service invariants
		for (Entry<String, String> inv : service_invariants_map.entrySet()) {
			
			Label l = new Label(inv.getKey());
			l.setStyle("-fx-max-width: Infinity;" + 
					"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
				    "-fx-padding: 6px;" +
				    "-fx-border-color: black;");
			
			Tooltip tp = new Tooltip();
			tp.setText(inv.getValue());
			l.setTooltip(tp);
			
			service_invariants_label_map.put(inv.getKey(), l);
			v.getChildren().add(l);
			
		}
		//entity invariants
		for (Entry<String, String> inv : entity_invariants_map.entrySet()) {
			
			String INVname = inv.getKey();
			Label l = new Label(INVname);
			if (INVname.contains("AssociationInvariants")) {
				l.setStyle("-fx-max-width: Infinity;" + 
					"-fx-background-color: linear-gradient(to right, #099b17 0%, #F0FFFF 100%);" +
				    "-fx-padding: 6px;" +
				    "-fx-border-color: black;");
			} else {
				l.setStyle("-fx-max-width: Infinity;" + 
									"-fx-background-color: linear-gradient(to right, #7FFF00 0%, #F0FFFF 100%);" +
								    "-fx-padding: 6px;" +
								    "-fx-border-color: black;");
			}	
			Tooltip tp = new Tooltip();
			tp.setText(inv.getValue());
			l.setTooltip(tp);
			
			entity_invariants_label_map.put(inv.getKey(), l);
			v.getChildren().add(l);
			
		}
		ScrollPane scrollPane = new ScrollPane(v);
		scrollPane.setFitToWidth(true);
		all_invariant_pane.setMaxHeight(850);
		
		all_invariant_pane.setContent(scrollPane);
	}	
	
	
	
	/* 
	*	mainPane add listener
	*/
	public void setListeners() {
		 mainPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
			 
			 	if (newTab.getText().equals("System State")) {
			 		System.out.println("refresh all");
			 		refreshAll();
			 	}
		    
		    });
	}
	
	
	//checking all invariants
	public void checkAllInvariants() {
		
		invairantPanelUpdate();
	
	}	
	
	//refresh all
	public void refreshAll() {
		
		invairantPanelUpdate();
		classStatisticUpdate();
		generateObjectTable();
	}
	
	
	//update association
	public void updateAssociation(String className) {
		
		for (AssociationInfo assoc : allassociationData.get(className)) {
			assoc.computeAssociationNumber();
		}
		
	}
	
	public void updateAssociation(String className, int index) {
		
		for (AssociationInfo assoc : allassociationData.get(className)) {
			assoc.computeAssociationNumber(index);
		}
		
	}	
	
	public void generateObjectTable() {
		
		allObjectTables = new LinkedHashMap<String, TableView>();
		
		TableView<Map<String, String>> tableBankCard = new TableView<Map<String, String>>();

		//super entity attribute column
						
		//attributes table column
		TableColumn<Map<String, String>, String> tableBankCard_CardID = new TableColumn<Map<String, String>, String>("CardID");
		tableBankCard_CardID.setMinWidth("CardID".length()*10);
		tableBankCard_CardID.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("CardID"));
		    }
		});	
		tableBankCard.getColumns().add(tableBankCard_CardID);
		TableColumn<Map<String, String>, String> tableBankCard_Password = new TableColumn<Map<String, String>, String>("Password");
		tableBankCard_Password.setMinWidth("Password".length()*10);
		tableBankCard_Password.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("Password"));
		    }
		});	
		tableBankCard.getColumns().add(tableBankCard_Password);
		TableColumn<Map<String, String>, String> tableBankCard_Balance = new TableColumn<Map<String, String>, String>("Balance");
		tableBankCard_Balance.setMinWidth("Balance".length()*10);
		tableBankCard_Balance.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("Balance"));
		    }
		});	
		tableBankCard.getColumns().add(tableBankCard_Balance);
		TableColumn<Map<String, String>, String> tableBankCard_CardStatus = new TableColumn<Map<String, String>, String>("CardStatus");
		tableBankCard_CardStatus.setMinWidth("CardStatus".length()*10);
		tableBankCard_CardStatus.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("CardStatus"));
		    }
		});	
		tableBankCard.getColumns().add(tableBankCard_CardStatus);
		TableColumn<Map<String, String>, String> tableBankCard_Catalog = new TableColumn<Map<String, String>, String>("Catalog");
		tableBankCard_Catalog.setMinWidth("Catalog".length()*10);
		tableBankCard_Catalog.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("Catalog"));
		    }
		});	
		tableBankCard.getColumns().add(tableBankCard_Catalog);
		
		//table data
		ObservableList<Map<String, String>> dataBankCard = FXCollections.observableArrayList();
		List<BankCard> rsBankCard = EntityManager.getAllInstancesOf("BankCard");
		for (BankCard r : rsBankCard) {
			//table entry
			Map<String, String> unit = new HashMap<String, String>();
			
			unit.put("CardID", String.valueOf(r.getCardID()));
			unit.put("Password", String.valueOf(r.getPassword()));
			unit.put("Balance", String.valueOf(r.getBalance()));
			unit.put("CardStatus", String.valueOf(r.getCardStatus()));
			unit.put("Catalog", String.valueOf(r.getCatalog()));

			dataBankCard.add(unit);
		}
		
		tableBankCard.getSelectionModel().selectedIndexProperty().addListener(
							 (observable, oldValue, newValue) ->  { 
							 										 //get selected index
							 										 objectindex = tableBankCard.getSelectionModel().getSelectedIndex();
							 			 				 			 System.out.println("select: " + objectindex);

							 			 				 			 //update association object information
							 			 				 			 if (objectindex != -1)
										 			 					 updateAssociation("BankCard", objectindex);
							 			 				 			 
							 			 				 		  });
		
		tableBankCard.setItems(dataBankCard);
		allObjectTables.put("BankCard", tableBankCard);
		
		TableView<Map<String, String>> tableUser = new TableView<Map<String, String>>();

		//super entity attribute column
						
		//attributes table column
		TableColumn<Map<String, String>, String> tableUser_UserID = new TableColumn<Map<String, String>, String>("UserID");
		tableUser_UserID.setMinWidth("UserID".length()*10);
		tableUser_UserID.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("UserID"));
		    }
		});	
		tableUser.getColumns().add(tableUser_UserID);
		TableColumn<Map<String, String>, String> tableUser_Name = new TableColumn<Map<String, String>, String>("Name");
		tableUser_Name.setMinWidth("Name".length()*10);
		tableUser_Name.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("Name"));
		    }
		});	
		tableUser.getColumns().add(tableUser_Name);
		TableColumn<Map<String, String>, String> tableUser_Address = new TableColumn<Map<String, String>, String>("Address");
		tableUser_Address.setMinWidth("Address".length()*10);
		tableUser_Address.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("Address"));
		    }
		});	
		tableUser.getColumns().add(tableUser_Address);
		
		//table data
		ObservableList<Map<String, String>> dataUser = FXCollections.observableArrayList();
		List<User> rsUser = EntityManager.getAllInstancesOf("User");
		for (User r : rsUser) {
			//table entry
			Map<String, String> unit = new HashMap<String, String>();
			
			unit.put("UserID", String.valueOf(r.getUserID()));
			if (r.getName() != null)
				unit.put("Name", String.valueOf(r.getName()));
			else
				unit.put("Name", "");
			if (r.getAddress() != null)
				unit.put("Address", String.valueOf(r.getAddress()));
			else
				unit.put("Address", "");

			dataUser.add(unit);
		}
		
		tableUser.getSelectionModel().selectedIndexProperty().addListener(
							 (observable, oldValue, newValue) ->  { 
							 										 //get selected index
							 										 objectindex = tableUser.getSelectionModel().getSelectedIndex();
							 			 				 			 System.out.println("select: " + objectindex);

							 			 				 			 //update association object information
							 			 				 			 if (objectindex != -1)
										 			 					 updateAssociation("User", objectindex);
							 			 				 			 
							 			 				 		  });
		
		tableUser.setItems(dataUser);
		allObjectTables.put("User", tableUser);
		
		TableView<Map<String, String>> tableTransaction = new TableView<Map<String, String>>();

		//super entity attribute column
						
		//attributes table column
		TableColumn<Map<String, String>, String> tableTransaction_WithdrewNum = new TableColumn<Map<String, String>, String>("WithdrewNum");
		tableTransaction_WithdrewNum.setMinWidth("WithdrewNum".length()*10);
		tableTransaction_WithdrewNum.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("WithdrewNum"));
		    }
		});	
		tableTransaction.getColumns().add(tableTransaction_WithdrewNum);
		TableColumn<Map<String, String>, String> tableTransaction_BalanceAfterWithdraw = new TableColumn<Map<String, String>, String>("BalanceAfterWithdraw");
		tableTransaction_BalanceAfterWithdraw.setMinWidth("BalanceAfterWithdraw".length()*10);
		tableTransaction_BalanceAfterWithdraw.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("BalanceAfterWithdraw"));
		    }
		});	
		tableTransaction.getColumns().add(tableTransaction_BalanceAfterWithdraw);
		
		//table data
		ObservableList<Map<String, String>> dataTransaction = FXCollections.observableArrayList();
		List<Transaction> rsTransaction = EntityManager.getAllInstancesOf("Transaction");
		for (Transaction r : rsTransaction) {
			//table entry
			Map<String, String> unit = new HashMap<String, String>();
			
			unit.put("WithdrewNum", String.valueOf(r.getWithdrewNum()));
			unit.put("BalanceAfterWithdraw", String.valueOf(r.getBalanceAfterWithdraw()));

			dataTransaction.add(unit);
		}
		
		tableTransaction.getSelectionModel().selectedIndexProperty().addListener(
							 (observable, oldValue, newValue) ->  { 
							 										 //get selected index
							 										 objectindex = tableTransaction.getSelectionModel().getSelectedIndex();
							 			 				 			 System.out.println("select: " + objectindex);

							 			 				 			 //update association object information
							 			 				 			 if (objectindex != -1)
										 			 					 updateAssociation("Transaction", objectindex);
							 			 				 			 
							 			 				 		  });
		
		tableTransaction.setItems(dataTransaction);
		allObjectTables.put("Transaction", tableTransaction);
		
		TableView<Map<String, String>> tableBankClerk = new TableView<Map<String, String>>();

		//super entity attribute column
						
		//attributes table column
		TableColumn<Map<String, String>, String> tableBankClerk_ClerkID = new TableColumn<Map<String, String>, String>("ClerkID");
		tableBankClerk_ClerkID.setMinWidth("ClerkID".length()*10);
		tableBankClerk_ClerkID.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("ClerkID"));
		    }
		});	
		tableBankClerk.getColumns().add(tableBankClerk_ClerkID);
		TableColumn<Map<String, String>, String> tableBankClerk_Name = new TableColumn<Map<String, String>, String>("Name");
		tableBankClerk_Name.setMinWidth("Name".length()*10);
		tableBankClerk_Name.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("Name"));
		    }
		});	
		tableBankClerk.getColumns().add(tableBankClerk_Name);
		TableColumn<Map<String, String>, String> tableBankClerk_Post = new TableColumn<Map<String, String>, String>("Post");
		tableBankClerk_Post.setMinWidth("Post".length()*10);
		tableBankClerk_Post.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("Post"));
		    }
		});	
		tableBankClerk.getColumns().add(tableBankClerk_Post);
		
		//table data
		ObservableList<Map<String, String>> dataBankClerk = FXCollections.observableArrayList();
		List<BankClerk> rsBankClerk = EntityManager.getAllInstancesOf("BankClerk");
		for (BankClerk r : rsBankClerk) {
			//table entry
			Map<String, String> unit = new HashMap<String, String>();
			
			unit.put("ClerkID", String.valueOf(r.getClerkID()));
			if (r.getName() != null)
				unit.put("Name", String.valueOf(r.getName()));
			else
				unit.put("Name", "");
			unit.put("Post", String.valueOf(r.getPost()));

			dataBankClerk.add(unit);
		}
		
		tableBankClerk.getSelectionModel().selectedIndexProperty().addListener(
							 (observable, oldValue, newValue) ->  { 
							 										 //get selected index
							 										 objectindex = tableBankClerk.getSelectionModel().getSelectedIndex();
							 			 				 			 System.out.println("select: " + objectindex);

							 			 				 			 //update association object information
							 			 				 			 if (objectindex != -1)
										 			 					 updateAssociation("BankClerk", objectindex);
							 			 				 			 
							 			 				 		  });
		
		tableBankClerk.setItems(dataBankClerk);
		allObjectTables.put("BankClerk", tableBankClerk);
		
		TableView<Map<String, String>> tableReceipt = new TableView<Map<String, String>>();

		//super entity attribute column
						
		//attributes table column
		TableColumn<Map<String, String>, String> tableReceipt_Time = new TableColumn<Map<String, String>, String>("Time");
		tableReceipt_Time.setMinWidth("Time".length()*10);
		tableReceipt_Time.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("Time"));
		    }
		});	
		tableReceipt.getColumns().add(tableReceipt_Time);
		TableColumn<Map<String, String>, String> tableReceipt_OperationCount = new TableColumn<Map<String, String>, String>("OperationCount");
		tableReceipt_OperationCount.setMinWidth("OperationCount".length()*10);
		tableReceipt_OperationCount.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("OperationCount"));
		    }
		});	
		tableReceipt.getColumns().add(tableReceipt_OperationCount);
		TableColumn<Map<String, String>, String> tableReceipt_Operation = new TableColumn<Map<String, String>, String>("Operation");
		tableReceipt_Operation.setMinWidth("Operation".length()*10);
		tableReceipt_Operation.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("Operation"));
		    }
		});	
		tableReceipt.getColumns().add(tableReceipt_Operation);
		TableColumn<Map<String, String>, String> tableReceipt_BeforeAmount = new TableColumn<Map<String, String>, String>("BeforeAmount");
		tableReceipt_BeforeAmount.setMinWidth("BeforeAmount".length()*10);
		tableReceipt_BeforeAmount.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("BeforeAmount"));
		    }
		});	
		tableReceipt.getColumns().add(tableReceipt_BeforeAmount);
		TableColumn<Map<String, String>, String> tableReceipt_AfterAmount = new TableColumn<Map<String, String>, String>("AfterAmount");
		tableReceipt_AfterAmount.setMinWidth("AfterAmount".length()*10);
		tableReceipt_AfterAmount.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("AfterAmount"));
		    }
		});	
		tableReceipt.getColumns().add(tableReceipt_AfterAmount);
		TableColumn<Map<String, String>, String> tableReceipt_CardID = new TableColumn<Map<String, String>, String>("CardID");
		tableReceipt_CardID.setMinWidth("CardID".length()*10);
		tableReceipt_CardID.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("CardID"));
		    }
		});	
		tableReceipt.getColumns().add(tableReceipt_CardID);
		TableColumn<Map<String, String>, String> tableReceipt_UserID = new TableColumn<Map<String, String>, String>("UserID");
		tableReceipt_UserID.setMinWidth("UserID".length()*10);
		tableReceipt_UserID.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("UserID"));
		    }
		});	
		tableReceipt.getColumns().add(tableReceipt_UserID);
		
		//table data
		ObservableList<Map<String, String>> dataReceipt = FXCollections.observableArrayList();
		List<Receipt> rsReceipt = EntityManager.getAllInstancesOf("Receipt");
		for (Receipt r : rsReceipt) {
			//table entry
			Map<String, String> unit = new HashMap<String, String>();
			
			if (r.getTime() != null)
				unit.put("Time", r.getTime().format(dateformatter));
			else
				unit.put("Time", "");
			unit.put("OperationCount", String.valueOf(r.getOperationCount()));
			unit.put("Operation", String.valueOf(r.getOperation()));
			unit.put("BeforeAmount", String.valueOf(r.getBeforeAmount()));
			unit.put("AfterAmount", String.valueOf(r.getAfterAmount()));
			unit.put("CardID", String.valueOf(r.getCardID()));
			unit.put("UserID", String.valueOf(r.getUserID()));

			dataReceipt.add(unit);
		}
		
		tableReceipt.getSelectionModel().selectedIndexProperty().addListener(
							 (observable, oldValue, newValue) ->  { 
							 										 //get selected index
							 										 objectindex = tableReceipt.getSelectionModel().getSelectedIndex();
							 			 				 			 System.out.println("select: " + objectindex);

							 			 				 			 //update association object information
							 			 				 			 if (objectindex != -1)
										 			 					 updateAssociation("Receipt", objectindex);
							 			 				 			 
							 			 				 		  });
		
		tableReceipt.setItems(dataReceipt);
		allObjectTables.put("Receipt", tableReceipt);
		
		TableView<Map<String, String>> tableATM = new TableView<Map<String, String>>();

		//super entity attribute column
						
		//attributes table column
		TableColumn<Map<String, String>, String> tableATM_ATMid = new TableColumn<Map<String, String>, String>("ATMid");
		tableATM_ATMid.setMinWidth("ATMid".length()*10);
		tableATM_ATMid.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("ATMid"));
		    }
		});	
		tableATM.getColumns().add(tableATM_ATMid);
		TableColumn<Map<String, String>, String> tableATM_Address = new TableColumn<Map<String, String>, String>("Address");
		tableATM_Address.setMinWidth("Address".length()*10);
		tableATM_Address.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("Address"));
		    }
		});	
		tableATM.getColumns().add(tableATM_Address);
		
		//table data
		ObservableList<Map<String, String>> dataATM = FXCollections.observableArrayList();
		List<ATM> rsATM = EntityManager.getAllInstancesOf("ATM");
		for (ATM r : rsATM) {
			//table entry
			Map<String, String> unit = new HashMap<String, String>();
			
			unit.put("ATMid", String.valueOf(r.getATMid()));
			if (r.getAddress() != null)
				unit.put("Address", String.valueOf(r.getAddress()));
			else
				unit.put("Address", "");

			dataATM.add(unit);
		}
		
		tableATM.getSelectionModel().selectedIndexProperty().addListener(
							 (observable, oldValue, newValue) ->  { 
							 										 //get selected index
							 										 objectindex = tableATM.getSelectionModel().getSelectedIndex();
							 			 				 			 System.out.println("select: " + objectindex);

							 			 				 			 //update association object information
							 			 				 			 if (objectindex != -1)
										 			 					 updateAssociation("ATM", objectindex);
							 			 				 			 
							 			 				 		  });
		
		tableATM.setItems(dataATM);
		allObjectTables.put("ATM", tableATM);
		
		TableView<Map<String, String>> tableBank = new TableView<Map<String, String>>();

		//super entity attribute column
						
		//attributes table column
		TableColumn<Map<String, String>, String> tableBank_BankName = new TableColumn<Map<String, String>, String>("BankName");
		tableBank_BankName.setMinWidth("BankName".length()*10);
		tableBank_BankName.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("BankName"));
		    }
		});	
		tableBank.getColumns().add(tableBank_BankName);
		TableColumn<Map<String, String>, String> tableBank_Address = new TableColumn<Map<String, String>, String>("Address");
		tableBank_Address.setMinWidth("Address".length()*10);
		tableBank_Address.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("Address"));
		    }
		});	
		tableBank.getColumns().add(tableBank_Address);
		TableColumn<Map<String, String>, String> tableBank_StuffNum = new TableColumn<Map<String, String>, String>("StuffNum");
		tableBank_StuffNum.setMinWidth("StuffNum".length()*10);
		tableBank_StuffNum.setCellValueFactory(new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {	   
			@Override
		    public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> data) {
		        return new ReadOnlyStringWrapper(data.getValue().get("StuffNum"));
		    }
		});	
		tableBank.getColumns().add(tableBank_StuffNum);
		
		//table data
		ObservableList<Map<String, String>> dataBank = FXCollections.observableArrayList();
		List<Bank> rsBank = EntityManager.getAllInstancesOf("Bank");
		for (Bank r : rsBank) {
			//table entry
			Map<String, String> unit = new HashMap<String, String>();
			
			if (r.getBankName() != null)
				unit.put("BankName", String.valueOf(r.getBankName()));
			else
				unit.put("BankName", "");
			if (r.getAddress() != null)
				unit.put("Address", String.valueOf(r.getAddress()));
			else
				unit.put("Address", "");
			unit.put("StuffNum", String.valueOf(r.getStuffNum()));

			dataBank.add(unit);
		}
		
		tableBank.getSelectionModel().selectedIndexProperty().addListener(
							 (observable, oldValue, newValue) ->  { 
							 										 //get selected index
							 										 objectindex = tableBank.getSelectionModel().getSelectedIndex();
							 			 				 			 System.out.println("select: " + objectindex);

							 			 				 			 //update association object information
							 			 				 			 if (objectindex != -1)
										 			 					 updateAssociation("Bank", objectindex);
							 			 				 			 
							 			 				 		  });
		
		tableBank.setItems(dataBank);
		allObjectTables.put("Bank", tableBank);
		

		
	}
	
	/* 
	* update all object tables with sub dataset
	*/ 
	public void updateBankCardTable(List<BankCard> rsBankCard) {
			ObservableList<Map<String, String>> dataBankCard = FXCollections.observableArrayList();
			for (BankCard r : rsBankCard) {
				Map<String, String> unit = new HashMap<String, String>();
				
				
				unit.put("CardID", String.valueOf(r.getCardID()));
				unit.put("Password", String.valueOf(r.getPassword()));
				unit.put("Balance", String.valueOf(r.getBalance()));
				unit.put("CardStatus", String.valueOf(r.getCardStatus()));
				unit.put("Catalog", String.valueOf(r.getCatalog()));
				dataBankCard.add(unit);
			}
			
			allObjectTables.get("BankCard").setItems(dataBankCard);
	}
	public void updateUserTable(List<User> rsUser) {
			ObservableList<Map<String, String>> dataUser = FXCollections.observableArrayList();
			for (User r : rsUser) {
				Map<String, String> unit = new HashMap<String, String>();
				
				
				unit.put("UserID", String.valueOf(r.getUserID()));
				if (r.getName() != null)
					unit.put("Name", String.valueOf(r.getName()));
				else
					unit.put("Name", "");
				if (r.getAddress() != null)
					unit.put("Address", String.valueOf(r.getAddress()));
				else
					unit.put("Address", "");
				dataUser.add(unit);
			}
			
			allObjectTables.get("User").setItems(dataUser);
	}
	public void updateTransactionTable(List<Transaction> rsTransaction) {
			ObservableList<Map<String, String>> dataTransaction = FXCollections.observableArrayList();
			for (Transaction r : rsTransaction) {
				Map<String, String> unit = new HashMap<String, String>();
				
				
				unit.put("WithdrewNum", String.valueOf(r.getWithdrewNum()));
				unit.put("BalanceAfterWithdraw", String.valueOf(r.getBalanceAfterWithdraw()));
				dataTransaction.add(unit);
			}
			
			allObjectTables.get("Transaction").setItems(dataTransaction);
	}
	public void updateBankClerkTable(List<BankClerk> rsBankClerk) {
			ObservableList<Map<String, String>> dataBankClerk = FXCollections.observableArrayList();
			for (BankClerk r : rsBankClerk) {
				Map<String, String> unit = new HashMap<String, String>();
				
				
				unit.put("ClerkID", String.valueOf(r.getClerkID()));
				if (r.getName() != null)
					unit.put("Name", String.valueOf(r.getName()));
				else
					unit.put("Name", "");
				unit.put("Post", String.valueOf(r.getPost()));
				dataBankClerk.add(unit);
			}
			
			allObjectTables.get("BankClerk").setItems(dataBankClerk);
	}
	public void updateReceiptTable(List<Receipt> rsReceipt) {
			ObservableList<Map<String, String>> dataReceipt = FXCollections.observableArrayList();
			for (Receipt r : rsReceipt) {
				Map<String, String> unit = new HashMap<String, String>();
				
				
				if (r.getTime() != null)
					unit.put("Time", r.getTime().format(dateformatter));
				else
					unit.put("Time", "");
				unit.put("OperationCount", String.valueOf(r.getOperationCount()));
				unit.put("Operation", String.valueOf(r.getOperation()));
				unit.put("BeforeAmount", String.valueOf(r.getBeforeAmount()));
				unit.put("AfterAmount", String.valueOf(r.getAfterAmount()));
				unit.put("CardID", String.valueOf(r.getCardID()));
				unit.put("UserID", String.valueOf(r.getUserID()));
				dataReceipt.add(unit);
			}
			
			allObjectTables.get("Receipt").setItems(dataReceipt);
	}
	public void updateATMTable(List<ATM> rsATM) {
			ObservableList<Map<String, String>> dataATM = FXCollections.observableArrayList();
			for (ATM r : rsATM) {
				Map<String, String> unit = new HashMap<String, String>();
				
				
				unit.put("ATMid", String.valueOf(r.getATMid()));
				if (r.getAddress() != null)
					unit.put("Address", String.valueOf(r.getAddress()));
				else
					unit.put("Address", "");
				dataATM.add(unit);
			}
			
			allObjectTables.get("ATM").setItems(dataATM);
	}
	public void updateBankTable(List<Bank> rsBank) {
			ObservableList<Map<String, String>> dataBank = FXCollections.observableArrayList();
			for (Bank r : rsBank) {
				Map<String, String> unit = new HashMap<String, String>();
				
				
				if (r.getBankName() != null)
					unit.put("BankName", String.valueOf(r.getBankName()));
				else
					unit.put("BankName", "");
				if (r.getAddress() != null)
					unit.put("Address", String.valueOf(r.getAddress()));
				else
					unit.put("Address", "");
				unit.put("StuffNum", String.valueOf(r.getStuffNum()));
				dataBank.add(unit);
			}
			
			allObjectTables.get("Bank").setItems(dataBank);
	}
	
	/* 
	* update all object tables
	*/ 
	public void updateBankCardTable() {
			ObservableList<Map<String, String>> dataBankCard = FXCollections.observableArrayList();
			List<BankCard> rsBankCard = EntityManager.getAllInstancesOf("BankCard");
			for (BankCard r : rsBankCard) {
				Map<String, String> unit = new HashMap<String, String>();


				unit.put("CardID", String.valueOf(r.getCardID()));
				unit.put("Password", String.valueOf(r.getPassword()));
				unit.put("Balance", String.valueOf(r.getBalance()));
				unit.put("CardStatus", String.valueOf(r.getCardStatus()));
				unit.put("Catalog", String.valueOf(r.getCatalog()));
				dataBankCard.add(unit);
			}
			
			allObjectTables.get("BankCard").setItems(dataBankCard);
	}
	public void updateUserTable() {
			ObservableList<Map<String, String>> dataUser = FXCollections.observableArrayList();
			List<User> rsUser = EntityManager.getAllInstancesOf("User");
			for (User r : rsUser) {
				Map<String, String> unit = new HashMap<String, String>();


				unit.put("UserID", String.valueOf(r.getUserID()));
				if (r.getName() != null)
					unit.put("Name", String.valueOf(r.getName()));
				else
					unit.put("Name", "");
				if (r.getAddress() != null)
					unit.put("Address", String.valueOf(r.getAddress()));
				else
					unit.put("Address", "");
				dataUser.add(unit);
			}
			
			allObjectTables.get("User").setItems(dataUser);
	}
	public void updateTransactionTable() {
			ObservableList<Map<String, String>> dataTransaction = FXCollections.observableArrayList();
			List<Transaction> rsTransaction = EntityManager.getAllInstancesOf("Transaction");
			for (Transaction r : rsTransaction) {
				Map<String, String> unit = new HashMap<String, String>();


				unit.put("WithdrewNum", String.valueOf(r.getWithdrewNum()));
				unit.put("BalanceAfterWithdraw", String.valueOf(r.getBalanceAfterWithdraw()));
				dataTransaction.add(unit);
			}
			
			allObjectTables.get("Transaction").setItems(dataTransaction);
	}
	public void updateBankClerkTable() {
			ObservableList<Map<String, String>> dataBankClerk = FXCollections.observableArrayList();
			List<BankClerk> rsBankClerk = EntityManager.getAllInstancesOf("BankClerk");
			for (BankClerk r : rsBankClerk) {
				Map<String, String> unit = new HashMap<String, String>();


				unit.put("ClerkID", String.valueOf(r.getClerkID()));
				if (r.getName() != null)
					unit.put("Name", String.valueOf(r.getName()));
				else
					unit.put("Name", "");
				unit.put("Post", String.valueOf(r.getPost()));
				dataBankClerk.add(unit);
			}
			
			allObjectTables.get("BankClerk").setItems(dataBankClerk);
	}
	public void updateReceiptTable() {
			ObservableList<Map<String, String>> dataReceipt = FXCollections.observableArrayList();
			List<Receipt> rsReceipt = EntityManager.getAllInstancesOf("Receipt");
			for (Receipt r : rsReceipt) {
				Map<String, String> unit = new HashMap<String, String>();


				if (r.getTime() != null)
					unit.put("Time", r.getTime().format(dateformatter));
				else
					unit.put("Time", "");
				unit.put("OperationCount", String.valueOf(r.getOperationCount()));
				unit.put("Operation", String.valueOf(r.getOperation()));
				unit.put("BeforeAmount", String.valueOf(r.getBeforeAmount()));
				unit.put("AfterAmount", String.valueOf(r.getAfterAmount()));
				unit.put("CardID", String.valueOf(r.getCardID()));
				unit.put("UserID", String.valueOf(r.getUserID()));
				dataReceipt.add(unit);
			}
			
			allObjectTables.get("Receipt").setItems(dataReceipt);
	}
	public void updateATMTable() {
			ObservableList<Map<String, String>> dataATM = FXCollections.observableArrayList();
			List<ATM> rsATM = EntityManager.getAllInstancesOf("ATM");
			for (ATM r : rsATM) {
				Map<String, String> unit = new HashMap<String, String>();


				unit.put("ATMid", String.valueOf(r.getATMid()));
				if (r.getAddress() != null)
					unit.put("Address", String.valueOf(r.getAddress()));
				else
					unit.put("Address", "");
				dataATM.add(unit);
			}
			
			allObjectTables.get("ATM").setItems(dataATM);
	}
	public void updateBankTable() {
			ObservableList<Map<String, String>> dataBank = FXCollections.observableArrayList();
			List<Bank> rsBank = EntityManager.getAllInstancesOf("Bank");
			for (Bank r : rsBank) {
				Map<String, String> unit = new HashMap<String, String>();


				if (r.getBankName() != null)
					unit.put("BankName", String.valueOf(r.getBankName()));
				else
					unit.put("BankName", "");
				if (r.getAddress() != null)
					unit.put("Address", String.valueOf(r.getAddress()));
				else
					unit.put("Address", "");
				unit.put("StuffNum", String.valueOf(r.getStuffNum()));
				dataBank.add(unit);
			}
			
			allObjectTables.get("Bank").setItems(dataBank);
	}
	
	public void classStatisicBingding() {
	
		 classInfodata = FXCollections.observableArrayList();
	 	 bankcard = new ClassInfo("BankCard", EntityManager.getAllInstancesOf("BankCard").size());
	 	 classInfodata.add(bankcard);
	 	 user = new ClassInfo("User", EntityManager.getAllInstancesOf("User").size());
	 	 classInfodata.add(user);
	 	 transaction = new ClassInfo("Transaction", EntityManager.getAllInstancesOf("Transaction").size());
	 	 classInfodata.add(transaction);
	 	 bankclerk = new ClassInfo("BankClerk", EntityManager.getAllInstancesOf("BankClerk").size());
	 	 classInfodata.add(bankclerk);
	 	 receipt = new ClassInfo("Receipt", EntityManager.getAllInstancesOf("Receipt").size());
	 	 classInfodata.add(receipt);
	 	 atm = new ClassInfo("ATM", EntityManager.getAllInstancesOf("ATM").size());
	 	 classInfodata.add(atm);
	 	 bank = new ClassInfo("Bank", EntityManager.getAllInstancesOf("Bank").size());
	 	 classInfodata.add(bank);
	 	 
		 class_statisic.setItems(classInfodata);
		 
		 //Class Statisic Binding
		 class_statisic.getSelectionModel().selectedItemProperty().addListener(
				 (observable, oldValue, newValue) ->  { 
				 										 //no selected object in table
				 										 objectindex = -1;
				 										 
				 										 //get lastest data, reflect updateTableData method
				 										 try {
												 			 Method updateob = this.getClass().getMethod("update" + newValue.getName() + "Table", null);
												 			 updateob.invoke(this);			 
												 		 } catch (Exception e) {
												 		 	 e.printStackTrace();
												 		 }		 										 
				 	
				 										 //show object table
				 			 				 			 TableView obs = allObjectTables.get(newValue.getName());
				 			 				 			 if (obs != null) {
				 			 				 				object_statics.setContent(obs);
				 			 				 				object_statics.setText("All Objects " + newValue.getName() + ":");
				 			 				 			 }
				 			 				 			 
				 			 				 			 //update association information
							 			 				 updateAssociation(newValue.getName());
				 			 				 			 
				 			 				 			 //show association information
				 			 				 			 ObservableList<AssociationInfo> asso = allassociationData.get(newValue.getName());
				 			 				 			 if (asso != null) {
				 			 				 			 	association_statisic.setItems(asso);
				 			 				 			 }
				 			 				 		  });
	}
	
	public void classStatisticUpdate() {
	 	 bankcard.setNumber(EntityManager.getAllInstancesOf("BankCard").size());
	 	 user.setNumber(EntityManager.getAllInstancesOf("User").size());
	 	 transaction.setNumber(EntityManager.getAllInstancesOf("Transaction").size());
	 	 bankclerk.setNumber(EntityManager.getAllInstancesOf("BankClerk").size());
	 	 receipt.setNumber(EntityManager.getAllInstancesOf("Receipt").size());
	 	 atm.setNumber(EntityManager.getAllInstancesOf("ATM").size());
	 	 bank.setNumber(EntityManager.getAllInstancesOf("Bank").size());
		
	}
	
	/**
	 * association binding
	 */
	public void associationStatisicBingding() {
		
		allassociationData = new HashMap<String, ObservableList<AssociationInfo>>();
		
		ObservableList<AssociationInfo> BankCard_association_data = FXCollections.observableArrayList();
		AssociationInfo BankCard_associatition_BelongedUser = new AssociationInfo("BankCard", "User", "BelongedUser", false);
		BankCard_association_data.add(BankCard_associatition_BelongedUser);
		AssociationInfo BankCard_associatition_Has = new AssociationInfo("BankCard", "Transaction", "Has", true);
		BankCard_association_data.add(BankCard_associatition_Has);
		AssociationInfo BankCard_associatition_BelongedToBank = new AssociationInfo("BankCard", "Bank", "BelongedToBank", false);
		BankCard_association_data.add(BankCard_associatition_BelongedToBank);
		
		allassociationData.put("BankCard", BankCard_association_data);
		
		ObservableList<AssociationInfo> User_association_data = FXCollections.observableArrayList();
		AssociationInfo User_associatition_OwnedCard = new AssociationInfo("User", "BankCard", "OwnedCard", true);
		User_association_data.add(User_associatition_OwnedCard);
		
		allassociationData.put("User", User_association_data);
		
		ObservableList<AssociationInfo> Transaction_association_data = FXCollections.observableArrayList();
		AssociationInfo Transaction_associatition_InvolvedCaed = new AssociationInfo("Transaction", "BankCard", "InvolvedCaed", false);
		Transaction_association_data.add(Transaction_associatition_InvolvedCaed);
		
		allassociationData.put("Transaction", Transaction_association_data);
		
		ObservableList<AssociationInfo> BankClerk_association_data = FXCollections.observableArrayList();
		AssociationInfo BankClerk_associatition_ManageUser = new AssociationInfo("BankClerk", "User", "ManageUser", true);
		BankClerk_association_data.add(BankClerk_associatition_ManageUser);
		AssociationInfo BankClerk_associatition_ManageBankCard = new AssociationInfo("BankClerk", "BankCard", "ManageBankCard", true);
		BankClerk_association_data.add(BankClerk_associatition_ManageBankCard);
		
		allassociationData.put("BankClerk", BankClerk_association_data);
		
		ObservableList<AssociationInfo> Receipt_association_data = FXCollections.observableArrayList();
		AssociationInfo Receipt_associatition_ReceiptFromATM = new AssociationInfo("Receipt", "ATM", "ReceiptFromATM", false);
		Receipt_association_data.add(Receipt_associatition_ReceiptFromATM);
		
		allassociationData.put("Receipt", Receipt_association_data);
		
		ObservableList<AssociationInfo> ATM_association_data = FXCollections.observableArrayList();
		AssociationInfo ATM_associatition_BelongedtoBank = new AssociationInfo("ATM", "Bank", "BelongedtoBank", false);
		ATM_association_data.add(ATM_associatition_BelongedtoBank);
		AssociationInfo ATM_associatition_ATMproduceReceipt = new AssociationInfo("ATM", "Receipt", "ATMproduceReceipt", false);
		ATM_association_data.add(ATM_associatition_ATMproduceReceipt);
		
		allassociationData.put("ATM", ATM_association_data);
		
		ObservableList<AssociationInfo> Bank_association_data = FXCollections.observableArrayList();
		AssociationInfo Bank_associatition_OwnedATM = new AssociationInfo("Bank", "ATM", "OwnedATM", true);
		Bank_association_data.add(Bank_associatition_OwnedATM);
		AssociationInfo Bank_associatition_OwnedBankCard = new AssociationInfo("Bank", "BankCard", "OwnedBankCard", true);
		Bank_association_data.add(Bank_associatition_OwnedBankCard);
		
		allassociationData.put("Bank", Bank_association_data);
		
		
		association_statisic.getSelectionModel().selectedItemProperty().addListener(
			    (observable, oldValue, newValue) ->  { 
	
							 		if (newValue != null) {
							 			 try {
							 			 	 if (newValue.getNumber() != 0) {
								 				 //choose object or not
								 				 if (objectindex != -1) {
									 				 Class[] cArg = new Class[1];
									 				 cArg[0] = List.class;
									 				 //reflect updateTableData method
										 			 Method updateob = this.getClass().getMethod("update" + newValue.getTargetClass() + "Table", cArg);
										 			 //find choosen object
										 			 Object selectedob = EntityManager.getAllInstancesOf(newValue.getSourceClass()).get(objectindex);
										 			 //reflect find association method
										 			 Method getAssociatedObject = selectedob.getClass().getMethod("get" + newValue.getAssociationName());
										 			 List r = new LinkedList();
										 			 //one or mulity?
										 			 if (newValue.getIsMultiple() == true) {
											 			 
											 			r = (List) getAssociatedObject.invoke(selectedob);
										 			 }
										 			 else {
										 				r.add(getAssociatedObject.invoke(selectedob));
										 			 }
										 			 //invoke update method
										 			 updateob.invoke(this, r);
										 			  
										 			 
								 				 }
												 //bind updated data to GUI
					 				 			 TableView obs = allObjectTables.get(newValue.getTargetClass());
					 				 			 if (obs != null) {
					 				 				object_statics.setContent(obs);
					 				 				object_statics.setText("Targets Objects " + newValue.getTargetClass() + ":");
					 				 			 }
					 				 		 }
							 			 }
							 			 catch (Exception e) {
							 				 e.printStackTrace();
							 			 }
							 		}
					 		  });
		
	}	
	
	

    //prepare data for contract
	public void prepareData() {
		
		//definition map
		definitions_map = new HashMap<String, String>();
		
		//precondition map
		preconditions_map = new HashMap<String, String>();
		preconditions_map.put("ejectCard", "true");
		preconditions_map.put("printReceipt", "true");
		preconditions_map.put("withdrawCash", "true");
		preconditions_map.put("inputPassword", "true");
		preconditions_map.put("inputCard", "true");
		preconditions_map.put("depositFunds", "true");
		preconditions_map.put("changePassword", "true");
		preconditions_map.put("checkBalance", "true");
		preconditions_map.put("cardIdentification", "true");
		preconditions_map.put("createTransaction", "true");
		preconditions_map.put("queryTransaction", "true");
		preconditions_map.put("modifyTransaction", "true");
		preconditions_map.put("deleteTransaction", "true");
		preconditions_map.put("createUser", "true");
		preconditions_map.put("queryUser", "true");
		preconditions_map.put("modifyUser", "true");
		preconditions_map.put("deleteUser", "true");
		preconditions_map.put("createBankCard", "true");
		preconditions_map.put("queryBankCard", "true");
		preconditions_map.put("modifyBankCard", "true");
		preconditions_map.put("deleteBankCard", "true");
		preconditions_map.put("transferMoney", "true");
		preconditions_map.put("checkBankStatement", "true");
		preconditions_map.put("closeAccount", "true");
		preconditions_map.put("clerkAuthorization", "true");
		preconditions_map.put("checkLog", "true");
		preconditions_map.put("printLog", "true");
		preconditions_map.put("checkCash", "true");
		preconditions_map.put("printDetails", "true");
		preconditions_map.put("clerkExit", "true");
		preconditions_map.put("changeMode", "true");
		preconditions_map.put("checkUser", "true");
		preconditions_map.put("checkCard", "true");
		
		//postcondition map
		postconditions_map = new HashMap<String, String>();
		postconditions_map.put("ejectCard", "true");
		postconditions_map.put("printReceipt", "true");
		postconditions_map.put("withdrawCash", "true");
		postconditions_map.put("inputPassword", "true");
		postconditions_map.put("inputCard", "true");
		postconditions_map.put("depositFunds", "true");
		postconditions_map.put("changePassword", "true");
		postconditions_map.put("checkBalance", "true");
		postconditions_map.put("cardIdentification", "true");
		postconditions_map.put("createTransaction", "true");
		postconditions_map.put("queryTransaction", "true");
		postconditions_map.put("modifyTransaction", "true");
		postconditions_map.put("deleteTransaction", "true");
		postconditions_map.put("createUser", "true");
		postconditions_map.put("queryUser", "true");
		postconditions_map.put("modifyUser", "true");
		postconditions_map.put("deleteUser", "true");
		postconditions_map.put("createBankCard", "true");
		postconditions_map.put("queryBankCard", "true");
		postconditions_map.put("modifyBankCard", "true");
		postconditions_map.put("deleteBankCard", "true");
		postconditions_map.put("transferMoney", "true");
		postconditions_map.put("checkBankStatement", "true");
		postconditions_map.put("closeAccount", "true");
		postconditions_map.put("clerkAuthorization", "true");
		postconditions_map.put("checkLog", "true");
		postconditions_map.put("printLog", "true");
		postconditions_map.put("checkCash", "true");
		postconditions_map.put("printDetails", "true");
		postconditions_map.put("clerkExit", "true");
		postconditions_map.put("changeMode", "true");
		postconditions_map.put("checkUser", "true");
		postconditions_map.put("checkCard", "true");
		
		//service invariants map
		service_invariants_map = new LinkedHashMap<String, String>();
		
		//entity invariants map
		entity_invariants_map = new LinkedHashMap<String, String>();
		
	}
	
	public void generatOperationPane() {
		
		 operationPanels = new LinkedHashMap<String, GridPane>();
		
		 // ==================== GridPane_ejectCard ====================
		 GridPane ejectCard = new GridPane();
		 ejectCard.setHgap(4);
		 ejectCard.setVgap(6);
		 ejectCard.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> ejectCard_content = ejectCard.getChildren();
		 Label ejectCard_label = new Label("This operation is no intput parameters..");
		 ejectCard_label.setMinWidth(Region.USE_PREF_SIZE);
		 ejectCard_content.add(ejectCard_label);
		 GridPane.setConstraints(ejectCard_label, 0, 0);
		 operationPanels.put("ejectCard", ejectCard);
		 
		 // ==================== GridPane_printReceipt ====================
		 GridPane printReceipt = new GridPane();
		 printReceipt.setHgap(4);
		 printReceipt.setVgap(6);
		 printReceipt.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> printReceipt_content = printReceipt.getChildren();
		 Label printReceipt_label = new Label("This operation is no intput parameters..");
		 printReceipt_label.setMinWidth(Region.USE_PREF_SIZE);
		 printReceipt_content.add(printReceipt_label);
		 GridPane.setConstraints(printReceipt_label, 0, 0);
		 operationPanels.put("printReceipt", printReceipt);
		 
		 // ==================== GridPane_withdrawCash ====================
		 GridPane withdrawCash = new GridPane();
		 withdrawCash.setHgap(4);
		 withdrawCash.setVgap(6);
		 withdrawCash.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> withdrawCash_content = withdrawCash.getChildren();
		 Label withdrawCash_quantity_label = new Label("quantity:");
		 withdrawCash_quantity_label.setMinWidth(Region.USE_PREF_SIZE);
		 withdrawCash_content.add(withdrawCash_quantity_label);
		 GridPane.setConstraints(withdrawCash_quantity_label, 0, 0);
		 
		 withdrawCash_quantity_t = new TextField();
		 withdrawCash_content.add(withdrawCash_quantity_t);
		 withdrawCash_quantity_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(withdrawCash_quantity_t, 1, 0);
		 operationPanels.put("withdrawCash", withdrawCash);
		 
		 // ==================== GridPane_inputPassword ====================
		 GridPane inputPassword = new GridPane();
		 inputPassword.setHgap(4);
		 inputPassword.setVgap(6);
		 inputPassword.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> inputPassword_content = inputPassword.getChildren();
		 Label inputPassword_password_label = new Label("password:");
		 inputPassword_password_label.setMinWidth(Region.USE_PREF_SIZE);
		 inputPassword_content.add(inputPassword_password_label);
		 GridPane.setConstraints(inputPassword_password_label, 0, 0);
		 
		 inputPassword_password_t = new TextField();
		 inputPassword_content.add(inputPassword_password_t);
		 inputPassword_password_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(inputPassword_password_t, 1, 0);
		 operationPanels.put("inputPassword", inputPassword);
		 
		 // ==================== GridPane_inputCard ====================
		 GridPane inputCard = new GridPane();
		 inputCard.setHgap(4);
		 inputCard.setVgap(6);
		 inputCard.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> inputCard_content = inputCard.getChildren();
		 Label inputCard_cardID_label = new Label("cardID:");
		 inputCard_cardID_label.setMinWidth(Region.USE_PREF_SIZE);
		 inputCard_content.add(inputCard_cardID_label);
		 GridPane.setConstraints(inputCard_cardID_label, 0, 0);
		 
		 inputCard_cardID_t = new TextField();
		 inputCard_content.add(inputCard_cardID_t);
		 inputCard_cardID_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(inputCard_cardID_t, 1, 0);
		 operationPanels.put("inputCard", inputCard);
		 
		 // ==================== GridPane_depositFunds ====================
		 GridPane depositFunds = new GridPane();
		 depositFunds.setHgap(4);
		 depositFunds.setVgap(6);
		 depositFunds.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> depositFunds_content = depositFunds.getChildren();
		 Label depositFunds_quantity_label = new Label("quantity:");
		 depositFunds_quantity_label.setMinWidth(Region.USE_PREF_SIZE);
		 depositFunds_content.add(depositFunds_quantity_label);
		 GridPane.setConstraints(depositFunds_quantity_label, 0, 0);
		 
		 depositFunds_quantity_t = new TextField();
		 depositFunds_content.add(depositFunds_quantity_t);
		 depositFunds_quantity_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(depositFunds_quantity_t, 1, 0);
		 operationPanels.put("depositFunds", depositFunds);
		 
		 // ==================== GridPane_changePassword ====================
		 GridPane changePassword = new GridPane();
		 changePassword.setHgap(4);
		 changePassword.setVgap(6);
		 changePassword.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> changePassword_content = changePassword.getChildren();
		 Label changePassword_label = new Label("This operation is no intput parameters..");
		 changePassword_label.setMinWidth(Region.USE_PREF_SIZE);
		 changePassword_content.add(changePassword_label);
		 GridPane.setConstraints(changePassword_label, 0, 0);
		 operationPanels.put("changePassword", changePassword);
		 
		 // ==================== GridPane_checkBalance ====================
		 GridPane checkBalance = new GridPane();
		 checkBalance.setHgap(4);
		 checkBalance.setVgap(6);
		 checkBalance.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> checkBalance_content = checkBalance.getChildren();
		 Label checkBalance_label = new Label("This operation is no intput parameters..");
		 checkBalance_label.setMinWidth(Region.USE_PREF_SIZE);
		 checkBalance_content.add(checkBalance_label);
		 GridPane.setConstraints(checkBalance_label, 0, 0);
		 operationPanels.put("checkBalance", checkBalance);
		 
		 // ==================== GridPane_cardIdentification ====================
		 GridPane cardIdentification = new GridPane();
		 cardIdentification.setHgap(4);
		 cardIdentification.setVgap(6);
		 cardIdentification.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> cardIdentification_content = cardIdentification.getChildren();
		 Label cardIdentification_label = new Label("This operation is no intput parameters..");
		 cardIdentification_label.setMinWidth(Region.USE_PREF_SIZE);
		 cardIdentification_content.add(cardIdentification_label);
		 GridPane.setConstraints(cardIdentification_label, 0, 0);
		 operationPanels.put("cardIdentification", cardIdentification);
		 
		 // ==================== GridPane_createTransaction ====================
		 GridPane createTransaction = new GridPane();
		 createTransaction.setHgap(4);
		 createTransaction.setVgap(6);
		 createTransaction.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> createTransaction_content = createTransaction.getChildren();
		 Label createTransaction_withdrewnum_label = new Label("withdrewnum:");
		 createTransaction_withdrewnum_label.setMinWidth(Region.USE_PREF_SIZE);
		 createTransaction_content.add(createTransaction_withdrewnum_label);
		 GridPane.setConstraints(createTransaction_withdrewnum_label, 0, 0);
		 
		 createTransaction_withdrewnum_t = new TextField();
		 createTransaction_content.add(createTransaction_withdrewnum_t);
		 createTransaction_withdrewnum_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(createTransaction_withdrewnum_t, 1, 0);
		 Label createTransaction_balanceafterwithdraw_label = new Label("balanceafterwithdraw:");
		 createTransaction_balanceafterwithdraw_label.setMinWidth(Region.USE_PREF_SIZE);
		 createTransaction_content.add(createTransaction_balanceafterwithdraw_label);
		 GridPane.setConstraints(createTransaction_balanceafterwithdraw_label, 0, 1);
		 
		 createTransaction_balanceafterwithdraw_t = new TextField();
		 createTransaction_content.add(createTransaction_balanceafterwithdraw_t);
		 createTransaction_balanceafterwithdraw_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(createTransaction_balanceafterwithdraw_t, 1, 1);
		 operationPanels.put("createTransaction", createTransaction);
		 
		 // ==================== GridPane_queryTransaction ====================
		 GridPane queryTransaction = new GridPane();
		 queryTransaction.setHgap(4);
		 queryTransaction.setVgap(6);
		 queryTransaction.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> queryTransaction_content = queryTransaction.getChildren();
		 Label queryTransaction_withdrewNum_label = new Label("withdrewNum:");
		 queryTransaction_withdrewNum_label.setMinWidth(Region.USE_PREF_SIZE);
		 queryTransaction_content.add(queryTransaction_withdrewNum_label);
		 GridPane.setConstraints(queryTransaction_withdrewNum_label, 0, 0);
		 
		 queryTransaction_withdrewNum_t = new TextField();
		 queryTransaction_content.add(queryTransaction_withdrewNum_t);
		 queryTransaction_withdrewNum_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(queryTransaction_withdrewNum_t, 1, 0);
		 operationPanels.put("queryTransaction", queryTransaction);
		 
		 // ==================== GridPane_modifyTransaction ====================
		 GridPane modifyTransaction = new GridPane();
		 modifyTransaction.setHgap(4);
		 modifyTransaction.setVgap(6);
		 modifyTransaction.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> modifyTransaction_content = modifyTransaction.getChildren();
		 Label modifyTransaction_withdrewnum_label = new Label("withdrewnum:");
		 modifyTransaction_withdrewnum_label.setMinWidth(Region.USE_PREF_SIZE);
		 modifyTransaction_content.add(modifyTransaction_withdrewnum_label);
		 GridPane.setConstraints(modifyTransaction_withdrewnum_label, 0, 0);
		 
		 modifyTransaction_withdrewnum_t = new TextField();
		 modifyTransaction_content.add(modifyTransaction_withdrewnum_t);
		 modifyTransaction_withdrewnum_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(modifyTransaction_withdrewnum_t, 1, 0);
		 Label modifyTransaction_balanceafterwithdraw_label = new Label("balanceafterwithdraw:");
		 modifyTransaction_balanceafterwithdraw_label.setMinWidth(Region.USE_PREF_SIZE);
		 modifyTransaction_content.add(modifyTransaction_balanceafterwithdraw_label);
		 GridPane.setConstraints(modifyTransaction_balanceafterwithdraw_label, 0, 1);
		 
		 modifyTransaction_balanceafterwithdraw_t = new TextField();
		 modifyTransaction_content.add(modifyTransaction_balanceafterwithdraw_t);
		 modifyTransaction_balanceafterwithdraw_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(modifyTransaction_balanceafterwithdraw_t, 1, 1);
		 operationPanels.put("modifyTransaction", modifyTransaction);
		 
		 // ==================== GridPane_deleteTransaction ====================
		 GridPane deleteTransaction = new GridPane();
		 deleteTransaction.setHgap(4);
		 deleteTransaction.setVgap(6);
		 deleteTransaction.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> deleteTransaction_content = deleteTransaction.getChildren();
		 Label deleteTransaction_withdrewNum_label = new Label("withdrewNum:");
		 deleteTransaction_withdrewNum_label.setMinWidth(Region.USE_PREF_SIZE);
		 deleteTransaction_content.add(deleteTransaction_withdrewNum_label);
		 GridPane.setConstraints(deleteTransaction_withdrewNum_label, 0, 0);
		 
		 deleteTransaction_withdrewNum_t = new TextField();
		 deleteTransaction_content.add(deleteTransaction_withdrewNum_t);
		 deleteTransaction_withdrewNum_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(deleteTransaction_withdrewNum_t, 1, 0);
		 operationPanels.put("deleteTransaction", deleteTransaction);
		 
		 // ==================== GridPane_createUser ====================
		 GridPane createUser = new GridPane();
		 createUser.setHgap(4);
		 createUser.setVgap(6);
		 createUser.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> createUser_content = createUser.getChildren();
		 Label createUser_userid_label = new Label("userid:");
		 createUser_userid_label.setMinWidth(Region.USE_PREF_SIZE);
		 createUser_content.add(createUser_userid_label);
		 GridPane.setConstraints(createUser_userid_label, 0, 0);
		 
		 createUser_userid_t = new TextField();
		 createUser_content.add(createUser_userid_t);
		 createUser_userid_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(createUser_userid_t, 1, 0);
		 Label createUser_name_label = new Label("name:");
		 createUser_name_label.setMinWidth(Region.USE_PREF_SIZE);
		 createUser_content.add(createUser_name_label);
		 GridPane.setConstraints(createUser_name_label, 0, 1);
		 
		 createUser_name_t = new TextField();
		 createUser_content.add(createUser_name_t);
		 createUser_name_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(createUser_name_t, 1, 1);
		 Label createUser_address_label = new Label("address:");
		 createUser_address_label.setMinWidth(Region.USE_PREF_SIZE);
		 createUser_content.add(createUser_address_label);
		 GridPane.setConstraints(createUser_address_label, 0, 2);
		 
		 createUser_address_t = new TextField();
		 createUser_content.add(createUser_address_t);
		 createUser_address_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(createUser_address_t, 1, 2);
		 operationPanels.put("createUser", createUser);
		 
		 // ==================== GridPane_queryUser ====================
		 GridPane queryUser = new GridPane();
		 queryUser.setHgap(4);
		 queryUser.setVgap(6);
		 queryUser.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> queryUser_content = queryUser.getChildren();
		 Label queryUser_userID_label = new Label("userID:");
		 queryUser_userID_label.setMinWidth(Region.USE_PREF_SIZE);
		 queryUser_content.add(queryUser_userID_label);
		 GridPane.setConstraints(queryUser_userID_label, 0, 0);
		 
		 queryUser_userID_t = new TextField();
		 queryUser_content.add(queryUser_userID_t);
		 queryUser_userID_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(queryUser_userID_t, 1, 0);
		 operationPanels.put("queryUser", queryUser);
		 
		 // ==================== GridPane_modifyUser ====================
		 GridPane modifyUser = new GridPane();
		 modifyUser.setHgap(4);
		 modifyUser.setVgap(6);
		 modifyUser.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> modifyUser_content = modifyUser.getChildren();
		 Label modifyUser_userid_label = new Label("userid:");
		 modifyUser_userid_label.setMinWidth(Region.USE_PREF_SIZE);
		 modifyUser_content.add(modifyUser_userid_label);
		 GridPane.setConstraints(modifyUser_userid_label, 0, 0);
		 
		 modifyUser_userid_t = new TextField();
		 modifyUser_content.add(modifyUser_userid_t);
		 modifyUser_userid_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(modifyUser_userid_t, 1, 0);
		 Label modifyUser_name_label = new Label("name:");
		 modifyUser_name_label.setMinWidth(Region.USE_PREF_SIZE);
		 modifyUser_content.add(modifyUser_name_label);
		 GridPane.setConstraints(modifyUser_name_label, 0, 1);
		 
		 modifyUser_name_t = new TextField();
		 modifyUser_content.add(modifyUser_name_t);
		 modifyUser_name_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(modifyUser_name_t, 1, 1);
		 Label modifyUser_address_label = new Label("address:");
		 modifyUser_address_label.setMinWidth(Region.USE_PREF_SIZE);
		 modifyUser_content.add(modifyUser_address_label);
		 GridPane.setConstraints(modifyUser_address_label, 0, 2);
		 
		 modifyUser_address_t = new TextField();
		 modifyUser_content.add(modifyUser_address_t);
		 modifyUser_address_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(modifyUser_address_t, 1, 2);
		 operationPanels.put("modifyUser", modifyUser);
		 
		 // ==================== GridPane_deleteUser ====================
		 GridPane deleteUser = new GridPane();
		 deleteUser.setHgap(4);
		 deleteUser.setVgap(6);
		 deleteUser.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> deleteUser_content = deleteUser.getChildren();
		 Label deleteUser_userID_label = new Label("userID:");
		 deleteUser_userID_label.setMinWidth(Region.USE_PREF_SIZE);
		 deleteUser_content.add(deleteUser_userID_label);
		 GridPane.setConstraints(deleteUser_userID_label, 0, 0);
		 
		 deleteUser_userID_t = new TextField();
		 deleteUser_content.add(deleteUser_userID_t);
		 deleteUser_userID_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(deleteUser_userID_t, 1, 0);
		 operationPanels.put("deleteUser", deleteUser);
		 
		 // ==================== GridPane_createBankCard ====================
		 GridPane createBankCard = new GridPane();
		 createBankCard.setHgap(4);
		 createBankCard.setVgap(6);
		 createBankCard.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> createBankCard_content = createBankCard.getChildren();
		 Label createBankCard_cardid_label = new Label("cardid:");
		 createBankCard_cardid_label.setMinWidth(Region.USE_PREF_SIZE);
		 createBankCard_content.add(createBankCard_cardid_label);
		 GridPane.setConstraints(createBankCard_cardid_label, 0, 0);
		 
		 createBankCard_cardid_t = new TextField();
		 createBankCard_content.add(createBankCard_cardid_t);
		 createBankCard_cardid_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(createBankCard_cardid_t, 1, 0);
		 Label createBankCard_password_label = new Label("password:");
		 createBankCard_password_label.setMinWidth(Region.USE_PREF_SIZE);
		 createBankCard_content.add(createBankCard_password_label);
		 GridPane.setConstraints(createBankCard_password_label, 0, 1);
		 
		 createBankCard_password_t = new TextField();
		 createBankCard_content.add(createBankCard_password_t);
		 createBankCard_password_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(createBankCard_password_t, 1, 1);
		 Label createBankCard_balance_label = new Label("balance:");
		 createBankCard_balance_label.setMinWidth(Region.USE_PREF_SIZE);
		 createBankCard_content.add(createBankCard_balance_label);
		 GridPane.setConstraints(createBankCard_balance_label, 0, 2);
		 
		 createBankCard_balance_t = new TextField();
		 createBankCard_content.add(createBankCard_balance_t);
		 createBankCard_balance_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(createBankCard_balance_t, 1, 2);
		 Label createBankCard_cardstatus_label = new Label("cardstatus:");
		 createBankCard_cardstatus_label.setMinWidth(Region.USE_PREF_SIZE);
		 createBankCard_content.add(createBankCard_cardstatus_label);
		 GridPane.setConstraints(createBankCard_cardstatus_label, 0, 3);
		 
		 createBankCard_cardstatus_t = new TextField();
		 createBankCard_content.add(createBankCard_cardstatus_t);
		 createBankCard_cardstatus_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(createBankCard_cardstatus_t, 1, 3);
		 Label createBankCard_catalog_label = new Label("catalog:");
		 createBankCard_catalog_label.setMinWidth(Region.USE_PREF_SIZE);
		 createBankCard_content.add(createBankCard_catalog_label);
		 GridPane.setConstraints(createBankCard_catalog_label, 0, 4);
		 
		 createBankCard_catalog_t = new TextField();
		 createBankCard_content.add(createBankCard_catalog_t);
		 createBankCard_catalog_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(createBankCard_catalog_t, 1, 4);
		 operationPanels.put("createBankCard", createBankCard);
		 
		 // ==================== GridPane_queryBankCard ====================
		 GridPane queryBankCard = new GridPane();
		 queryBankCard.setHgap(4);
		 queryBankCard.setVgap(6);
		 queryBankCard.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> queryBankCard_content = queryBankCard.getChildren();
		 Label queryBankCard_cardID_label = new Label("cardID:");
		 queryBankCard_cardID_label.setMinWidth(Region.USE_PREF_SIZE);
		 queryBankCard_content.add(queryBankCard_cardID_label);
		 GridPane.setConstraints(queryBankCard_cardID_label, 0, 0);
		 
		 queryBankCard_cardID_t = new TextField();
		 queryBankCard_content.add(queryBankCard_cardID_t);
		 queryBankCard_cardID_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(queryBankCard_cardID_t, 1, 0);
		 operationPanels.put("queryBankCard", queryBankCard);
		 
		 // ==================== GridPane_modifyBankCard ====================
		 GridPane modifyBankCard = new GridPane();
		 modifyBankCard.setHgap(4);
		 modifyBankCard.setVgap(6);
		 modifyBankCard.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> modifyBankCard_content = modifyBankCard.getChildren();
		 Label modifyBankCard_cardid_label = new Label("cardid:");
		 modifyBankCard_cardid_label.setMinWidth(Region.USE_PREF_SIZE);
		 modifyBankCard_content.add(modifyBankCard_cardid_label);
		 GridPane.setConstraints(modifyBankCard_cardid_label, 0, 0);
		 
		 modifyBankCard_cardid_t = new TextField();
		 modifyBankCard_content.add(modifyBankCard_cardid_t);
		 modifyBankCard_cardid_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(modifyBankCard_cardid_t, 1, 0);
		 Label modifyBankCard_password_label = new Label("password:");
		 modifyBankCard_password_label.setMinWidth(Region.USE_PREF_SIZE);
		 modifyBankCard_content.add(modifyBankCard_password_label);
		 GridPane.setConstraints(modifyBankCard_password_label, 0, 1);
		 
		 modifyBankCard_password_t = new TextField();
		 modifyBankCard_content.add(modifyBankCard_password_t);
		 modifyBankCard_password_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(modifyBankCard_password_t, 1, 1);
		 Label modifyBankCard_balance_label = new Label("balance:");
		 modifyBankCard_balance_label.setMinWidth(Region.USE_PREF_SIZE);
		 modifyBankCard_content.add(modifyBankCard_balance_label);
		 GridPane.setConstraints(modifyBankCard_balance_label, 0, 2);
		 
		 modifyBankCard_balance_t = new TextField();
		 modifyBankCard_content.add(modifyBankCard_balance_t);
		 modifyBankCard_balance_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(modifyBankCard_balance_t, 1, 2);
		 Label modifyBankCard_cardstatus_label = new Label("cardstatus:");
		 modifyBankCard_cardstatus_label.setMinWidth(Region.USE_PREF_SIZE);
		 modifyBankCard_content.add(modifyBankCard_cardstatus_label);
		 GridPane.setConstraints(modifyBankCard_cardstatus_label, 0, 3);
		 
		 modifyBankCard_cardstatus_t = new TextField();
		 modifyBankCard_content.add(modifyBankCard_cardstatus_t);
		 modifyBankCard_cardstatus_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(modifyBankCard_cardstatus_t, 1, 3);
		 Label modifyBankCard_catalog_label = new Label("catalog:");
		 modifyBankCard_catalog_label.setMinWidth(Region.USE_PREF_SIZE);
		 modifyBankCard_content.add(modifyBankCard_catalog_label);
		 GridPane.setConstraints(modifyBankCard_catalog_label, 0, 4);
		 
		 modifyBankCard_catalog_t = new TextField();
		 modifyBankCard_content.add(modifyBankCard_catalog_t);
		 modifyBankCard_catalog_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(modifyBankCard_catalog_t, 1, 4);
		 operationPanels.put("modifyBankCard", modifyBankCard);
		 
		 // ==================== GridPane_deleteBankCard ====================
		 GridPane deleteBankCard = new GridPane();
		 deleteBankCard.setHgap(4);
		 deleteBankCard.setVgap(6);
		 deleteBankCard.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> deleteBankCard_content = deleteBankCard.getChildren();
		 Label deleteBankCard_cardID_label = new Label("cardID:");
		 deleteBankCard_cardID_label.setMinWidth(Region.USE_PREF_SIZE);
		 deleteBankCard_content.add(deleteBankCard_cardID_label);
		 GridPane.setConstraints(deleteBankCard_cardID_label, 0, 0);
		 
		 deleteBankCard_cardID_t = new TextField();
		 deleteBankCard_content.add(deleteBankCard_cardID_t);
		 deleteBankCard_cardID_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(deleteBankCard_cardID_t, 1, 0);
		 operationPanels.put("deleteBankCard", deleteBankCard);
		 
		 // ==================== GridPane_transferMoney ====================
		 GridPane transferMoney = new GridPane();
		 transferMoney.setHgap(4);
		 transferMoney.setVgap(6);
		 transferMoney.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> transferMoney_content = transferMoney.getChildren();
		 Label transferMoney_cardID_label = new Label("cardID:");
		 transferMoney_cardID_label.setMinWidth(Region.USE_PREF_SIZE);
		 transferMoney_content.add(transferMoney_cardID_label);
		 GridPane.setConstraints(transferMoney_cardID_label, 0, 0);
		 
		 transferMoney_cardID_t = new TextField();
		 transferMoney_content.add(transferMoney_cardID_t);
		 transferMoney_cardID_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(transferMoney_cardID_t, 1, 0);
		 Label transferMoney_quantity_label = new Label("quantity:");
		 transferMoney_quantity_label.setMinWidth(Region.USE_PREF_SIZE);
		 transferMoney_content.add(transferMoney_quantity_label);
		 GridPane.setConstraints(transferMoney_quantity_label, 0, 1);
		 
		 transferMoney_quantity_t = new TextField();
		 transferMoney_content.add(transferMoney_quantity_t);
		 transferMoney_quantity_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(transferMoney_quantity_t, 1, 1);
		 operationPanels.put("transferMoney", transferMoney);
		 
		 // ==================== GridPane_checkBankStatement ====================
		 GridPane checkBankStatement = new GridPane();
		 checkBankStatement.setHgap(4);
		 checkBankStatement.setVgap(6);
		 checkBankStatement.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> checkBankStatement_content = checkBankStatement.getChildren();
		 Label checkBankStatement_label = new Label("This operation is no intput parameters..");
		 checkBankStatement_label.setMinWidth(Region.USE_PREF_SIZE);
		 checkBankStatement_content.add(checkBankStatement_label);
		 GridPane.setConstraints(checkBankStatement_label, 0, 0);
		 operationPanels.put("checkBankStatement", checkBankStatement);
		 
		 // ==================== GridPane_closeAccount ====================
		 GridPane closeAccount = new GridPane();
		 closeAccount.setHgap(4);
		 closeAccount.setVgap(6);
		 closeAccount.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> closeAccount_content = closeAccount.getChildren();
		 Label closeAccount_label = new Label("This operation is no intput parameters..");
		 closeAccount_label.setMinWidth(Region.USE_PREF_SIZE);
		 closeAccount_content.add(closeAccount_label);
		 GridPane.setConstraints(closeAccount_label, 0, 0);
		 operationPanels.put("closeAccount", closeAccount);
		 
		 // ==================== GridPane_clerkAuthorization ====================
		 GridPane clerkAuthorization = new GridPane();
		 clerkAuthorization.setHgap(4);
		 clerkAuthorization.setVgap(6);
		 clerkAuthorization.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> clerkAuthorization_content = clerkAuthorization.getChildren();
		 Label clerkAuthorization_clerkID_label = new Label("clerkID:");
		 clerkAuthorization_clerkID_label.setMinWidth(Region.USE_PREF_SIZE);
		 clerkAuthorization_content.add(clerkAuthorization_clerkID_label);
		 GridPane.setConstraints(clerkAuthorization_clerkID_label, 0, 0);
		 
		 clerkAuthorization_clerkID_t = new TextField();
		 clerkAuthorization_content.add(clerkAuthorization_clerkID_t);
		 clerkAuthorization_clerkID_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(clerkAuthorization_clerkID_t, 1, 0);
		 Label clerkAuthorization_authorizationCode_label = new Label("authorizationCode:");
		 clerkAuthorization_authorizationCode_label.setMinWidth(Region.USE_PREF_SIZE);
		 clerkAuthorization_content.add(clerkAuthorization_authorizationCode_label);
		 GridPane.setConstraints(clerkAuthorization_authorizationCode_label, 0, 1);
		 
		 clerkAuthorization_authorizationCode_t = new TextField();
		 clerkAuthorization_content.add(clerkAuthorization_authorizationCode_t);
		 clerkAuthorization_authorizationCode_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(clerkAuthorization_authorizationCode_t, 1, 1);
		 operationPanels.put("clerkAuthorization", clerkAuthorization);
		 
		 // ==================== GridPane_checkLog ====================
		 GridPane checkLog = new GridPane();
		 checkLog.setHgap(4);
		 checkLog.setVgap(6);
		 checkLog.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> checkLog_content = checkLog.getChildren();
		 Label checkLog_fromTime_label = new Label("fromTime:");
		 checkLog_fromTime_label.setMinWidth(Region.USE_PREF_SIZE);
		 checkLog_content.add(checkLog_fromTime_label);
		 GridPane.setConstraints(checkLog_fromTime_label, 0, 0);
		 
		 checkLog_fromTime_t = new TextField();
		 checkLog_content.add(checkLog_fromTime_t);
		 checkLog_fromTime_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(checkLog_fromTime_t, 1, 0);
		 Label checkLog_toTime_label = new Label("toTime:");
		 checkLog_toTime_label.setMinWidth(Region.USE_PREF_SIZE);
		 checkLog_content.add(checkLog_toTime_label);
		 GridPane.setConstraints(checkLog_toTime_label, 0, 1);
		 
		 checkLog_toTime_t = new TextField();
		 checkLog_content.add(checkLog_toTime_t);
		 checkLog_toTime_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(checkLog_toTime_t, 1, 1);
		 operationPanels.put("checkLog", checkLog);
		 
		 // ==================== GridPane_printLog ====================
		 GridPane printLog = new GridPane();
		 printLog.setHgap(4);
		 printLog.setVgap(6);
		 printLog.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> printLog_content = printLog.getChildren();
		 Label printLog_label = new Label("This operation is no intput parameters..");
		 printLog_label.setMinWidth(Region.USE_PREF_SIZE);
		 printLog_content.add(printLog_label);
		 GridPane.setConstraints(printLog_label, 0, 0);
		 operationPanels.put("printLog", printLog);
		 
		 // ==================== GridPane_checkCash ====================
		 GridPane checkCash = new GridPane();
		 checkCash.setHgap(4);
		 checkCash.setVgap(6);
		 checkCash.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> checkCash_content = checkCash.getChildren();
		 Label checkCash_label = new Label("This operation is no intput parameters..");
		 checkCash_label.setMinWidth(Region.USE_PREF_SIZE);
		 checkCash_content.add(checkCash_label);
		 GridPane.setConstraints(checkCash_label, 0, 0);
		 operationPanels.put("checkCash", checkCash);
		 
		 // ==================== GridPane_printDetails ====================
		 GridPane printDetails = new GridPane();
		 printDetails.setHgap(4);
		 printDetails.setVgap(6);
		 printDetails.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> printDetails_content = printDetails.getChildren();
		 Label printDetails_label = new Label("This operation is no intput parameters..");
		 printDetails_label.setMinWidth(Region.USE_PREF_SIZE);
		 printDetails_content.add(printDetails_label);
		 GridPane.setConstraints(printDetails_label, 0, 0);
		 operationPanels.put("printDetails", printDetails);
		 
		 // ==================== GridPane_clerkExit ====================
		 GridPane clerkExit = new GridPane();
		 clerkExit.setHgap(4);
		 clerkExit.setVgap(6);
		 clerkExit.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> clerkExit_content = clerkExit.getChildren();
		 Label clerkExit_label = new Label("This operation is no intput parameters..");
		 clerkExit_label.setMinWidth(Region.USE_PREF_SIZE);
		 clerkExit_content.add(clerkExit_label);
		 GridPane.setConstraints(clerkExit_label, 0, 0);
		 operationPanels.put("clerkExit", clerkExit);
		 
		 // ==================== GridPane_changeMode ====================
		 GridPane changeMode = new GridPane();
		 changeMode.setHgap(4);
		 changeMode.setVgap(6);
		 changeMode.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> changeMode_content = changeMode.getChildren();
		 Label changeMode_pin_label = new Label("pin:");
		 changeMode_pin_label.setMinWidth(Region.USE_PREF_SIZE);
		 changeMode_content.add(changeMode_pin_label);
		 GridPane.setConstraints(changeMode_pin_label, 0, 0);
		 
		 changeMode_pin_t = new TextField();
		 changeMode_content.add(changeMode_pin_t);
		 changeMode_pin_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(changeMode_pin_t, 1, 0);
		 operationPanels.put("changeMode", changeMode);
		 
		 // ==================== GridPane_checkUser ====================
		 GridPane checkUser = new GridPane();
		 checkUser.setHgap(4);
		 checkUser.setVgap(6);
		 checkUser.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> checkUser_content = checkUser.getChildren();
		 Label checkUser_userID_label = new Label("userID:");
		 checkUser_userID_label.setMinWidth(Region.USE_PREF_SIZE);
		 checkUser_content.add(checkUser_userID_label);
		 GridPane.setConstraints(checkUser_userID_label, 0, 0);
		 
		 checkUser_userID_t = new TextField();
		 checkUser_content.add(checkUser_userID_t);
		 checkUser_userID_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(checkUser_userID_t, 1, 0);
		 operationPanels.put("checkUser", checkUser);
		 
		 // ==================== GridPane_checkCard ====================
		 GridPane checkCard = new GridPane();
		 checkCard.setHgap(4);
		 checkCard.setVgap(6);
		 checkCard.setPadding(new Insets(8, 8, 8, 8));
		 
		 ObservableList<Node> checkCard_content = checkCard.getChildren();
		 Label checkCard_cardID_label = new Label("cardID:");
		 checkCard_cardID_label.setMinWidth(Region.USE_PREF_SIZE);
		 checkCard_content.add(checkCard_cardID_label);
		 GridPane.setConstraints(checkCard_cardID_label, 0, 0);
		 
		 checkCard_cardID_t = new TextField();
		 checkCard_content.add(checkCard_cardID_t);
		 checkCard_cardID_t.setMinWidth(Region.USE_PREF_SIZE);
		 GridPane.setConstraints(checkCard_cardID_t, 1, 0);
		 operationPanels.put("checkCard", checkCard);
		 
	}	

	public void actorTreeViewBinding() {
		
		 

		TreeItem<String> treeRootadministrator = new TreeItem<String>("Root node");
		
		TreeItem<String> subTreeRoot_BankCard = new TreeItem<String>("manageBankCard");
					 		subTreeRoot_BankCard.getChildren().addAll(Arrays.asList(					 		
					 			 		new TreeItem<String>("createBankCard"),
					 			 		new TreeItem<String>("queryBankCard"),
					 			 		new TreeItem<String>("modifyBankCard"),
					 			 		new TreeItem<String>("deleteBankCard")					 			 	
					 			 	));							 		
		TreeItem<String> subTreeRoot_User = new TreeItem<String>("manageUser");
					 		subTreeRoot_User.getChildren().addAll(Arrays.asList(					 		
					 			 		new TreeItem<String>("createUser"),
					 			 		new TreeItem<String>("queryUser"),
					 			 		new TreeItem<String>("modifyUser"),
					 			 		new TreeItem<String>("deleteUser")					 			 	
					 			 	));							 		
		TreeItem<String> subTreeRoot_Transaction = new TreeItem<String>("manageTransaction");
					 		subTreeRoot_Transaction.getChildren().addAll(Arrays.asList(					 		
					 			 		new TreeItem<String>("createTransaction"),
					 			 		new TreeItem<String>("queryTransaction"),
					 			 		new TreeItem<String>("modifyTransaction"),
					 			 		new TreeItem<String>("deleteTransaction")					 			 	
					 			 	));							 		
		TreeItem<String> subTreeRoot_BankClerk = new TreeItem<String>("manageBankClerk");
					 		subTreeRoot_BankClerk.getChildren().addAll(Arrays.asList(					 		
					 			 		new TreeItem<String>("createBankClerk"),
					 			 		new TreeItem<String>("queryBankClerk"),
					 			 		new TreeItem<String>("modifyBankClerk"),
					 			 		new TreeItem<String>("deleteBankClerk")					 			 	
					 			 	));							 		
		
					 			
						 		
		treeRootadministrator.getChildren().addAll(Arrays.asList(
		 	subTreeRoot_BankCard,
		 	subTreeRoot_User,
		 	subTreeRoot_Transaction,
		 	subTreeRoot_BankClerk
				));	
				
	 			treeRootadministrator.setExpanded(true);

		actor_treeview_administrator.setShowRoot(false);
		actor_treeview_administrator.setRoot(treeRootadministrator);
	 		
		actor_treeview_administrator.getSelectionModel().selectedItemProperty().addListener(
		 				 (observable, oldValue, newValue) -> { 
		 				 								
		 				 							 //clear the previous return
		 											 operation_return_pane.setContent(new Label());
		 											 
		 				 							 clickedOp = newValue.getValue();
		 				 							 GridPane op = operationPanels.get(clickedOp);
		 				 							 VBox vb = opInvariantPanel.get(clickedOp);
		 				 							 
		 				 							 //op pannel
		 				 							 if (op != null) {
		 				 								 operation_paras.setContent(operationPanels.get(newValue.getValue()));
		 				 								 
		 				 								 ObservableList<Node> l = operationPanels.get(newValue.getValue()).getChildren();
		 				 								 choosenOperation = new LinkedList<TextField>();
		 				 								 for (Node n : l) {
		 				 								 	 if (n instanceof TextField) {
		 				 								 	 	choosenOperation.add((TextField)n);
		 				 								 	  }
		 				 								 }
		 				 								 
		 				 								 definition.setText(definitions_map.get(newValue.getValue()));
		 				 								 precondition.setText(preconditions_map.get(newValue.getValue()));
		 				 								 postcondition.setText(postconditions_map.get(newValue.getValue()));
		 				 								 
		 				 						     }
		 				 							 else {
		 				 								 Label l = new Label(newValue.getValue() + " is no contract information in requirement model.");
		 				 								 l.setPadding(new Insets(8, 8, 8, 8));
		 				 								 operation_paras.setContent(l);
		 				 							 }	
		 				 							 
		 				 							 //op invariants
		 				 							 if (vb != null) {
		 				 							 	ScrollPane scrollPane = new ScrollPane(vb);
		 				 							 	scrollPane.setFitToWidth(true);
		 				 							 	invariants_panes.setMaxHeight(200); 
		 				 							 	//all_invariant_pane.setContent(scrollPane);	
		 				 							 	
		 				 							 	invariants_panes.setContent(scrollPane);
		 				 							 } else {
		 				 							 	 Label l = new Label(newValue.getValue() + " is no related invariants");
		 				 							     l.setPadding(new Insets(8, 8, 8, 8));
		 				 							     invariants_panes.setContent(l);
		 				 							 }
		 				 							 
		 				 							 //reset pre- and post-conditions area color
		 				 							 precondition.setStyle("-fx-background-color:#FFFFFF; -fx-control-inner-background: #FFFFFF ");
		 				 							 postcondition.setStyle("-fx-background-color:#FFFFFF; -fx-control-inner-background: #FFFFFF");
		 				 							 //reset condition panel title
		 				 							 precondition_pane.setText("Precondition");
		 				 							 postcondition_pane.setText("Postcondition");
		 				 						} 
		 				 				);

		
		
		 
		TreeItem<String> treeRootcustomer = new TreeItem<String>("Root node");
			TreeItem<String> subTreeRoot_withdrawCash = new TreeItem<String>("withdrawCash");
			subTreeRoot_withdrawCash.getChildren().addAll(Arrays.asList(
					
				 	new TreeItem<String>("inputCard"),
				 	new TreeItem<String>("inputPassword"),
				 	new TreeItem<String>("withdrawCash"),
				 	new TreeItem<String>("printReceipt"),
				 	new TreeItem<String>("ejectCard")
				 	));
			TreeItem<String> subTreeRoot_depositFunds = new TreeItem<String>("depositFunds");
			subTreeRoot_depositFunds.getChildren().addAll(Arrays.asList(
					
				 	new TreeItem<String>("inputCard"),
				 	new TreeItem<String>("inputPassword"),
				 	new TreeItem<String>("depositFunds"),
				 	new TreeItem<String>("printReceipt"),
				 	new TreeItem<String>("ejectCard")
				 	));
			TreeItem<String> subTreeRoot_changePassword = new TreeItem<String>("changePassword");
			subTreeRoot_changePassword.getChildren().addAll(Arrays.asList(
					
				 	new TreeItem<String>("inputCard"),
				 	new TreeItem<String>("inputPassword"),
				 	new TreeItem<String>("changePassword"),
				 	new TreeItem<String>("inputPassword"),
				 	new TreeItem<String>("ejectCard")
				 	));
			TreeItem<String> subTreeRoot_checkBalance = new TreeItem<String>("checkBalance");
			subTreeRoot_checkBalance.getChildren().addAll(Arrays.asList(
					
				 	new TreeItem<String>("inputCard"),
				 	new TreeItem<String>("inputPassword"),
				 	new TreeItem<String>("checkBalance"),
				 	new TreeItem<String>("ejectCard")
				 	));
			TreeItem<String> subTreeRoot_transferMoney = new TreeItem<String>("transferMoney");
			subTreeRoot_transferMoney.getChildren().addAll(Arrays.asList(
					
				 	new TreeItem<String>("inputCard"),
				 	new TreeItem<String>("inputPassword"),
				 	new TreeItem<String>("transferMoney"),
				 	new TreeItem<String>("printReceipt"),
				 	new TreeItem<String>("ejectCard")
				 	));
			TreeItem<String> subTreeRoot_checkBankStatement = new TreeItem<String>("checkBankStatement");
			subTreeRoot_checkBankStatement.getChildren().addAll(Arrays.asList(
					
				 	new TreeItem<String>("inputCard"),
				 	new TreeItem<String>("inputPassword"),
				 	new TreeItem<String>("checkBankStatement"),
				 	new TreeItem<String>("printReceipt"),
				 	new TreeItem<String>("ejectCard")
				 	));
			TreeItem<String> subTreeRoot_closeAccount = new TreeItem<String>("closeAccount");
			subTreeRoot_closeAccount.getChildren().addAll(Arrays.asList(
					
				 	new TreeItem<String>("inputCard"),
				 	new TreeItem<String>("inputPassword"),
				 	new TreeItem<String>("closeAccount"),
				 	new TreeItem<String>("ejectCard")
				 	));
		
		treeRootcustomer.getChildren().addAll(Arrays.asList(
			new TreeItem<String>("withdrawCash"),
			new TreeItem<String>("depositFunds"),
			new TreeItem<String>("changePassword"),
			new TreeItem<String>("checkBalance"),
			new TreeItem<String>("transferMoney"),
			new TreeItem<String>("checkBankStatement"),
			new TreeItem<String>("closeAccount")
					));
		
		treeRootcustomer.setExpanded(true);

		actor_treeview_customer.setShowRoot(false);
		actor_treeview_customer.setRoot(treeRootcustomer);
		
		//TreeView click, then open the GridPane for inputing parameters
		actor_treeview_customer.getSelectionModel().selectedItemProperty().addListener(
						 (observable, oldValue, newValue) -> { 
						 								
						 							 //clear the previous return
													 operation_return_pane.setContent(new Label());
													 
						 							 clickedOp = newValue.getValue();
						 							 GridPane op = operationPanels.get(clickedOp);
						 							 VBox vb = opInvariantPanel.get(clickedOp);
						 							 
						 							 //op pannel
						 							 if (op != null) {
						 								 operation_paras.setContent(operationPanels.get(newValue.getValue()));
						 								 
						 								 ObservableList<Node> l = operationPanels.get(newValue.getValue()).getChildren();
						 								 choosenOperation = new LinkedList<TextField>();
						 								 for (Node n : l) {
						 								 	 if (n instanceof TextField) {
						 								 	 	choosenOperation.add((TextField)n);
						 								 	  }
						 								 }
						 								 
						 								 definition.setText(definitions_map.get(newValue.getValue()));
						 								 precondition.setText(preconditions_map.get(newValue.getValue()));
						 								 postcondition.setText(postconditions_map.get(newValue.getValue()));
						 								 
						 						     }
						 							 else {
						 								 Label l = new Label(newValue.getValue() + " is no contract information in requirement model.");
						 								 l.setPadding(new Insets(8, 8, 8, 8));
						 								 operation_paras.setContent(l);
						 							 }	
						 							 
						 							 //op invariants
						 							 if (vb != null) {
						 							 	ScrollPane scrollPane = new ScrollPane(vb);
						 							 	scrollPane.setFitToWidth(true);
						 							 	invariants_panes.setMaxHeight(200); 
						 							 	//all_invariant_pane.setContent(scrollPane);	
						 							 	
						 							 	invariants_panes.setContent(scrollPane);
						 							 } else {
						 							 	 Label l = new Label(newValue.getValue() + " is no related invariants");
						 							     l.setPadding(new Insets(8, 8, 8, 8));
						 							     invariants_panes.setContent(l);
						 							 }
						 							 
						 							 //reset pre- and post-conditions area color
						 							 precondition.setStyle("-fx-background-color:#FFFFFF; -fx-control-inner-background: #FFFFFF ");
						 							 postcondition.setStyle("-fx-background-color:#FFFFFF; -fx-control-inner-background: #FFFFFF");
						 							 //reset condition panel title
						 							 precondition_pane.setText("Precondition");
						 							 postcondition_pane.setText("Postcondition");
						 						} 
						 				);
		TreeItem<String> treeRootbankclerk = new TreeItem<String>("Root node");
			TreeItem<String> subTreeRoot_manageUser = new TreeItem<String>("manageUser");
			subTreeRoot_manageUser.getChildren().addAll(Arrays.asList(
					
				 	));
			TreeItem<String> subTreeRoot_manageBankCard = new TreeItem<String>("manageBankCard");
			subTreeRoot_manageBankCard.getChildren().addAll(Arrays.asList(
					
				 	));
			TreeItem<String> subTreeRoot_checkCash = new TreeItem<String>("checkCash");
			subTreeRoot_checkCash.getChildren().addAll(Arrays.asList(
					
				 	new TreeItem<String>("changeMode"),
				 	new TreeItem<String>("clerkAuthorization"),
				 	new TreeItem<String>("checkCash"),
				 	new TreeItem<String>("printDetails"),
				 	new TreeItem<String>("clerkExit"),
				 	new TreeItem<String>("changeMode")
				 	));
			TreeItem<String> subTreeRoot_checkLog = new TreeItem<String>("checkLog");
			subTreeRoot_checkLog.getChildren().addAll(Arrays.asList(
					
				 	new TreeItem<String>("changeMode"),
				 	new TreeItem<String>("clerkAuthorization"),
				 	new TreeItem<String>("checkLog"),
				 	new TreeItem<String>("printLog"),
				 	new TreeItem<String>("clerkExit"),
				 	new TreeItem<String>("changeMode")
				 	));
		
		treeRootbankclerk.getChildren().addAll(Arrays.asList(
			subTreeRoot_manageUser,
			subTreeRoot_manageBankCard,
			new TreeItem<String>("checkCash"),
			new TreeItem<String>("checkLog")
					));
		
		treeRootbankclerk.setExpanded(true);

		actor_treeview_bankclerk.setShowRoot(false);
		actor_treeview_bankclerk.setRoot(treeRootbankclerk);
		
		//TreeView click, then open the GridPane for inputing parameters
		actor_treeview_bankclerk.getSelectionModel().selectedItemProperty().addListener(
						 (observable, oldValue, newValue) -> { 
						 								
						 							 //clear the previous return
													 operation_return_pane.setContent(new Label());
													 
						 							 clickedOp = newValue.getValue();
						 							 GridPane op = operationPanels.get(clickedOp);
						 							 VBox vb = opInvariantPanel.get(clickedOp);
						 							 
						 							 //op pannel
						 							 if (op != null) {
						 								 operation_paras.setContent(operationPanels.get(newValue.getValue()));
						 								 
						 								 ObservableList<Node> l = operationPanels.get(newValue.getValue()).getChildren();
						 								 choosenOperation = new LinkedList<TextField>();
						 								 for (Node n : l) {
						 								 	 if (n instanceof TextField) {
						 								 	 	choosenOperation.add((TextField)n);
						 								 	  }
						 								 }
						 								 
						 								 definition.setText(definitions_map.get(newValue.getValue()));
						 								 precondition.setText(preconditions_map.get(newValue.getValue()));
						 								 postcondition.setText(postconditions_map.get(newValue.getValue()));
						 								 
						 						     }
						 							 else {
						 								 Label l = new Label(newValue.getValue() + " is no contract information in requirement model.");
						 								 l.setPadding(new Insets(8, 8, 8, 8));
						 								 operation_paras.setContent(l);
						 							 }	
						 							 
						 							 //op invariants
						 							 if (vb != null) {
						 							 	ScrollPane scrollPane = new ScrollPane(vb);
						 							 	scrollPane.setFitToWidth(true);
						 							 	invariants_panes.setMaxHeight(200); 
						 							 	//all_invariant_pane.setContent(scrollPane);	
						 							 	
						 							 	invariants_panes.setContent(scrollPane);
						 							 } else {
						 							 	 Label l = new Label(newValue.getValue() + " is no related invariants");
						 							     l.setPadding(new Insets(8, 8, 8, 8));
						 							     invariants_panes.setContent(l);
						 							 }
						 							 
						 							 //reset pre- and post-conditions area color
						 							 precondition.setStyle("-fx-background-color:#FFFFFF; -fx-control-inner-background: #FFFFFF ");
						 							 postcondition.setStyle("-fx-background-color:#FFFFFF; -fx-control-inner-background: #FFFFFF");
						 							 //reset condition panel title
						 							 precondition_pane.setText("Precondition");
						 							 postcondition_pane.setText("Postcondition");
						 						} 
						 				);
	}

	/**
	*    Execute Operation
	*/
	@FXML
	public void execute(ActionEvent event) {
		
		switch (clickedOp) {
		case "ejectCard" : ejectCard(); break;
		case "printReceipt" : printReceipt(); break;
		case "withdrawCash" : withdrawCash(); break;
		case "inputPassword" : inputPassword(); break;
		case "inputCard" : inputCard(); break;
		case "depositFunds" : depositFunds(); break;
		case "changePassword" : changePassword(); break;
		case "checkBalance" : checkBalance(); break;
		case "cardIdentification" : cardIdentification(); break;
		case "createTransaction" : createTransaction(); break;
		case "queryTransaction" : queryTransaction(); break;
		case "modifyTransaction" : modifyTransaction(); break;
		case "deleteTransaction" : deleteTransaction(); break;
		case "createUser" : createUser(); break;
		case "queryUser" : queryUser(); break;
		case "modifyUser" : modifyUser(); break;
		case "deleteUser" : deleteUser(); break;
		case "createBankCard" : createBankCard(); break;
		case "queryBankCard" : queryBankCard(); break;
		case "modifyBankCard" : modifyBankCard(); break;
		case "deleteBankCard" : deleteBankCard(); break;
		case "transferMoney" : transferMoney(); break;
		case "checkBankStatement" : checkBankStatement(); break;
		case "closeAccount" : closeAccount(); break;
		case "clerkAuthorization" : clerkAuthorization(); break;
		case "checkLog" : checkLog(); break;
		case "printLog" : printLog(); break;
		case "checkCash" : checkCash(); break;
		case "printDetails" : printDetails(); break;
		case "clerkExit" : clerkExit(); break;
		case "changeMode" : changeMode(); break;
		case "checkUser" : checkUser(); break;
		case "checkCard" : checkCard(); break;
		
		}
		
		System.out.println("execute buttion clicked");
		
		//checking relevant invariants
		opInvairantPanelUpdate();
	}

	/**
	*    Refresh All
	*/		
	@FXML
	public void refresh(ActionEvent event) {
		
		refreshAll();
		System.out.println("refresh all");
	}		
	
	/**
	*    Save All
	*/			
	@FXML
	public void save(ActionEvent event) {
		
		Stage stage = (Stage) mainPane.getScene().getWindow();
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save State to File");
		fileChooser.setInitialFileName("*.state");
		fileChooser.getExtensionFilters().addAll(
		         new ExtensionFilter("RMCode State File", "*.state"));
		
		File file = fileChooser.showSaveDialog(stage);
		
		if (file != null) {
			System.out.println("save state to file " + file.getAbsolutePath());				
			EntityManager.save(file);
		}
	}
	
	/**
	*    Load All
	*/			
	@FXML
	public void load(ActionEvent event) {
		
		Stage stage = (Stage) mainPane.getScene().getWindow();
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open State File");
		fileChooser.getExtensionFilters().addAll(
		         new ExtensionFilter("RMCode State File", "*.state"));
		
		File file = fileChooser.showOpenDialog(stage);
		
		if (file != null) {
			System.out.println("choose file" + file.getAbsolutePath());
			EntityManager.load(file); 
		}
		
		//refresh GUI after load data
		refreshAll();
	}
	
	
	//precondition unsat dialog
	public void preconditionUnSat() {
		
		Alert alert = new Alert(AlertType.WARNING, "");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(mainPane.getScene().getWindow());
        alert.getDialogPane().setContentText("Precondtion is not satisfied");
        alert.getDialogPane().setHeaderText(null);
        alert.showAndWait();	
	}
	
	//postcondition unsat dialog
	public void postconditionUnSat() {
		
		Alert alert = new Alert(AlertType.WARNING, "");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(mainPane.getScene().getWindow());
        alert.getDialogPane().setContentText("Postcondtion is not satisfied");
        alert.getDialogPane().setHeaderText(null);
        alert.showAndWait();	
	}

	public void thirdpartyServiceUnSat() {
		
		Alert alert = new Alert(AlertType.WARNING, "");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(mainPane.getScene().getWindow());
        alert.getDialogPane().setContentText("third party service is exception");
        alert.getDialogPane().setHeaderText(null);
        alert.showAndWait();	
	}		
	
	
	public void ejectCard() {
		
		System.out.println("execute ejectCard");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: ejectCard in service: ATMSystem ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(atmsystem_service.ejectCard(
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void printReceipt() {
		
		System.out.println("execute printReceipt");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: printReceipt in service: ATMSystem ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(atmsystem_service.printReceipt(
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void withdrawCash() {
		
		System.out.println("execute withdrawCash");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: withdrawCash in service: ATMSystem ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(atmsystem_service.withdrawCash(
			withdrawCash_quantity_t.getText()
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void inputPassword() {
		
		System.out.println("execute inputPassword");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: inputPassword in service: ATMSystem ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(atmsystem_service.inputPassword(
			inputPassword_password_t.getText()
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void inputCard() {
		
		System.out.println("execute inputCard");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: inputCard in service: ATMSystem ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(atmsystem_service.inputCard(
			inputCard_cardID_t.getText()
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void depositFunds() {
		
		System.out.println("execute depositFunds");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: depositFunds in service: ATMSystem ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(atmsystem_service.depositFunds(
			depositFunds_quantity_t.getText()
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void changePassword() {
		
		System.out.println("execute changePassword");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: changePassword in service: ATMSystem ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(atmsystem_service.changePassword(
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void checkBalance() {
		
		System.out.println("execute checkBalance");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: checkBalance in service: ATMSystem ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(atmsystem_service.checkBalance(
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void cardIdentification() {
		
		System.out.println("execute cardIdentification");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: cardIdentification in service: ATMSystem ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(atmsystem_service.cardIdentification(
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void createTransaction() {
		
		System.out.println("execute createTransaction");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: createTransaction in service: ManageTransactionCRUDService ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(managetransactioncrudservice_service.createTransaction(
			createTransaction_withdrewnum_t.getText(),
			createTransaction_balanceafterwithdraw_t.getText()
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void queryTransaction() {
		
		System.out.println("execute queryTransaction");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: queryTransaction in service: ManageTransactionCRUDService ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(managetransactioncrudservice_service.queryTransaction(
			queryTransaction_withdrewNum_t.getText()
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void modifyTransaction() {
		
		System.out.println("execute modifyTransaction");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: modifyTransaction in service: ManageTransactionCRUDService ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(managetransactioncrudservice_service.modifyTransaction(
			modifyTransaction_withdrewnum_t.getText(),
			modifyTransaction_balanceafterwithdraw_t.getText()
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void deleteTransaction() {
		
		System.out.println("execute deleteTransaction");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: deleteTransaction in service: ManageTransactionCRUDService ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(managetransactioncrudservice_service.deleteTransaction(
			deleteTransaction_withdrewNum_t.getText()
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void createUser() {
		
		System.out.println("execute createUser");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: createUser in service: ManageUserCRUDService ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(manageusercrudservice_service.createUser(
			createUser_userid_t.getText(),
			createUser_name_t.getText(),
			createUser_address_t.getText()
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void queryUser() {
		
		System.out.println("execute queryUser");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: queryUser in service: ManageUserCRUDService ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(manageusercrudservice_service.queryUser(
			queryUser_userID_t.getText()
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void modifyUser() {
		
		System.out.println("execute modifyUser");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: modifyUser in service: ManageUserCRUDService ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(manageusercrudservice_service.modifyUser(
			modifyUser_userid_t.getText(),
			modifyUser_name_t.getText(),
			modifyUser_address_t.getText()
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void deleteUser() {
		
		System.out.println("execute deleteUser");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: deleteUser in service: ManageUserCRUDService ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(manageusercrudservice_service.deleteUser(
			deleteUser_userID_t.getText()
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void createBankCard() {
		
		System.out.println("execute createBankCard");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: createBankCard in service: ManageBankCardCRUDService ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(managebankcardcrudservice_service.createBankCard(
			createBankCard_cardid_t.getText(),
			createBankCard_password_t.getText(),
			createBankCard_balance_t.getText(),
			createBankCard_cardstatus_t.getText(),
			createBankCard_catalog_t.getText()
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void queryBankCard() {
		
		System.out.println("execute queryBankCard");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: queryBankCard in service: ManageBankCardCRUDService ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(managebankcardcrudservice_service.queryBankCard(
			queryBankCard_cardID_t.getText()
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void modifyBankCard() {
		
		System.out.println("execute modifyBankCard");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: modifyBankCard in service: ManageBankCardCRUDService ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(managebankcardcrudservice_service.modifyBankCard(
			modifyBankCard_cardid_t.getText(),
			modifyBankCard_password_t.getText(),
			modifyBankCard_balance_t.getText(),
			modifyBankCard_cardstatus_t.getText(),
			modifyBankCard_catalog_t.getText()
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void deleteBankCard() {
		
		System.out.println("execute deleteBankCard");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: deleteBankCard in service: ManageBankCardCRUDService ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(managebankcardcrudservice_service.deleteBankCard(
			deleteBankCard_cardID_t.getText()
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void transferMoney() {
		
		System.out.println("execute transferMoney");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: transferMoney in service: ATMSystem ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(atmsystem_service.transferMoney(
			transferMoney_cardID_t.getText(),
			transferMoney_quantity_t.getText()
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void checkBankStatement() {
		
		System.out.println("execute checkBankStatement");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: checkBankStatement in service: ATMSystem ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(atmsystem_service.checkBankStatement(
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void closeAccount() {
		
		System.out.println("execute closeAccount");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: closeAccount in service: ATMSystem ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(atmsystem_service.closeAccount(
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void clerkAuthorization() {
		
		System.out.println("execute clerkAuthorization");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: clerkAuthorization in service: ATMSystem ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(atmsystem_service.clerkAuthorization(
			clerkAuthorization_clerkID_t.getText(),
			clerkAuthorization_authorizationCode_t.getText()
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void checkLog() {
		
		System.out.println("execute checkLog");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: checkLog in service: ATMSystem ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(atmsystem_service.checkLog(
			checkLog_fromTime_t.getText(),
			checkLog_toTime_t.getText()
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void printLog() {
		
		System.out.println("execute printLog");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: printLog in service: ATMSystem ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(atmsystem_service.printLog(
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void checkCash() {
		
		System.out.println("execute checkCash");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: checkCash in service: ATMSystem ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(atmsystem_service.checkCash(
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void printDetails() {
		
		System.out.println("execute printDetails");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: printDetails in service: ATMSystem ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(atmsystem_service.printDetails(
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void clerkExit() {
		
		System.out.println("execute clerkExit");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: clerkExit in service: ATMSystem ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(atmsystem_service.clerkExit(
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void changeMode() {
		
		System.out.println("execute changeMode");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: changeMode in service: ATMSystem ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(atmsystem_service.changeMode(
			changeMode_pin_t.getText()
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void checkUser() {
		
		System.out.println("execute checkUser");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: checkUser in service: ATMSystem ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(atmsystem_service.checkUser(
			checkUser_userID_t.getText()
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}
	public void checkCard() {
		
		System.out.println("execute checkCard");
		String time = String.format("%1$tH:%1$tM:%1$tS", System.currentTimeMillis());
		log.appendText(time + " -- execute operation: checkCard in service: ATMSystem ");
		
		try {
			//invoke op with parameters
			//return value is primitive type, bind result to label.
			String result = String.valueOf(atmsystem_service.checkCard(
			checkCard_cardID_t.getText()
			));	
			Label l = new Label(result);
			l.setPadding(new Insets(8, 8, 8, 8));
			operation_return_pane.setContent(l);
		    log.appendText(" -- success!\n");
		    //set pre- and post-conditions text area color
		    precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #7CFC00");
		    //contract evaluation result
		    precondition_pane.setText("Precondition: True");
		    postcondition_pane.setText("Postcondition: True");
		    
		    
		} catch (PreconditionException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (PostconditionException e) {
			log.appendText(" -- failed!\n");
			postcondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");	
			postcondition_pane.setText("Postcondition: False");
			postconditionUnSat();
			
		} catch (NumberFormatException e) {
			log.appendText(" -- failed!\n");
			precondition.setStyle("-fx-background-color:#000000; -fx-control-inner-background: #FF0000");
			precondition_pane.setText("Precondition: False");	
			preconditionUnSat();
			
		} catch (Exception e ) {		
			if (e instanceof ThirdPartyServiceException)
				thirdpartyServiceUnSat();
		}
	}




	//select object index
	int objectindex;
	
	@FXML
	TabPane mainPane;

	@FXML
	TextArea log;
	
	@FXML
	TreeView<String> actor_treeview_customer;
	@FXML
	TreeView<String> actor_treeview_bankclerk;
	
	@FXML
	TreeView<String> actor_treeview_administrator;


	@FXML
	TextArea definition;
	@FXML
	TextArea precondition;
	@FXML
	TextArea postcondition;
	@FXML
	TextArea invariants;
	
	@FXML
	TitledPane precondition_pane;
	@FXML
	TitledPane postcondition_pane;
	
	//chosen operation textfields
	List<TextField> choosenOperation;
	String clickedOp;
		
	@FXML
	TableView<ClassInfo> class_statisic;
	@FXML
	TableView<AssociationInfo> association_statisic;
	
	Map<String, ObservableList<AssociationInfo>> allassociationData;
	ObservableList<ClassInfo> classInfodata;
	
	ATMSystem atmsystem_service;
	ThirdPartyServices thirdpartyservices_service;
	ManageBankCardCRUDService managebankcardcrudservice_service;
	ManageUserCRUDService manageusercrudservice_service;
	ManageTransactionCRUDService managetransactioncrudservice_service;
	
	ClassInfo bankcard;
	ClassInfo user;
	ClassInfo transaction;
	ClassInfo bankclerk;
	ClassInfo receipt;
	ClassInfo atm;
	ClassInfo bank;
		
	@FXML
	TitledPane object_statics;
	Map<String, TableView> allObjectTables;
	
	@FXML
	TitledPane operation_paras;
	
	@FXML
	TitledPane operation_return_pane;
	
	@FXML 
	TitledPane all_invariant_pane;
	
	@FXML
	TitledPane invariants_panes;
	
	Map<String, GridPane> operationPanels;
	Map<String, VBox> opInvariantPanel;
	
	//all textfiled or eumntity
	TextField withdrawCash_quantity_t;
	TextField inputPassword_password_t;
	TextField inputCard_cardID_t;
	TextField depositFunds_quantity_t;
	TextField createTransaction_withdrewnum_t;
	TextField createTransaction_balanceafterwithdraw_t;
	TextField queryTransaction_withdrewNum_t;
	TextField modifyTransaction_withdrewnum_t;
	TextField modifyTransaction_balanceafterwithdraw_t;
	TextField deleteTransaction_withdrewNum_t;
	TextField createUser_userid_t;
	TextField createUser_name_t;
	TextField createUser_address_t;
	TextField queryUser_userID_t;
	TextField modifyUser_userid_t;
	TextField modifyUser_name_t;
	TextField modifyUser_address_t;
	TextField deleteUser_userID_t;
	TextField createBankCard_cardid_t;
	TextField createBankCard_password_t;
	TextField createBankCard_balance_t;
	TextField createBankCard_cardstatus_t;
	TextField createBankCard_catalog_t;
	TextField queryBankCard_cardID_t;
	TextField modifyBankCard_cardid_t;
	TextField modifyBankCard_password_t;
	TextField modifyBankCard_balance_t;
	TextField modifyBankCard_cardstatus_t;
	TextField modifyBankCard_catalog_t;
	TextField deleteBankCard_cardID_t;
	TextField transferMoney_cardID_t;
	TextField transferMoney_quantity_t;
	TextField clerkAuthorization_clerkID_t;
	TextField clerkAuthorization_authorizationCode_t;
	TextField checkLog_fromTime_t;
	TextField checkLog_toTime_t;
	TextField changeMode_pin_t;
	TextField checkUser_userID_t;
	TextField checkCard_cardID_t;
	
	HashMap<String, String> definitions_map;
	HashMap<String, String> preconditions_map;
	HashMap<String, String> postconditions_map;
	HashMap<String, String> invariants_map;
	LinkedHashMap<String, String> service_invariants_map;
	LinkedHashMap<String, String> entity_invariants_map;
	LinkedHashMap<String, Label> service_invariants_label_map;
	LinkedHashMap<String, Label> entity_invariants_label_map;
	LinkedHashMap<String, Label> op_entity_invariants_label_map;
	LinkedHashMap<String, Label> op_service_invariants_label_map;
	

	
}
