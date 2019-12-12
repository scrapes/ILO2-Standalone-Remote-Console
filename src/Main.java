/*
<applet code='ParamApplet' width='200' height='200'>
<param name='param' value='foo'>
</applet>
*/


import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Security;
import java.util.*;

import com.hp.ilo2.remcons.remcons;
import mjson.Json;
import sun.misc.BASE64Encoder;

import java.util.List;

/*


Set-Cookie: hp-iLO-Login=; Expires=Sun, 01 Jan 1990 12:00:00 GMT
Set-Cookie: hp-iLO-Session=00000005:::LMQJVGLGKQGMIAAEGQHZJUORCOBVQOUZIEXNVTUO; Path=/; Secure



var sessionkey="LMQJVGLGKQGMIAAEGQHZJUORCOBVQOUZIEXNVTUO";
var sessionindex="00000005";


*/

public class Main {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko";

    private static final String COOKIE_FILE = "data.cook";

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

    private static CookieManager cookieManager = new CookieManager();


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
        BASE64Encoder enc = new BASE64Encoder(); //Authenticate

        con.setRequestProperty("Cookie", "hp-iLO-Login=" + sessionIndex + ":" + enc.encode(username.getBytes()) + ":" + enc.encode(password.getBytes()) + ":" + sessionKey);


        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));

        //noinspection StatementWithEmptyBody
        while (in.readLine() != null) { } // discard
        in.close();

        List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
        PrintWriter writer = new PrintWriter(COOKIE_FILE, "UTF-8");
        for (HttpCookie cookie : cookies) {
            System.out.format("Session cookie: %s: %s\n", cookie.getDomain(), cookie);
            writer.println(cookie.toString().replace("\"", ""));
        }
        writer.close();

    }


    private static HashMap<String, String> hmap = new HashMap<>();

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
        if(supercookie != "") {
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
        SSLUtilities.trustAllHostnames();
        SSLUtilities.trustAllHttpsCertificates();
        CookieHandler.setDefault(cookieManager);
        try {
            String config = new String(Files.readAllBytes(Paths.get("config.json")));
            System.out.println("Config JSON:" + config);
            Json js = Json.read(config);
            username = js.at("Username").asString();
            password = js.at("Password").asString();
            setHostname(js.at("Hostname").asString());
        } catch (Exception e) {
            System.err.println("Error in parsing config file!");
            e.printStackTrace();
            return;
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

                if(!isValid(lastline)) {
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
            jf.setBounds(0, 0, 1070,880);
            jf.setVisible(true);
            c.add(rmc);
            rmc.init();
            rmc.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
