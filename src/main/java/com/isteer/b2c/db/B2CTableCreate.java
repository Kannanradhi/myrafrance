package com.isteer.b2c.db;

import com.isteer.b2c.config.B2CAppConstant;

public class B2CTableCreate {
	
	public static final String TABLE_B2C_PRODUCT_MASTER = "b2c_product_master";
	public static final String TABLE_B2C_CONTACT_MASTER = "b2c_contact_master";
	public static final String TABLE_B2C_CONTACT_INDIVIDUAL = "b2c_contact_individual";
	public static final String TABLE_B2C_PENDING_BILLS = "b2c_customer_pending_bills";
	public static final String TABLE_B2C_CUSTOMER_CREDITS = "b2c_customer_credits";
	public static final String TABLE_B2C_PENDING_ORDERS = "b2c_pending_orders";
	public static final String TABLE_B2C_COLLECTIONS = "b2c_collections";
	//public static final String TABLE_B2C_COLLECTION_STATUS = "b2c_collection_status";

	public static final String CREATE_TABLE_PRODUCT_MASTER = "CREATE TABLE IF NOT EXISTS "
			
			+ TABLE_B2C_PRODUCT_MASTER 
			
			+ "( mikey INTEGER PRIMARY KEY NOT NULL , "
			+ "part_no VARCHAR(100) NOT NULL , "
			+ "display_code VARCHAR(300) NOT NULL , "
			+ "remark TEXT , "
			+ "color_name VARCHAR(100) DEFAULT '' , "
			+ "list_price NUMERIC DEFAULT '0.00' , "
			+ "standard_price NUMERIC DEFAULT '0.00' , "
			+ "mrp_rate NUMERIC DEFAULT '0.00' , "
			+ "taxPercent NUMERIC DEFAULT '0.00' , "
			+ "manu_name VARCHAR(100) DEFAULT '' , "
			+ "category VARCHAR(100) DEFAULT '' );";
	
	public static enum COLOUMNS_PRODUCT_MASTER {
		
		mikey, part_no, display_code, remark, color_name, list_price, standard_price, mrp_rate, taxPercent, manu_name, category
	}

	public static String[] getColoumnArrayTableProductMaster() {
		
		String[] names = new String[COLOUMNS_PRODUCT_MASTER.values().length];
		int index = 0;

		for (COLOUMNS_PRODUCT_MASTER coloumn : COLOUMNS_PRODUCT_MASTER.values()) {
			names[index++] = coloumn.name();
		}

		return names;
	}
	
	public static final String CREATE_TABLE_CONTACT_MASTER = "CREATE TABLE IF NOT EXISTS "
			
			+ TABLE_B2C_CONTACT_MASTER 
			
			+ "( cmkey int(20) PRIMARY KEY NOT NULL , "
			+ "userkey int(10) DEFAULT NULL , "
			+ "cmp_phone1 varchar(20) , "
			+ "cmp_phone2 varchar(20) , "
			+ "cmp_email varchar(50) , "
			+ "company_name varchar(255) , "
			+ "address1 text , "
			+ "address2 text , "
			+ "address3 text , "
			+ "area varchar(255) , "
			+ "city varchar(100) , "
			+ "state int(11) DEFAULT '0', "
			+ "country varchar(150) , "
			+ "pincode varchar(12) , "
			+ "email varchar(100) , "
			+ "phone1 varchar(20) , "
			+ "phone2 varchar(20) , "
			+ "website varchar(50) , "
			+ "industry varchar(200) DEFAULT NULL , "
			+ "category1 varchar(100) DEFAULT NULL , "
			+ "category2 varchar(100) DEFAULT NULL , "
			+ "category3 varchar(100) DEFAULT NULL , "
			+ "display_code varchar(30) , "
			+ "area_name varchar(100) , "
			+ "first_name varchar(100) , "
			+ "cus_key int(20) );";

	public static enum COLOUMNS_CONTACT_MASTER {
		
		cmkey, userkey, cmp_phone1, cmp_phone2, cmp_email, company_name, address1, address2, address3, area, 
		city, state, country, pincode, email, phone1, phone2, website, industry, category1, category2, category3,
		display_code, area_name, first_name, cus_key
		
	}
		
	public static String[] getColoumnArrayTableContacts() {
		
		String[] names = new String[COLOUMNS_CONTACT_MASTER.values().length];
		int index = 0;

		for (COLOUMNS_CONTACT_MASTER coloumn : COLOUMNS_CONTACT_MASTER.values()) {
			names[index++] = coloumn.name();
		}

		return names;
	}
	
	public static final String CREATE_TABLE_CONTACT_INDIVIDUAL = "CREATE TABLE IF NOT EXISTS "
			
