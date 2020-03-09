package tw.dp103g3.dish;

import java.util.List;

public interface DishDao {
	List<Dish> getAll();
	
	List<Dish> getAllShow(int shop_id);
	
	List<Dish> getDishByShopId(int shop_id);
	
	int insert(Dish dish, byte[] image);
	
	int update(Dish dish, byte[] image);
	
	int saveAccount(Dish dish);
	
	Dish getDishById(int id);
	
	byte[] getImage(int id);
	
	Dish getAccount(int id);
}
