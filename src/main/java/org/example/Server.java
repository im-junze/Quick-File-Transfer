package org.example;

import org.example.common.Config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TransferQueue;

public class Server {
    //    private static String dest = "/home/desk/";
    private static String dest = Config.dest;
    private static int port = Config.port;
    private static File log;
    private static PrintWriter pw;
    private ServerSocketChannel ssc;
    ByteBuffer buf;
    static {
        log = new File("log.log");
      try {
          if (!log.exists()){
              log.createNewFile();
          }
          pw =new PrintWriter(new FileOutputStream(log),true);
      }catch (Exception e){
          e.printStackTrace();
      }

    }



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

    private void accept(SocketChannel accept) throws IOException {
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
        buf.clear();
//            写入数据
        FileOutputStream fos = new FileOutputStream(file);
        while ((len = accept.read(buf)) != -1) {
            buf.flip();
            fos.write(buf.array(), 0, len);
            buf.clear();
        }
    }


}
