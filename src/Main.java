/*
<applet code='ParamApplet' width='200' height='200'>
<param name='param' value='foo'>
</applet>
*/


import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    private static String _username = "";
    private static String _password = "";
    private static String Hostname = "";

    private static String Sessionkey = "";
    private static String Sessionindex = "";
    private static String supercookie = "";


    private static CookieManager cookieManager = new CookieManager();

    private static void Stage1() throws Exception
    {
        SSLUtilities.trustAllHostnames();
        SSLUtilities.trustAllHttpsCertificates();
        String url = "https://" + Hostname + "/login.htm";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        //con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Referer", "https://" + Hostname + "/login.htm");
        con.setRequestProperty("Host", Hostname);
        con.setRequestProperty("Accept-Language", "de-DE");
        con.setRequestProperty("Cookie", "hp-iLO-Login=");


        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String res = response.toString();
        Sessionkey = res.split("var sessionkey=\"")[1].split("\";")[0];
        Sessionindex = res.split("var sessionindex=\"")[1].split("\";")[0];
        System.out.println(Sessionkey);
        System.out.println(Sessionindex);
    }
    private static void Stage2() throws Exception {


        SSLUtilities.trustAllHostnames();
        SSLUtilities.trustAllHttpsCertificates();

        String url = "https://" + Hostname + "/index.htm";
        URL obj = new URL(url);

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Referer", "https://" + Hostname + "/login.htm");
        con.setRequestProperty("Host", Hostname);
        con.setRequestProperty("Accept-Language", "de-DE");
        //Cookie:
        con.setDoOutput(true);
        BASE64Encoder enc = new BASE64Encoder(); //Authenticate

        con.setRequestProperty("Cookie", "hp-iLO-Login=" + Sessionindex + ":" + enc.encode(_username.getBytes()) + ":" + enc.encode(_password.getBytes()) + ":" + Sessionkey);


        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
        PrintWriter writer = new PrintWriter(COOKIE_FILE, "UTF-8");
        for (HttpCookie cookie : cookies) {
            System.out.println(cookie.getDomain());
            System.out.println(cookie);
            writer.println(cookie.toString().replace("\"", ""));
        }
        writer.close();

    }

    private static HashMap<String, String> hmap = new HashMap<>();
    private static void Stage3() throws Exception
    {
        // https://" + Hostname + "/drc2fram.htm?restart=1
        SSLUtilities.trustAllHostnames();
        SSLUtilities.trustAllHttpsCertificates();
        String url = "https://" + Hostname + "/drc2fram.htm?restart=1";
        URL obj = new URL(url);

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        //con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Referer", "https://" + Hostname + "/login.htm");
        con.setRequestProperty("Host", Hostname);
        con.setRequestProperty("Accept-Language", "de-DE");
        if(supercookie != "")
        {
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

        System.out.println(hmap.get("CABBASE"));



    }

    public static boolean isValid(String cookie) throws Exception
    {
        CookieHandler.setDefault(cookieManager);
        String url = "https://" + Hostname + "/ie_index.htm";
        URL obj = new URL(url);

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        //con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Referer", "https://" + Hostname + "/login.htm");
        con.setRequestProperty("Host", Hostname);
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
    public static void main(String[] args){

        SSLUtilities.trustAllHostnames();
        SSLUtilities.trustAllHttpsCertificates();
        CookieHandler.setDefault(cookieManager);
        try {
            String config = new String(Files.readAllBytes(Paths.get("config.json")));
            System.out.println("Config JSON:" + config);
            Json js = Json.read(config);
            _username = js.at("Username").asString();
            _password = js.at("Password").asString();
            Hostname = js.at("Hostname").asString();
        }
        catch (Exception e){
            System.err.println("Error in parsing config file!");
            e.printStackTrace();
            return;
        }
        try
        {
            try (BufferedReader br = new BufferedReader(new FileReader("data.cook"))) {
                System.out.println("Found Datastore");
                String line;
                String lastline = "";
                while ((line = br.readLine()) != null) {
                    cookieManager.getCookieStore().add(new URI("https://" + Hostname + ""), new HttpCookie(line.split("=")[0], line.split("=")[1]));
                    lastline = line;
                }

                if(!isValid(lastline))
                {
                    System.out.println("Datastore not Valid, requesting Cookie");
                    Stage1();
                    Stage2();

                }
                else
                {
                    supercookie = lastline;
                }

            }
            catch(Exception e)
            {
                e.printStackTrace();
                System.out.println("Didnt found data Store, requesting Cookie");
                Stage1();
                Stage2();

            }
            Stage3();
            //hmap.put("IPADDR", Hostname);
            //hmap.put("DEBUG", "suckAdIck");

            remcons rmc = new remcons(hmap);
            rmc.SetHost(Hostname);

            JFrame jf = new JFrame ();
            Container c = jf.getContentPane ();
            jf.setBounds (0, 0, 1070,880);
            jf.setVisible (true);
            c.add (rmc);
            rmc.init();
            rmc.start();




        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
