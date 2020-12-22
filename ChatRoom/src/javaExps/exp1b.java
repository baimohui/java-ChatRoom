package javaExps;

import java.util.Scanner;

abstract class Employee{
    public abstract double ComputerSalary();
    public double basis;
}
class Manager extends Employee{
    public Manager(double m_basis){
        basis=m_basis;
    }
    public double ComputerSalary(){
        return basis;
    }
    public void show(){
        System.out.println("工资:"+ComputerSalary());
    }
}
class worker extends Employee{
    public int days;
    worker(int days,double m_basis){
        this.days=days;
        basis=m_basis;
    }
    public double ComputerSalary(){
        return days*basis;
    }
    public void show(){
        System.out.println("工资:"+ComputerSalary());
    }
}

class Saleman extends Employee{
    public int days;
    Saleman(int days,double m_basis){
        this.days=days;
        basis=m_basis;
    }
    public double ComputerSalary(){
        return days*basis;
    }
    public void show(){
        System.out.println("工资:"+ComputerSalary());
    }
}

public class exp1b{
    public static void main(String args[]){
        double basis;
        int days;
        double commission;
        System.out.print("请输入基本工资：");
        Scanner reader=new Scanner(System.in);
        basis=reader.nextDouble();
        System.out.print("请输入工作天数：");
        days=reader.nextInt();
        System.out.print("请输入销售提成：");
        commission=reader.nextDouble();
        Manager a=new Manager(basis);
        a.show();
        Saleman b=new Saleman((int)basis,commission);
        b.show();
        worker c=new worker(days,basis);
        c.show();
    }
}


