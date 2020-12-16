package common.model.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 */

public class Message implements Serializable {
    private static final long serialVersionUID = 1820192075144114657L;
    /** 消息接收者 */
    private User toUser;
    /** 消息发送者 */
    private User fromUser;
    /** 消息内容 */
    private String message;
    /** 发送时间 */
    private Date sendTime;


    public User getToUser() {
        return toUser;
    }
    public void setToUser(User toUser) {
        this.toUser = toUser;
    }
    public User getFromUser() {
        return fromUser;
    }
    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public Date getSendTime() {
        return sendTime;
    }
    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }
}
