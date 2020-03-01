package tw.dp103g3.address;

import java.util.List;

public interface AddressDao {
	List<Address> getAllShow(int mem_id);
	
	int insert(Address address);
	
	int update(Address address);
	
	List<Address> getAll(int mem_id);
	
	Address findById(int adrs_id);
}
