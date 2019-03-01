package com.isteer.b2c.db;

public class DSRTableCreate {
	
	public static final String TABLE_AERP_EVENT_MASTER = "aerp_event_master";
	public static final String TABLE_AERP_CONTACT_MASTER = "aerp_contact_master";
	public static final String TABLE_AERP_CUSTOMERWISE_TARGET = "aerp_customer_wise_target";
	public static final String TABLE_AERP_MASTER_ITEM = "aerp_master_item";
	public static final String TABLE_AERP_ITEM_MSTR = "aerp_item_mstr";
	public static final String TABLE_AERP_PROSPECT_MSTR = "aerp_prospect_mstr";
	public static final String TABLE_AERP_APP_CONFIG = "aerp_app_config";
	public static final String TABLE_AERP_ATTENDENCE_LOG = "aerp_attendence_log";
	public static final String TABLE_AERP_LOCATION_LOG = "aerp_location_log";
	public static final String TABLE_AERP_ALARM_LOG = "aerp_alarm_log";
	public static final String TABLE_SEM_PRODUCT_MASTER = "sem_product_master";

	public static final String CREATE_TABLE_EVENT_MASTER = "CREATE TABLE IF NOT EXISTS "
			
			+ TABLE_AERP_EVENT_MASTER
			
			+ "( event_key int NOT NULL PRIMARY KEY , "
			+ "event_user_key int , " 
			+ "event_type varchar(100) DEFAULT 'Visit' , "
			+ "event_title varchar(100) , "
			+ "from_date_time varchar(20) , "
			+ "to_date_time varchar(20) , "
			+ "event_description text DEFAULT '' , "
			+ "status int DEFAULT 'Pending' , "
			+ "cmkey int , "
			+ "area varchar(100) , "
			+ "latitude varchar(15) DEFAULT '0.0' , "
			+ "longitude varchar(15) DEFAULT '0.0' , "
			+ "altitude varchar(15) DEFAULT '0.0' , "
			+ "visit_update_time varchar(20) , "
			+ "action_response text DEFAULT '' , "
			+ "plan text DEFAULT '' , "
			+ "objective text DEFAULT '' , "
			+ "strategy text DEFAULT '' , "
			+ "customer_name  varchar(100) , "
			+ "preparation text DEFAULT '' , "
			+ "event_visited_latitude varchar(15) DEFAULT '0.0' , "
			+ "event_visited_longitude varchar(15) DEFAULT '0.0' , "
			+ "event_visited_altitude varchar(15) DEFAULT '0.0' , "
			
			+ "entered_on varchar(20) , "
			+ "completed_on varchar(20) , "
			+ "cancelled_on varchar(20) , "
			
			+ "event_month varchar(15) , "
			+ "event_date varchar(15) , "
			+ "event_date_absolute int , "

			+ "action text DEFAULT '' , "
			+ "is_synced_to_server int DEFAULT '0' , "
			
			+ "mins_of_meet text DEFAULT '' ,"
			+ "competition_pricing text DEFAULT '' ,"
			+ "feedback text DEFAULT '' ,"
			+ "order_taken text DEFAULT '' ,"
			+ "product_display text DEFAULT '' ,"
			+ "promotion_activated text DEFAULT '' ,"
			+ "visited_con_key int  );";

	public static enum COLOUMNS_EVENT_MASTER {
		
		event_key, event_user_key, event_type, event_title, from_date_time, to_date_time, event_description, status, cmkey, area,
		latitude, longitude, altitude, visit_update_time, action_response, plan, objective, strategy, customer_name,
		preparation, event_visited_latitude, event_visited_longitude, event_visited_altitude, 
		
		entered_on, completed_on, cancelled_on, 
		
