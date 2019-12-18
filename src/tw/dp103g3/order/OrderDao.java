package tw.dp103g3.order;

import java.util.List;

public interface OrderDao {
	int insert(Order order);
	
	int update(Order order);
	
	List<Order> findByOrderId (int order_id);
	
	List<Order> findByCase(int id, String type, int State);
	
	List<Order> findByCase(int id, String type);
}
