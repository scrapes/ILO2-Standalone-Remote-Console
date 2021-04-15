/*
<applet code='ParamApplet' width='200' height='200'>
<param name='param' value='foo'>
</applet>
*/


import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.security.Security;
import java.util.*;
import java.util.List;

import com.hp.ilo2.remcons.remcons;

/*


Set-Cookie: hp-iLO-Login=; Expires=Sun, 01 Jan 1990 12:00:00 GMT
Set-Cookie: hp-iLO-Session=00000005:::LMQJVGLGKQGMIAAEGQHZJUORCOBVQOUZIEXNVTUO; Path=/; Secure



var sessionkey="LMQJVGLGKQGMIAAEGQHZJUORCOBVQOUZIEXNVTUO";
var sessionindex="00000005";


*/

public class Main {
    private static final String USAGE_TEXT = "Usage: \n" +
            "- ILO2RemCon.jar <Hostname or IP> <Username> <Password>\n" +
            "- ILO2RemCon.jar -c <Path to config.properties>";

    private static final String DEFAULT_CONFIG_PATH = "config.properties";
    private static final String COOKIE_FILE = "data.cook";

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko";


    private static String username = "";
    private static String password = "";
    private static String hostname = "";

    public static void setHostname(String hostname) {
        Main.hostname = hostname;
        Main.loginURL = "https://" + hostname + "/login.htm";
    }

    private static String loginURL = "";

    private static String sessionKey = "";
    private static String sessionIndex = "";
    private static String supercookie = "";

    private final static CookieManager cookieManager = new CookieManager();


    private static void Stage1() throws Exception {
        SSLUtilities.trustAllHostnames();
        SSLUtilities.trustAllHttpsCertificates();
        System.setProperty("https.protocols", "TLSv1");
        System.setProperty("javax.net.debug", "all");
        Security.setProperty("jdk.tls.disabledAlgorithms", "");
        URL obj = new URL(loginURL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        //con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Referer", loginURL);
        con.setRequestProperty("Host", hostname);
        con.setRequestProperty("Accept-Language", "de-DE");
        con.setRequestProperty("Cookie", "hp-iLO-Login=");


        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String res = response.toString();
        sessionKey = res.split("var sessionkey=\"")[1].split("\";")[0];
        sessionIndex = res.split("var sessionindex=\"")[1].split("\";")[0];
        System.out.println("Session key: " + sessionKey);
        System.out.println("Session  ID: " + sessionIndex);
    }


    private static void Stage2() throws Exception {
        SSLUtilities.trustAllHostnames();
        SSLUtilities.trustAllHttpsCertificates();

        URL obj = new URL("https://" + hostname + "/index.htm");

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Referer", loginURL);
        con.setRequestProperty("Host", hostname);
        con.setRequestProperty("Accept-Language", "de-DE");
        //Cookie:
        con.setDoOutput(true);

        Base64.Encoder enc2 = Base64.getMimeEncoder(); //Authenticate
        String cookieVal = String.format("hp-iLO-Login=%s:%s:%s:%s",
                sessionIndex,
                enc2.encodeToString(username.getBytes()),
                enc2.encodeToString(password.getBytes()),
                sessionKey
        );
        con.setRequestProperty("Cookie", cookieVal);


        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));

        //noinspection StatementWithEmptyBody
        while (in.readLine() != null) {
        } // discard
        in.close();

