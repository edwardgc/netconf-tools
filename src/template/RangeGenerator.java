package template;

import util.FileUtils;

public class RangeGenerator {

    public static final int FIELDS_LENGTH = 9;

    public static final String prefix = "range-flow";
    public static final String TEMPLATE_FILE = Constants.WORK_DIR + prefix + "-template.xml";
    public static final String NAMES_FILE = Constants.WORK_DIR + prefix + "-names.csv";
    public static final String OUT_FILE = Constants.WORK_DIR + prefix + "-out.xml";

    public static final String DHCP_FILE = Constants.WORK_DIR + "dhcp.xml";
    public static final String PPPOE_FILE = Constants.WORK_DIR + "pppoe.xml";

    public static final String HEAD_FILE = Constants.WORK_DIR + "head.txt";
    public static final String TAIL_FILE = Constants.WORK_DIR + "tail.txt";

    private String template;
    private String dhcp;
    private String pppoe;

    public RangeGenerator()
    {
        try {
            template = FileUtils.readFile(TEMPLATE_FILE);
            dhcp = FileUtils.readFile(DHCP_FILE);
            pppoe = FileUtils.readFile(PPPOE_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class RangeConfig {
        public String templateName;
        public String vsiPrefix;
        public String vsiSurfix;
        public String lowLayer;
        public int vlan0;
        public int vlan1Start;
        public int vlan1End;
        public boolean dhcp;
        public boolean pppoe;
    }

    private RangeConfig parseRow(String row) {
        if(row.startsWith("#")) return null;
        String fields[] = row.split(",");
        if(fields.length != FIELDS_LENGTH) return null;
        RangeConfig config = new RangeConfig();
        config.templateName = fields[0];
        config.vsiPrefix = fields[1];
        config.vsiSurfix = fields[2];
        config.lowLayer = fields[3];
        config.vlan0 = Integer.parseInt(fields[4]);
        config.vlan1Start = Integer.parseInt(fields[5]);
        config.vlan1End = Integer.parseInt(fields[6]);
        config.dhcp = (fields[7].equalsIgnoreCase("Y"));
        config.pppoe = (fields[8].equalsIgnoreCase("Y"));
        return config;
    }


    public String genVsi(RangeConfig config) {
        String content = "";
        for(int i = config.vlan1Start ; i <= config.vlan1End; i++) {
            String out = template.replace("{templateName}", config.templateName);
            out = out.replace("{vsiPrefix}", config.vsiPrefix);
            out = out.replace("{vsiSurfix}", config.vsiSurfix);
            out = out.replace("{lowLayer}", config.lowLayer);
            out = out.replace("{vlan0}", "" + config.vlan0);
            out = out.replace("{vlan1}", "" + i);
            if(config.dhcp) {
                out = out.replace("{dhcp}", dhcp);
            } else {
                out = out.replace("{dhcp}", "");
            }
            if(config.pppoe) {
                out = out.replace("{pppoe}", pppoe);
            } else {
                out = out.replace("{pppoe}", "");
            }
            content += out;
        }
        return content;
    }

    public static void main(String args[]) throws Exception {
        String content = "";
        RangeGenerator gen = new RangeGenerator();
        content += FileUtils.readFile(HEAD_FILE);
        String tail = FileUtils.readFile(TAIL_FILE);

        String names = FileUtils.readFile(NAMES_FILE);

        String data[] = names.split("\n");
        for(String row : data) {
            RangeConfig config = gen.parseRow(row);
            if(config == null) {
                continue;
            }
            content += gen.genVsi(config);
        }
        content += tail;
        FileUtils.writeFile(OUT_FILE, content);
    }
}
