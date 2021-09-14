package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtils {

    public static String readFile(String src) throws Exception {
        File file = new File(src);
        FileReader fileReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fileReader);
        StringBuilder sb = new StringBuilder();
        String temp = "";
        while ((temp = br.readLine()) != null) {
            sb.append(temp + "\n");
        }
        br.close();
        return sb.toString();
    }

    public static void writeFile(String file, String content) throws Exception {
        FileOutputStream fos = new FileOutputStream(file, false);
        fos.write(content.getBytes());
        fos.close();
    }

    public static List<String> getFilesInDirByFilter(String dir, String Filter) {
        File parentDir = new File(dir);
        RegexFilenameFilter reg = new RegexFilenameFilter(Filter);
        String[] files = parentDir.list(reg);
        List<String> fileList = new ArrayList<>(files.length);
        Collections.addAll(fileList,  files);
        return fileList;
    }

}
