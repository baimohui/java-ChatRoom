package client;

import client.model.entity.MatesListModel;
import client.model.entity.OnlineUserListModel;
import client.ui.ChatFrame;
import client.util.ClientUtil;
import client.util.JFrameShaker;
import common.model.entity.*;
import common.util.IOUtil;
import common.util.SocketUtil;
import server.model.service.UserService;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static client.DataBuffer.*;
import static client.DataBuffer.matesListModel;

/**
 * 〈一句话功能简述〉<br> 
 * 〈客户端线程，不断监听服务器发送过来的信息〉
 *
 */

public class ClientThread extends Thread {
    private JFrame currentFrame;  //当前窗体

    public ClientThread(JFrame frame){
        currentFrame = frame;
    }
    public void run() {
        try {
            while (DataBuffer.clientSeocket.isConnected()) {
                Response response = (Response) DataBuffer.ois.readObject();
                ResponseType type = response.getType();

                System.out.println("获取了响应内容：" + type);
                if (type == ResponseType.LOGIN) {
                    User newUser = (User)response.getData("loginUser");
                    DataBuffer.onlineUserListModel.addElement(newUser);
                    ChatFrame.onlineCountLbl.setText(
                            "在线用户列表("+ DataBuffer.onlineUserListModel.getSize() +")");
                    ClientUtil.appendTxt2MsgListArea("【系统消息】用户"+newUser.getNickname() + "上线了！\n", currentUser);
                }else if(type == ResponseType.LOGOUT){
                    User newUser = (User)response.getData("logoutUser");
                    //退出前保存聊天记录
                    if(currentUser.getId()==newUser.getId()) {
                        UserService userService = new UserService();
                        System.out.println("退出前有保存聊天记录吗！" + currentUser.getChatRecords());
                        userService.saveUser(currentUser);
                    }
                    DataBuffer.onlineUserListModel.removeElement(newUser);
                    ChatFrame.onlineCountLbl.setText(
                            "在线用户列表(" + DataBuffer.onlineUserListModel.getSize() + ")");
                }else if(type == ResponseType.CHAT){ //聊天
                    Message msg = (Message)response.getData("txtMsg");
//                    System.out.println("ClientThread有保存聊天记录吗！"+currentUser.getChatRecords());
                    ClientUtil.appendTxt2MsgListArea(msg.getMessage(), currentUser);
                    currentUser.addRecord(msg.getMessage());
                }else if(type == ResponseType.SHAKE){ //振动
                    Message msg = (Message)response.getData("ShakeMsg");
                    ClientUtil.appendTxt2MsgListArea(msg.getMessage(), currentUser);
                    new JFrameShaker(this.currentFrame).startShake();
                }else if(type == ResponseType.TOSENDFILE){ //准备发送文件
                    toSendFile(response);
                }else if(type == ResponseType.AGREERECEIVEFILE){ //对方同意接收文件
                    sendFile(response);
                }else if(type == ResponseType.REFUSERECEIVEFILE){ //对方拒绝接收文件
                    ClientUtil.appendTxt2MsgListArea("【文件消息】对方拒绝接收，文件发送失败！\n", currentUser);
                }else if(type == ResponseType.RECEIVEFILE){ //开始接收文件
                    receiveFile(response);
                }else if(type == ResponseType.BOARD){
                    Message msg = (Message)response.getData("txtMsg");
                    ClientUtil.appendTxt2MsgListArea(msg.getMessage(), currentUser);
                }else if(type == ResponseType.REMOVE){
                    ChatFrame.remove();
                }else if(type == ResponseType.TOSENDMIX){ //发送好友申请
                    toSendMix(response);
                }else if(type == ResponseType.AGREESENDMIX) { //同意好友申请
                    agreeSendMix(response);
                }else if(type == ResponseType.AGREESENDDELMIX) { //删除好友
                    agreeSendDelMix(response);
                }
            }
        } catch (IOException e) {
//            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 客户端接收到其它用户发来的好友申请
     */
    private void toSendMix(Response response) throws IOException {
        Message sendMix = (Message)response.getData("sendMix");
        String fromName = sendMix.getFromUser().getNickname() + "(" + sendMix.getFromUser().getId() + ")";
        int select = JOptionPane.showConfirmDialog(this.currentFrame,
                fromName + "请求添加您为好友，您接受此申请吗？",
                "好友申请", JOptionPane.YES_NO_OPTION);
            Request request = new Request();
            request.setAttribute("sendMix", sendMix);
            if(select == JOptionPane.YES_OPTION) {
                request.setAction("agreeSendMix");
                ClientUtil.appendTxt2MsgListArea("【系统消息】您已成功添加"+fromName+"为好友。\n", currentUser);
            } else {
                request.setAction("refuseSendMix");
                ClientUtil.appendTxt2MsgListArea("【系统消息】您已拒绝"+fromName+"的好友申请。\n", currentUser);
            }
            ClientUtil.sendTextRequest2(request);
    }

    /**
     * 同意好友申请
     */
    @SuppressWarnings("unchecked")
    public void agreeSendMix(Response response) {
        synchronized (this) {
            Message sendMix = (Message)response.getData("sendMix");
            int fromUserId = (int) sendMix.getFromUser().getId();
            int toUserId = (int) sendMix.getToUser().getId();
            matesList = (ArrayList<List<User>>) response.getData("matesList");
            System.out.println("当前用户为："+currentUser.getNickname());
            if(currentUser.getId()==sendMix.getFromUser().getId()) {
                MatesListModel matesListModel2 = matesListModels.get(fromUserId-1);
                matesListModel2.addElement(sendMix.getToUser());
                matesListModels.set(fromUserId-1, matesListModel2);
                String str = "【系统消息】"+sendMix.getToUser().getNickname()+"已同意添加您为好友。\n";
                ClientUtil.appendTxt2MsgListArea(str, currentUser);

                System.out.println(sendMix.getFromUser().getNickname()+"当前好友列表为"+matesListModel2.getmateList());
            } else if(currentUser.getId()==sendMix.getToUser().getId()) {
                MatesListModel matesListModel = matesListModels.get(toUserId-1);
                matesListModel.addElement(sendMix.getFromUser());
                matesListModels.set(toUserId-1, matesListModel);
                System.out.println(sendMix.getToUser().getNickname()+"当前好友列表为"+matesListModel.getmateList());
            }
        }
    }

    /**
     * 删除好友
     */
    @SuppressWarnings("unchecked")
    public void agreeSendDelMix(Response response) {
        synchronized (this) {
            Message sendDelMix = (Message)response.getData("sendDelMix");
            int fromUserId = (int) sendDelMix.getFromUser().getId();
            int toUserId = (int) sendDelMix.getToUser().getId();
            matesList = (ArrayList<List<User>>) response.getData("matesList");

            if(currentUser.getId()==sendDelMix.getFromUser().getId()) {
                MatesListModel matesListModel2 = matesListModels.get(fromUserId-1);
//                System.out.println(sendDelMix.getFromUser().getNickname()+"当前真实好友列表为"+currentUser.getMateList());
                // 对matesListModels的修改也连同修改了currentUser的mateList，具体可看其源码
                matesListModel2.removeElement(sendDelMix.getToUser());
                matesListModels.set(fromUserId-1, matesListModel2);
                System.out.println(sendDelMix.getFromUser().getNickname()+"当前好友列表为"+matesListModel2.getmateList());
            } else if(currentUser.getId()==sendDelMix.getToUser().getId()) {
                MatesListModel matesListModel = matesListModels.get(toUserId - 1);
                matesListModel.removeElement(sendDelMix.getFromUser());
                matesListModels.set(toUserId - 1, matesListModel);
                System.out.println(sendDelMix.getToUser().getNickname() + "当前好友列表为" + matesListModel.getmateList());
            }
        }
    }

    /** 发送文件 */
    private void sendFile(Response response) {
        final FileInfo sendFile = (FileInfo)response.getData("sendFile");

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        Socket socket = null;
        try {
            socket = new Socket(sendFile.getDestIp(),sendFile.getDestPort());//套接字连接
            bis = new BufferedInputStream(new FileInputStream(sendFile.getSrcName()));//文件读入
            bos = new BufferedOutputStream(socket.getOutputStream());//文件写出

            byte[] buffer = new byte[1024];
            int n = -1;
            while ((n = bis.read(buffer)) != -1){
                bos.write(buffer, 0, n);
            }
            bos.flush();
            synchronized (this) {
                ClientUtil.appendTxt2MsgListArea("【文件消息】文件发送完毕!\n", currentUser);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            IOUtil.close(bis,bos);
            SocketUtil.close(socket);
        }
    }

    /** 接收文件 */
    private void receiveFile(Response response) {
        final FileInfo sendFile = (FileInfo)response.getData("sendFile");

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        ServerSocket serverSocket = null;
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(sendFile.getDestPort());
            socket = serverSocket.accept(); //接收
            bis = new BufferedInputStream(socket.getInputStream());//缓冲读
            bos = new BufferedOutputStream(new FileOutputStream(sendFile.getDestName()));//缓冲写出

            byte[] buffer = new byte[1024];
            int n = -1;
            while ((n = bis.read(buffer)) != -1){
                bos.write(buffer, 0, n);
            }
            bos.flush();
            synchronized (this) {
                ClientUtil.appendTxt2MsgListArea("【文件消息】文件接收完毕!存放在["
                        + sendFile.getDestName()+"]\n", currentUser);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            IOUtil.close(bis,bos);
            SocketUtil.close(socket);
            SocketUtil.close(serverSocket);
        }
    }

    /** 准备发送文件	 */
    private void toSendFile(Response response) {
        FileInfo sendFile = (FileInfo)response.getData("sendFile");

        String fromName = sendFile.getFromUser().getNickname()
                + "(" + sendFile.getFromUser().getId() + ")";
        String fileName = sendFile.getSrcName()
                .substring(sendFile.getSrcName().lastIndexOf(File.separator)+1);

        int select = JOptionPane.showConfirmDialog(this.currentFrame,
                fromName + " 向您发送文件 [" + fileName+ "]!\n同意接收吗?",
                "接收文件", JOptionPane.YES_NO_OPTION);
        try {
            Request request = new Request();
            request.setAttribute("sendFile", sendFile);

            if (select == JOptionPane.YES_OPTION) {
                JFileChooser jfc = new JFileChooser();
                jfc.setSelectedFile(new File(fileName));
                int result = jfc.showSaveDialog(this.currentFrame);

                if (result == JFileChooser.APPROVE_OPTION){
                    //设置目的地文件名
                    sendFile.setDestName(jfc.getSelectedFile().getCanonicalPath());
                    //设置目标地的IP和接收文件的端口
                    sendFile.setDestIp(DataBuffer.ip);
                    sendFile.setDestPort(DataBuffer.RECEIVE_FILE_PORT);

                    request.setAction("agreeReceiveFile");
//                    receiveFile(response);
                    ClientUtil.appendTxt2MsgListArea("【文件消息】您已同意接收来自 "
                            + fromName +" 的文件，正在接收文件 ...\n", currentUser);
                } else {
                    request.setAction("refuseReceiveFile");
                    ClientUtil.appendTxt2MsgListArea("【文件消息】您已拒绝接收来自 "
                            + fromName +" 的文件!\n", currentUser);
                }
            } else {
                request.setAction("refuseReceiveFile");
                ClientUtil.appendTxt2MsgListArea("【文件消息】您已拒绝接收来自 "
                        + fromName +" 的文件!\n", currentUser);
            }
            ClientUtil.sendTextRequest2(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

