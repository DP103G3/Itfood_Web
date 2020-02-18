package tw.dp103g3.comment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import tw.dp103g3.shop.Shop;

@WebServlet("/CommentServlet")
public class CommentServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private final static String CONTENT_TYPE = "text/html; charset=utf-8";
	CommentDao commentDao = null;
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		BufferedReader br = request.getReader();
		StringBuilder jsonIn = new StringBuilder();
		String line = null;
		while((line = br.readLine()) != null) {
			jsonIn.append(line);
		}
		
		JsonObject jsonObject = gson.fromJson(jsonIn.toString(), JsonObject.class);
		if(commentDao == null) {
			commentDao = new CommentDaoMySqlImpl();
		}
		
		System.out.println("input: " + jsonIn.toString());
		
		String action = jsonObject.get("action").getAsString();
		
		if(action.equals("commentInsert") || action.equals("commentUpdate")) {
			String commentJson = jsonObject.get("comment").getAsString();
			String shopJson = jsonObject.get("shop").getAsString();
			System.out.println("commentJson = " + commentJson);
			Comment comment = gson.fromJson(commentJson, Comment.class);
			Shop shop = gson.fromJson(shopJson, Shop.class);
			
			int count = 0;
			if (action.equals("commentInsert")) {
				count = commentDao.insert(comment, shop);
			} else if (action.equals("commentUpdate")) {
				count = commentDao.update(comment, shop);
			}
			writeText(response, String.valueOf(count));
		} else if (action.equals("findByCommentId")) {
			int cmt_id = jsonObject.get("cmt_id").getAsInt();
			Comment comment = commentDao.findByCommentId(cmt_id);
			writeText(response, gson.toJson(comment));
		} else if (action.equals("findByCase")) {
			int id = jsonObject.get("id").getAsInt();
			String type = jsonObject.get("type").getAsString();
			List<Comment> comments = commentDao.findByCase(id, type);
			writeText(response, gson.toJson(comments));
		} else if (action.equals("findByCaseWithState")) {
			int id = jsonObject.get("id").getAsInt();
			String type = jsonObject.get("type").getAsString();
			int state = jsonObject.get("state").getAsInt();
			List<Comment> comments = commentDao.findByCase(id, type, state);
			writeText(response, gson.toJson(comments));
		} else if (action.equals("reply")) {
			String commentJson = jsonObject.get("comment").getAsString();
			Comment comment = gson.fromJson(commentJson, Comment.class);
			int count = commentDao.reply(comment);
			writeText(response, String.valueOf(count));
		}
	}
   
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		commentDao = new CommentDaoMySqlImpl();
		List<Comment> comments = commentDao.findByCase(1, "shop");
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		writeText(response, gson.toJson(comments));
	}

	private void writeText(HttpServletResponse response, String outText) throws IOException {
		response.setContentType(CONTENT_TYPE);
		PrintWriter out = response.getWriter();
		out.print(outText);
		// 將輸出資料列印出來除錯用
		System.out.println("output: " + outText);
	}

}
