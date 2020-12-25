package server.controller;

import client.model.entity.MatesListModel;
import common.model.entity.*;
import server.DataBuffer;
import server.OnlineClientIOCache;
import server.model.service.UserService;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static client.DataBuffer.currentUser;
import static server.DataBuffer.matesList;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 */

public class RequestProcessor implements Runnable {
    private Socket currentClientSocket;  //当前正在请求服务器的客户端Socket
    public static ArrayList<List<User>> matesList;
    public RequestProcessor(Socket currentClientSocket) {
        this.currentClientSocket = currentClientSocket;
    }

    public void run() {
        boolean flag = true; //是否不间断监听
        matesList = new ArrayList<List<User>>(100);
        if(matesList.size() < 100) {
            for (int i = 0; i < 100; i++) {
                matesList.add(new ArrayList<User>());
            }
        }

        try {
            OnlineClientIOCache currentClientIOCache = new OnlineClientIOCache(
                    new ObjectInputStream(currentClientSocket.getInputStream()),
                    new ObjectOutputStream(currentClientSocket.getOutputStream()));
            while (flag) { //不停地读取客户端发过来的请求对象
                //从请求输入流中读取到客户端提交的请求对象
                Request request = (Request) currentClientIOCache.getOis().readObject();
                System.out.println("Server读取了客户端的请求:" + request.getAction());

                String actionName = request.getAction();   //获取请求中的动作
                switch (actionName) {
                    case "userRegister":       //用户注册
                        register(currentClientIOCache, request);
                        break;
                    case "userLogin":   //用户登录
                        login(currentClientIOCache, request);
                        break;
                    case "exit":        //请求断开连接
                        flag = logout(currentClientIOCache, request);
                        break;
                    case "chat":        //聊天
                        chat(request);
                        break;
                    case "shake":       //振动
                        shake(request);
                        break;
                    case "toSendFile":  //准备发送文件
                        toSendFile(request);
                        break;
                    case "agreeReceiveFile":  //同意接收文件
                        agreeReceiveFile(request);
                        break;
                    case "refuseReceiveFile":  //拒绝接收文件
                        refuseReceiveFile(request);
                        break;
                    case "toMix":  //发送好友申请
                        toMix(request);
                        break;
                    case "agreeSendMix": //同意好友申请
                        agreeSendMix(request);
                        break;
                    case "toDelMix":  //删除好友
                        toDelMix(request);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拒绝接收文件
     */
    private void refuseReceiveFile(Request request) throws IOException {
        FileInfo sendFile = (FileInfo) request.getAttribute("sendFile");
        Response response = new Response();  //创建一个响应对象
        response.setType(ResponseType.REFUSERECEIVEFILE);
        response.setData("sendFile", sendFile);
        response.setStatus(ResponseStatus.OK);
        //向请求方的输出流输出响应
        OnlineClientIOCache ocic = DataBuffer.onlineUserIOCacheMap.get(sendFile.getFromUser().getId());
        this.sendResponse(ocic, response);
    }

    /**
     * 同意接收文件
     */
    private void agreeReceiveFile(Request request) throws IOException {
        FileInfo sendFile = (FileInfo) request.getAttribute("sendFile");
        //向请求方(发送方)的输出流输出响应
        Response response = new Response();  //创建一个响应对象
        response.setType(ResponseType.AGREERECEIVEFILE);
        response.setData("sendFile", sendFile);
        response.setStatus(ResponseStatus.OK);
        OnlineClientIOCache sendIO = DataBuffer.onlineUserIOCacheMap.get(sendFile.getFromUser().getId());
        this.sendResponse(sendIO, response);

        //向接收方发出接收文件的响应
        Response response2 = new Response();  //创建一个响应对象
        response2.setType(ResponseType.RECEIVEFILE);
        response2.setData("sendFile", sendFile);
        response2.setStatus(ResponseStatus.OK);
        OnlineClientIOCache receiveIO = DataBuffer.onlineUserIOCacheMap.get(sendFile.getToUser().getId());
        this.sendResponse(receiveIO, response2);
    }

    /**
     * 客户端退出
     */
    public boolean logout(OnlineClientIOCache oio, Request request) throws IOException {
        System.out.println(currentClientSocket.getInetAddress().getHostAddress()
                + ":" + currentClientSocket.getPort() + "走了");

        User user = (User) request.getAttribute("user");

        //把当前上线客户端的IO从Map中删除
        DataBuffer.onlineUserIOCacheMap.remove(user.getId());
        //从在线用户缓存Map中删除当前用户
        DataBuffer.onlineUsersMap.remove(user.getId());

        Response response = new Response();  //创建一个响应对象
        response.setType(ResponseType.LOGOUT);
        response.setData("logoutUser", user);
        oio.getOos().writeObject(response);  //把响应对象往客户端写
        oio.getOos().flush();
        currentClientSocket.close();  //关闭这个客户端Socket

        DataBuffer.onlineUserTableModel.remove(user.getId()); //把当前下线用户从在线用户表Model中删除
//        //下线消息通知给其它在线用户，但不要通知到自己
//        for (Long id : DataBuffer.onlineUserIOCacheMap.keySet()) {
//            sendResponse(DataBuffer.onlineUserIOCacheMap.get(id), response);
//        }
        iteratorResponse(response);//通知所有其它在线客户端

        return false;  //断开监听
    }

    /**
     * 注册
     */
    public void register(OnlineClientIOCache oio, Request request) throws IOException {
        User user = (User) request.getAttribute("user");
        UserService userService = new UserService();
        userService.addUser(user);

        Response response = new Response();  //创建一个响应对象
        response.setStatus(ResponseStatus.OK);
        response.setData("user", user);

        oio.getOos().writeObject(response);  //把响应对象往客户端写
        oio.getOos().flush();

        //把新注册用户添加到RegistedUserTableModel中
        DataBuffer.registedUserTableModel.add(new String[]{
                String.valueOf(user.getId()),
                user.getPassword(),
                user.getNickname(),
                String.valueOf(user.getSex())
        });
    }

    /**
     * 登录
     */
    public void login(OnlineClientIOCache currentClientIO, Request request) throws IOException {
        String idStr = (String) request.getAttribute("id");
        String password = (String) request.getAttribute("password");
        UserService userService = new UserService();
//        login函数用来核对用户id和密码是否正确，正确则返回User对象，错误则返回null/
        User user = userService.login(Long.parseLong(idStr), password);

        Response response = new Response();  //创建一个响应对象
        if (null != user) {
            if (DataBuffer.onlineUsersMap.containsKey(user.getId())) { //用户已经登录了
                response.setStatus(ResponseStatus.OK);
                response.setData("msg", "该用户已经在别处上线了！"); //弹窗展示信息
                currentClientIO.getOos().writeObject(response);  //把响应对象往客户端写
                currentClientIO.getOos().flush();
            } else { //正确登录
                DataBuffer.onlineUsersMap.put(user.getId(), user); //添加到在线用户

                //设置在线用户
                response.setData("onlineUsers",
                        new CopyOnWriteArrayList<User>(DataBuffer.onlineUsersMap.values()));

                response.setStatus(ResponseStatus.OK);
                response.setData("user", user);
                currentClientIO.getOos().writeObject(response);  //把响应对象往客户端写
                currentClientIO.getOos().flush();

                //通知其它用户有人上线了
                Response response2 = new Response();
                response2.setType(ResponseType.LOGIN);
                response2.setData("loginUser", user);
                response2.setData("onlineUsers", new CopyOnWriteArrayList<User>(DataBuffer.onlineUsersMap.values()));
                iteratorResponse(response2);

                //把当前上线的用户IO添加到缓存Map中
                DataBuffer.onlineUserIOCacheMap.put(user.getId(), currentClientIO);

                //把当前上线用户添加到OnlineUserTableModel中
                DataBuffer.onlineUserTableModel.add(
                        new String[]{String.valueOf(user.getId()),
                                user.getNickname(),
                                String.valueOf(user.getSex())});
            }
        } else { //登录失败
            response.setStatus(ResponseStatus.OK);
            response.setData("msg", "账号或密码不正确！");
            currentClientIO.getOos().writeObject(response);
            currentClientIO.getOos().flush();
        }
    }

    /**
     * 发送添加好友申请
     */
    public void toMix(Request request) throws IOException {
        Response response = new Response();
        response.setStatus(ResponseStatus.OK);
        response.setType(ResponseType.TOSENDMIX);
        Message sendMix = (Message)request.getAttribute("Message");
        response.setData("sendMix", sendMix);
        //给接收方转发申请方的请求
        OnlineClientIOCache ioCache = DataBuffer.onlineUserIOCacheMap.get(sendMix.getToUser().getId());
        sendResponse(ioCache, response);
    }

    /**
     * 同意好友申请
     */
    public void agreeSendMix(Request request) throws IOException {
        Message sendMix = (Message) request.getAttribute("sendMix");
        int fromUserId = (int) sendMix.getFromUser().getId();
        int toUserId = (int) sendMix.getToUser().getId();

        Response response = new Response();
        response.setType(ResponseType.AGREESENDMIX);
        response.setStatus(ResponseStatus.OK);
        response.setData("sendMix",sendMix);

        List<User> mateList = matesList.get(fromUserId-1);
        mateList.add(sendMix.getToUser());
        matesList.set(fromUserId-1, mateList);
        List<User> mateList2 = matesList.get(toUserId-1);
        mateList2.add(sendMix.getFromUser());
        matesList.set(toUserId-1, mateList2);
        response.setData("matesList", matesList);

        OnlineClientIOCache sendIO1 = DataBuffer.onlineUserIOCacheMap.get(sendMix.getFromUser().getId());
        OnlineClientIOCache sendIO2 = DataBuffer.onlineUserIOCacheMap.get(sendMix.getToUser().getId());
        this.sendResponse(sendIO1, response);
        this.sendResponse(sendIO2, response);
    }

    /**
     * 删除好友
     */
    public void toDelMix(Request request) throws IOException {
        Message sendDelMix = (Message) request.getAttribute("sendDelMix");
        int fromUserId = (int) sendDelMix.getFromUser().getId();
        int toUserId = (int) sendDelMix.getToUser().getId();
        Response response = new Response();
        response.setType(ResponseType.AGREESENDDELMIX);
        response.setStatus(ResponseStatus.OK);
        response.setData("sendDelMix",sendDelMix);

        System.out.println("接收方添加的好友为"+matesList.get(toUserId-1));
        System.out.println("发送方添加的好友为"+matesList.get(fromUserId-1));

        // 删除好友，matesList进行相应的更新
        List<User> mateList = matesList.get(fromUserId-1);
        mateList.remove(sendDelMix.getToUser());
        matesList.set(fromUserId-1, mateList);

        List<User> mateList2 = matesList.get(toUserId-1);
        mateList2.remove(sendDelMix.getFromUser());
        matesList.set(toUserId-1, mateList2);
        response.setData("matesList", matesList);

        System.out.println("接收方当前好友为"+matesList.get(toUserId-1));
        System.out.println("发送方当前好友为"+matesList.get(fromUserId-1));

        OnlineClientIOCache sendIO1 = DataBuffer.onlineUserIOCacheMap.get(sendDelMix.getFromUser().getId());
        this.sendResponse(sendIO1, response);

        // 考虑到要删除的好友可能不在线
        User offlineUser = sendDelMix.getToUser();
        if (DataBuffer.onlineUsersMap.containsKey(offlineUser.getId())) { //要删除的用户已经登录了
            OnlineClientIOCache sendIO2 = DataBuffer.onlineUserIOCacheMap.get(offlineUser.getId());
            this.sendResponse(sendIO2, response);
        } else { //如果要删除的对象不在线，则要单独对其mateList进行更新保存
            offlineUser.delMate(sendDelMix.getFromUser());
            UserService userService = new UserService();
            userService.saveUser(offlineUser);
        }
    }

    /**
     * 聊天
     */
    public void chat(Request request) throws IOException {
        Message msg = (Message) request.getAttribute("msg");
        Response response = new Response();
        response.setStatus(ResponseStatus.OK);
        response.setType(ResponseType.CHAT);
        response.setData("txtMsg", msg);

        if (msg.getToUser() != null) { //私聊:只给私聊的对象返回响应
            OnlineClientIOCache io1 = DataBuffer.onlineUserIOCacheMap.get(msg.getToUser().getId());
            OnlineClientIOCache io2 = DataBuffer.onlineUserIOCacheMap.get(msg.getFromUser().getId());
            sendResponse(io1, response);
            sendResponse(io2, response);
        } else {  //群聊:给除了发消息的所有客户端都返回响应
            for (Long id : DataBuffer.onlineUserIOCacheMap.keySet()) {
//                if (msg.getFromUser().getId() == id) {
//                    continue;
//                }
                sendResponse(DataBuffer.onlineUserIOCacheMap.get(id), response);
            }
        }
    }

    /*广播*/
    public static void board(String str) throws IOException {
        User user = new User(1, "admin");
        Message msg = new Message();
        msg.setFromUser(user);
        msg.setSendTime(new Date());

        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        StringBuffer sb = new StringBuffer();
        sb.append(" ").append(df.format(msg.getSendTime())).append(" ");
        sb.append("系统通知\n  " + str + "\n");
        msg.setMessage(sb.toString());

        Response response = new Response();
        response.setStatus(ResponseStatus.OK);
        response.setType(ResponseType.BOARD);
        response.setData("txtMsg", msg);

        for (Long id : DataBuffer.onlineUserIOCacheMap.keySet()) {
            sendResponse_sys(DataBuffer.onlineUserIOCacheMap.get(id), response);
        }
    }

    /*踢除用户*/
    public static void remove(User user_) throws IOException {
        User user = new User(1, "admin");
        Message msg = new Message();
        msg.setFromUser(user);
        msg.setSendTime(new Date());
        msg.setToUser(user_);

        StringBuffer sb = new StringBuffer();
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        sb.append(" ").append(df.format(msg.getSendTime())).append(" ");
        sb.append("系统通知您\n  " + "您被强制下线" + "\n");
        msg.setMessage(sb.toString());

        Response response = new Response();
        response.setStatus(ResponseStatus.OK);
        response.setType(ResponseType.REMOVE);
        response.setData("txtMsg", msg);

        OnlineClientIOCache io = DataBuffer.onlineUserIOCacheMap.get(msg.getToUser().getId());
        sendResponse_sys(io, response);
    }

    /*私信*/
    public static void chat_sys(String str, User user_) throws IOException {
        User user = new User(1, "admin");
        Message msg = new Message();
        msg.setFromUser(user);
        msg.setSendTime(new Date());
        msg.setToUser(user_);

        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        StringBuffer sb = new StringBuffer();
        sb.append(" ").append(df.format(msg.getSendTime())).append(" ");
        sb.append("系统通知您\n  " + str + "\n");
        msg.setMessage(sb.toString());

        Response response = new Response();
        response.setStatus(ResponseStatus.OK);
        response.setType(ResponseType.CHAT);
        response.setData("txtMsg", msg);

        OnlineClientIOCache io = DataBuffer.onlineUserIOCacheMap.get(msg.getToUser().getId());
        sendResponse_sys(io, response);
    }

    /**
     * 发送振动
     */
    public void shake(Request request) throws IOException {
        Message msg = (Message) request.getAttribute("msg");

        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        StringBuffer sb = new StringBuffer();
        sb.append(" ").append(msg.getFromUser().getNickname())
                .append("(").append(msg.getFromUser().getId()).append(") ")
                .append(df.format(msg.getSendTime())).append("\n  给您发送了一个窗口抖动\n");
        msg.setMessage(sb.toString());

        Response response = new Response();
        response.setStatus(ResponseStatus.OK);
        response.setType(ResponseType.SHAKE);
        response.setData("ShakeMsg", msg);

        OnlineClientIOCache io = DataBuffer.onlineUserIOCacheMap.get(msg.getToUser().getId());
        sendResponse(io, response);
    }

    /**
     * 准备发送文件
     */
    public void toSendFile(Request request) throws IOException {
        Response response = new Response();
        response.setStatus(ResponseStatus.OK);
        response.setType(ResponseType.TOSENDFILE);
        FileInfo sendFile = (FileInfo) request.getAttribute("file");
        response.setData("sendFile", sendFile);
        //给文件接收方转发文件发送方的请求
        OnlineClientIOCache ioCache = DataBuffer.onlineUserIOCacheMap.get(sendFile.getToUser().getId());
        sendResponse(ioCache, response);
    }

    /**
     * 给所有在线客户都发送响应
     */
    private void iteratorResponse(Response response) throws IOException {
        for (OnlineClientIOCache onlineUserIO : DataBuffer.onlineUserIOCacheMap.values()) {
            ObjectOutputStream oos = onlineUserIO.getOos();
            oos.writeObject(response);
            oos.flush();
        }
    }

    /**
     * 向指定客户端IO的输出流中输出指定响应
     */
    private void sendResponse(OnlineClientIOCache onlineUserIO, Response response) throws IOException {
        ObjectOutputStream oos = onlineUserIO.getOos();
        oos.writeObject(response);
        oos.flush();
    }

    /**
     * 向指定客户端IO的输出流中输出指定响应
     */
    private static void sendResponse_sys(OnlineClientIOCache onlineUserIO, Response response) throws IOException {
        ObjectOutputStream oos = onlineUserIO.getOos();
        oos.writeObject(response);
        oos.flush();
    }
}
