package tw.dp103g3.favorite;

import java.util.List;

public interface FavoriteDao {
	
	int insert(int memberId, int shopId);
	
	int delete(int memberId, int shopId);
	
	List<Favorite> findByMemberId(int memberId);
	

}