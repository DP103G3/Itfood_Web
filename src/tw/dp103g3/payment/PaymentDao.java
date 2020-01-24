package tw.dp103g3.payment;

import java.util.List;

public interface PaymentDao {
	int insert(Payment payment);
	int update(Payment payment);
	
	List <Payment> getByMemberId(int mem_id, int state);
	

}
