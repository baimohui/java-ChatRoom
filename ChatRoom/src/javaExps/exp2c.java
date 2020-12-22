package javaExps;

class Station extends Thread
{
    // 站台可售票数
    private static int ticket = 20;

    public void run() {
        // 同步代码块
        while (ticket>0)
        {
            synchronized (this) {
                if (ticket > 0) {
                    System.out.println(Thread.currentThread().getName() + "卖出第" + (21 - ticket--) + "张票");
                } else {
                    System.out.println("票已售完，请下次再来！");
                    System.exit(0);
                }
            }
                try {
                    // 为模拟售票窗口间的售票动作的时间间隔，设置售票窗口线程在每次卖出票后休眠0.5秒
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }



//        while(ticket>0)//同步锁
//        {
//            synchronized(this) {
//                if(ticket>0)
//                {
//                    System.out.println(Thread.currentThread().getName()+"卖出第"+(21-ticket--)+"张票");
//                }
//                else{
//                    System.out.println("票已售完，请下次再来！");
//                    System.exit(0);
//                }
//            }
//            try {
//                Thread.sleep(50);
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }

    }

    public static void main(String[] args)
    {
        // 实例化站台对象
        Station s = new Station();

        // 创建三个售票窗口线程进行售票
        new Thread(s,"售票窗口A").start();
        new Thread(s,"售票窗口B").start();
        new Thread(s,"售票窗口C").start();
    }
}