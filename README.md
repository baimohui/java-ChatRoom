# Chat_Room
Java Socket 通信实现的聊天室  



## 功能介绍

 本程序基于CS架构实现，程序主要借鉴参考 [https://github.com/leo6033/Chat_Room](https://github.com/leo6033/Chat_Room)，并在原程序的基本框架下扩充了添加和删除好友以及保存聊天日志等功能。基本功能实现如下：

1. 用Java的GUI界面编写聊天室服务器端和客户端， 支持多个客户端连接到一个服务器。
2. 用户能够进行注册和登录。
3. 用户可以群聊和私聊。
4. 在线用户列表能在所有客户端上实时显示。
5. 用户之间可以进行文件传输。
6. 用户彼此可以添加和删除好友。
7. 用户可以查看自己的聊天日志。
8. 能够发送窗口振动。
9. 服务器能够对所有或单个用户发送通知，能够强行让用户下线。
10. 服务器能够查看在线用户和注册用户。



## 使用基本说明

1. 代码建议在IntelliJ IDEA上运行。在eclipse上运行中文可能出现乱码。

2. 首先运行服务端代码（ChatRoom\src\server\MainServer.java）。

   <img src="https://github.com/baimohui/java-/blob/main/des-pics/image-20201215154543001.png" alt="server" width=50% />

3. 然后运行客户端代码（ChatRoom\src\client\ClientThread.java），每次运行都相当于创建一个新的用户进程。

   <img src="https://github.com/baimohui/java-/blob/main/des-pics/image-20201215155603532.png" alt="client" width=25% />

4. 用户服务管理代码（ChatRoom\src\server\model\service\UserService）。可以通过运行它来手动修改用户信息，非必要。

5. 用户成功登录后的聊天室界面展示。

   左上块是聊天信息展示区，在这里能够看到用户的群聊信息和系统通知。左下块是文本编辑区，用来发送文本信息。

   在聊天信息展示区和文本编辑区之间还有一排按钮和选项。左边四个按钮的功能分别为待定、添加好友、发送窗口震动、发送文件，右边的私聊选项勾上以后即实现对单个用户私聊。

   右侧分为三个部分：在线用户列表、好友列表、当前用户。选中一个在线用户后点击右键可以向对方请求添加好友，选中一个好友后点击右键可以删除好友。

   <img src="https://github.com/baimohui/java-/blob/main/des-pics/image-20201215160108670.png" width=40% />
   



## 程序待完善的地方

程序本身还存在一些bug和待完善的地方，基本说明如下：

1. 删除好友时，必须要当对方上线时才能进行该操作，否则会导致好友列表信息出现混乱。（待解决）

2. 用户不能自己注销账号，不能修改个人信息（头像、昵称、密码等）。（待完善）

   ......

项目还在更新和完善中，欢迎关注。