		event_month, event_date, event_date_absolute, anticipate, is_synced_to_server, mins_of_meet,competition_pricing,feedback
		,order_taken,product_display,promotion_activated,visited_con_key

		}
	public static String[] getcoloumns_event_master() {

		String[] names = new String[COLOUMNS_EVENT_MASTER.values().length];
		int index = 0;

		for (COLOUMNS_EVENT_MASTER coloumn : COLOUMNS_EVENT_MASTER.values()) {
			names[index++] = coloumn.name();
		}

		return names;
	}
	
	public static final String CREATE_TABLE_CONTACT_MASTER = "CREATE TABLE IF NOT EXISTS "
			
			+ TABLE_AERP_CONTACT_MASTER 
			
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
			+ "area_name varchar(100) );";
	
	public static enum COLOUMNS_CONTACT_MASTER {
		
		cmkey, userkey, cmp_phone1, cmp_phone2, cmp_email, company_name, address1, address2, address3, area, 
		city, state, country, pincode, email, phone1, phone2, website, industry, category1, category2, category3, display_code, area_name
		
	}
		
	public static String[] getColoumnArrayTableContacts() {
		
		String[] names = new String[COLOUMNS_CONTACT_MASTER.values().length];
		int index = 0;

		for (COLOUMNS_CONTACT_MASTER coloumn : COLOUMNS_CONTACT_MASTER.values()) {
			names[index++] = coloumn.name();
		}

		return names;
	}
	
	public static final String CREATE_TABLE_CUSTOMERWISE_TARGET = "CREATE TABLE IF NOT EXISTS "
			
			+ TABLE_AERP_CUSTOMERWISE_TARGET 
			  
			+ "( cp_key int PRIMARY KEY NOT NULL , "
			+ "cmkey int DEFAULT '0' , "
			+ "sekey int DEFAULT '0' , "
			+ "manu_code varchar(20) DEFAULT '' , "
			+ "item_code varchar(20) DEFAULT '' , "
			+ "prod_key int DEFAULT '0' , "
			+ "mapping_status varchar(30) DEFAULT NULL , "
			+ "actual_qty int DEFAULT '0' , "
			+ "annual_qty int DEFAULT '0' , "
			+ "type varchar(20) DEFAULT '' , "
			+ "uom varchar(30) DEFAULT NULL , "
			+ "buying_frequency varchar(20) DEFAULT NULL , "
			+ "added_date varchar(20) DEFAULT NULL , "
			+ "added_by int DEFAULT '0' , "	
			+ "target_date varchar(20) DEFAULT NULL , "
			+ "replaced_brand varchar(30) DEFAULT '' , "
			+ "replaced_product varchar(30) DEFAULT NULL , "
			+ "lost_date varchar(20) DEFAULT NULL , "
			+ "lost_reason varchar(30) DEFAULT NULL , "
			+ "insert_stat int DEFAULT '0' , "
			+ "modified_by int DEFAULT '0' , "
			+ "modified_on varchar(20) , "
			+ "status varchar(20) DEFAULT '' , "
			+ "brand varchar(30) , "
			+ "prod_name varchar(50) , "
			+ "category_name varchar(50) , "
			+ "map_status varchar(50) , "
			+ "remarks varchar(300) DEFAULT '' , " 
	
			+ "is_synced_to_server int);";
	
	public static enum COLOUMNS_CUSTOMERWISE_TARGET {
		
		cp_key, cmkey, sekey, manu_code, item_code, prod_key, mapping_status, actual_qty, annual_qty, type, 
		uom, buying_frequency, added_date, added_by, target_date, replaced_brand, replaced_product, lost_date,
		lost_reason, insert_stat, modified_by, modified_on, status, brand, prod_name, category_name, map_status, remarks, 
		
		is_synced_to_server
	}
		
	public static final String CREATE_TABLE_SEM_PRODUCT_MASTER = "CREATE TABLE IF NOT EXISTS "
			
			+ TABLE_SEM_PRODUCT_MASTER 
			
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
	
	public static enum COLOUMNS_SEM_PRODUCT_MASTER {
		
