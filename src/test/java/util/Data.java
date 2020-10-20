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
     * sauvegarde une donnée sous le nom name
     * @param name nom de sauvegarde de la donnée
     * @param value valeur à sauvegarder
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
     * si name est une propertie de test_env.properties renvoie la valeur de la properties, sinon renvoie la valeur de la donnée sauvegardée sous le nom name s'il y a une sauvegarde sous ce nom, sinon renvoie name
     * si name est de la forme bla{blo}bli{blu}, ça renvoi Data.get(bla{blo}bli{blu}) si ça existe sinon bla+Data.get(blo) + bli + Data.get(blu)
     * si name est de la forme "sysdate;+/-N;format", ça renvoi la date du jour + ou - "N" jour(s) au format "format"
     * @param name nom de sauvegarde de la donnée
     * @return si name est une propertie de test_env.properties renvoie la valeur de la properties, sinon renvoie la valeur de la donnée sauvegardée sous le nom name s'il y a une sauvegarde sous ce nom, sinon renvoie name
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
     * renvoie la valeur d'un contenu : contenu d'un fichier si contenu est un fichier dans target/test-classes/DataSet/langue, sinon Data.get(contenu)
     * @param content nom de sauvegarde de la donnée
     * @return la valeur d'un contenu : contenu d'un fichier si contenu est un fichier dans target/test-classes/DataSet/langue, sinon Data.get(contenu)
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

    /**
     * renvoie la base64 d'un contenu : contenu d'un fichier si contenu est un fichier dans target/test-classes/DataSet/langue, sinon Data.get(contenu)
     * @param content nom de sauvegarde de la donnée
     * @return la valeur d'un contenu : contenu d'un fichier si contenu est un fichier dans target/test-classes/DataSet/langue, sinon Data.get(contenu)
     */
    public static String getBase64IsFile(String content) {
        String base64;
        try
        {
            String fichier;
            if (new File(content).exists()) {
                fichier = content;
                base64 = new String(Base64.getEncoder().encode(Files.readAllBytes(Paths.get(fichier))));
            } else {
                if (new File("target/test-classes/DataSet/" + content).exists()) {
                    fichier = "target/test-classes/DataSet/" + content;
                    base64 = new String(Base64.getEncoder().encode(Files.readAllBytes(Paths.get(fichier))));
                } else {
                    fichier = "target/test-classes/DataSet/fr/" + content;
                    base64 = new String(Base64.getEncoder().encode(Files.readAllBytes(Paths.get(fichier))));
                }
            }
        }
        catch (Exception e)
        {
            base64 = new String(Base64.getEncoder().encode(content.getBytes()));
        }
        return base64;
    }
}