			+ TABLE_B2C_CONTACT_INDIVIDUAL
			
			+ "( con_key int NOT NULL PRIMARY KEY , "
			+ "contact_key int DEFAULT NULL , "
			+ "first_name varchar(100) DEFAULT NULL , "
			+ "last_name varchar(50) DEFAULT NULL , "
			+ "sex varchar(10) DEFAULT NULL , "
			+ "dob varchar(20) DEFAULT NULL , "
			+ "wedding varchar(20) DEFAULT NULL , "
			+ "company_name varchar(50) DEFAULT NULL , "
			+ "designation varchar(50) DEFAULT NULL , "
			+ "address1 text , "
			+ "address2 text , "
			+ "area varchar(255) DEFAULT NULL , "
			+ "city varchar(100) DEFAULT NULL , "
			+ "state int DEFAULT NULL , "
			+ "country varchar(150) DEFAULT NULL , "
			+ "email varchar(100) DEFAULT NULL , "
			+ "phone1 varchar(20) DEFAULT NULL , "
			+ "phone2 varchar(20) DEFAULT NULL , "
			+ "pincode varchar(12) DEFAULT NULL , "
			+ "unit_key int DEFAULT NULL , "
			+ "se_key int DEFAULT NULL , "
			+ "entered_by int DEFAULT NULL , "
			+ "entered_on varchar(20) DEFAULT NULL , "
			+ "last_modified_by int DEFAULT NULL , "
			+ "last_modified_on varchar(20) DEFAULT NULL , "
			+ "active_status int DEFAULT NULL , "
			+ "latitude varchar(15) DEFAULT '0.0' , "
			+ "longitude varchar(15) DEFAULT '0.0' , "
			+ "altitude varchar(15) DEFAULT '0.0' , "

			+ "is_synced_to_server int);";
	
	public static enum COLOUMNS_CONTACT_INDIVIDUAL {
		
		con_key, contact_key, first_name, last_name, sex, dob, wedding, company_name, designation, 
		address1, address2, area, city, state, country, email, phone1, phone2, pincode, unit_key, 
		se_key, entered_by, entered_on, last_modified_by, last_modified_on, active_status, 
		latitude, longitude, altitude,

		is_synced_to_server
	}
	
	public static String[] getColoumnArrayTableContactIndividual() {
		
		String[] names = new String[COLOUMNS_CONTACT_INDIVIDUAL.values().length];
		int index = 0;

		for (COLOUMNS_CONTACT_INDIVIDUAL coloumn : COLOUMNS_CONTACT_INDIVIDUAL.values()) {
			names[index++] = coloumn.name();
		}

		return names;
		
	}
	
	public static final String CREATE_TABLE_PENDING_BILLS = "CREATE TABLE IF NOT EXISTS "
			
			+ TABLE_B2C_PENDING_BILLS
			
			+ "( invoice_key int PRIMARY KEY NOT NULL , "
			+ "customer_key int , "
			+ "invoice_no int , "
			+ "inv_date varchar(10) DEFAULT '' , "
			+ "invoice_amount NUMERIC DEFAULT '0.00' , "
			+ "pending_amount NUMERIC DEFAULT '0.00' , "
			+ "contact_key NUMERIC NOT NULL , "
			+ "type varchar(10) DEFAULT '' );";
    	
	public static enum COLOUMNS_PENDING_BILLS {
		
		invoice_key, customer_key, invoice_no, inv_date, invoice_amount, pending_amount, contact_key, type,map_bill_amount,pay_coll_key
		
	}
	
	public static String[] getColoumnArrayTablePendingBills() {
		
		String[] names = new String[COLOUMNS_PENDING_BILLS.values().length];
		int index = 0;

		for (COLOUMNS_PENDING_BILLS coloumn : COLOUMNS_PENDING_BILLS.values()) {
			names[index++] = coloumn.name();
		}

		return names;
		
	}
	
	public static final String CREATE_TABLE_CUSTOMER_CREDITS = "CREATE TABLE IF NOT EXISTS "
			
			+ TABLE_B2C_CUSTOMER_CREDITS
			
			+ "( customer_key int PRIMARY KEY NOT NULL , "
			+ "credit_days int DEFAULT '0' , "
			+ "credit_used int DEFAULT '0' , "
			+ "unmapped_amount NUMERIC DEFAULT '0.00' , "
			+ "suspense_amount NUMERIC DEFAULT '0.00' , "
			+ "contact_key NUMERIC NOT NULL , "
			+ "max_credit_limit NUMERIC DEFAULT '0.00' , "
			+ "tin_no varchar(50) DEFAULT '');";

	public static enum COLOUMNS_CUSTOMER_CREDITS {
		