		mikey, part_no, display_code, remark, color_name, list_price, standard_price, mrp_rate, taxPercent, manu_name, category
	}
		
	public static String[] getColoumnsTableProductMaster() {
		
		String[] names = new String[COLOUMNS_SEM_PRODUCT_MASTER.values().length];
		int index = 0;

		for (COLOUMNS_SEM_PRODUCT_MASTER coloumn : COLOUMNS_SEM_PRODUCT_MASTER.values()) {
			names[index++] = coloumn.name();
		}

		return names;
	}
	
	public static final String CREATE_TABLE_EVENT_CALENDAR = "CREATE TABLE IF NOT EXISTS aerp_event_calender45522222( event_key int NOT NULL PRIMARY KEY ,event_user_key int, title varchar(50), event_type varchar(100), event_category_key int,from_date_time varchar(20) ,to_date_time varchar(20),event_repeat_config varchar(50),repeat_condition varchar(20) ,event_description text,status int,parent_event_key int,child_event_key int,entered_by int,entered_on varchar(20),last_modified_by int ,last_modified_on varchar(20),completed_by int,completed_on varchar(20),cancelled_by int,cancelled_on varchar(20),dsr_status varchar(25),followup_key int,cmkey int,latitude varchar(15),longitude varchar(15),altitude varchar(15), serverstatus varhcar(50));";
	
	public static final String CREATE_TABLE_CONTACT_SALES_EXECUTIVE = "CREATE TABLE IF NOT EXISTS aerp_contact_sales_executive(key INTEGER PRIMARY KEY   AUTOINCREMENT, keyno int(20),cmkey int(20) ,sekey int(20),remarks varchar(100) ,brand varchar(100));";
	
	public static final String CREATE_TABLE_VISIT_COUNT = "CREATE TABLE IF NOT EXISTS aerp_visit_count(visit_count int,userkey int,cmkey int);";
	
	//public static final String CREATE_TABLE_CUSTOMERWISE_TARGET = "CREATE TABLE IF NOT EXISTS aerp_customer_wise_target(cp_key int PRIMARY KEY,cmkey int,sekey int,category varchar(30) ,spancopstatus varchar(30) ,productname varchar(30) ,manu_code varchar(20),item_code varchar(20),prod_key int,mapping_status varchar,actual_qty int,annual_qty int,type varchar(20) ,uom varchar(30),buying_frequency varchar(20) ,added_date varchar(20) ,added_by int ,target_date varchar(20) ,replaced_brand varchar(30) ,replaced_product varchar(30) ,lost_date varchar(20) ,lost_reason varchar(30) ,insert_stat int(10) ,modified_by int ,modified_on varchar(50),status varchar(20) );";
	
	public static final String CREATE_TABLE_NOTIFICATION_SEND_LOG = "CREATE TABLE IF NOT EXISTS aerp_notification_send_log(key INTEGER PRIMARY KEY   AUTOINCREMENT,nsl_key int(11) NOT NULL ,sent_to text,sent_to_addrs text,sent_subject text, sent_body text,sent_status text, send_by int(7) DEFAULT '0',send_on varchar(20) DEFAULT '');";
	
	public static final String CREATE_TABLE_ENQ_CUST_INFO = "CREATE TABLE if not exists enq_cust_info(key INTEGER PRIMARY KEY   AUTOINCREMENT,ENQ_KEY int(10) NOT NULL, unit_key int(11) DEFAULT '0',ENQ_NO int(25) NOT NULL,ENQ_DATE varchar(8) DEFAULT NULL,ENQ_CUST_NAME varchar(50) DEFAULT NULL,ENQ_CUST_ADD1 varchar(200) DEFAULT '');";
	
