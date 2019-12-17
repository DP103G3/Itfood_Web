package tw.dp103g3.shop;

import java.util.List;

public interface ShopDao {
	int insert(Shop shop, byte[] image);
	
	int update(Shop shop, byte[] image);
	
	List<Shop> getAll();
	
	List<Shop> getAllShow();
}
