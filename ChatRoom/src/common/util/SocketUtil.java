package common.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 〈网络操作相关工具类〉<br>
 * 〈〉
 *
 */

public class SocketUtil {
    /** 关闭Socket */
    public static void close(Socket socket) {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** 关闭ServerSocket */
    public static void close(ServerSocket ss) {
        if (ss != null && !ss.isClosed()) {
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
