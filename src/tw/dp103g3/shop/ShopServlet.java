package tw.dp103g3.shop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
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

import static tw.dp103g3.main.Common.*;

import tw.dp103g3.delivery.Delivery;
import tw.dp103g3.main.ImageUtil;

@SuppressWarnings("serial")
@WebServlet("/ShopServlet")
public class ShopServlet extends HttpServlet {
	private ShopDao shopDao = null;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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
		if (shopDao == null) {
			shopDao = new ShopDaoMysqlImpl();
		}

		String action = jsonObject.get("action").getAsString();
		String shopJson = null;
		Shop shop = null;
		byte[] image = null;
		int count = 0;
		List<Shop> shops = null;
		int id = 0;
		switch (action) {
		case "insert":
		case "update":
			shopJson = jsonObject.get("shop").getAsString();
			shop = gson.fromJson(shopJson, Shop.class);
			if (jsonObject.get("imageBase64") != null) {
				String imageBase64 = jsonObject.get("imageBase64").getAsString();
				if (imageBase64 != null && !imageBase64.isEmpty()) {
					image = Base64.getMimeDecoder().decode(imageBase64);
				}
			}
			if (action.equals("insert")) {
				count = shopDao.insert(shop, image);
			} else {
				count = shopDao.update(shop, image);
			}
			writeText(response, String.valueOf(count));
			break;
		case "getAll":
			shops = shopDao.getAll();
			writeText(response, gson.toJson(shops));
			break;
		case "getAllShow":
			id = jsonObject.get("id").getAsInt();
			shops = shopDao.getAllShow(id);
			writeText(response, gson.toJson(shops));
			break;
		case "getImage":
			OutputStream os = response.getOutputStream();
			id = jsonObject.get("id").getAsInt();
			int imageSize = jsonObject.get("imageSize").getAsInt();
			image = shopDao.getImage(id);
			if (image != null) {
				image = ImageUtil.shink(image, imageSize);
				response.setContentType("image/jpeg");
				response.setContentLength(image.length);
				os.write(image);
			}
			break;
		case "getShopById":
			id = jsonObject.get("id").getAsInt();
			shop = shopDao.getShopById(id);
			writeText(response, gson.toJson(shop));
			break;
		case "login":
			String email = jsonObject.get("email").getAsString();
			String password = jsonObject.get("password").getAsString();
			id = shopDao.login(email, password);
			writeText(response, String.valueOf(id));
			break;
		case "getShopAllById":
			id = jsonObject.get("id").getAsInt();
			shop = shopDao.getShopAllById(id);
			writeText(response, gson.toJson(shop));
			break;
		case "setShopUpDateById":
			id = jsonObject.get("id").getAsInt();
			shop = shopDao.setShopUpDateById(id);
			writeText(response, gson.toJson(shop));
			break;
		case "getAccount":
			id = jsonObject.get("id").getAsInt();
			shop = shopDao.getAccount(id);
			writeText(response, gson.toJson(shop));
			System.out.println("getAccount: " + shop);
			break;
		case "saveAccount":
			shopJson = jsonObject.get("shop").getAsString();
			shop = gson.fromJson(shopJson, Shop.class);
			count = shopDao.saveAccount(shop);
			writeText(response, String.valueOf(count));
			System.out.println("saveAccount = " + shopJson);
			break;
		case "updatePassword":
			shopJson = jsonObject.get("shop").getAsString();
			shop = gson.fromJson(shopJson, Shop.class);
			count = shopDao.updatePassword(shop);
			writeText(response, String.valueOf(count));
			System.out.println("update = " + shopJson);
			break;
		default:
			writeText(response, "");
			break;
		}
	}

	private void writeText(HttpServletResponse response, String outText) throws IOException {
		response.setContentType(CONTENT_TYPE);
		PrintWriter out = response.getWriter();
		out.print(outText);
		System.out.println("outputWT: " + outText);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (shopDao == null) {
			shopDao = new ShopDaoMysqlImpl();
		}
		List<Shop> shops = shopDao.getAllShow(1);
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		String outText = gson.toJson(shops);
		writeText(response, outText);
	}

}