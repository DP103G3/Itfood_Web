package tw.dp103g3.dish;

import java.util.List;

public interface DishDao {
	List<Dish> getAll();
	
	List<Dish> getAllShow(int shop_id);
	
	List<Dish> getAllByShopId(int shop_id);
	
	int insert(Dish dish, byte[] image);
	
	int update(Dish dish, byte[] image);
	
	Dish getDishById(int id);
	
	byte[] getImage(int id);
}