		customer_key, credit_days, credit_used, unmapped_amount, suspense_amount, contact_key, max_credit_limit, tin_no
		
	}
	
	public static String[] getColoumnArrayTableCustomerCredits() {
		
		String[] names = new String[COLOUMNS_CUSTOMER_CREDITS.values().length];
		int index = 0;

		for (COLOUMNS_CUSTOMER_CREDITS coloumn : COLOUMNS_CUSTOMER_CREDITS.values()) {
			names[index++] = coloumn.name();
		}

		return names;
		
	}
	
	public static final String CREATE_TABLE_PENDING_ORDERS = "CREATE TABLE IF NOT EXISTS "
			
			+ TABLE_B2C_PENDING_ORDERS
			
			+ "( so_key int NOT NULL , "
			+ "so_item_key int PRIMARY KEY NOT NULL , "
			+ "customer_key int , "
			+ "mi_key int , "
			+ "mi_name varchar(300) DEFAULT '' , "
			+ "date varchar(10) DEFAULT '' , "
			+ "ordered_qty int DEFAULT '0' , "
			+ "contact_key NUMERIC NOT NULL , "
			+ "pending_qty int DEFAULT '0' , "
			+ "purchase_order_type varchar(20) DEFAULT '' , "
			+ "taxPercent NUMERIC DEFAULT '0.00' , "
			+ "is_synced_to_server int DEFAULT '0');";
	
	public static enum COLOUMNS_PENDING_ORDERS {
		
        so_key, so_item_key, customer_key, mi_key, mi_name, date, ordered_qty, contact_key, pending_qty, 
        purchase_order_type, taxPercent, is_synced_to_server
		
	}
	
	public static String[] getColoumnArrayTablePendingOrders() {
		
		String[] names = new String[COLOUMNS_PENDING_ORDERS.values().length];
		int index = 0;

		for (COLOUMNS_PENDING_ORDERS coloumn : COLOUMNS_PENDING_ORDERS.values()) {
			names[index++] = coloumn.name();
		}

		return names;
		
	}
	
	public static final String CREATE_TABLE_COLLECTIONS = "CREATE TABLE IF NOT EXISTS "
			
			+ TABLE_B2C_COLLECTIONS
			+ "( pay_coll_key int NOT NULL , "
			+ "sc_ledger_key int DEFAULT '-1' , "
			+ "cus_key int NOT NULL , "
			+ "amount NUMERIC DEFAULT '0.00' , "
			+ "trans_date varchar(10) DEFAULT '' , "
			+ "entered_date_time varchar(10) DEFAULT '' , "
			+ "payment_mode varchar(10) DEFAULT '" + B2CAppConstant.PAY_CASH +"' , "
			+ "receipt_no varchar(20) DEFAULT '' , "
			+ "cheque_no varchar(10) DEFAULT '' , "
			+ "cheque_date varchar(10) DEFAULT '' , "
			+ "bank varchar(300) DEFAULT '' , "
			+ "contact_key NUMERIC NOT NULL , "
			+ "remarks varchar(300) DEFAULT '' , "
			+ "is_synced_to_server int DEFAULT '0');";
	
	public static enum COLOUMNS_COLLECTIONS {
		
		pay_coll_key, sc_ledger_key, cus_key, amount, trans_date, entered_date_time, payment_mode, receipt_no, cheque_no, cheque_date, bank, 
		contact_key, remarks, is_synced_to_server
		
	}
	
	public static String[] getColoumnArrayTableCollections() {
		
		String[] names = new String[COLOUMNS_COLLECTIONS.values().length];
		int index = 0;

		for (COLOUMNS_COLLECTIONS coloumn : COLOUMNS_COLLECTIONS.values()) {
			names[index++] = coloumn.name();
		}

		return names;
		
	}
	
/*	public static final String CREATE_TABLE_COLLECTION_STATUS = "CREATE TABLE IF NOT EXISTS "
			
			+ TABLE_B2C_COLLECTION_STATUS
			+ "( cus_key int NOT NULL , "
			+ "contact_key int NOT NULL , "
			+ "suspense_amount NUMERIC DEFAULT '0.00' , "
			+ "unmapped_amount NUMERIC DEFAULT '0.00');";
	
	public static enum COLOUMNS_COLLECTION_STATUS {
		
		cus_key, contact_key, suspense_amount, unmapped_amount
		
	}
	
	public static String[] getColoumnArrayTableCollectionStatus() {
		
		String[] names = new String[COLOUMNS_COLLECTION_STATUS.values().length];
		int index = 0;

		for (COLOUMNS_COLLECTION_STATUS coloumn : COLOUMNS_COLLECTION_STATUS.values()) {
			names[index++] = coloumn.name();
		}

		return names;
		
	}
	*/
}