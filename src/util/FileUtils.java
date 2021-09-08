package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

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

}