	public static final String CREATE_TABLE_ENQ_ITEM_DESC = "CREATE TABLE IF NOT EXISTS enq_item_desc(key INTEGER PRIMARY KEY   AUTOINCREMENT,ENQ_KEY int(10) DEFAULT NULL,ENQ_CHILD_KEY int(10) NOT NULL,MI_KEY_REF int(11) DEFAULT '0',ENQ_QTY int(15) DEFAULT NULL,MESSURMENT varchar(25) DEFAULT NULL,COMM_RATE double(100) DEFAULT NULL,ENQ_ITEM_TOTAL double(100) DEFAULT NULL,ENQ_DESC varchar(1500) DEFAULT NULL,ENQ_REMARKS varchar(300) DEFAULT '',NON_STD_ITEM text,TAX_PERCENT varchar(12) DEFAULT NULL);";
	
	public static final String CREATE_TABLE_CONTACT_FOLLOW1 = "CREATE TABLE IF NOT EXISTS aerp_contact_follow1(plan varchar(50),objective varchar(50),preparation varchar(50),strategy varchar(50),action_date date DEFAULT NULL,action_type varchar(50) DEFAULT NULL,customer_name varchar(255) DEFAULT NULL,action_response text,reschedule_date date DEFAULT NULL,action_by varchar(100) DEFAULT NULL,action_byid int unsigned DEFAULT NULL,entered_by varchar(100) DEFAULT NULL,status varchar(100) DEFAULT NULL,purpose varchar(20) DEFAULT NULL,action_required_by varchar(200) DEFAULT NULL,act_enterd_by varchar(40) DEFAULT NULL,act_enterd_date date DEFAULT NULL,act_request_date date DEFAULT NULL,completed_by int DEFAULT '0',completed_on varchar(20) DEFAULT '',enq_key  DEFAULT '0',cancel_by  DEFAULT '0',cancel_on varchar(20) DEFAULT '', event_key int DEFAULT '0');";
	
	public static final String CREATE_TABLE_CONTACT_FOLLOW = "CREATE TABLE IF NOT EXISTS aerp_contact_follow(plan varchar(50),objective varchar(50),preparation varchar(50),strategy varchar(50),action_date date DEFAULT NULL,action_type varchar(50) DEFAULT NULL,customer_name varchar(255) DEFAULT NULL,action_response text,reschedule_date date DEFAULT NULL,action_by varchar(100) DEFAULT NULL,action_byid int unsigned DEFAULT NULL,entered_by varchar(100) DEFAULT NULL,status varchar(100) DEFAULT NULL,purpose varchar(20) DEFAULT NULL,action_required_by varchar(200) DEFAULT NULL,act_enterd_by varchar(40) DEFAULT NULL,act_enterd_date date DEFAULT NULL,act_request_date date DEFAULT NULL,completed_by int DEFAULT '0',completed_on varchar(20) DEFAULT '',enq_key  DEFAULT '0',cancel_by  DEFAULT '0',cancel_on varchar(20) DEFAULT '', event_key int DEFAULT '0');";

