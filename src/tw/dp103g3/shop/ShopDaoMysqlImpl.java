package tw.dp103g3.shop;

import java.util.List;

public class ShopDaoMysqlImpl implements ShopDao {
	
	public ShopDaoMysqlImpl() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int insert(Shop shop, byte[] image) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Shop shop, byte[] image) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Shop> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
