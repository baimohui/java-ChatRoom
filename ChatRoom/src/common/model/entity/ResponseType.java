package common.model.entity;

/**
 * 〈响应类型枚举〉<br>
 * 〈〉
 *
 */

public enum ResponseType {
    /**
     * 文本内容
     */
    TEXT,
    /**
     * 准备发送文件
     */
    TOSENDFILE,
    /**
     * 同意接收文件
     */
    AGREERECEIVEFILE,
    /**
     * 拒绝接收文件
     */
    REFUSERECEIVEFILE,
    /**
     * 发送文件
     */
    RECEIVEFILE,
    /**
     * 客户端登录
     */
    LOGIN,
    /**
     * 客户端退出
     */
    LOGOUT,
    /**
     * 聊天
     */
    CHAT,
    /**
     * 振动
     */
    SHAKE,
    /**
     * 其它
     */
    OTHER,
    /*
     * 广播
     */
    BOARD,
    /*
     * 踢除
     */
    REMOVE,
    /**
     * 准备发送好友申请
     */
    TOSENDMIX,
    /**
     * 同意好友申请
     */
    AGREESENDMIX,
    /**
     * 删除好友
     */
    AGREESENDDELMIX
}
