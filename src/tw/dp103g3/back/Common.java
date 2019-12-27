package tw.dp103g3.back;

public class Common {
	// MySQL 8之後連線URL需加上SSL與時區設定
	public final static String URL = "jdbc:mysql://localhost:3306/itfood?useUnicode=yes&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Taipei";;
	public final static String CLASS_NAME = "com.mysql.cj.jdbc.Driver";
	public final static String USER = "root";
	public final static String PASSWORD = "59003010";
}
