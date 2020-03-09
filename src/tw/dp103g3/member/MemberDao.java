package tw.dp103g3.member;

import java.util.List;
import java.util.Map;

public interface MemberDao {
	int insert(Member member);

	int update(Member member);
	
	int saveAccount(Member member);
	
    int updatePassword(Member member);
	
	Member getAccount(int mem_id);

	Member findById(int mem_id);
	
	Map<String, Integer> login(String email, String password);

	List<Member> getAll();

}
