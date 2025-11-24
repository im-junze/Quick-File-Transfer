package org.example;

import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class Client {
    //    static   String name = "E:\\project\\3\\Quick-File-Transfer\\src\\main\\java\\org\\example\\openjdk-8u43-linux-x64.tar.gz";
    static String name = "E:\\project\\3\\Quick-File-Transfer\\src\\main\\java\\org\\example\\a.txt";
//    static String name = "E:\\project\\3\\Quick-File-Transfer\\src\\main\\java\\org\\example\\test";

    public static void main(String[] args) throws Exception {
        InetAddress local = InetAddress.getLocalHost();
        System.out.println("your ip is "+local.getHostAddress());
        //创建SocketChannel
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 8888));
//        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("192.168.61.20", 8888));
        //设置阻塞和非阻塞
        socketChannel.configureBlocking(false);
        //读操作
        RandomAccessFile file = new RandomAccessFile(name, "rw");
        FileChannel channel = file.getChannel();
        String[] split = name.split("\\\\");
        ByteBuffer buf = ByteBuffer.wrap(split[split.length - 1].getBytes());
        socketChannel.write(buf);
        buf = ByteBuffer.allocate(1024);
        while (true){
            int read = socketChannel.read(buf);
            if (read>0) break;
            else Thread.sleep(50);
        }
        long fileSize = file.length();
        long transferred = 0;
        int flag;
        while ((flag = channel.read(buf)) != -1) {
            buf.flip();
            socketChannel.write(buf);
            buf.clear();
            transferred += flag;
            int progress = (int) ((transferred * 100) / fileSize);
            System.out.print("\r传输进度: " + progress + "% (" + transferred + "/" + fileSize + " bytes)");
//            Thread.sleep(1000);
        }

        socketChannel.close();
        System.out.println("transfer over");
    }
}
