package tw.dp103g3.favorite;

import java.util.List;

import tw.dp103g3.shop.Shop;

public interface FavoriteDao {
	
	int insert(int memberId, int shopId);
	
	int delete(int memberId, int shopId);
	
	List<Shop> findByMemberId(int memberId);
	

}