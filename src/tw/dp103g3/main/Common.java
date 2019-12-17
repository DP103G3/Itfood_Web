package tw.dp103g3.main;

import java.time.format.DateTimeFormatter;

public class Common {

	public final static String URL = "jdbc:mysql://localhost:3306/itfood?useUnicode=yes&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Taipei";;
	public final static String CLASS_NAME = "com.mysql.cj.jdbc.Driver";
	public final static String USER = "";
	public final static String PASSWORD = "";
	public final static String CONTENT_TYPE = "text/html; charset=utf-8";
	
	public final static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	public final static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
}
