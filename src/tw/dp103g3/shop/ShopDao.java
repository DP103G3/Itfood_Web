package tw.dp103g3.shop;

import java.util.List;

public interface ShopDao {
	int insert(Shop shop, byte[] image);

	int update(Shop shop, byte[] image);
	
	int saveAccount(Shop shop);

	List<Shop> getAll();

	List<Shop> getAllShow();

	byte[] getImage(int id);
	
	Shop getShopById(int id);
	
	int login(String email, String password);
	
	Shop getShopAllById(int id);
	
	Shop setShopUpDateById(int id);
	
	Shop getAccount(int id);
	
	Shop getShopByIdDelivery(int id);
}
