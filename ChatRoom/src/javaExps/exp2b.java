package javaExps;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
class exp2b{
    public static void main(String args[]){
        JFrame f = new JFrame("Menu");
        //设置默认窗体在屏幕中央
        int x = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int y = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        f.setLocation((x - f.getWidth()) / 2, (y-f.getHeight())/ 2);
        f.setPreferredSize(new Dimension(330,230));
        f.setResizable(false);
        JMenuBar mb = new JMenuBar();
        f.setJMenuBar(mb);
        JMenu m1 = new JMenu("File");
        JMenu m2 = new JMenu("Format");
        JMenu m3 = new JMenu("Help");
        mb.add(m1);
        mb.add(m2);
        mb.add(m3);
        JMenu m11 = new JMenu("中文");
        JMenu m12 = new JMenu("进制");
//        JMenuItem m13 = new JMenuItem("Load");
//        JMenuItem m14 = new JMenuItem("Quit");
        m2.add(m11);
        m2.add(m12);
//        m2.add(m13);
//        m2.addSeparator();
//        m2.add(m14);
        JMenuItem m121 = new JMenuItem("二进制");
        JMenuItem m122 = new JMenuItem("八进制");
        JMenuItem m123 = new JMenuItem("十进制");
        m12.add(m121);
        m12.add(m122);
        m12.add(m123);
        f.pack();
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    } }
