package tw.dp103g3.member;

import java.util.List;

public interface MemberDao {
	int insert(Member member);

	int update(Member member);

	int delete(int mem_id);

	Member findById(int mem_id);

	List<Member> getAll();

}