package org.example;

import org.example.common.ChannelType;
import org.example.common.Config;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class SelectServer {
    //    private static String dest = "/home/desk/";
    private static String dest = Config.dest;
    private static int port = Config.port;
    private static File log;
    private static PrintWriter pw;
    private static ServerSocketChannel ssc;
    private static Selector selector;
    private static ByteBuffer buf;

    static {
        try {
            selector = Selector.open();
            ssc = ServerSocketChannel.open();
            //绑定
            ssc.socket().bind(new InetSocketAddress(port));
            //设置非阻塞模式
            ssc.configureBlocking(false);
            buf = ByteBuffer.allocate(1024);
            log = new File("log.log");
            if (!log.exists()) {
                log.createNewFile();
            }
            pw = new PrintWriter(new FileOutputStream(log), true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void main(String[] args) throws IOException {

        SelectServer server = new SelectServer();
        server.ssc.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                if (next.isAcceptable()) {
                    SocketChannel accept = ssc.accept();
                    pw.println("Incoming connection from: " + accept.socket().getRemoteSocketAddress());
                    accept.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, ChannelType.server);
                } else if (next.isReadable()) {
                    if (ChannelType.server.equals((ChannelType) next.attachment())) {
                        accept((SocketChannel) next.channel());
                    }


                }


                iterator.remove();
            }


        }

    }

    public void listener() throws Exception {

        while (true) {
//            System.out.println("Waiting for connections");
            SocketChannel accept = ssc.accept();
            if (accept == null) { //没有链接传入
                Thread.sleep(2000);
                continue;
            }
            pw.println("Incoming connection from: " + accept.socket().getRemoteSocketAddress());
//            先接受文件名字
            accept(accept);
        }
    }

    private static void accept(SocketChannel accept) throws IOException {
        int len;
        len = accept.read(buf);
        String fileName = new String(buf.array(), 0, len, StandardCharsets.UTF_8);
        fileName = fileName.replace("/", File.separator);
        fileName = dest + fileName;
        File file = new File(fileName);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
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
