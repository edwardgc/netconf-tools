package template;

import util.FileUtils;

public class StGenerator {

    public static final int FIELDS_LENGTH = 14;

    public static final String prefix = "st";
    public static final String VSI_FILE = Constants.WORK_DIR + prefix + "-vsi.xml";
    public static final String FDB_FILE = Constants.WORK_DIR + prefix + "-fdb.xml";
    public static final String FWD_FDB_FILE = Constants.WORK_DIR + prefix + "-fwd-fdb.xml";
    public static final String NAMES_FILE = Constants.WORK_DIR + prefix + "-names.csv";
    public static final String OUT_FILE = Constants.WORK_DIR + prefix + "-out.xml";

    public static final String DHCP_FILE = Constants.WORK_DIR + prefix + "-dhcp.xml";
    public static final String PPPOE_FILE = Constants.WORK_DIR + prefix + "-pppoe.xml";

    public static final String HEAD_FILE = Constants.WORK_DIR + "head.txt";
    public static final String TAIL_FILE = Constants.WORK_DIR + "tail.txt";

    private String vsi;
    private String dhcp;
    private String pppoe;
    private String fwdFdb;
    private String fdb;

    public StGenerator()
    {
        try {
            vsi = FileUtils.readFile(VSI_FILE);
            dhcp = FileUtils.readFile(DHCP_FILE);
            pppoe = FileUtils.readFile(PPPOE_FILE);
            fwdFdb = FileUtils.readFile(FWD_FDB_FILE);
            fdb = FileUtils.readFile(FDB_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class RangeConfig {
        public String userTemplateName;
        public String userVsiPrefix;
        public String lowLayer;
        public int userVlan0;
        public int userVlan1Start;
        public int userVlan1End;
        public boolean dhcp;
        public boolean pppoe;
        public String netTemplateName;
        public String netVsiPrefix;
        public int netVlan0;
        public int netVlan1Start;
        public int netVlan1End;
        public String fwdType;
    }

    private RangeConfig parseRow(String row) {
        if(row.startsWith("#")) return null;
        String fields[] = row.split(",");
        if(fields.length != FIELDS_LENGTH) {
            System.out.println("Fields number wrong: " + fields.length + " " + row);
            return null;
        }
        RangeConfig config = new RangeConfig();
        config.userTemplateName = fields[0];
        config.userVsiPrefix = fields[1];
        config.lowLayer = fields[2];
        config.userVlan0 = Integer.parseInt(fields[3]);
        config.userVlan1Start = Integer.parseInt(fields[4]);
        config.userVlan1End = Integer.parseInt(fields[5]);
        config.dhcp = (fields[6].equalsIgnoreCase("Y"));
        config.pppoe = (fields[7].equalsIgnoreCase("Y"));
        config.netTemplateName = fields[8];
        config.netVsiPrefix = fields[9];
        config.netVlan0 = Integer.parseInt(fields[10]);
        config.netVlan1Start = Integer.parseInt(fields[11]);
        config.netVlan1End = Integer.parseInt(fields[12]);
        config.fwdType = fields[13];
        return config;
    }


    public String genVsi(RangeConfig config) {
        String content = "";
        int netVlan1 = config.netVlan1Start;
        for(int i = config.userVlan1Start ; i <= config.userVlan1End; i++) {
            String out = vsi.replace("{userTemplateName}", config.userTemplateName);
            out = out.replace("{userVsiPrefix}", config.userVsiPrefix);
            out = out.replace("{lowLayer}", config.lowLayer);
            out = out.replace("{userVlan0}", "" + config.userVlan0);
            out = out.replace("{userVlan1}", "" + i);
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

            out = out.replace("{netTemplateName}", config.netTemplateName);
            out = out.replace("{netVsiPrefix}", config.netVsiPrefix);
            out = out.replace("{netVlan0}", "" + config.netVlan0);
            out = out.replace("{netVlan1}", "" + netVlan1++);

            if(config.fwdType.equalsIgnoreCase("rb")) {
                String fwdFdbOut = fwdFdb.replace("{lowLayer}", config.lowLayer);
                fwdFdbOut = fwdFdbOut.replace("{userVlan1}", "" + i);
                out = out.replace("{fwdFdb}", fwdFdbOut);
                String fdbOut = fdb.replace("{lowLayer}", config.lowLayer);
                fdbOut = fdbOut.replace("{userVlan1}", "" + i);
                out = out.replace("{fdb}", fdbOut);
            } else {
                out = out.replace("{fwdFdb}", "");
                out = out.replace("{fdb}", "");
            }

            content += out;
        }
        return content;
    }

    public static void main(String args[]) throws Exception {
        String content = "";
        StGenerator gen = new StGenerator();
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
