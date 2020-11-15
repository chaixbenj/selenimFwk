package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Properties;

public class Data {
    private static HashMap<String,String> savedData = new HashMap<String, String>();

    /**
     * save a value under a name
     * @param name
     * @param value
     */
    public static void save(String name, String value) {
        // on charge dans 2 tableaux de string et dans un fichier properties au cas où il y ait un redémarrage de webdriver
        if (name!=null && !name.equals("")) {
            name = name.replace(" ", "_");
            if (savedData.containsKey(name)) {
                savedData.replace(name, value);
            } else {
                savedData.put(name, value);
            }
        }
    }

    /**
     * get a value by a name
     * @param name
     * @return
     */
    public static String get(String name) {
        String data=null;
        if (name!=null) {
            String nameNoBlank = name.replace(" ", "_");
            data = TestProperties.get(nameNoBlank);
            if (data == null && savedData.containsKey(nameNoBlank)) {
                    data = String.valueOf(savedData.get(nameNoBlank));
            }
        }
        if (data==null) data = name;
        if (data!=null) {
            if (data.contains("{")) {
                for (String subdata:data.split("\\s*\\{\\s*")
                ) {
                    if (subdata.indexOf("}")>0) {
                        String var = subdata.split("\\s*\\}\\s*")[0];
                        String varVar = get(var);
                        if (!varVar.equals(var)) data = data.replace("{" + var + "}", varVar);
                    }
                }
            }
            if (data.startsWith("sysdate;")) {
                try {
                    LocalDateTime sysdate = LocalDateTime.now();
                    sysdate = sysdate.plusDays(Integer.parseInt((data.split(";")[1]).trim()));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(data.split(";")[2]);
                    data = sysdate.format(formatter);
                } catch (Exception e) {
                    // on fait rien
                }
            }
        }
        return data;
    }


    /**
     * if arg is a file path return its content, else return Data.get(content)
     * @param content
     * @return
     */
    public static String getContentIfIsFile(String content) {
        String contentcontent;
        try
        {
            String fichier;
            if (new File(content).exists()) {
                fichier = content;
            } else {
                if (new File("target/test-classes/DataSet/" + content).exists()) {
                    fichier = "target/test-classes/DataSet/" + content;
                } else {
                    fichier = "target/test-classes/DataSet/fr/" + content;
                }
            }
            contentcontent = new String ( Files.readAllBytes( Paths.get(fichier) ) );
        }
        catch (Exception e)
        {
            contentcontent = Data.get(content);
        }
        return contentcontent;
    }
}
