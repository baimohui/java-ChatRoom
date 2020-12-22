package client.util;

import client.DataBuffer;
import client.ui.ChatFrame;
import common.model.entity.Request;
import common.model.entity.Response;
import common.model.entity.User;
import server.model.service.UserService;

import java.io.IOException;

/**
 * 〈客户端发送请求到服务器的工具〉<br>
 * 〈〉
 *
 */

public class ClientUtil {

    /** 发送请求对象,主动接收响应 */
    public static Response sendTextRequest(Request request) throws IOException {
        Response response = null;
        try {
            // 发送请求
            DataBuffer.oos.writeObject(request);
            DataBuffer.oos.flush();
            System.out.println("客户端发送了请求对象:" + request.getAction());

            if(!"exit".equals(request.getAction())){
                // 获取响应
                response = (Response) DataBuffer.ois.readObject();
                System.out.println("客户端获取到了响应对象:" + response.getStatus());
            }else{
                System.out.println("客户端断开连接了");
            }
        } catch (IOException e) {
            throw e;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return response;
    }

    /** 发送请求对象,不主动接收响应 */
    public static void sendTextRequest2(Request request) throws IOException {
        try {
            DataBuffer.oos.writeObject(request); // 发送请求
            DataBuffer.oos.flush();
            System.out.println("客户端发送了请求对象:" + request.getAction());
        } catch (IOException e) {
            throw e;
        }
    }

    /** 把指定文本添加到消息列表文本域中 */
    public static void appendTxt2MsgListArea(String txt) {
        ChatFrame.msgListArea.append(txt);
        //把光标定位到文本域的最后一行
        ChatFrame.msgListArea.setCaretPosition(ChatFrame.msgListArea.getDocument().getLength());
    }

    public static void appendTxt2MsgListArea(String txt, User user) {
//        user.addRecord(txt);
//        System.out.println("聊天记录更新："+user.getChatRecords());
        ChatFrame.msgListArea.append(txt);
        //把光标定位到文本域的最后一行
        ChatFrame.msgListArea.setCaretPosition(ChatFrame.msgListArea.getDocument().getLength());
    }
}
