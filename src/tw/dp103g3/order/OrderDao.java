package tw.dp103g3.order;

import java.util.List;

public interface OrderDao {
	int insert(Order order);
	
	int update(Order order);
	
	List<Order> findByOrderId (int order_id);
	
	List<Order> findByMemId (int mem_id);
	
	List<Order> findByShopId (int shop_id);
	
	List<Order> findByDelId(int del_id);
	
	List<Order> findMemOrderByStatus(int mem_id, int order_status);
	
	List<Order> findShopOrderByStatus(int shop_id, int order_status);
	
	List<Order> findDelOrderByStatus(int del_id, int order_status);
	


}
