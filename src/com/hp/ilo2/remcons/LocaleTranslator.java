package com.hp.ilo2.remcons;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;


class LocaleTranslator {
    private Hashtable<String, Hashtable<Character, String>> locales;
    private Hashtable<String, String> aliases;
    private Hashtable<Character, String> selected;
    private Hashtable<String, String> reverse_alias;
    boolean showgui = false;
    boolean windows = true;

    String selected_name;

    private String euro1 = " €\033[+4";
    private String euro2 = " €\033[+e";


    private String belgian = "\001\021 \021\001 \027\032 \032\027 !8 \"3 #\033[+3 $] %\" &1 '4 (5 )- *} +? ,m -= .< /> 0) 1! 2@ 3# 4$ 5% 6^ 7& 8* 9( :. ;, <ð =/ >ñ ?M @\033[+2 AQ M: QA WZ ZW [\033[+[ \\\033[+ð ]\033[+] ^[  _+ `\033[+\\  aq m; qa wz zw {\033[+9 |\033[+1 }\033[+0 ~\033[+/  £| §6 ¨{  °_ ²` ³~ ´\033[+'  µ\\ À\033[+\\Q Á\033[+'Q Â[Q Ã\033[+/Q Ä{Q È\033[+\\E É\033[+'E Ê[E Ë{E Ì\033[+\\I Í\033[+'I Î[I Ï{I Ñ\033[+/N Ò\033[+\\O Ó\033[+'O Ô[O Õ\033[+/O Ö{O Ù\033[+\\U Ú\033[+'U Û[U Ü{U Ý\033[+'Y à\033[+\\q á\033[+'q â[q ã\033[+/q ä{q ç9 è\033[+\\e é\033[+'e ê[e ë{e ì\033[+\\i í\033[+'i î[i ï{i ñ\033[+/n ò\033[+\\o ó\033[+'o ô[o õ\033[+/o ö{o ù\033[+\\u ú\033[+'u û[u ü{u ý\033[+'y ÿ{y";


    private String british = "\"@ #\\ @\" \\ð |ñ ~| £# ¦\033[+` ¬~ Á\033[+A á\033[+a É\033[+E é\033[+e Í\033[+I í\033[+i Ó\033[+O ó\033[+o Ú\033[+U ú\033[+u";


    private String danish = "\"@ $\033[+4 &^ '\\ (* )( *| +- -/ /& :> ;< <ð =) >ñ ?_ @\033[+2 [\033[+8 \\\033[+ð ]\033[+9 ^}  _? `+  {\033[+7 |\033[+= }\033[+0 ~\033[+]  £\033[+3 ¤$ §~ ¨]  ´=  ½` À+A Á=A Â}A Ã\033[+]A Ä]A Å{ Æ: È+E É=E Ê}E Ë]E Ì+I Í=I Î}I Ï]I Ñ\033[+]N Ò+O Ó=O Ô}O Õ\033[+]O Ö]O Ø\" Ù+U Ú=U Û}U Ü]U Ý=Y à+a á=a â}a ã\033[+]a ä]a å[ æ; è+e é=e ê}e ë]e ì+i í=i î}i ï]i ñ\033[+]n ò+o ó=o ô}o õ\033[+]o ö]o ø' ù+u ú=u û}u ü]u ý=y ÿ]y";


    private String finnish = "\"@ $\033[+4 &^ '\\ (* )( *| +- -/ /& :> ;< <ð =) >ñ ?_ @\033[+2 [\033[+8 \\\033[+- ]\033[+9 ^}  _? `+  {\033[+7 |\033[+ð }\033[+0 ~\033[+]  £\033[+3 ¤$ §` ¨]  ´=  ½~ À+A Á=A Â}A Ã\033[+]A Ä]A Å{ È+E É=E Ê}E Ë]E Ì+I Í=I Î}I Ï]I Ñ\033[+]N Ò+O Ó=O Ô}O Õ\033[+]O Ö]O Ù+U Ú=U Û}U Ü]U Ý=Y à+a á=a â}a ã\033[+]a ä]a å[ è+e é=e ê}e ë]e ì+i í=i î}i ï]i ñ\033[+]n ò+o ó=o ô}o õ\033[+]o ö]o ù+u ú=u û}u ü]u ý=y ÿ]y";


