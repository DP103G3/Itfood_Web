package tw.dp103g3.delivery;

import java.util.List;
import java.util.Map;

public interface DeliveryDao {
	int insert(Delivery delivery);

	int update(Delivery delivery);
	
	int saveAccount(Delivery delivery);
	
	Delivery getAccount(int del_id);

	Delivery findById(int del_id);
	
	Map<String, Integer> login(String email, String password);

	List<Delivery> getAll();

}
