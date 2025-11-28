package org.example.Util;

import org.example.common.Config;

import java.io.IOException;
import java.nio.file.*;

public class WatchConfig implements Runnable {


    @Override
    public void run() {
        Path path = Paths.get("config.properties");

        WatchService watcher = null;
        try {
            watcher = FileSystems.getDefault().newWatchService();
            path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            while (true) {
                WatchKey key = watcher.take();

                for (WatchEvent<?> event : key.pollEvents()) {

                    if (event.kind() == StandardWatchEventKinds.OVERFLOW) {

                        continue;
                    }
                    Path fileName = (Path) event.context();
                    System.out.println("文件更新: " + fileName);
                    Config.config();
                }

                if (!key.reset()) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
