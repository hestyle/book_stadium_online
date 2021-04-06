package cn.edu.hestyle.bookstadium.websocket;

import cn.edu.hestyle.bookstadium.entity.ChatMessage;
import cn.edu.hestyle.bookstadium.entity.StadiumManager;
import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.entity.WebSocketMessage;
import cn.edu.hestyle.bookstadium.jwt.exception.TokenVerificationFailedException;
import cn.edu.hestyle.bookstadium.service.IStadiumManagerService;
import cn.edu.hestyle.bookstadium.service.IUserService;
import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/4/6 10:19 上午
 */
@Component
@ServerEndpoint("/webSocket/{token}")
public class WebSocketServer {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    /** 客户端的数量 */
    private static AtomicInteger onlineCount = new AtomicInteger(0);
    /** 存放user的 websocket session */
    private static ConcurrentHashMap<Integer, Session> userSessionHashMap;
    /** 存放stadiumManager的 websocket session */
    private static ConcurrentHashMap<Integer, Session> stadiumManagerSessionHashMap;

    static {
        userSessionHashMap = new ConcurrentHashMap<>();
        stadiumManagerSessionHashMap = new ConcurrentHashMap<>();
    }

    private Session session;
    private Integer accountId;
    private String accountRole;

    @Autowired
    private IUserService userService;
    @Autowired
    private IStadiumManagerService stadiumManagerService;

    /**
     * 连接建立成功调用的方法
     **/
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        Integer accountId;
        String accountRole;
        try {
            accountId = Integer.parseInt(JWT.decode(token).getAudience().get(0));
            accountRole = JWT.decode(token).getAudience().get(1);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Token解析失败！token = " + token);
            throw new TokenVerificationFailedException("Token无效，请重新登录！");
        }
        if (User.USER_ROLE.equals(accountRole)) {
            onlineCount.incrementAndGet();
            userSessionHashMap.put(accountId, session);
        } else if (StadiumManager.STADIUM_MANAGER_ROLE.equals(accountRole)) {
            onlineCount.incrementAndGet();
            stadiumManagerSessionHashMap.put(accountId, session);
        } else {
            logger.warn("Token解析失败！token = " + token);
            throw new TokenVerificationFailedException("Token无效，请重新登录！");
        }
        this.session = session;
        this.accountId = accountId;
        this.accountRole = accountRole;
        logger.info("有新连接加入：{}，当前在线人数为：{}", session.getId(), onlineCount.get());
        WebSocketMessage webSocketMessage = new WebSocketMessage(WebSocketMessage.WEBSOCKET_MESSAGE_TYPE_COMMON, "连接成功！");
        try {
            sendWebSocketMessage(session, webSocketMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        // 检查token是否过期
//        Date expiresDate = JWT.decode(token).getExpiresAt();
//        if (expiresDate.before(new Date())) {
//            logger.warn("Token 已过期！token = " + token);
//            throw new TokenVerificationFailedException("Token 已失效，请重新登录！");
//        }
//        // 验证token是否与数据库一致
//        if (User.USER_ROLE.equals(accountRole)) {
//            // User角色的token验证
//            User user = null;
//            try {
//                user = userService.systemFindById(accountId);
//            } catch (Exception e) {
//                e.printStackTrace();
//                logger.warn("Token验证失败！token = " + token);
//                throw new TokenVerificationFailedException("Token无效，请重新登录！");
//            }
//            if (user == null || !token.equals(user.getToken())) {
//                logger.warn("Token验证失败！token = " + token);
//                throw new TokenVerificationFailedException("Token无效，请重新登录！");
//            }
//            logger.warn("Token通过验证！token = " + token + "，user = " + user);
//        } else if (StadiumManager.STADIUM_MANAGER_ROLE.equals(accountRole)) {
//            // StadiumManager角色的token验证
//            StadiumManager stadiumManager = null;
//            try {
//                stadiumManager = stadiumManagerService.systemFindById(accountId);
//            } catch (Exception e) {
//                e.printStackTrace();
//                logger.warn("Token验证失败！token = " + token);
//                throw new TokenVerificationFailedException("Token无效，请重新登录！");
//            }
//            if (stadiumManager == null || !token.equals(stadiumManager.getToken())) {
//                logger.warn("Token验证失败！token = " + token);
//                throw new TokenVerificationFailedException("Token无效，请重新登录！");
//            }
//            logger.warn("Token通过验证！token = " + token + "，stadiumManager = " + stadiumManager);
//        } else {
//            logger.warn("Token验证失败！token = " + token);
//            throw new TokenVerificationFailedException("Token无效，请重新登录！");
//        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (User.USER_ROLE.equals(accountRole)) {
            userSessionHashMap.remove(accountId);
        } else if (StadiumManager.STADIUM_MANAGER_ROLE.equals(accountRole)) {
            stadiumManagerSessionHashMap.remove(accountId);
        }
        onlineCount.decrementAndGet(); // 在线数减1
        logger.info("有一连接关闭：{}，当前在线人数为：{}", session.getId(), onlineCount.get());
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     **/
    @OnMessage
    public void onMessage(String message, Session session) {
        logger.info("收到来自 accountRole = "+ accountRole + ", accountId = " + accountId + " 的信息: "+message);
    }

    /**
     * websocket出错
     * @param session   session
     * @param error     error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
        logger.warn("websocket出错！session = " + session);
    }

    /**
     * 发送chatMessage
     * @param chatMessage   chatMessage
     * @return              true or message
     */
    public static boolean sendChatMessage(ChatMessage chatMessage) {
        if (chatMessage == null) {
            logger.warn("Websocket消息发送错误！chatMessage为空！");
            return false;
        }
        // 根据chatMessage从map中取出session
        Session sendAccountSession;
        if (ChatMessage.MESSAGE_TYPE_USER_TO_MANAGER.equals(chatMessage.getMessageType())) {
            sendAccountSession = stadiumManagerSessionHashMap.getOrDefault(chatMessage.getToAccountId(), null);
        } else if (ChatMessage.MESSAGE_TYPE_MANAGER_TO_USER.equals(chatMessage.getMessageType())) {
            sendAccountSession = userSessionHashMap.getOrDefault(chatMessage.getToAccountId(), null);
        } else {
            logger.warn("Websocket消息发送错误！chatMessage类型错误！chatMessage = " + chatMessage);
            return false;
        }
        if (sendAccountSession == null) {
            logger.warn("Websocket消息发送失败！该用户未在线！chatMessage = " + chatMessage);
            return false;
        }
        // 将chatMessage转json
        ObjectMapper objectMapper = new ObjectMapper();
        String chatMessageString = "";
        try {
            chatMessageString = objectMapper.writeValueAsString(chatMessage);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("ChatMessage转json错误！chatMessage = " + chatMessage);
        }
        WebSocketMessage webSocketMessage = new WebSocketMessage(WebSocketMessage.WEBSOCKET_MESSAGE_TYPE_CHAT_MESSAGE, chatMessageString);
        // 发送消息
        try {
            sendWebSocketMessage(sendAccountSession, webSocketMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 发送webSocketMessage
     * @param webSocketMessage      webSocketMessage
     */
    private static void sendWebSocketMessage(Session session, WebSocketMessage webSocketMessage) throws Exception {
        if (session == null) {
            logger.warn("WebSocket 消息发送失败，session参数为null! webSocketMessage = " + webSocketMessage);
            throw new Exception("WebSocket 消息发送失败，session参数为null!");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(webSocketMessage);
        // 发送消息
        session.getBasicRemote().sendText(message);
        logger.warn("WebSocket 消息发送成功! webSocketMessage = " + webSocketMessage);
    }
}
