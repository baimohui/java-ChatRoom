package server.model.service;

import common.model.entity.User;
import common.util.IOUtil;
import server.DataBuffer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 */

public class UserService {
    private static int idCount = 0; //id

    /** 新增用户 */
    public void addUser(User user){
        List<User> users = this.loadAllUser();
        idCount = users.size();
        user.setId(++idCount);
        users.add(user);
        this.saveAllUser(users);
    }

    /** 删除用户 */
    public void delUser(User user){
        List<User> users = this.loadAllUser();
        users.remove(user);
        this.saveAllUser(users);
    }

    /** 保存用户信息 */
    public void saveUser(User savedUser) {
        List<User> users = this.loadAllUser();
        for (User user : users) {
            if(savedUser.getId()==user.getId()) {
                users.set((int)savedUser.getId()-1, savedUser);
                break;
            }
        }
        this.saveAllUser(users);
    }

    /** 重载 保存两个用户信息 */
    public void saveUser(User savedUser1, User savedUser2) {
        List<User> users = this.loadAllUser();
        for (User user : users) {
            int i=0;
            if(savedUser1.getId() == user.getId()){
                users.set((int)savedUser1.getId()-1, savedUser1);
                i++;
            }else if(savedUser2.getId()==user.getId()) {
                users.set((int)savedUser2.getId()-1, savedUser2);
                i++;
            }
            if(i==2) break;
        }
        saveAllUser(users);
    }

    /** 用户登录 */
    public User login(long id, String password){
        User result = null;
        List<User> users = this.loadAllUser();
        for (User user : users) {
            if(id == user.getId() && password.equals(user.getPassword())){
                result = user;
                break;
            }
        }
        this.saveAllUser(users);
        return result;
    }

    /** 根据ID加载用户 */
    public User loadUser(long id){
        User result = null;
        List<User> users = loadAllUser();
        for (User user : users) {
            if(id == user.getId()){
                result = user;
                break;
            }
        }
        return result;
    }


    /** 加载所有用户 */
    @SuppressWarnings("unchecked")
    public List<User> loadAllUser() {
        List<User> list = null;
        list = new ArrayList<User>(100);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(
                    new FileInputStream(
                            DataBuffer.configProp.getProperty("dbpath")));
            Object obj;
            obj = ois.readObject();
            list = (List<User>) obj;

        } catch (Exception e) {
            System.out.println("果然是这里读取失败了2");
            e.printStackTrace();
        }finally{
            IOUtil.close(ois);
        }
        return list;
    }

    private void saveAllUser(List<User> users) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(
                    new FileOutputStream(
                            DataBuffer.configProp.getProperty("dbpath")));
            //写回用户信息
            oos.writeObject(users);
            oos.flush();
        } catch (Exception e) {
            System.out.println("saveAllUser ERROR!!!");
            e.printStackTrace();
        }finally{
            IOUtil.close(oos);
        }
    }

    /** 初始化几个测试用户 */
    public void initUser(){
        System.out.println("UserService的user初始化");
        User user = new User("2000", "张三", 'm', 1);
        user.setId(1);
        User user2 = new User("2000", "李四", 'm', 4);
        user2.setId(2);
        User user3 = new User("2000", "王五", 'm', 2);
        user3.setId(3);

        List<User> users = new CopyOnWriteArrayList<User>();
        users.add(user);
        users.add(user2);
        users.add(user3);

//        User user1 = this.loadUser(1);
//        User user2 = this.loadUser(2);
//        User user3 = this.loadUser(3);
//        this.delUser(user1);
//        this.delUser(user2);
//        this.delUser(user3);
        this.saveAllUser(users);
    }

    public static void main(String[] args){
        new UserService().initUser();
        List<User> users = new UserService().loadAllUser();
        new UserService().saveAllUser(users);
        for (User user : users) {
            System.out.println(user);
            System.out.println(user.getChatRecords());
        }
    }
}
