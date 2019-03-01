package com.isteer.b2c.room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;

import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.dao.Attendence_DAO;
import com.isteer.b2c.dao.BillData_DAO;
import com.isteer.b2c.dao.CollectionData_DAO;
import com.isteer.b2c.dao.CreditData_DAO;
import com.isteer.b2c.dao.CustomerData_DAO;
import com.isteer.b2c.dao.CustomerIndividual_DAO;
import com.isteer.b2c.dao.EventData_DAO;
import com.isteer.b2c.dao.LocationData_DAO;
import com.isteer.b2c.dao.PendingOrderData_DAO;
import com.isteer.b2c.dao.ProductData_DAO;
import com.isteer.b2c.dao.SpancopData_DAO;
import com.isteer.b2c.model.AttendenceData;
import com.isteer.b2c.model.BillData;
import com.isteer.b2c.model.CollectionData;
import com.isteer.b2c.model.CreditData;
import com.isteer.b2c.model.CustomerData;
import com.isteer.b2c.model.CustomerIndividual;
import com.isteer.b2c.model.EventData;
import com.isteer.b2c.model.LocationData;
import com.isteer.b2c.model.PendingOrderData;
import com.isteer.b2c.model.ProductData;
import com.isteer.b2c.model.SpancopData;

@Database(entities = {BillData.class, CreditData.class,CustomerData.class,
        CustomerIndividual.class, PendingOrderData.class,ProductData.class, CollectionData.class,
        EventData.class, SpancopData.class, LocationData.class, AttendenceData.class}
        ,version = 10)
public abstract class RoomDB extends RoomDatabase {

    public abstract CustomerData_DAO customerData_dao();
    public abstract CustomerIndividual_DAO customerindividual_dao();
    public abstract BillData_DAO billData_dao();
    public abstract CreditData_DAO creditData_dao();
    public abstract PendingOrderData_DAO pendingorderdata_dao();
    public abstract ProductData_DAO productData_dao();
    public abstract CollectionData_DAO collectiondata_dao();
    public abstract EventData_DAO eventdata_dao();
    public abstract SpancopData_DAO  spancopdata_dao();
    public abstract LocationData_DAO locationData_dao();
    public abstract Attendence_DAO attendence_dao();

   public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE b2c_contact_master");
            database.execSQL("CREATE TABLE IF NOT EXISTS `b2c_contact_master` (`cmkey` INTEGER NOT NULL, `GST_NO` TEXT, `userkey` TEXT, `cmp_phone1` TEXT, `cmp_phone2` TEXT, `cmp_email` TEXT, `company_name` TEXT, `address1` TEXT, `address2` TEXT, `address3` TEXT, `area` TEXT, `city` TEXT, `state` TEXT, `country` TEXT, `pincode` TEXT, `email` TEXT, `phone1` TEXT, `phone2` TEXT, `website` TEXT, `industry` TEXT, `category1` TEXT, `category2` TEXT, `category3` TEXT, `display_code` TEXT, `area_name` TEXT, `first_name` TEXT, `cus_key` TEXT, `latitude` TEXT, `longitude` TEXT, `is_synced_to_server` INTEGER NOT NULL DEFAULT 1, PRIMARY KEY(`cmkey`))");



        }
    };

   public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE b2c_pending_orders");

            database.execSQL("CREATE TABLE IF NOT EXISTS `b2c_pending_orders` (`so_key` INTEGER NOT NULL, `so_item_key` INTEGER NOT NULL, `customer_key` TEXT, `mi_key` TEXT, `mi_name` TEXT, `tax_percent` TEXT, `date` TEXT, `ordered_qty` TEXT, `contact_key` TEXT, `pending_qty` TEXT, `purchase_order_type` TEXT, `is_synced_to_server` INTEGER NOT NULL, PRIMARY KEY(`so_item_key`))");


        }
    };
   public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER  TABLE b2c_contact_master ADD COLUMN max_credit_limit TEXT DEFAULT 0.00");
            database.execSQL("ALTER  TABLE b2c_contact_master ADD COLUMN available_credit TEXT DEFAULT 0.00");



        }
    };
    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE aerp_attendence_log");

            database.execSQL("CREATE TABLE IF NOT EXISTS `aerp_attendence_log` (`att_key` INTEGER NOT NULL, `user_key` TEXT, `date` TEXT, `start_time` TEXT, `stop_time` TEXT, `status` TEXT, `update_status` TEXT, `start_latitude` REAL NOT NULL, `start_longitude` REAL NOT NULL, `stop_latitude` REAL NOT NULL, `stop_longitude` REAL NOT NULL, PRIMARY KEY(`att_key`))");

        }
    };
    public static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE b2c_collections");

            database.execSQL("CREATE TABLE IF NOT EXISTS `b2c_collections` (`pay_coll_key` INTEGER NOT NULL, `customer_name` TEXT, `sc_ledger_key` TEXT, `cus_key` TEXT, `amount` TEXT, `trans_date` TEXT, `entered_date_time` TEXT, `payment_mode` TEXT, `receipt_no` TEXT, `cheque_no` TEXT, `cheque_date` TEXT, `bank` TEXT, `contact_key` TEXT, `remarks` TEXT, `is_synced_to_server` INTEGER NOT NULL, PRIMARY KEY(`pay_coll_key`))");

        }
    };
    public static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE aerp_location_log");

            database.execSQL("CREATE TABLE IF NOT EXISTS `aerp_location_log` (`loc_key` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `user_key` TEXT, `date_time` TEXT, `altitude` REAL NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `update_status` TEXT, `battery_level` INTEGER NOT NULL)");

        }
    };
    public static final Migration MIGRATION_7_8 = new Migration(7,8) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE b2c_pending_orders ADD COLUMN entered_on TEXT");


        }
    };
    public static final Migration MIGRATION_8_9 = new Migration(8,9) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE b2c_contact_master ADD COLUMN credit_days INTEGER DEFAULT 0 NOT NULL");


        }
    };
    public static final Migration MIGRATION_9_10 = new Migration(9,10) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE b2c_contact_master ADD COLUMN amount_exceed TEXT");


        }
    };
}
