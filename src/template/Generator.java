package template;

import util.FileUtils;

public class Generator {

    public static final String prefix = "org";
//    public static final String prefix = "eth";
    public static final String TEMPLATE_FILE = Constants.WORK_DIR + prefix + "-template.xml";
    public static final String NAMES_FILE = Constants.WORK_DIR + prefix + "-names.csv";
    public static final String OUT_FILE = Constants.WORK_DIR + prefix + "-out.xml";

    public static final String HEAD_FILE = Constants.WORK_DIR + "head.txt";
    public static final String TAIL_FILE = Constants.WORK_DIR + "tail.txt";

    public String gen(String template, String vsiName, String lowlayer, String vlan, String netVsiName) {
        String out = template.replace("{1}", vsiName);
        out = out.replace("{2}", lowlayer);
        out = out.replace("{3}", vlan);
        out = out.replace("{4}", netVsiName);
        return out;
    }

    public static void main(String args[]) throws Exception {
        String content = "";
        Generator gen = new Generator();
        content += FileUtils.readFile(HEAD_FILE);
        String tail = FileUtils.readFile(TAIL_FILE);
        String template = FileUtils.readFile(TEMPLATE_FILE);
        String names = FileUtils.readFile(NAMES_FILE);

        String data[] = names.split("\n");
        for(String row : data) {
            String fields[] = row.split(",");
            if(fields.length <= 1) {
                continue;
            }
            String out = template;
            int i = 0;
            for(String field : fields) {
                i++;
                out = out.replace("{" + i + "}", field);
            }
            content = content + out;
        }
        content += tail;
        FileUtils.writeFile(OUT_FILE, content);
    }

}
