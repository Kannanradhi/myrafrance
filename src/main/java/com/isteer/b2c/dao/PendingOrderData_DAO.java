package com.isteer.b2c.dao;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.isteer.b2c.model.BillData;
import com.isteer.b2c.model.CustomerIndividual;
import com.isteer.b2c.model.EventData;
import com.isteer.b2c.model.OrderNewData;
import com.isteer.b2c.model.PendingOrderData;

import java.util.ArrayList;
import java.util.List;


@Dao
public interface PendingOrderData_DAO {

    @Update
    void updatePendingOrderData(PendingOrderData data);

    @Insert
    Long insertPendingOrderData(PendingOrderData customerData);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllPendingOrderData(List<PendingOrderData> customerData);

    @Query("Delete FROM  b2c_pending_orders  ")
    void clearTable();

    @Query("SELECT count(*) FROM b2c_pending_orders WHERE so_key = :id")
    boolean isOldEntry(int id);

    @Query("SELECT * FROM b2c_pending_orders WHERE customer_key = :cus_key and ordered_qty != 0 order by date desc,so_item_key desc ")
    List<PendingOrderData> fetchAllOrders(String cus_key);

    @Query("SELECT * FROM b2c_pending_orders WHERE customer_key = :cus_key and ordered_qty != 0 order by date desc,so_item_key desc ")
    DataSource.Factory<Integer,PendingOrderData> fetchAllOrdersPaged(String cus_key);

    @Query("select a.customer_key,b.cus_key,b.cmkey,b.cmkey as contact_key ,b.company_name ,b.area_name ,"+
            "sum(a.pending_qty) as quantity, a.mi_key,a.pending_qty,a.date,a.ordered_qty,a.mi_name ,d.list_price  ,"+
            "d.taxPercent ,a.pending_qty  from b2c_pending_orders a,b2c_contact_master b,b2c_product_master d "+
            " where a.customer_key =  b.cus_key and a.mi_key =  d.mikey " +
            "group by  a.customer_key order by  b.company_name ")
    DataSource.Factory<Integer,OrderNewData> getActionOrder();

@Query("select a.customer_key,b.cus_key,b.cmkey,b.cmkey as contact_key ,b.company_name ,b.area_name as area_name" +
        ",sum(a.pending_qty) as quantity,b.cus_key , " +
        "a.mi_key,a.pending_qty,a.date,a.ordered_qty,a.mi_name , " +
        "c.list_price as list_price,c.taxPercent as taxPercent, "  +
        "a.pending_qty as orderValue  from b2c_pending_orders a,b2c_contact_master b,b2c_product_master c" +
        " where a.customer_key =  b.cus_key and  a.mi_key =  c.mikey  "+
        "group by  a.so_item_key order by  a.date desc ,so_item_key desc ")
DataSource.Factory<Integer,OrderNewData> getReportOrder();

@Query("select a.customer_key,b.cus_key,b.cmkey,b.cmkey as contact_key ,b.company_name ,b.area_name as area_name" +
        ",sum(a.pending_qty) as quantity,b.cus_key , " +
        "a.mi_key,a.pending_qty,a.date,a.ordered_qty,a.mi_name , " +
        "c.list_price as list_price,c.taxPercent as taxPercent, "  +
        "a.pending_qty as orderValue,a.so_item_key  from b2c_pending_orders a,b2c_contact_master b,b2c_product_master c" +
        " where a.customer_key =  b.cus_key and  a.mi_key =  c.mikey  and a.date between :fromdate and :todate "+
        "group by  a.so_item_key order by  a.date desc ,so_item_key desc")
    DataSource.Factory<Integer,OrderNewData> getReportOrderByDate(String fromdate,String todate);

@Query("select sum(a.pending_qty) as quantity,a.pending_qty,a.date,a.ordered_qty, c.taxPercent ,c.list_price " +
         "from b2c_pending_orders a,b2c_contact_master b,b2c_product_master c " +
        " where a.customer_key =  b.cus_key and " +
        "a.mi_key =  c.mikey and a.date LIKE :todaydate group by  a.so_item_key order by  a.customer_key ")

   List<OrderNewData> gettotalSale( String todaydate);

@Query("select sum(a.pending_qty) as quantity,a.pending_qty,a.date,a.ordered_qty, c.taxPercent ,c.list_price " +
         "from b2c_pending_orders a,b2c_contact_master b,b2c_product_master c " +
        " where a.customer_key =  b.cus_key and " +
        "a.mi_key =  c.mikey and a.date between :fromdate and :todaydate group by  a.so_item_key order by  a.customer_key ")

   List<OrderNewData> getMTDSale(String fromdate,String todaydate);

@Query("select a.mi_key,a.pending_qty,a.date,a.ordered_qty,a.mi_name ,b.list_price ,b.taxPercent ," +
        "a.pending_qty as orderValue from b2c_pending_orders a,b2c_product_master b where a.mi_key =  b.mikey and" +
        " a.customer_key =:customer_key")
    List<OrderNewData> fetchPendingOrderValue(String customer_key);

@Query("Update b2c_pending_orders SET so_key =:so_key, so_item_key =:so_item_key, is_synced_to_server =1 where so_item_key =:dummyKey  "+
        "AND  customer_key =:cust_key")
    void updatePendingOrderSoItemKeyData(String cust_key, String so_key, String so_item_key, String dummyKey);

@Query("UPDATE b2c_pending_orders SET ordered_qty =:newOrderQty, pending_qty =:newPendingQty," +
        " purchase_order_type =:purchaseordertype,entered_on =:enteredon,is_synced_to_server =0 WHERE " +
        "so_key =:so_key AND so_item_key =:so_item_key AND customer_key =:customer_key ")
    int modifyPendingOrder(String customer_key, int so_key, String so_item_key, String newOrderQty,
                           String newPendingQty, String purchaseordertype,String enteredon);

@Query("SELECT * FROM b2c_pending_orders WHERE is_synced_to_server = 0 ")
    List<PendingOrderData> fetchCustomSelection();

    @Query("DELETE  From b2c_pending_orders  WHERE " +
            "so_key =:so_key AND so_item_key =:so_item_key AND customer_key =:customer_key")
    void deletePendingOrderSoKeyData(String customer_key, String so_key, String so_item_key);
}
