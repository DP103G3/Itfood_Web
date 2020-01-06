package tw.dp103g3.favorite;

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
import com.google.gson.JsonObject;

import static tw.dp103g3.main.Common.*;

@WebServlet("/FavoriteServlet")
public class FavoriteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	FavoriteDao favoriteDao = null;

	public FavoriteServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		Gson gson = new Gson();
		BufferedReader br = request.getReader();
		StringBuilder jsonIn = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			jsonIn.append(line);
		}

		System.out.println("input: " + jsonIn);

		JsonObject jsonObject = gson.fromJson(jsonIn.toString(), JsonObject.class);
		if (favoriteDao == null) {
			favoriteDao = new FavoriteDaoMySqlImpl();
		}

		String action = jsonObject.get("action").getAsString();

		if (action.equals("favoriteInsert")) {
			String favoriteJson = jsonObject.get("favorite").getAsString();
			System.out.println("favoriteJson = " + favoriteJson);
			Favorite favorite = gson.fromJson(favoriteJson, Favorite.class);
			int count = 0;
			count = favoriteDao.insert(favorite.getMemberId(), favorite.getShopId());
			
			writeText(response, String.valueOf(count));
		} else if(action.equals("favoriteDelete")) {
			String favoriteJson = jsonObject.get("favorite").getAsString();
			System.out.println("favoriteJson = " + favoriteJson);
			Favorite favorite = gson.fromJson(favoriteJson, Favorite.class);
			int count = 0;
			count = favoriteDao.delete(favorite.getMemberId(), favorite.getShopId());
			
			writeText(response, String.valueOf(count));
		} else if (action.equals("findByMemberId")) {
			int memId = jsonObject.get("memId").getAsInt();
			List<Favorite> favorites = favoriteDao.findByMemberId(memId);
			if (!favorites.isEmpty()) {
				writeText(response, gson.toJson(favorites));
			} else {
				writeText(response, null);
			}
		} else {
			writeText(response, "");
		}
	}

	private void writeText(HttpServletResponse response, String outText) throws IOException {
		response.setContentType(CONTENT_TYPE);
		PrintWriter out = response.getWriter();
		out.print(outText);
		System.out.println("output: " + outText);
	}

}
