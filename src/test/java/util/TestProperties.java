package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestProperties {
    public static String environnement;
    public static String browser;
    public static int browser_width;
    public static int browser_heigth;
    public static int implicit_wait;
    public static String sistemaUrl;
    public static String download_rep;
    public static String image_temp_rep;
    public static String baseURL;

    public static void loadProperties(String env) {
        final Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("target/test-classes/test_" + env + ".properties");
            prop.load(input);
            environnement = env;
            browser = prop.getProperty("browser");
            browser_width = Integer.parseInt(prop.getProperty("browser_width"));
            browser_heigth = Integer.parseInt(prop.getProperty("browser_heigth"));
            implicit_wait = Integer.parseInt(prop.getProperty("implicit_wait"));
            download_rep = prop.getProperty("download_rep");
            image_temp_rep = prop.getProperty("image_temp_rep");
            baseURL = prop.getProperty("baseURL");

        } catch (final IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * renvoie la valeur d'une propertie key du fichier test_environnement.properties
     * @param key extension du properties : test_extension.properties (correspond au propfile)
     * @return la valeur d'une propertie key du fichier test_environnement.properties
     */
    public static String get(String key)  {
        final Properties prop = new Properties();
        InputStream input = null;
        String value=null;
        try {
            input = new FileInputStream("target/test-classes/test_" + environnement + ".properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            prop.load(input);
            value = prop.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }
}