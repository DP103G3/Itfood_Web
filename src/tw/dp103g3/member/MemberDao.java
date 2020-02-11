package tw.dp103g3.member;

import java.util.List;

public interface MemberDao {
	int insert(Member member);

	int update(Member member);
	
	int saveAccount(Member member);
	
	Member getAccount(int mem_id);

	Member findById(int mem_id);
	
	Member findByEmail(String email);

	List<Member> getAll();

}
