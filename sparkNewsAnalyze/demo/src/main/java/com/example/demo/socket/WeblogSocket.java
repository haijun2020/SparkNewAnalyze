package com.example.demo.socket;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@ServerEndpoint("/websocket")
public class WeblogSocket {
    Logger log= LoggerFactory.getLogger(WeblogSocket.class);
    WeblogService  weblogService = new WeblogService();
    @OnMessage
    public void onMessage(String message, Session session)
            throws IOException, InterruptedException {
        while(true){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("titleName", weblogService.queryWeblogs().get("titleName"));
            map.put("titleCount",weblogService.queryWeblogs().get("titleCount"));
            map.put("titleSum", weblogService.titleCount());

            session.getBasicRemote().
                    sendText(JSON.toJSONString(map));
            // 每5秒去数据库获得数据
            Thread.sleep(5000);
            map.clear();
        }
    }
    @OnOpen
    public void onOpen () {
        System.out.println("Client connected");
    }
    @OnClose
    public void onClose () {
        System.out.println("Connection closed");
    }
}