    private String french = "\001\021 \021\001 \027\032 \032\027 !/ \"3 #\033[+3 $] %\" &1 '4 (5 )- *\\ ,m -6 .< /> 0) 1! 2@ 3# 4$ 5% 6^ 7& 8* 9( :. ;, <ð >ñ ?M @\033[+0 AQ M: QA WZ ZW [\033[+5 \\\033[+8 ]\033[+- ^\033[+9 _8 `\033[+7 aq m; qa wz zw {\033[+4 |\033[+6 }\033[+= ~\033[+2 £} ¤\033[+] §? ¨{  °_ ²` µ| Â[Q Ä{Q Ê[E Ë{E Î[I Ï{I Ô[O Ö{O Û[U Ü{U à0 â[q ä{q ç9 è7 é2 ê[e ë{e î[i ï{i ô[o ö{o ù' û[u ü{u ÿ{y";


    private String french_canadian = "\"@ #` '< /# <\\ >| ?^ @\033[+2 [\033[+[ \\\033[+` ]\033[+] ^[  `'  {\033[+' |~ }\033[+\\ ~\033[+; ¢\033[+4 £\033[+3 ¤\033[+5 ¦\033[+7 §\033[+o ¨}  «ð ¬\033[+6 ­\033[+. ¯\033[+, °\033[+ð ±\033[+1 ²\033[+8 ³\033[+9 ´\033[+/  µ\033[+m ¶\033[+p ¸]  »ñ ¼\033[+0 ½\033[+- ¾\033[+= À'A Á\033[+/A Â[A Ä}A Ç]C È'E É? Ê[E Ë}E Ì'I Í\033[+/I Î[I Ï}I Ò'O Ó\033[+/O Ô[O Ö}O Ù'U Ú\033[+/U Û[U Ü}U Ý\033[+/Y à'a á\033[+/a â[a ä}a ç]c è'e é\033[+/e ê[e ë}e ì'i í\033[+/i î[i ï}i ò'o ó\033[+/o ô[o ö}o ù'u ú\033[+/u û[u ü}u ý\033[+/y ÿ}y";


    private String german = "\031\032 \032\031 \"@ #\\ &^ '| (* )( *} +] -/ /& :> ;< <ð =) >ñ ?_ @\033[+q YZ ZY [\033[+8 \\\033[+- ]\033[+9 ^`  _? `+  yz zy {\033[+7 |\033[+ð }\033[+0 ~\033[+] §# °~ ²\033[+2 ³\033[+3 ´=  µ\033[+m À+A Á=A Â`A Ä\" È+E É=E Ê`E Ì+I Í=I Î`I Ò+O Ó=O Ô`O Ö: Ù+U Ú=U Û`U Ü{ Ý=Z ß- à+a á=a â`a ä' è+e é=e ê`e ì+i í=i î`i ò+o ó=o ô`o ö; ù+u ú=u û`u ü[ ý=z";


    private String italian = "\"@ #\033[+' &^ '- (* )( *} +] -/ /& :> ;< <ð =) >ñ ?_ @\033[+; [\033[+[ \\` ]\033[+] ^+ _? |~ £# §| °\" à' ç: è[ é{ ì= ò; ù\\";


    private String japanese = "\"@ &^ '& (* )( *\" +: :' =_ @[ [] \\ò ]\\ ^= _ó `{ {} ¥ô |õ }| ~+";


    private String latin_american = "\"@ &^ '- (* )( *} +] -/ /& :> ;< <ð =) >ñ ?_ @\033[+q [\" \\\033[+- ]| ^\033[+'  _? `\033[+\\  {' |` }\\ ~\033[+] ¡+ ¨{  ¬\033[+` °~ ´[  ¿= À\033[+\\A Á[A Â\033[+'A Ä{A È\033[+\\E É[E Ê\033[+'E Ë{E Ì\033[+\\I Í[I Î\033[+'I Ï{I Ñ: Ò\033[+\\O Ó[O Ô\033[+'O Ö{O Ù\033[+\\U Ú[U Û\033[+'U Ü{U Ý[Y à\033[+\\a á[a â\033[+'a ä{a è\033[+\\e é[e ê\033[+'e ë{e ì\033[+\\i í[i î\033[+'i ï{i ñ; ò\033[+\\o ó[o ô\033[+'o ö{o ù\033[+\\u ú[u û\033[+'u ü{u ý[y ÿ{y";


