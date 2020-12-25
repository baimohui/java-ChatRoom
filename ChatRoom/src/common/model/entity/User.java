package common.model.entity;

import javax.jws.soap.SOAPBinding;
import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 */

public class User implements Serializable {
    private static final long serialVersionUID = 5942011574971970871L;
    private long id;
    private String password;
    private String nickname;
    private int head;
    private char sex;
    private List<User> mateList;
    private List<String> chatRecords;

    public User(String password, String nickname, char sex, int head){
        this.password = password;
        this.sex = sex;
        this.head = head;
        this.mateList = new ArrayList<User>(100);
        this.chatRecords = new ArrayList<String>(300);

        if(nickname.equals(""))
        {
            this.nickname = "未命名";
        }else{
            this.nickname = nickname;
        }
        //非常重要的一步。因为如果只对chatRecords分配空间，而没有分配实际值时，对其进行readObject操作时会报错！
        String sb = "【系统消息】用户"+ nickname + "上线了！\n";
        this.chatRecords.add(sb);
    }

    public User(long id, String password){
        this.id = id;
        this.password = password;
    }

    public User(long id, List<User> mateList) {
        this.id = id;
        this.mateList = mateList;
    }

    public long getId(){
        return  id;
    }

    public void setId(long id){
        this.id = id;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getPassword(){
        return password;
    }

    public void setSex(char sex){
        this.sex=sex;
    }

    public char getSex(){
        return this.sex;
    }

    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    public String getNickname(){
        return this.nickname;
    }

    public void setHead(int head){
        this.head = head;
    }

    public int getHead(){
        return this.head;
    }

    public void setMateList(List<User> mateList) {this.mateList=mateList; };

    public void addMate(User user) {
        if(this.mateList == null) {
            this.mateList = new ArrayList<User>(100);
        }
        if(this.mateList.contains(user)) {
            return;
        }
        this.mateList.add(user);
    }

    public void delMate(User user) {
        this.mateList.remove(user);
    }

    public List<User> getMateList() { return this.mateList;}

    // 查看要检验的用户是否已存在自身的好友列表中
    public boolean testMateId(User testUser) {
        for(User mate: this.mateList) {
            if(mate.getId()== testUser.getId()) {
                return true;
            }
        }
        return false;
    }

    public void addRecord(String str) {
        if(this.chatRecords == null) {
            this.chatRecords = new ArrayList<String>(300);
            String sb = "【系统消息】用户"+this.nickname + "上线了！\n";
            this.chatRecords.add(sb);
        }
        this.chatRecords.add(str);
    }

    public List<String> getChatRecords() {
        if(this.chatRecords == null) {
            this.chatRecords = new ArrayList<String>(300);
            String sb = "【系统消息】用户"+this.nickname + "上线了！\n";
            this.chatRecords.add(sb);
        }
        return this.chatRecords;
    }

    public void clearChatRecords() {
        this.chatRecords = null;
    }

    public ImageIcon getHeadIcon(){
        ImageIcon image = new ImageIcon("images/"+head+".png");
        return image;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + head;
        result = prime * result + (int)(id ^ (id >> 32));
        result = prime * result + ((nickname == null) ? 0 : nickname.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + sex;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if(head != other.head || id != other.id || sex != other.sex)
            return false;
        if(nickname == null){
            if(other.nickname != null)
                return false;
        }else if(!nickname.equals(other.nickname))
            return false;
        if(password == null){
            if(other.password != null)
                return false;
        }else if(!password.equals(other.password))
            return  false;
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getName()
                + "[id=" + this.id
                + ",pwd=" + this.password
                + ",nickname=" + this.nickname
                + ",head=" + this.head
                + ",sex=" + this.sex
                + "]";
    }
}
