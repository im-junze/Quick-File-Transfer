package org.example;

import org.example.Client;
import org.example.Server;
import org.example.common.Config;

import java.io.File;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws Exception {

        ExecutorService poll = Executors.newFixedThreadPool(3);
        InetAddress local = InetAddress.getLocalHost();
        System.out.println("your ip is " + local.getHostAddress());
        poll.execute(() -> {
            Server server = new Server();
            server.init();
            try {
                System.out.println("server服务启动");
                server.listener();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("如果需要传递文件可以输入地址或者已经配置或者输入y");
            String next = sc.nextLine();
            if (next.equals("y")) {
                Client.start();
                continue;
            }
            File file = new File(next);
            if (!file.exists()) continue;
            else {
                Config.setSrcFile(next);
                Client.start();
            }

        }

    }


}
