package org.example.Util;

import org.example.common.FileType;

import java.io.File;

public class FileUtil {


    public static FileType check(String path) {
        File file = new File(path);
        boolean directory = file.isDirectory();
        return directory ? FileType.Directory : FileType.File;

    }

    public static boolean createFile(String File) {
        File file = new File("");


        return true;


    }

}
