package org.example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Server {
    //    private static String dest = "/home/desk/";
    private static String dest = "E://";
    private static int port = 8888;

    private ServerSocketChannel ssc;
    ByteBuffer buf;

    public boolean init() {

        try {
            ssc = ServerSocketChannel.open();
            //绑定
            ssc.socket().bind(new InetSocketAddress(port));
            //设置非阻塞模式
            ssc.configureBlocking(false);
            buf = ByteBuffer.allocate(1024);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    private void listener() throws Exception {

        while (true) {
//            System.out.println("Waiting for connections");
            SocketChannel accept = ssc.accept();
            if (accept == null) { //没有链接传入
                Thread.sleep(2000);
                continue;
            }
            System.out.println("Incoming connection from: " + accept.socket().getRemoteSocketAddress());
//            先接受文件名字

            int len;
            len = accept.read(buf);
            String fileName = new String(buf.array(), 0, len, StandardCharsets.UTF_8);
            fileName = dest + fileName;
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
//            向client回复收到
            accept.write(ByteBuffer.wrap("OK".getBytes()));
            buf = ByteBuffer.allocate(1024);
//            写入数据
            FileOutputStream fos = new FileOutputStream(file);
            while ((len = accept.read(buf)) != -1) {
                buf.flip();
                fos.write(buf.array(), 0, len);
                buf.clear();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("监听已经启动");
        InetAddress local = InetAddress.getLocalHost();
        System.out.println("your ip is " + local.getHostAddress());
        Server server = new Server();
        server.init();
        server.listener();

    }

}
