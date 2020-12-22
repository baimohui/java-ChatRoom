package client.ui;

import client.ClientThread;
import client.DataBuffer;
import client.model.entity.MyCellRenderer;
import client.model.entity.OnlineUserListModel;
import client.model.entity.MatesListModel;
import client.util.ClientUtil;
import client.util.JFrameShaker;
import common.model.entity.FileInfo;
import common.model.entity.Message;
import common.model.entity.Request;
import common.model.entity.User;
import server.model.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static client.DataBuffer.currentUser;
import static client.DataBuffer.matesListModels;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 */

public class ChatFrame extends JFrame {
    private static final long serialVersionUID = -2310785591507878535L;
    /**
     * 聊天对方的信息Label
     */
    private JLabel otherInfoLbl;
    /**
     * 当前用户信息Lbl
     */
    private JLabel currentUserLbl;
    /**
     * 聊天信息列表区域
     */
    public static JTextArea msgListArea;
    /**
     * 要发送的信息区域
     */
    public static JTextArea sendArea;
    /**
     * 在线用户列表
     */
    public static JList onlineList;
    /**
     * 用户好友列表
     */
    public static JList mateList;
    /**
     * 在线用户数统计Lbl
     */
    public static JLabel onlineCountLbl;
    /**
     * 准备发送的文件
     */
    public static FileInfo sendFile;
    /**
     * 发送添加好友和删除好友的申请
     */
    public static Message sendMix;
    public static Message sendDelMix;
    /**
     * 私聊复选框
     */
    public JCheckBox rybqBtn;

    public ChatFrame() {
        this.init();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    public void init() {
        this.setTitle("JX聊天室");
        this.setSize(550, 500);
        this.setResizable(false);
        User currentUser = DataBuffer.currentUser;
        System.out.println("currentUser："+currentUser);
        System.out.println("currentUser昵称："+currentUser.getNickname());
        System.out.println("currentUser聊天记录："+currentUser.getChatRecords());

        // 设置默认窗体在屏幕中央
        int x = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int y = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        this.setLocation((x - this.getWidth()) / 2, (y - this.getHeight()) / 2);

        //左边主面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        // 右边用户面板
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BorderLayout());

        // 创建一个分隔窗格
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                mainPanel, userPanel);
        splitPane.setDividerLocation(380);
        splitPane.setDividerSize(10);
        splitPane.setOneTouchExpandable(true);
        this.add(splitPane, BorderLayout.CENTER);

