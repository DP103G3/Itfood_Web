package tw.dp103g3.dish;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import static tw.dp103g3.main.Common.CONTENT_TYPE;
import tw.dp103g3.main.ImageUtil;
import tw.dp103g3.shop.Shop;

@SuppressWarnings("serial")
@WebServlet("/DishServlet")
public class DishServlet extends HttpServlet {
	private final static String CONTENT_TYPE = "text/html; charset=utf-8";
	private DishDao dishDao = null;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		BufferedReader br = request.getReader();
		StringBuilder jsonIn = new StringBuilder();
		String line = "";
		while ((line = br.readLine()) != null) {
			jsonIn.append(line);
		}
		System.out.println("input: " + jsonIn.toString());
		JsonObject jsonObject = gson.fromJson(jsonIn.toString(), JsonObject.class);
		if (dishDao == null) {
			dishDao = new DishDaoMysqlImpl();
		}
		
		String action = jsonObject.get("action").getAsString();
		List<Dish> dishes = null;
		Dish dish = null;
		int count = 0;
		int id = 0;
		byte[] image = null;
		switch (action) {
			case "getAll":
				dishes = dishDao.getAll();
				writeText(response, gson.toJson(dishes));
				break;
			case "getAllShow":
			case "getAllByShopId":
				int shop_id = jsonObject.get("shop_id").getAsInt();
				if (action.equals("getAllShow")) {
					dishes = dishDao.getAllShow(shop_id);
				} else {
					dishes = dishDao.getAllByShopId(shop_id);
				}
				writeText(response, gson.toJson(dishes));
				break;
			case "insert":
			case "update":
				String dishJson = jsonObject.get("dish").getAsString();
				dish = gson.fromJson(dishJson, Dish.class);
				if (jsonObject.get("imageBase64") != null) {
					String imageBase64 = jsonObject.get("imageBase64").getAsString();
					image = Base64.getMimeDecoder().decode(imageBase64);
				}
				if (action.equals("insert")) {
					count = dishDao.insert(dish, image);
				} else {
					count = dishDao.update(dish, image);
				}
				writeText(response, String.valueOf(count));
				break;
			case "getDishById":
				id = jsonObject.get("id").getAsInt();
				dish = dishDao.getDishById(id);
				writeText(response, gson.toJson(dish, Dish.class));
				break;
			case "getDishByShopId":
				int shopid = jsonObject.get("shop_id").getAsInt();
				dishes = dishDao.getDishByShopId(shopid);
				writeText(response, gson.toJson(dishes));
				break;
			case "getAccount":
				id = jsonObject.get("id").getAsInt();
				dish = dishDao.getAccount(id);
				writeText(response, gson.toJson(dish));
				System.out.println("getAccount: " + dish);
				break;
			case "saveAccount":
				dishJson = jsonObject.get("dish").getAsString();
				dish = gson.fromJson(dishJson, Dish.class);
				count = dishDao.saveAccount(dish);
				writeText(response, String.valueOf(count));
				System.out.println("saveAccount = " + dishJson);
				break;
			case "getImage":
				id = jsonObject.get("id").getAsInt();
				int imageSize = jsonObject.get("imageSize").getAsInt();
				image = dishDao.getImage(id);
				OutputStream os = response.getOutputStream();
				if (image != null) {
					image = ImageUtil.shink(image, imageSize);
					response.setContentType("image/jpeg");
					response.setContentLength(image.length);
					os.write(image);
				}
				break;
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (dishDao == null) {
			dishDao = new DishDaoMysqlImpl();
		}
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		List<Dish> dishes = new ArrayList<Dish>();
		dishes = dishDao.getAll();
		writeText(response, gson.toJson(dishes));
	}
	
	private void writeText(HttpServletResponse response, String outText) throws IOException {
		response.setContentType(CONTENT_TYPE);
		PrintWriter out = response.getWriter();
		out.write(outText);
		System.out.println("output: " + outText);
	}

}