	/*Aerp Master Item Table */
	public static final String CREATE_TABLE_AERP_MASTER_ITEM = "CREATE TABLE IF NOT EXISTS aerp_master_item(MI_KEY INTEGER PRIMARY KEY NOT NULL,U_ITEM_CODE varchar(16) NOT NULL,ITEM_CODE varchar(6) DEFAULT NULL,THICKNESS_DIA varchar(3) DEFAULT NULL,COLOR_CODE varchar(2) DEFAULT NULL,MANU_CODE varchar(3) DEFAULT NULL,MATERIAL_TYPE_CODE varchar(2) DEFAULT NULL,item_key int(5) DEFAULT '0',thickness_dia_key int(5) DEFAULT '0',color_key int(5) DEFAULT '0',manu_key int(5) DEFAULT '0',material_type_key int(5) DEFAULT '0',REMARKS text NOT NULL,SHORT_DESC varchar(100) DEFAULT NULL,LONG_DESC text,tally_item_desc varchar(100) DEFAULT NULL,TYPE int(11) DEFAULT NULL,MIN_INVENTARY double(18,2) DEFAULT '0.00',MIN_REODR_QTY double(18,2) DEFAULT '0.00',MAX_REODR_QTY double(18,2) DEFAULT '0.00',MAX_INVENTAR double(18,2) DEFAULT '0.00',LIST_PRICE double(18,2) DEFAULT NULL,standard_price double(18,2) DEFAULT NULL,mrp_rate double(18,2) DEFAULT '0.00',basic_pur_cost double(18,2) DEFAULT '0.00',stock_trnf_price double(18,2) DEFAULT '0.00',item_moving_status varchar(5) DEFAULT NULL,BOM int(11) DEFAULT '0',CATALOG_NO varchar(60) DEFAULT NULL,MIN_REODR_LEVEL double(18,2) DEFAULT NULL,MAX_STOCK_LEVEL double(18,2) DEFAULT NULL,item_type varchar(5) DEFAULT NULL,item_type_key int(5) DEFAULT NULL,category7_key int(5) DEFAULT NULL,grade_desc varchar(25) DEFAULT NULL,SHORT_NAME varchar(20) DEFAULT NULL,DISORDER int(11) DEFAULT NULL,display_code varchar(75) DEFAULT NULL,ACTIVE_STATUS int(11) DEFAULT NULL,tariff_sh_no varchar(15) DEFAULT NULL,lp_tax_key int(5) DEFAULT NULL,op_tax_key int(5) DEFAULT NULL,discount_per double(5,2) DEFAULT '0.00',item_base_unit varchar(5) DEFAULT '',item_package_unit varchar(5) DEFAULT '',item_pack_to_base_conversion decimal(5,2) DEFAULT '1.00',created_by int(11) DEFAULT '0',created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,modified_by int(11) DEFAULT '0',modified_on datetime DEFAULT '0000-00-00 00:00:00',rate double(18,4) DEFAULT '0.0000',is_non_standard char(1) DEFAULT 'N',mi_post_status int(1) DEFAULT '0');";
	
	public static enum COLOUMNS_AERP_MASTER_ITEM {
		MI_KEY,U_ITEM_CODE,ITEM_CODE,THICKNESS_DIA,COLOR_CODE,MANU_CODE,MATERIAL_TYPE_CODE,item_key,thickness_dia_key,color_key,manu_key,material_type_key,REMARKS,                    
		SHORT_DESC,LONG_DESC,tally_item_desc,TYPE,MIN_INVENTARY,MIN_REODR_QTY,MAX_REODR_QTY,MAX_INVENTAR,LIST_PRICE,standard_price,mrp_rate,basic_pur_cost,             
		stock_trnf_price,item_moving_status,BOM,CATALOG_NO,MIN_REODR_LEVEL,MAX_STOCK_LEVEL,item_type,item_type_key,category7_key,grade_desc,SHORT_NAME,                 
		DISORDER,display_code,ACTIVE_STATUS,tariff_sh_no,lp_tax_key,op_tax_key,discount_per,item_base_unit,item_package_unit,item_pack_to_base_conversion,
		created_by,created_on,modified_by,modified_on,rate,is_non_standard,mi_post_status             
	}

	/*Aerp Item Mstr Table */
	public static final String CREATE_TABLE_AERP_ITEM_MSTR = "CREATE TABLE IF NOT EXISTS aerp_item_mstr(s_no INTEGER PRIMARY KEY NOT NULL,ref_no int(11) NOT NULL,item_code varchar(6) NOT NULL,item_type varchar(2) DEFAULT NULL,item_description varchar(50) DEFAULT NULL,item_shortname varchar(50) DEFAULT '',mfgr_ref_key int(11) NOT NULL,item_unit int(11) DEFAULT NULL,entered_by int(11) DEFAULT '0',entered_on varchar(20) DEFAULT '',last_modified_by int(11) DEFAULT '0',last_modified_on varchar(20) DEFAULT '');";
	
