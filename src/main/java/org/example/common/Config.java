package org.example.common;

import org.example.Client;
import org.junit.Test;

import java.io.*;
import java.util.Properties;

public class Config {
    public static String srcFile;
    public static int port;
    public static String remoteHost;
    public static final Properties properties = new Properties();
    public static   String dest;
    private static String fileName= "config.properties";
    static {
        config();
    }
    public static void setSrcFile(String file){
        properties.setProperty("srcFile", file);
        try {
            // 使用应用程序工作目录保存配置文件
            String configPath = System.getProperty("user.dir") + File.separator + fileName;
            try (OutputStream ps = new FileOutputStream(configPath)) {
                properties.store(ps, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void config() {
        InputStream is = Config.class.getClassLoader().getResourceAsStream(fileName);
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        remoteHost = properties.getProperty("remoteHost");
        port = Integer.parseInt(properties.getProperty("port"));
        srcFile = properties.getProperty("srcFile");
        dest = properties.getProperty("dest");
    }
    @Test
    public void test(){
        properties.list(System.out);
        setSrcFile("name");
        properties.list(System.out);
    }
}
