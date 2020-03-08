package tw.dp103g3.order;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@ServerEndpoint("/OrderSocket/{user}")
public class OrderSocket {
	private final String TAG = "TAG_OrderSocket";
	private static Map<String, Session> sessionsMap = new ConcurrentHashMap<>();
	Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	@OnOpen
	public void onOpen(@PathParam("user") String user, Session userSession) throws IOException {
		sessionsMap.put(user, userSession);
		System.out.println(TAG + "Socket open connection: " + user);
	}
	
	@OnMessage
	public void onMessage(Session userSession, String orderMessageJson) {
		OrderMessage orderMessage = gson.fromJson(orderMessageJson, OrderMessage.class);
		String receiver = orderMessage.getReceiver();
		Session receiverSession = sessionsMap.get(receiver);
		String message = gson.toJson(orderMessage.getOrder());
		if (receiverSession != null && receiverSession.isOpen()) {
			receiverSession.getAsyncRemote().sendText(message);
		} else {
			sessionsMap.remove(receiver);
		}
		System.out.println(TAG + "receiver: " + receiver);
		System.out.println(TAG + "Message received: " + message);
	}
	
	@OnError
	public void onError(Session userSession, Throwable e) {
		System.out.println(TAG + "Error:" + e.toString());
	}
	
	@OnClose
	public void onClose(Session userSession, CloseReason reason) {
		Set<String> userNames = sessionsMap.keySet();
		for (String userName : userNames) {
			if (sessionsMap.get(userName).equals(userSession)) {
				sessionsMap.remove(userName);
				break;
			}
		}
	}
}