	public static enum COLOUMNS_AERP_ITEM_MSTR {
		s_no,ref_no,item_code,item_type,item_description,item_shortname,mfgr_ref_key,item_unit,entered_by,entered_on,last_modified_by,last_modified_on
	}
	
	/*Aerp Prospect Mstr Table */
	public static final String CREATE_TABLE_AERP_PROSPECT_MSTR = "CREATE TABLE IF NOT EXISTS aerp_prospect_mstr(pros_key  INTEGER PRIMARY KEY AUTOINCREMENT,pros_name varchar(20) DEFAULT ' ',status varchar(20) DEFAULT ' ',enteredBy int(5) DEFAULT '0',enteredOn varchar(20) DEFAULT '',last_modified_by int(5) DEFAULT '0',last_modified_on varchar(20) DEFAULT '');";
	
	public static enum COLOUMNS_AERP_PROSPECT_MSTR {
		pros_key,pros_name,status,enteredBy,enteredOn,last_modified_by,last_modified_on
	}
	
	/*Aerp App Config*/
	public static final String CREATE_TABLE_AERP_APP_CONFIG = "CREATE TABLE IF NOT EXISTS aerp_app_config(config_key  INTEGER PRIMARY KEY AUTOINCREMENT,config_name varchar(20) DEFAULT ' ',config_value varchar(20) DEFAULT ' ');";
	
	public static enum COLOUMNS_AERP_APP_CONFIG {
		config_key,config_name,config_value
	}

	/*Aerp AttendenceData Log*/
	public static final String CREATE_TABLE_AERP_ATTENDENCE_LOG = "CREATE TABLE IF NOT EXISTS aerp_attendence_log(att_key  INTEGER PRIMARY KEY AUTOINCREMENT,user_key  varchar(10) DEFAULT ' ',date varchar(10) DEFAULT ' ',start_time varchar(10) DEFAULT ' ',stop_time varchar(10) DEFAULT ' ',status varchar(10) DEFAULT ' ',update_status varchar(10) DEFAULT ' ');";
	
	public static enum COLOUMNS_AERP_ATTENDENCE_LOG {
		att_key,user_key,date,start_time,stop_time,status,update_status
	}
	
	
	/*Aerp Location Log*/
	public static final String CREATE_TABLE_AERP_LOCATION_LOG = "CREATE TABLE IF NOT EXISTS aerp_location_log(loc_key  INTEGER PRIMARY KEY AUTOINCREMENT,user_key  varchar(10) DEFAULT ' ',date_time varchar(20) DEFAULT ' ',longitude NUMERIC DEFAULT ' ',latitude NUMERIC DEFAULT ' ',altitude NUMERIC DEFAULT ' ',update_status varchar(20) DEFAULT '');";
	
	public static enum COLOUMNS_AERP_LOCATION_LOG {
		loc_key,user_key,date_time,longitude,latitude,altitude,update_status,battery_level
	}
	
	/*Aerp Alarm Log*/
	public static final String CREATE_TABLE_AERP_ALARM_LOG = "CREATE TABLE IF NOT EXISTS aerp_alarm_log(ala_key  INTEGER PRIMARY KEY AUTOINCREMENT,user_key  varchar(10) DEFAULT ' ',start_date_time varchar(20) DEFAULT ' ',off_date_time varchar(20) DEFAULT ' ',longitude NUMERIC DEFAULT ' ',latitude NUMERIC DEFAULT ' ',altitude NUMERIC DEFAULT ' ',update_status varchar(20) DEFAULT '',record_status varchar(20) DEFAULT ' ');";
	
	public static enum COLOUMNS_AERP_ALARM_LOG {
		ala_key,user_key,start_date_time,off_date_time,longitude,latitude,altitude,update_status,record_status
	}
	
	
}