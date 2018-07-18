/*     */ package com.hp.ilo2.remcons;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Locale;
/*     */ import java.util.Properties;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class LocaleTranslator
/*     */ {
/*     */   Hashtable locales;
/*     */   Hashtable aliases;
/*     */   Hashtable selected;
/*     */   Hashtable reverse_alias;
/*  24 */   public boolean showgui = false;
/*  25 */   public boolean windows = true;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   String selected_name;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  74 */   String euro1 = " €\033[+4";
/*  75 */   String euro2 = " €\033[+e";
/*     */   
/*     */ 
/*  78 */   String belgian = "\001\021 \021\001 \027\032 \032\027 !8 \"3 #\033[+3 $] %\" &1 '4 (5 )- *} +? ,m -= .< /> 0) 1! 2@ 3# 4$ 5% 6^ 7& 8* 9( :. ;, <ð =/ >ñ ?M @\033[+2 AQ M: QA WZ ZW [\033[+[ \\\033[+ð ]\033[+] ^[  _+ `\033[+\\  aq m; qa wz zw {\033[+9 |\033[+1 }\033[+0 ~\033[+/  £| §6 ¨{  °_ ²` ³~ ´\033[+'  µ\\ À\033[+\\Q Á\033[+'Q Â[Q Ã\033[+/Q Ä{Q È\033[+\\E É\033[+'E Ê[E Ë{E Ì\033[+\\I Í\033[+'I Î[I Ï{I Ñ\033[+/N Ò\033[+\\O Ó\033[+'O Ô[O Õ\033[+/O Ö{O Ù\033[+\\U Ú\033[+'U Û[U Ü{U Ý\033[+'Y à\033[+\\q á\033[+'q â[q ã\033[+/q ä{q ç9 è\033[+\\e é\033[+'e ê[e ë{e ì\033[+\\i í\033[+'i î[i ï{i ñ\033[+/n ò\033[+\\o ó\033[+'o ô[o õ\033[+/o ö{o ù\033[+\\u ú\033[+'u û[u ü{u ý\033[+'y ÿ{y";
/*     */   
/*     */ 
/*  81 */   String british = "\"@ #\\ @\" \\ð |ñ ~| £# ¦\033[+` ¬~ Á\033[+A á\033[+a É\033[+E é\033[+e Í\033[+I í\033[+i Ó\033[+O ó\033[+o Ú\033[+U ú\033[+u";
/*     */   
/*     */ 
/*  84 */   String danish = "\"@ $\033[+4 &^ '\\ (* )( *| +- -/ /& :> ;< <ð =) >ñ ?_ @\033[+2 [\033[+8 \\\033[+ð ]\033[+9 ^}  _? `+  {\033[+7 |\033[+= }\033[+0 ~\033[+]  £\033[+3 ¤$ §~ ¨]  ´=  ½` À+A Á=A Â}A Ã\033[+]A Ä]A Å{ Æ: È+E É=E Ê}E Ë]E Ì+I Í=I Î}I Ï]I Ñ\033[+]N Ò+O Ó=O Ô}O Õ\033[+]O Ö]O Ø\" Ù+U Ú=U Û}U Ü]U Ý=Y à+a á=a â}a ã\033[+]a ä]a å[ æ; è+e é=e ê}e ë]e ì+i í=i î}i ï]i ñ\033[+]n ò+o ó=o ô}o õ\033[+]o ö]o ø' ù+u ú=u û}u ü]u ý=y ÿ]y";
/*     */   
/*     */ 
/*  87 */   String finnish = "\"@ $\033[+4 &^ '\\ (* )( *| +- -/ /& :> ;< <ð =) >ñ ?_ @\033[+2 [\033[+8 \\\033[+- ]\033[+9 ^}  _? `+  {\033[+7 |\033[+ð }\033[+0 ~\033[+]  £\033[+3 ¤$ §` ¨]  ´=  ½~ À+A Á=A Â}A Ã\033[+]A Ä]A Å{ È+E É=E Ê}E Ë]E Ì+I Í=I Î}I Ï]I Ñ\033[+]N Ò+O Ó=O Ô}O Õ\033[+]O Ö]O Ù+U Ú=U Û}U Ü]U Ý=Y à+a á=a â}a ã\033[+]a ä]a å[ è+e é=e ê}e ë]e ì+i í=i î}i ï]i ñ\033[+]n ò+o ó=o ô}o õ\033[+]o ö]o ù+u ú=u û}u ü]u ý=y ÿ]y";
/*     */   
/*     */ 
/*  90 */   String french = "\001\021 \021\001 \027\032 \032\027 !/ \"3 #\033[+3 $] %\" &1 '4 (5 )- *\\ ,m -6 .< /> 0) 1! 2@ 3# 4$ 5% 6^ 7& 8* 9( :. ;, <ð >ñ ?M @\033[+0 AQ M: QA WZ ZW [\033[+5 \\\033[+8 ]\033[+- ^\033[+9 _8 `\033[+7 aq m; qa wz zw {\033[+4 |\033[+6 }\033[+= ~\033[+2 £} ¤\033[+] §? ¨{  °_ ²` µ| Â[Q Ä{Q Ê[E Ë{E Î[I Ï{I Ô[O Ö{O Û[U Ü{U à0 â[q ä{q ç9 è7 é2 ê[e ë{e î[i ï{i ô[o ö{o ù' û[u ü{u ÿ{y";
/*     */   
/*     */ 
/*  93 */   String french_canadian = "\"@ #` '< /# <\\ >| ?^ @\033[+2 [\033[+[ \\\033[+` ]\033[+] ^[  `'  {\033[+' |~ }\033[+\\ ~\033[+; ¢\033[+4 £\033[+3 ¤\033[+5 ¦\033[+7 §\033[+o ¨}  «ð ¬\033[+6 ­\033[+. ¯\033[+, °\033[+ð ±\033[+1 ²\033[+8 ³\033[+9 ´\033[+/  µ\033[+m ¶\033[+p ¸]  »ñ ¼\033[+0 ½\033[+- ¾\033[+= À'A Á\033[+/A Â[A Ä}A Ç]C È'E É? Ê[E Ë}E Ì'I Í\033[+/I Î[I Ï}I Ò'O Ó\033[+/O Ô[O Ö}O Ù'U Ú\033[+/U Û[U Ü}U Ý\033[+/Y à'a á\033[+/a â[a ä}a ç]c è'e é\033[+/e ê[e ë}e ì'i í\033[+/i î[i ï}i ò'o ó\033[+/o ô[o ö}o ù'u ú\033[+/u û[u ü}u ý\033[+/y ÿ}y";
/*     */   
/*     */ 
/*  96 */   String german = "\031\032 \032\031 \"@ #\\ &^ '| (* )( *} +] -/ /& :> ;< <ð =) >ñ ?_ @\033[+q YZ ZY [\033[+8 \\\033[+- ]\033[+9 ^`  _? `+  yz zy {\033[+7 |\033[+ð }\033[+0 ~\033[+] §# °~ ²\033[+2 ³\033[+3 ´=  µ\033[+m À+A Á=A Â`A Ä\" È+E É=E Ê`E Ì+I Í=I Î`I Ò+O Ó=O Ô`O Ö: Ù+U Ú=U Û`U Ü{ Ý=Z ß- à+a á=a â`a ä' è+e é=e ê`e ì+i í=i î`i ò+o ó=o ô`o ö; ù+u ú=u û`u ü[ ý=z";
/*     */   
/*     */ 
/*  99 */   String italian = "\"@ #\033[+' &^ '- (* )( *} +] -/ /& :> ;< <ð =) >ñ ?_ @\033[+; [\033[+[ \\` ]\033[+] ^+ _? |~ £# §| °\" à' ç: è[ é{ ì= ò; ù\\";
/*     */   
/*     */ 
/* 102 */   String japanese = "\"@ &^ '& (* )( *\" +: :' =_ @[ [] \\ò ]\\ ^= _ó `{ {} ¥ô |õ }| ~+";
/*     */   
/*     */ 
/* 105 */   String latin_american = "\"@ &^ '- (* )( *} +] -/ /& :> ;< <ð =) >ñ ?_ @\033[+q [\" \\\033[+- ]| ^\033[+'  _? `\033[+\\  {' |` }\\ ~\033[+] ¡+ ¨{  ¬\033[+` °~ ´[  ¿= À\033[+\\A Á[A Â\033[+'A Ä{A È\033[+\\E É[E Ê\033[+'E Ë{E Ì\033[+\\I Í[I Î\033[+'I Ï{I Ñ: Ò\033[+\\O Ó[O Ô\033[+'O Ö{O Ù\033[+\\U Ú[U Û\033[+'U Ü{U Ý[Y à\033[+\\a á[a â\033[+'a ä{a è\033[+\\e é[e ê\033[+'e ë{e ì\033[+\\i í[i î\033[+'i ï{i ñ; ò\033[+\\o ó[o ô\033[+'o ö{o ù\033[+\\u ú[u û\033[+'u ü{u ý[y ÿ{y";
/*     */   
/*     */ 
/* 108 */   String norwegian = "\"@ $\033[+4 &^ '\\ (* )( *| +- -/ /& :> ;< <ð =) >ñ ?_ @\033[+2 [\033[+8 \\= ]\033[+9 ^}  _? `+  {\033[+7 |` }\033[+0 ~\033[+]  £\033[+3 ¤$ §~ ¨]  ´\033[+=  À+A Á\033[+=A Â}A Ã\033[+]A Ä]A Å{ Æ\" È+E É\033[+=E Ê}E Ë]E Ì+I Í\033[+=I Î}I Ï]I Ñ\033[+]N Ò+O Ó\033[+=O Ô}O Õ\033[+]O Ö]O Ø: Ù+U Ú\033[+=U Û}U Ü]U Ý\033[+=Y à+a á\033[+=a â}a ã\033[+]a ä]a å[ æ' è+e é\033[+=e ê}e ë]e ì+i í\033[+=i î}i ï]i ñ\033[+]n ò+o ó\033[+=o ô}o õ\033[+]o ö]o ø; ù+u ú\033[+=u û}u ü]u ý\033[+=y ÿ]y";
/*     */   
/*     */ 
/* 111 */   String portuguese = "\"@ &^ '- (* )( *{ +[ -/ /& :> ;< <ð =) >ñ ?_ @\033[+2 [\033[+8 \\` ]\033[+9 ^|  _? `}  {\033[+7 |~ }\033[+0 ~\\  £\033[+3 §\033[+4 ¨\033[+[  ª\" «= ´]  º' »+ À}A Á]A Â|A Ã\\A Ä\033[+[A Ç: È}E É]E Ê|E Ë\033[+[E Ì}I Í]I Î|I Ï\033[+[I Ñ\\N Ò}O Ó]O Ô|O Õ\\O Ö\033[+[O Ù}U Ú]U Û|U Ü\033[+[U Ý]Y à}a á]a â|a ã\\a ä\033[+[a ç; è}e é]e ê|e ë\033[+[e ì}i í]i î|i ï\033[+[i ñ\\n ò}o ó]o ô|o õ\\o ö\033[+[o ù}u ú]u û|u ü\033[+[u ý]y ÿ\033[+[y";
/*     */   
/*     */ 
/* 114 */   String spanish = "\"@ #\033[+3 &^ '- (* )( *} +] -/ /& :> ;< <ð =) >ñ ?_ @\033[+2 [\033[+[ \\\033[+` ]\033[+] ^{  _? `[  {\033[+' |\033[+1 }\033[+\\ ¡= ¨\"  ª~ ¬\033[+6 ´'  ·# º` ¿+ À[A Á'A Â{A Ä\"A Ç| È[E É'E Ê{E Ë\"E Ì[I Í'I Î{I Ï\"I Ñ: Ò[O Ó'O Ô{O Ö\"O Ù[U Ú'U Û{U Ü\"U Ý'Y à[a á'a â{a ä\"a ç\\ è[e é'e ê{e ë\"e ì[i í'i î{i ï\"i ñ; ò[o ó'o ô{o ö\"o ù[u ú'u û{u ü\"u ý'y ÿ\"y";
/*     */   
/*     */ 
/* 117 */   String swedish = "\"@ $\033[+4 &^ '\\ (* )( *| +- -/ /& :> ;< <ð =) >ñ ?_ @\033[+2 [\033[+8 \\\033[+- ]\033[+9 ^}  _? `+  {\033[+7 |\033[+ð }\033[+0 ~\033[+]  £\033[+3 ¤$ §` ¨]  ´=  ½~ À+A Á=A Â}A Ã\033[+]A Ä]A Å{ È+E É=E Ê}E Ë]E Ì+I Í=I Î}I Ï]I Ñ\033[+]N Ò+O Ó=O Ô}O Õ\033[+]O Ö]O Ù+U Ú=U Û}U Ü]U Ý=Y à+a á=a â}a ã\033[+]a ä]a å[ è+e é=e ê}e ë]e ì+i í=i î}i ï]i ñ\033[+]n ò+o ó=o ô}o õ\033[+]o ö]o ù+u ú=u û}u ü]u ý=y ÿ]y";
/*     */   
/*     */ 
/* 120 */   String swiss_french = "\031\032 \032\031 !} \"@ #\033[+3 $\\ &^ '- (* )( *# +! -/ /& :> ;< <ð =) >ñ ?_ @\033[+2 YZ ZY [\033[+[ \\\033[+ð ]\033[+] ^=  _? `+  yz zy {\033[+' |\033[+7 }\033[+\\ ~\033[+=  ¢\033[+8 £| ¦\033[+1 §` ¨]  ¬\033[+6 °~ ´\033[+-  À+A Á\033[+-A Â=A Ã\033[+=A Ä]A È+E É\033[+-E Ê=E Ë]E Ì+I Í\033[+-I Î=I Ï]I Ñ\033[+=N Ò+O Ó\033[+-O Ô=O Õ\033[+=O Ö]O Ù+U Ú\033[+-U Û=U Ü]U Ý\033[+-Z à+a á\033[+-a â=a ã\033[+=a ä]a ç$ è+e é\033[+-e ê=e ë]e ì+i í\033[+-i î=i ï]i ñ\033[+=n ò+o ó\033[+-o ô=o õ\033[+=o ö]o ù+u ú\033[+-u û=u ü]u ý\033[+-z ÿ]z";
/*     */   
/*     */ 
/* 123 */   String swiss_german = "\031\032 \032\031 !} \"@ #\033[+3 $\\ &^ '- (* )( *# +! -/ /& :> ;< <ð =) >ñ ?_ @\033[+2 YZ ZY [\033[+[ \\\033[+ð ]\033[+] ^=  _? `+  yz zy {\033[+' |\033[+7 }\033[+\\ ~\033[+=  ¢\033[+8 £| ¦\033[+1 §` ¨]  ¬\033[+6 °~ ´\033[+-  À+A Á\033[+-A Â=A Ã\033[+=A Ä]A È+E É\033[+-E Ê=E Ë]E Ì+I Í\033[+-I Î=I Ï]I Ñ\033[+=N Ò+O Ó\033[+-O Ô=O Õ\033[+=O Ö]O Ù+U Ú\033[+-U Û=U Ü]U Ý\033[+-Z à+a á\033[+-a â=a ã\033[+=a ä]a ç$ è+e é\033[+-e ê=e ë]e ì+i í\033[+-i î=i ï]i ñ\033[+=n ò+o ó\033[+-o ô=o õ\033[+=o ö]o ù+u ú\033[+-u û=u ü]u ý\033[+-z ÿ]z";
/*     */   
/*     */   String create_accents(String paramString1, String paramString2)
/*     */   {
/* 127 */     StringBuffer localStringBuffer = new StringBuffer(256);
/*     */     
/*     */ 
/*     */ 
/* 131 */     for (int i = 0; i < paramString1.length(); i++) {
/* 132 */       char c = paramString1.charAt(i);
/* 133 */       if (c == '*') {
/* 134 */         localStringBuffer.append(paramString2);
/*     */       } else {
/* 136 */         localStringBuffer.append(c);
/*     */       }
/*     */     }
/* 139 */     return localStringBuffer.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   void parse_locale_str(String paramString, Hashtable paramHashtable)
/*     */   {
/* 145 */     int j = 0;
/* 146 */     char c = '\000';
/* 147 */     Character localCharacter = null;
/* 148 */     StringBuffer localStringBuffer = new StringBuffer(16);
/*     */     
/* 150 */     for (int i = 0; i < paramString.length(); i++) {
/* 151 */       c = paramString.charAt(i);
/* 152 */       if ((j == 0) && (c != ' ')) {
/* 153 */         j++;
/* 154 */         localCharacter = new Character(c);
/*     */       }
/*     */       else {
/* 157 */         if ((j == 1) && (c != ' '))
/*     */         {
/* 159 */           if (c == ' ') c = ' ';
/* 160 */           localStringBuffer.append(c);
/*     */         }
/* 162 */         if ((j == 1) && (c == ' '))
/*     */         {
/* 164 */           paramHashtable.put(localCharacter, localStringBuffer.toString());
/* 165 */           j = 0;
/* 166 */           localStringBuffer = new StringBuffer(16);
/*     */         }
/*     */       }
/*     */     }
/* 170 */     paramHashtable.put(localCharacter, localStringBuffer.toString());
/*     */   }
/*     */   
/*     */ 
/*     */   void add_locale(String paramString1, String paramString2, String paramString3)
/*     */   {
/* 176 */     Hashtable localHashtable = new Hashtable();
/*     */     
/*     */ 
/* 179 */     parse_locale_str(paramString2, localHashtable);
/* 180 */     this.locales.put(paramString1, localHashtable);
/* 181 */     this.aliases.put(paramString3, paramString1);
/* 182 */     this.reverse_alias.put(paramString1, paramString3);
/*     */   }
/*     */   
/*     */   void add_iso_alias(String paramString1, String paramString2) {
/* 186 */     this.locales.put(paramString2, this.locales.get(paramString1));
/* 187 */     this.reverse_alias.put(paramString2, this.reverse_alias.get(paramString1));
/*     */   }
/*     */   
/*     */   void add_alias(String paramString1, String paramString2)
/*     */   {
/* 192 */     this.aliases.put(paramString2, paramString1);
/* 193 */     this.reverse_alias.put(paramString1, paramString2);
/*     */   }
/*     */   
/*     */   public LocaleTranslator()
/*     */   {
/* 198 */     this.locales = new Hashtable();
/* 199 */     this.aliases = new Hashtable();
/* 200 */     this.reverse_alias = new Hashtable();
/*     */     
/* 202 */     String str2 = null;
/*     */     
/*     */ 
/*     */ 
/* 206 */     this.locales.put("en_US", new Hashtable());
/* 207 */     add_alias("en_US", "English (United States)");
/*     */     
/* 209 */     add_locale("en_GB", this.british + this.euro1, "English (United Kingdom)");
/* 210 */     add_locale("fr_FR", this.french + this.euro2, "French");
/* 211 */     add_locale("it_IT", this.italian + this.euro2, "Italian");
/* 212 */     add_locale("de_DE", this.german + this.euro2, "German");
/* 213 */     add_locale("es_ES", this.spanish + this.euro2, "Spanish (Spain)");
/*     */     
/* 215 */     add_locale("ja_JP", this.japanese, "Japanese");
/*     */     
/* 217 */     add_locale("es_MX", this.latin_american + this.euro2, "Spanish (Latin America)");
/* 218 */     add_iso_alias("es_MX", "es_AR");
/* 219 */     add_iso_alias("es_MX", "es_BO");
/* 220 */     add_iso_alias("es_MX", "es_CL");
/* 221 */     add_iso_alias("es_MX", "es_CO");
/* 222 */     add_iso_alias("es_MX", "es_CR");
/* 223 */     add_iso_alias("es_MX", "es_DO");
/* 224 */     add_iso_alias("es_MX", "es_EC");
/* 225 */     add_iso_alias("es_MX", "es_GT");
/* 226 */     add_iso_alias("es_MX", "es_HN");
/* 227 */     add_iso_alias("es_MX", "es_NI");
/* 228 */     add_iso_alias("es_MX", "es_PA");
/* 229 */     add_iso_alias("es_MX", "es_PE");
/* 230 */     add_iso_alias("es_MX", "es_PR");
/* 231 */     add_iso_alias("es_MX", "es_PY");
/* 232 */     add_iso_alias("es_MX", "es_SV");
/* 233 */     add_iso_alias("es_MX", "es_UY");
/* 234 */     add_iso_alias("es_MX", "es_VE");
/*     */     
/*     */ 
/* 237 */     add_locale("fr_BE", this.belgian + this.euro2, "French Belgium");
/* 238 */     add_locale("fr_CA", this.french_canadian + this.euro2, "French Canadian");
/* 239 */     add_locale("da_DK", this.danish + this.euro2, "Danish");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 245 */     add_locale("no_NO", this.norwegian + this.euro2, "Norwegian");
/* 246 */     add_locale("pt_PT", this.portuguese + this.euro2, "Portugese");
/*     */     
/*     */ 
/*     */ 
/* 250 */     add_locale("sv_SE", this.swedish + this.euro2, "Swedish");
/* 251 */     add_locale("fi_FI", this.finnish + this.euro2, "Finnish");
/*     */     
/* 253 */     add_locale("fr_CH", this.swiss_french + this.euro2, "Swiss (French)");
/* 254 */     add_locale("de_CH", this.swiss_german + this.euro2, "Swiss (German)");
/*     */     
/*     */ 
/* 257 */     Enumeration localEnumeration = remcons.prop.propertyNames();
/* 258 */     Object localObject; while (localEnumeration.hasMoreElements()) {
/* 259 */       String str1 = (String)localEnumeration.nextElement();
/* 260 */       if (str1.equals("locale.override")) {
/* 261 */         str2 = remcons.prop.getProperty(str1);
/* 262 */         System.out.println("Locale override: " + str2);
/* 263 */       } else if (str1.startsWith("locale.windows")) {
/* 264 */         this.windows = Boolean.valueOf(remcons.prop.getProperty(str1)).booleanValue();
/* 265 */       } else if (str1.startsWith("locale.showgui")) {
/* 266 */         this.showgui = Boolean.valueOf(remcons.prop.getProperty(str1)).booleanValue();
/* 267 */       } else if (str1.startsWith("locale.")) {
/* 268 */         localObject = str1.substring(7);
/* 269 */         String str3 = remcons.prop.getProperty(str1);
/* 270 */         System.out.println("Adding user defined local for " + (String)localObject);
/* 271 */         add_locale((String)localObject, str3, (String)localObject + " (User Defined)");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 276 */     if (str2 != null) {
/* 277 */       System.out.println("Trying to select locale: " + str2);
/* 278 */       if (selectLocale(str2) != 0) {
/* 279 */         System.out.println("No keyboard definition for " + str2);
/*     */       }
/*     */     } else {
/* 282 */       localObject = Locale.getDefault();
/* 283 */       System.out.println("Trying to select locale: " + ((Locale)localObject).toString());
/* 284 */       if (selectLocale(((Locale)localObject).toString()) != 0) {
/* 285 */         System.out.println("No keyboard definition for '" + ((Locale)localObject).toString() + "'");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public int selectLocale(String paramString)
/*     */   {
/* 292 */     String str = (String)this.aliases.get(paramString);
/* 293 */     if (str != null) {
/* 294 */       paramString = str;
/*     */     }
/* 296 */     this.selected = ((Hashtable)this.locales.get(paramString));
/* 297 */     this.selected_name = ((String)this.reverse_alias.get(paramString));
/* 298 */     return this.selected != null ? 0 : -1;
/*     */   }
/*     */   
/*     */ 
/*     */   public String translate(char paramChar)
/*     */   {
/* 304 */     Character localCharacter = new Character(paramChar);
/* 305 */     String str = null;
/*     */     
/* 307 */     if (this.selected != null) {
/* 308 */       str = (String)this.selected.get(localCharacter);
/*     */     }
/*     */     
/*     */ 
/* 312 */     return str == null ? localCharacter.toString() : str;
/*     */   }
/*     */   
/*     */   public String[] getLocales()
/*     */   {
/* 317 */     int i = this.aliases.size();
/* 318 */     String[] arrayOfString = new String[i];
/*     */     
/* 320 */     Enumeration localEnumeration = this.aliases.keys();
/*     */     
/* 322 */     int j = 0;
/* 323 */     while (localEnumeration.hasMoreElements()) {
/* 324 */       arrayOfString[(j++)] = ((String)localEnumeration.nextElement());
/*     */     }
/*     */     
/* 327 */     for (j = 0; j < i - 1; j++) {
/* 328 */       for (int k = j + 1; k < i; k++) {
/* 329 */         if (arrayOfString[k].compareTo(arrayOfString[j]) < 0) {
/* 330 */           String str = arrayOfString[k];
/* 331 */           arrayOfString[k] = arrayOfString[j];
/* 332 */           arrayOfString[j] = str;
/*     */         }
/*     */       }
/*     */     }
/* 336 */     return arrayOfString;
/*     */   }
/*     */   
/*     */   public String getSelected()
/*     */   {
/* 341 */     return this.selected_name;
/*     */   }
/*     */ }


/* Location:              C:\Users\anton\Documents\ILO2\rc175p10.jar!\com\hp\ilo2\remcons\LocaleTranslator.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */