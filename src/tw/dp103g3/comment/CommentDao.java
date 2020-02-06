package tw.dp103g3.comment;

import java.util.List;

import tw.dp103g3.shop.Shop;

public interface CommentDao {
	
	int insert(Comment comment, Shop shop);
	
	int update(Comment comment, Shop shop);
	
	Comment findByCommentId (int cmt_id);
	
	List<Comment> findByCase(int id, String type, int state);
	
	List<Comment> findByCase(int id, String type);
	

}