    private String norwegian = "\"@ $\033[+4 &^ '\\ (* )( *| +- -/ /& :> ;< <ð =) >ñ ?_ @\033[+2 [\033[+8 \\= ]\033[+9 ^}  _? `+  {\033[+7 |` }\033[+0 ~\033[+]  £\033[+3 ¤$ §~ ¨]  ´\033[+=  À+A Á\033[+=A Â}A Ã\033[+]A Ä]A Å{ Æ\" È+E É\033[+=E Ê}E Ë]E Ì+I Í\033[+=I Î}I Ï]I Ñ\033[+]N Ò+O Ó\033[+=O Ô}O Õ\033[+]O Ö]O Ø: Ù+U Ú\033[+=U Û}U Ü]U Ý\033[+=Y à+a á\033[+=a â}a ã\033[+]a ä]a å[ æ' è+e é\033[+=e ê}e ë]e ì+i í\033[+=i î}i ï]i ñ\033[+]n ò+o ó\033[+=o ô}o õ\033[+]o ö]o ø; ù+u ú\033[+=u û}u ü]u ý\033[+=y ÿ]y";


    private String portuguese = "\"@ &^ '- (* )( *{ +[ -/ /& :> ;< <ð =) >ñ ?_ @\033[+2 [\033[+8 \\` ]\033[+9 ^|  _? `}  {\033[+7 |~ }\033[+0 ~\\  £\033[+3 §\033[+4 ¨\033[+[  ª\" «= ´]  º' »+ À}A Á]A Â|A Ã\\A Ä\033[+[A Ç: È}E É]E Ê|E Ë\033[+[E Ì}I Í]I Î|I Ï\033[+[I Ñ\\N Ò}O Ó]O Ô|O Õ\\O Ö\033[+[O Ù}U Ú]U Û|U Ü\033[+[U Ý]Y à}a á]a â|a ã\\a ä\033[+[a ç; è}e é]e ê|e ë\033[+[e ì}i í]i î|i ï\033[+[i ñ\\n ò}o ó]o ô|o õ\\o ö\033[+[o ù}u ú]u û|u ü\033[+[u ý]y ÿ\033[+[y";


    private String spanish = "\"@ #\033[+3 &^ '- (* )( *} +] -/ /& :> ;< <ð =) >ñ ?_ @\033[+2 [\033[+[ \\\033[+` ]\033[+] ^{  _? `[  {\033[+' |\033[+1 }\033[+\\ ¡= ¨\"  ª~ ¬\033[+6 ´'  ·# º` ¿+ À[A Á'A Â{A Ä\"A Ç| È[E É'E Ê{E Ë\"E Ì[I Í'I Î{I Ï\"I Ñ: Ò[O Ó'O Ô{O Ö\"O Ù[U Ú'U Û{U Ü\"U Ý'Y à[a á'a â{a ä\"a ç\\ è[e é'e ê{e ë\"e ì[i í'i î{i ï\"i ñ; ò[o ó'o ô{o ö\"o ù[u ú'u û{u ü\"u ý'y ÿ\"y";


    private String swedish = "\"@ $\033[+4 &^ '\\ (* )( *| +- -/ /& :> ;< <ð =) >ñ ?_ @\033[+2 [\033[+8 \\\033[+- ]\033[+9 ^}  _? `+  {\033[+7 |\033[+ð }\033[+0 ~\033[+]  £\033[+3 ¤$ §` ¨]  ´=  ½~ À+A Á=A Â}A Ã\033[+]A Ä]A Å{ È+E É=E Ê}E Ë]E Ì+I Í=I Î}I Ï]I Ñ\033[+]N Ò+O Ó=O Ô}O Õ\033[+]O Ö]O Ù+U Ú=U Û}U Ü]U Ý=Y à+a á=a â}a ã\033[+]a ä]a å[ è+e é=e ê}e ë]e ì+i í=i î}i ï]i ñ\033[+]n ò+o ó=o ô}o õ\033[+]o ö]o ù+u ú=u û}u ü]u ý=y ÿ]y";


    private String swiss_french = "\031\032 \032\031 !} \"@ #\033[+3 $\\ &^ '- (* )( *# +! -/ /& :> ;< <ð =) >ñ ?_ @\033[+2 YZ ZY [\033[+[ \\\033[+ð ]\033[+] ^=  _? `+  yz zy {\033[+' |\033[+7 }\033[+\\ ~\033[+=  ¢\033[+8 £| ¦\033[+1 §` ¨]  ¬\033[+6 °~ ´\033[+-  À+A Á\033[+-A Â=A Ã\033[+=A Ä]A È+E É\033[+-E Ê=E Ë]E Ì+I Í\033[+-I Î=I Ï]I Ñ\033[+=N Ò+O Ó\033[+-O Ô=O Õ\033[+=O Ö]O Ù+U Ú\033[+-U Û=U Ü]U Ý\033[+-Z à+a á\033[+-a â=a ã\033[+=a ä]a ç$ è+e é\033[+-e ê=e ë]e ì+i í\033[+-i î=i ï]i ñ\033[+=n ò+o ó\033[+-o ô=o õ\033[+=o ö]o ù+u ú\033[+-u û=u ü]u ý\033[+-z ÿ]z";


