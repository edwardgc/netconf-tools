package util;

import java.io.File;
import java.io.FilenameFilter;

public class RegexFilenameFilter implements FilenameFilter{

    private String regex;

    RegexFilenameFilter(String regex) {
        this.regex = regex;
    }

    @Override
    public boolean accept(File dir, String name) {
        return name.matches(regex);
    }

}
