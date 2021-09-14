package performance;

import java.util.Date;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;

import util.FileUtils;

public class DeviationMain {

    private static String YANG_DIR = "yang";
    private static String FXS_DIR = "new_fxs";

    public static void main(String args[]) throws Exception {
        System.out.println(new Date());
        long startTime = System.currentTimeMillis();

        List<String> files = FileUtils.getFilesInDirByFilter(YANG_DIR, ".*yang");
        for(String file : files) {
            System.out.println(file);
        }
        String line = "ls -l";
        CommandLine cmdLine = CommandLine.parse(line);
        DefaultExecutor executor = new DefaultExecutor();
        ExecuteWatchdog watchdog = new ExecuteWatchdog(1);
        executor.setWatchdog(watchdog);
        int exitValue = executor.execute(cmdLine);

        System.out.println(new Date());
        long endTime = System.currentTimeMillis();
        System.out.print("exit: " + exitValue);
        System.out.println("running time: " + (endTime - startTime));
    }

}
