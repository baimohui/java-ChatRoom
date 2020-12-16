package client;

import client.model.entity.OnlineUserListModel;
import client.model.entity.MatesListModel;
import common.model.entity.User;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 〈一句话功能简述〉<br> 
 * 〈客户端数据缓存〉
 *
 */

public class DataBuffer {
    /** 当前客户端的用户信息 */
    public static User currentUser;
    /** 在线用户列表 */
    public static List<User> onlineUsers;
    /** 好友列表 */
    public static ArrayList<List<User>> matesList;
    /** 当前客户端连接到服务器的套节字 */
    public static Socket clientSeocket;
    /** 当前客户端连接到服务器的输出流 */
    public static ObjectOutputStream oos;
    /** 当前客户端连接到服务器的输入流 */
    public static ObjectInputStream ois;
    /** 服务器配置参数属性集 */
    public static Properties configProp;
    /** 当前客户端的屏幕尺寸 */
    public static Dimension screenSize;
    /** 本客户端的IP地址 */
    public static String ip ;
    /** 用来接收文件的端口 */
    public static final int RECEIVE_FILE_PORT = 6667;
    /** 在线用户JList的Model */
    public static OnlineUserListModel onlineUserListModel;
    /** 当前用户的好友list的Model */
    public static ArrayList<MatesListModel> matesListModels;
    public static MatesListModel matesListModel;

    static{
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        matesList = new ArrayList<List<User>>(100);
        matesListModels = new ArrayList<MatesListModel>(100);
        //加载服务器配置文件
        configProp = new Properties();
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
            configProp.load(Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("serverconfig.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private DataBuffer(){}
}