    private String swiss_german = "\031\032 \032\031 !} \"@ #\033[+3 $\\ &^ '- (* )( *# +! -/ /& :> ;< <ð =) >ñ ?_ @\033[+2 YZ ZY [\033[+[ \\\033[+ð ]\033[+] ^=  _? `+  yz zy {\033[+' |\033[+7 }\033[+\\ ~\033[+=  ¢\033[+8 £| ¦\033[+1 §` ¨]  ¬\033[+6 °~ ´\033[+-  À+A Á\033[+-A Â=A Ã\033[+=A Ä]A È+E É\033[+-E Ê=E Ë]E Ì+I Í\033[+-I Î=I Ï]I Ñ\033[+=N Ò+O Ó\033[+-O Ô=O Õ\033[+=O Ö]O Ù+U Ú\033[+-U Û=U Ü]U Ý\033[+-Z à+a á\033[+-a â=a ã\033[+=a ä]a ç$ è+e é\033[+-e ê=e ë]e ì+i í\033[+-i î=i ï]i ñ\033[+=n ò+o ó\033[+-o ô=o õ\033[+=o ö]o ù+u ú\033[+-u û=u ü]u ý\033[+-z ÿ]z";

    String create_accents(String paramString1, String paramString2) {
        StringBuilder localStringBuilder = new StringBuilder(256);

        for (int i = 0; i < paramString1.length(); i++) {
            char c = paramString1.charAt(i);
            if (c == '*') {
                localStringBuilder.append(paramString2);
            } else {
                localStringBuilder.append(c);
            }
        }
        return localStringBuilder.toString();
    }


    void parse_locale_str(String paramString, Hashtable<Character, String> paramHashtable) {
        int j = 0;
        char c;
        Character localCharacter = null;
        StringBuffer localStringBuffer = new StringBuffer(16);

        for (int i = 0; i < paramString.length(); i++) {
            c = paramString.charAt(i);
            if ((j == 0) && (c != ' ')) {
                j++;
                localCharacter = c;
            }
            else {
                if ((j == 1) && (c != ' '))
                {
                    if (c == ' ') c = ' ';
                    localStringBuffer.append(c);
                }
                if ((j == 1) && (c == ' '))
                {
                    paramHashtable.put(localCharacter, localStringBuffer.toString());
                    j = 0;
                    localStringBuffer = new StringBuffer(16);
                }
            }
        }
        paramHashtable.put(localCharacter, localStringBuffer.toString());
    }


    private void add_locale(String paramString1, String paramString2, String paramString3) {
        Hashtable<Character, String> localHashtable = new Hashtable<>();


        parse_locale_str(paramString2, localHashtable);
        this.locales.put(paramString1, localHashtable);
        this.aliases.put(paramString3, paramString1);
        this.reverse_alias.put(paramString1, paramString3);
    }

    private void add_iso_alias(String paramString1, String paramString2) {
        this.locales.put(paramString2, this.locales.get(paramString1));
        this.reverse_alias.put(paramString2, this.reverse_alias.get(paramString1));
    }

    private void add_alias(String paramString1, String paramString2) {
        this.aliases.put(paramString2, paramString1);
        this.reverse_alias.put(paramString1, paramString2);
    }