        List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
        PrintWriter writer = new PrintWriter(COOKIE_FILE, "UTF-8");
        for (HttpCookie cookie : cookies) {
            System.out.format("Session cookie: %s: %s\n", cookie.getDomain(), cookie);
            writer.println(cookie.toString().replace("\"", ""));
        }
        writer.close();

    }


    private final static HashMap<String, String> hmap = new HashMap<>();

    private static void Stage3() throws Exception {
        // https://" + hostname + "/drc2fram.htm?restart=1
        SSLUtilities.trustAllHostnames();
        SSLUtilities.trustAllHttpsCertificates();
        String url = "https://" + hostname + "/drc2fram.htm?restart=1";
        URL obj = new URL(url);

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        //con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Referer", loginURL);
        con.setRequestProperty("Host", hostname);
        con.setRequestProperty("Accept-Language", "de-DE");
        if (!supercookie.equals("")) {
            con.setRequestProperty("Cookie", supercookie);
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String res = response.toString();

        hmap.put("INFO0", res.split("info0=\"")[1].split("\";")[0]);
        hmap.put("INFO1", res.split("info1=\"")[1].split("\";")[0]);
        hmap.put("INFO3", res.split("info3=\"")[1].split("\";")[0]);
        hmap.put("INFO6", res.split("info6=\"")[1].split("\";")[0]);
        hmap.put("INFO7", res.split("info7=")[1].split(";")[0]);
        hmap.put("INFO8", res.split("info8=\"")[1].split("\";")[0]);

        hmap.put("INFOA", res.split("infoa=\"")[1].split("\";")[0]);
        hmap.put("INFOB", res.split("infob=\"")[1].split("\";")[0]);
        hmap.put("INFOC", res.split("infoc=\"")[1].split("\";")[0]);
        hmap.put("INFOD", res.split("infod=\"")[1].split("\";")[0]);

        hmap.put("INFOM", res.split("infom=")[1].split(";")[0]);
        hmap.put("INFOMM", res.split("infomm=")[1].split(";")[0]);

        hmap.put("INFON", res.split("infon=")[1].split(";")[0]);
        hmap.put("INFOO", res.split("infoo=\"")[1].split("\";")[0]);

        hmap.put("CABBASE", res.split("<PARAM NAME=CABBASE VALUE=")[1].split(">\"")[0]);

        System.out.println("CABBASE = " + hmap.get("CABBASE"));
    }


    public static boolean isValid(String cookie) throws Exception {
        CookieHandler.setDefault(cookieManager);
        String url = "https://" + hostname + "/ie_index.htm";
        URL obj = new URL(url);

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        //con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Referer", loginURL);
        con.setRequestProperty("Host", hostname);
        con.setRequestProperty("Accept-Language", "de-DE");
        con.setRequestProperty("Cookie", cookie);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String res = response.toString();

        return !(res.contains("Login Delay") || res.contains("Integrated Lights-Out 2 Login"));
    }


    public static void main(String[] args) {
        Optional<String> configPath = Optional.empty();

        switch (args.length) {
            case 0:
                // <no args>
                // try the default config location
                configPath = Optional.of(DEFAULT_CONFIG_PATH);
                break;
            case 2:
                // -c <path>
                if (args[0].equals("-c")) {
                    configPath = Optional.of(args[1]);
                } else {
                    System.out.println(USAGE_TEXT);
                }
                break;
            case 3:
                // <Hostname or IP> <Username> <Password>
                setHostname(args[0]);
                username = args[1];
                password = args[2];
                break;
            default:
                System.out.println(USAGE_TEXT);
                return;
        }

        SSLUtilities.trustAllHostnames();
        SSLUtilities.trustAllHttpsCertificates();
        CookieHandler.setDefault(cookieManager);

        if (configPath.isPresent()) {
            try (FileInputStream fis = new FileInputStream(configPath.get())) {
                Properties p = new Properties();
                p.load(fis);

                setHostname(p.getProperty("hostname"));
                username = p.getProperty("username");
                password = p.getProperty("password");
            } catch (Exception e) {
                System.err.println("Error in reading/parsing config file!");
                e.printStackTrace();
                return;
            }
        }

        try {
            try (BufferedReader br = new BufferedReader(new FileReader("data.cook"))) {
                System.out.println("Found datastore");
                String line;
                String lastline = "";
                while ((line = br.readLine()) != null) {
                    cookieManager.getCookieStore().add(new URI("https://" + hostname), new HttpCookie(line.split("=")[0], line.split("=")[1]));
                    lastline = line;
                }

                if (!isValid(lastline)) {
                    System.out.println("Datastore not valid, requesting Cookie");
                    Stage1();
                    Stage2();
                } else {
                    supercookie = lastline;
                }
            } catch (FileNotFoundException e) {
                System.out.println("Couldn't find datastore, requesting Cookie");
                Stage1();
                Stage2();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Stage3();
            //hmap.put("IPADDR", hostname);
            //hmap.put("DEBUG", "suckAdIck");

            remcons rmc = new remcons(hmap);
            rmc.SetHost(hostname);

            JFrame jf = new JFrame();
            Container c = jf.getContentPane();
            jf.setBounds(0, 0, 1070, 880);
            jf.setVisible(true);
            jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            c.add(rmc);
            rmc.init();
            rmc.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
