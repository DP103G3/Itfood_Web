package tw.dp103g3.comment;

import java.util.List;

public interface CommentDao {
	
	int insert(Comment comment);
	
	int update(Comment comment);
	
	Comment findByCommentId (int cmt_id);
	
	List<Comment> findByCase(int id, String type, int state);
	
	List<Comment> findByCase(int id, String type);
	

}