        // 左上边信息显示面板
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BorderLayout());
        // 右下连发送消息面板
        JPanel sendPanel = new JPanel();
        sendPanel.setLayout(new BorderLayout());

        // 创建一个分隔窗格
        JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                infoPanel, sendPanel);
        splitPane2.setDividerLocation(300);
        splitPane2.setDividerSize(1);
        mainPanel.add(splitPane2, BorderLayout.CENTER);

        otherInfoLbl = new JLabel("当前状态：群聊中...");
        infoPanel.add(otherInfoLbl, BorderLayout.NORTH);

        msgListArea = new JTextArea();
        for(String record:currentUser.getChatRecords()) {
            msgListArea.append(record);
        }
        msgListArea.setLineWrap(true);
        infoPanel.add(new JScrollPane(msgListArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

        JPanel tempPanel = new JPanel();
        tempPanel.setLayout(new BorderLayout());
        sendPanel.add(tempPanel, BorderLayout.NORTH);

        // 聊天按钮面板
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        tempPanel.add(btnPanel, BorderLayout.CENTER);

        // 清空聊天信息按钮
        JButton cancelBtn = new JButton(new ImageIcon("images/font.png"));
        cancelBtn.setMargin(new Insets(0, 0, 0, 0));
        cancelBtn.setToolTipText("清空聊天信息");
        btnPanel.add(cancelBtn);

        // 添加好友按钮
        JButton mixBtn = new JButton(new ImageIcon("images/sendFace.png"));
        mixBtn.setMargin(new Insets(0, 0 , 0, 0));
        mixBtn.setToolTipText("添加好友");
        btnPanel.add(mixBtn);

        // 发送窗口振动按钮
        JButton shakeBtn = new JButton(new ImageIcon("images/shake.png"));
        shakeBtn.setMargin(new Insets(0, 0, 0, 0));
        shakeBtn.setToolTipText("向对方发送窗口振动");
        btnPanel.add(shakeBtn);

        // 发送文件按钮
        JButton sendFileBtn = new JButton(new ImageIcon("images/sendPic.png"));
        sendFileBtn.setMargin(new Insets(0, 0, 0, 0));
        sendFileBtn.setToolTipText("向对方发送文件");
        btnPanel.add(sendFileBtn);

        // 私聊按钮
        rybqBtn = new JCheckBox("私聊");
        tempPanel.add(rybqBtn, BorderLayout.EAST);

        // 要发送的信息的区域
        sendArea = new JTextArea();
        sendArea.setLineWrap(true);
        sendPanel.add(new JScrollPane(sendArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

        // 聊天按钮面板
        JPanel btn2Panel = new JPanel();
        btn2Panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        this.add(btn2Panel, BorderLayout.SOUTH);
        JButton closeBtn = new JButton("关闭");
        closeBtn.setToolTipText("退出整个程序");
        btn2Panel.add(closeBtn);
        JButton submitBtn = new JButton("发送");
        submitBtn.setToolTipText("按Enter键发送消息");
        btn2Panel.add(submitBtn);
        sendPanel.add(btn2Panel, BorderLayout.SOUTH);

        // 在线用户列表面板
        JPanel onlineListPane = new JPanel();
        onlineListPane.setLayout(new BorderLayout());
        onlineCountLbl = new JLabel("在线用户列表(1)");
        onlineListPane.add(onlineCountLbl, BorderLayout.NORTH);

        // 新增好友列表面板
        JPanel matesPane = new JPanel();
        matesPane.setLayout(new BorderLayout());
        matesPane.add(new JLabel("好友列表"), BorderLayout.NORTH);

        // 当前用户面板
        JPanel currentUserPane = new JPanel();
        currentUserPane.setLayout(new BorderLayout());
        currentUserPane.add(new JLabel("当前用户"), BorderLayout.NORTH);

        // 右边用户列表创建一个分隔窗格
        JSplitPane splitPane3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                onlineListPane, matesPane);
        splitPane3.setDividerLocation(210);
        splitPane3.setDividerSize(1);
        userPanel.add(splitPane3);
        userPanel.add(currentUserPane, BorderLayout.SOUTH);

        // 获取在线用户并缓存
        DataBuffer.onlineUserListModel = new OnlineUserListModel(DataBuffer.onlineUsers);
        // 在线用户列表
        onlineList = new JList(DataBuffer.onlineUserListModel);
        onlineList.setCellRenderer(new MyCellRenderer());

        // 设置为单选模式
        onlineList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        onlineListPane.add(new JScrollPane(onlineList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

        // mateslist弹出菜单
        JPopupMenu pop1 = getListPop();
        onlineList.setComponentPopupMenu(pop1);
        int currentId = (int) DataBuffer.currentUser.getId();

        // mateslist初始化
        MatesListModel matesListModel;
        matesListModel = new MatesListModel(DataBuffer.currentUser.getMateList());
        matesListModels.set(currentId-1, matesListModel);
        mateList = new JList(matesListModels.get(currentId-1));
        mateList.setCellRenderer(new MyCellRenderer());
        mateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        matesPane.add(new JScrollPane(mateList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

        // mateslist弹出菜单
        JPopupMenu pop2 = delListPop();
        mateList.setComponentPopupMenu(pop2);

        //当前用户信息Label
        currentUserLbl = new JLabel();
        currentUserPane.add(currentUserLbl);

        ///////////////////////注册事件监听器/////////////////////////
        //关闭窗口
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                logout();
            }
        });

        //关闭按钮的事件
        closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                logout();
            }
        });

        //选择某个用户私聊
        rybqBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (rybqBtn.isSelected()) {
                    User selectedUser = (User) onlineList.getSelectedValue();
                    if (null == selectedUser) {
                        otherInfoLbl.setText("当前状态：私聊(选中在线用户列表中某个用户进行私聊)...");
                    } else if (DataBuffer.currentUser.getId() == selectedUser.getId()) {
                        otherInfoLbl.setText("当前状态：想自言自语?...系统不允许");
                    } else {
                        otherInfoLbl.setText("当前状态：与 " + selectedUser.getNickname()
                                + "(" + selectedUser.getId() + ") 私聊中...");
                    }
                } else {
                    otherInfoLbl.setText("当前状态：群聊...");
                }
            }
        });

        //选择某个用户
        onlineList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                User selectedUser = (User) onlineList.getSelectedValue();
                if (rybqBtn.isSelected()) {
                    if (DataBuffer.currentUser.getId() == selectedUser.getId()) {
                        otherInfoLbl.setText("当前状态：想自言自语?...系统不允许");
                    } else {
                        otherInfoLbl.setText("当前状态：与 " + selectedUser.getNickname()
                                + "(" + selectedUser.getId() + ") 私聊中...");
                    }
                }
            }
        });

        //发送文本消息
        sendArea.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == Event.ENTER) {
                    sendTxtMsg();
                }
            }
        });
        submitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                sendTxtMsg();
            }
        });
        //发送振动
        shakeBtn.addActionListener(event -> sendShakeMsg());
        //发送文件
        sendFileBtn.addActionListener(event -> sendFile());
        //发送添加好友请求
        mixBtn.addActionListener(e -> toMix());
        //清空聊天信息
        cancelBtn.addActionListener(e -> cancel());

        this.loadData();  //加载初始数据
    }

    /**
     * 创建列表上的弹出菜单对象，实现请求添加好友功能192 168 116 66
     */
    public JPopupMenu getListPop() {
        JPopupMenu pop1 = new JPopupMenu();
        JMenuItem to_mix = new JMenuItem("申请添加好友");
        to_mix.setActionCommand("toMix");
        to_mix.addActionListener(e -> toMix());
        pop1.add(to_mix);
        return pop1;
    }

    /**
     * 创建好友列表上的弹出菜单对象，实现删除好友功能
     */
    public JPopupMenu delListPop() {
        JPopupMenu pop2 = new JPopupMenu();
        JMenuItem del_mix = new JMenuItem("删除好友");
        del_mix.setActionCommand("delMix");
        del_mix.addActionListener(e -> delMix());
        pop2.add(del_mix);
        return pop2;
    }

    /**
     *申请添加好友
     */
    public void toMix() {
        User selectedUser = (User) onlineList.getSelectedValue();
        if (null != selectedUser) {
            if (DataBuffer.currentUser.getId() == selectedUser.getId()) {
                JOptionPane.showMessageDialog(ChatFrame.this, "不能将自己添加为好友!",
                        "请求失败", JOptionPane.ERROR_MESSAGE);
            } else if(DataBuffer.currentUser.testMateId(selectedUser)) {
                JOptionPane.showMessageDialog(ChatFrame.this, "对方已在您的好友列表中!",
                        "请求失败", JOptionPane.ERROR_MESSAGE);
            } else{
                sendMix = new Message();
                sendMix.setFromUser(DataBuffer.currentUser);
                sendMix.setToUser(selectedUser);
                sendMix.setSendTime(new Date());
                Request request = new Request();
                request.setAction("toMix");
                request.setAttribute("Message", sendMix);
                try {
                    ClientUtil.sendTextRequest2(request);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ClientUtil.appendTxt2MsgListArea("【系统消息】已向用户 "
                        + selectedUser.getNickname() + "("
                        + selectedUser.getId() + ") 发送添加好友申请，等待对方同意...\n", currentUser);
            }
        } else {
            JOptionPane.showMessageDialog(ChatFrame.this, "请选中要添加好友的对象！",
                    "添加好友失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 删除好友
     */
    public void delMix() {
        User selectedUser = (User) mateList.getSelectedValue();
        if (null != selectedUser) {
            int select = JOptionPane.showConfirmDialog(ChatFrame.this,
                    "确定要删除" + selectedUser.getNickname() + "吗？",
                    "删除好友", JOptionPane.YES_NO_OPTION);
            if(select == JOptionPane.YES_OPTION) {
                sendDelMix = new Message();
                sendDelMix.setFromUser(DataBuffer.currentUser);
                sendDelMix.setToUser(selectedUser);
                sendDelMix.setSendTime(new Date());
                Request request = new Request();
                request.setAction("toDelMix");
                request.setAttribute("sendDelMix", sendDelMix);
                try {
                    ClientUtil.sendTextRequest2(request);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ClientUtil.appendTxt2MsgListArea("【系统消息】您已将用户 "
                        + selectedUser.getNickname() + "("
                        + selectedUser.getId() + ") 从好友列表中删除\n", currentUser);
            }
        }else {
            JOptionPane.showMessageDialog(ChatFrame.this, "请选中要删除的好友对象!",
                    "操作失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 加载数据
     */
    public void loadData() {
        // 加载当前用户数据
        if (null != DataBuffer.currentUser) {
            currentUserLbl.setIcon(
                    new ImageIcon("images/" + DataBuffer.currentUser.getHead() + ".png"));
            currentUserLbl.setText(DataBuffer.currentUser.getNickname()
                    + "(" + DataBuffer.currentUser.getId() + ")");
        }
        // 设置在线用户列表
        onlineCountLbl.setText("在线用户列表(" + DataBuffer.onlineUserListModel.getSize() + ")");
        // 启动监听服务器消息的线程
        new ClientThread(this).start();
    }

    /**
     * 清空聊天记录
     */
    public void cancel() {
        msgListArea.setText("");
        currentUser.clearChatRecords();
        UserService userService = new UserService();
        userService.saveUser(currentUser);
    }

    /**
     * 发送振动
     */
    public void sendShakeMsg() {
        User selectedUser = (User) onlineList.getSelectedValue();
        if (null != selectedUser) {
            if (DataBuffer.currentUser.getId() == selectedUser.getId()) {
                JOptionPane.showMessageDialog(ChatFrame.this, "不能给自己发送振动!",
                        "不能发送", JOptionPane.ERROR_MESSAGE);
            } else {
                Message msg = new Message();
                msg.setFromUser(DataBuffer.currentUser);
                msg.setToUser(selectedUser);
                msg.setSendTime(new Date());

                DateFormat df = new SimpleDateFormat("HH:mm:ss");
                StringBuffer sb = new StringBuffer();
                sb.append(" ").append(msg.getFromUser().getNickname())
                        .append("(").append(msg.getFromUser().getId()).append(") ")
                        .append(df.format(msg.getSendTime()))
                        .append("\n  给").append(msg.getToUser().getNickname())
                        .append("(").append(msg.getToUser().getId()).append(") ")
                        .append("发送了一个窗口抖动\n");
                msg.setMessage(sb.toString());

                Request request = new Request();
                request.setAction("shake");
                request.setAttribute("msg", msg);
                try {
                    ClientUtil.sendTextRequest2(request);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ClientUtil.appendTxt2MsgListArea(msg.getMessage(), currentUser);
                new JFrameShaker(ChatFrame.this).startShake();
            }
        } else {
            JOptionPane.showMessageDialog(ChatFrame.this, "不能群发送振动!",
                    "不能发送", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 发送文本消息
     */
    public void sendTxtMsg() {
        String content = sendArea.getText();
        if ("".equals(content)) { //无内容
            JOptionPane.showMessageDialog(ChatFrame.this, "不能发送空消息!",
                    "不能发送", JOptionPane.ERROR_MESSAGE);
        } else { //发送
            User selectedUser = (User) onlineList.getSelectedValue();

            //如果设置了ToUser表示私聊，否则群聊
            Message msg = new Message();
            if (rybqBtn.isSelected()) {  //私聊
                if (null == selectedUser) {
                    JOptionPane.showMessageDialog(ChatFrame.this, "没有选择私聊对象!",
                            "不能发送", JOptionPane.ERROR_MESSAGE);
                    return;
                } else if (DataBuffer.currentUser.getId() == selectedUser.getId()) {
                    JOptionPane.showMessageDialog(ChatFrame.this, "不能给自己发送消息!",
                            "不能发送", JOptionPane.ERROR_MESSAGE);
                    return;
                } else {
                    msg.setToUser(selectedUser);
                }
            }
            msg.setFromUser(DataBuffer.currentUser);
            msg.setSendTime(new Date());

            DateFormat df = new SimpleDateFormat("HH:mm:ss");
            StringBuffer sb = new StringBuffer();
            sb.append(" ").append(df.format(msg.getSendTime())).append(" ")
                    .append(msg.getFromUser().getNickname())
                    .append("(").append(msg.getFromUser().getId()).append(") ");
            if (!this.rybqBtn.isSelected()) { //群聊
                sb.append("对大家说");
            }
            sb.append("\n  ").append(content).append("\n");
            msg.setMessage(sb.toString());

            Request request = new Request();
            request.setAction("chat");
            request.setAttribute("msg", msg);
            try {
                ClientUtil.sendTextRequest2(request);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //JTextArea中按“Enter”时，清空内容并回到首行
            InputMap inputMap = sendArea.getInputMap();
            ActionMap actionMap = sendArea.getActionMap();
            Object transferTextActionKey = "TRANSFER_TEXT";
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), transferTextActionKey);
            actionMap.put(transferTextActionKey, new AbstractAction() {
                private static final long serialVersionUID = 7041841945830590229L;
                public void actionPerformed(ActionEvent e) {
                    sendArea.setText("");
                    sendArea.requestFocus();
                }
            });
            sendArea.setText("");
//            ClientUtil.appendTxt2MsgListArea(msg.getMessage(), currentUser);
        }
    }

    /**
     * 发送文件
     */
    private void sendFile() {
        User selectedUser = (User) onlineList.getSelectedValue();
        if (null != selectedUser) {
            if (DataBuffer.currentUser.getId() == selectedUser.getId()) {
                JOptionPane.showMessageDialog(ChatFrame.this, "不能给自己发送文件!",
                        "不能发送", JOptionPane.ERROR_MESSAGE);
            } else {
                JFileChooser jfc = new JFileChooser();
                if (jfc.showOpenDialog(ChatFrame.this) == JFileChooser.APPROVE_OPTION) {
                    File file = jfc.getSelectedFile();
                    sendFile = new FileInfo();
                    sendFile.setFromUser(DataBuffer.currentUser);
                    sendFile.setToUser(selectedUser);
                    try {
                        sendFile.setSrcName(file.getCanonicalPath());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    sendFile.setSendTime(new Date());

                    Request request = new Request();
                    request.setAction("toSendFile");
                    request.setAttribute("file", sendFile);
                    try {
                        ClientUtil.sendTextRequest2(request);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ClientUtil.appendTxt2MsgListArea("【文件消息】向 "
                            + selectedUser.getNickname() + "("
                            + selectedUser.getId() + ") 发送文件 ["
                            + file.getName() + "]，等待对方接收...\n", currentUser);
                }
            }
        } else {
            JOptionPane.showMessageDialog(ChatFrame.this, "不能给所有在线用户发送文件!",
                    "不能发送", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 关闭客户端
     */
    private void logout() {
        int select = JOptionPane.showConfirmDialog(ChatFrame.this,
                "确定退出吗？\n\n退出程序将中断与服务器的连接!", "退出聊天室",
                JOptionPane.YES_NO_OPTION);
        if (select == JOptionPane.YES_OPTION) {
            Request req = new Request();
            req.setAction("exit");
            req.setAttribute("user", DataBuffer.currentUser);
            try {
                ClientUtil.sendTextRequest(req);
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                System.exit(0);
            }
        } else {
            this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        }
    }

    /*踢除*/
    public static void remove() {
        int select = JOptionPane.showConfirmDialog(sendArea,
                "您已被踢除？\n\n", "系统通知",
                JOptionPane.YES_NO_OPTION);

        Request req = new Request();
        req.setAction("exit");
        req.setAttribute("user", DataBuffer.currentUser);
        try {
            ClientUtil.sendTextRequest(req);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}
