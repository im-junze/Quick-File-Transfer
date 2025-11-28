package org.example;

import org.example.Util.FileUtil;
import org.example.common.Config;
import org.example.common.FileType;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class Client {

    static String name;
    static int prefixIndex;
    private static SocketChannel socketChannel;



    public void init() throws Exception {

        //创建SocketChannel
        socketChannel = SocketChannel.open(new InetSocketAddress(Config.remoteHost, Config.port));
        //设置阻塞和非阻塞
        socketChannel.configureBlocking(false);
        prefixIndex = name.lastIndexOf(File.separator) + 1;
    }

    public void close() throws Exception {
        socketChannel.close();
    }





    public void transferFile(String name) throws Exception {
        init();
        //读操作
        RandomAccessFile file = new RandomAccessFile(name, "rw");
        FileChannel channel = file.getChannel();
        //        如果parent不为null则带着目录传过去
        String tarName = name.substring(prefixIndex);
        tarName = tarName.replace(File.separator, "/");

        ByteBuffer buf = ByteBuffer.wrap(tarName.getBytes());
        socketChannel.write(buf);
        buf = ByteBuffer.allocate(1024);
        while (true) {
            int read = socketChannel.read(buf);
            if (read > 0) break;
            else Thread.sleep(5);
        }
        buf.clear();
        transfer(file, channel, buf);
        close();
    }

    private static void transfer(RandomAccessFile file, FileChannel channel, ByteBuffer buf) throws IOException {
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
        System.out.println("transfer over");
    }


    public void transferDirectory(String name) throws Exception {
        File dir = new File(name);
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                transferFile(file.getAbsolutePath());
            } else transferDirectory(file.getAbsolutePath());
        }

    }

    public static void start() throws Exception {
        Client client = new Client();
        name = Config.srcFile;
        FileType check = FileUtil.check(name);
        if (check.equals(FileType.File)) {
            client.transferFile(name);
            return;
        }
        if (check.equals(FileType.Directory)) {
            client.transferDirectory(name);
        }
    }
    public static void main(String[] args) throws Exception {
        InetAddress local = InetAddress.getLocalHost();
        System.out.println("your ip is " + local.getHostAddress());
        Client client = new Client();
//        client.init();
        name = Config.srcFile;
        FileType check = FileUtil.check(name);
        if (check.equals(FileType.File)) {
            client.transferFile(name);
            return;
        }
        if (check.equals(FileType.Directory)) {
            client.transferDirectory(name);
        }
    }
    /*
     * 首先会指定目录地址
     * 如果是文件夹的话则递归到下一个文件夹，将前面的文件夹只保留文件夹名就可以，再把后面的附加上
     * 利用splace把前面的替换掉就可以，或者用substrance直接截取
     *
     * */

}