    public LocaleTranslator() {
        this.locales = new Hashtable<>();
        this.aliases = new Hashtable<>();
        this.reverse_alias = new Hashtable<>();

        String str2 = null;

        this.locales.put("en_US", new Hashtable<Character, String>());
        add_alias("en_US", "English (United States)");

        add_locale("en_GB", this.british + this.euro1, "English (United Kingdom)");
        add_locale("fr_FR", this.french + this.euro2, "French");
        add_locale("it_IT", this.italian + this.euro2, "Italian");
        add_locale("de_DE", this.german + this.euro2, "German");
        add_locale("es_ES", this.spanish + this.euro2, "Spanish (Spain)");

        add_locale("ja_JP", this.japanese, "Japanese");

        add_locale("es_MX", this.latin_american + this.euro2, "Spanish (Latin America)");
        add_iso_alias("es_MX", "es_AR");
        add_iso_alias("es_MX", "es_BO");
        add_iso_alias("es_MX", "es_CL");
        add_iso_alias("es_MX", "es_CO");
        add_iso_alias("es_MX", "es_CR");
        add_iso_alias("es_MX", "es_DO");
        add_iso_alias("es_MX", "es_EC");
        add_iso_alias("es_MX", "es_GT");
        add_iso_alias("es_MX", "es_HN");
        add_iso_alias("es_MX", "es_NI");
        add_iso_alias("es_MX", "es_PA");
        add_iso_alias("es_MX", "es_PE");
        add_iso_alias("es_MX", "es_PR");
        add_iso_alias("es_MX", "es_PY");
        add_iso_alias("es_MX", "es_SV");
        add_iso_alias("es_MX", "es_UY");
        add_iso_alias("es_MX", "es_VE");

        add_locale("fr_BE", this.belgian + this.euro2, "French Belgium");
        add_locale("fr_CA", this.french_canadian + this.euro2, "French Canadian");
        add_locale("da_DK", this.danish + this.euro2, "Danish");

        add_locale("no_NO", this.norwegian + this.euro2, "Norwegian");
        add_locale("pt_PT", this.portuguese + this.euro2, "Portugese");

        add_locale("sv_SE", this.swedish + this.euro2, "Swedish");
        add_locale("fi_FI", this.finnish + this.euro2, "Finnish");

        add_locale("fr_CH", this.swiss_french + this.euro2, "Swiss (French)");
        add_locale("de_CH", this.swiss_german + this.euro2, "Swiss (German)");


        Enumeration propertyNames = remcons.prop.propertyNames();
        String localString;
        while (propertyNames.hasMoreElements()) {
            String currentPropName = (String)propertyNames.nextElement();
            if (currentPropName.equals("locale.override")) {
                str2 = remcons.prop.getProperty("locale.override");
                System.out.println("Locale override: " + str2);
            } else if (currentPropName.startsWith("locale.windows")) {
                this.windows = Boolean.valueOf(remcons.prop.getProperty(currentPropName));
            } else if (currentPropName.startsWith("locale.showgui")) {
                this.showgui = Boolean.valueOf(remcons.prop.getProperty(currentPropName));
            } else if (currentPropName.startsWith("locale.")) {
                localString = currentPropName.substring(7);
                String str3 = remcons.prop.getProperty(currentPropName);
                System.out.println("Adding user defined local for " + localString);
                add_locale(localString, str3, localString + " (User Defined)");
            }
        }

        if (str2 != null) {
            System.out.println("Trying to select locale: " + str2);
            if (selectLocale(str2) != 0) {
                System.out.println("No keyboard definition for " + str2);
            }
        } else {
            Locale locale = Locale.getDefault();
            System.out.println("Trying to select locale: " + locale.toString());
            if (selectLocale(locale.toString()) != 0) {
                System.out.println("No keyboard definition for '" + locale.toString() + "'");
            }
        }
    }

    public int selectLocale(String paramString) {
        String str = this.aliases.get(paramString);
        if (str != null) {
            paramString = str;
        }
        this.selected = this.locales.get(paramString);
        this.selected_name = this.reverse_alias.get(paramString);
        return this.selected != null ? 0 : -1;
    }

    public String translate(char aChar) {
        String str = null;

        if (this.selected != null) {
            str = this.selected.get(aChar);
        }

        return str == null ? Character.toString(aChar) : str;
    }

    public String[] getLocales() {
        int aliasCount = this.aliases.size();
        String[] aliasKeysArray = new String[aliasCount];

        Enumeration<String> aliasKeys = this.aliases.keys();

        int j = 0;
        while (aliasKeys.hasMoreElements()) {
            aliasKeysArray[(j++)] = aliasKeys.nextElement();
        }

        for (j = 0; j < aliasCount - 1; j++) {
            for (int k = j + 1; k < aliasCount; k++) {
                if (aliasKeysArray[k].compareTo(aliasKeysArray[j]) < 0) {
                    String tmp = aliasKeysArray[k];
                    aliasKeysArray[k] = aliasKeysArray[j];
                    aliasKeysArray[j] = tmp;
                }
            }
        }
        return aliasKeysArray;
    }

    public String getSelected() {
        return this.selected_name;
    }
}