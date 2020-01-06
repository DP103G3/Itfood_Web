package tw.dp103g3.order_detail;

import java.util.List;

public interface OrderDetailDao {
	
	int insert(OrderDetail orderDetail);
	
	int update(OrderDetail orderDetail);
	
	List<OrderDetail> findByOrderId(int order_id);
	

}
