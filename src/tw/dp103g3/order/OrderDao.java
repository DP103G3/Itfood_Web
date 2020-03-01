package tw.dp103g3.order;

import java.util.Calendar;
import java.util.List;

public interface OrderDao {
	int insert(Order order, String orderDetailsJson);
	
	int update(Order order);
	
	Order findByOrderId (int order_id);
	
//	List<Order> findByCase(int id, String type, int State);
	
	List<Order> findByCase(int id, String type);
	
	Cart getCart(List<Integer> dishIds, int mem_id);
	
	List<Order> findByCase(int id, String type, int State, Calendar date, boolean containDay);
	
	List<Order> findByDeliveryId(int id);
}
