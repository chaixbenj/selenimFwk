package element;

import driver.Driver;
import driver.Reporter;
import util.CsvUtil;
import util.DataSetUtil;
import util.JsonUtil;
import org.apache.commons.lang.StringUtils;
import java.util.HashMap;

public class Formulaire {
    /**
     * Réalise des setValue en masse à partir d'un json de jeu de données positionné dans jdd/langue. Pour chacune des clés "key", invoque la méthode setKey(keyValue) de la classe "pageObject"
     * @param pageObject : class de la page à laquelle s'applique le jdd
     * @param dataFile : fichier json jeu de donnée (par exemple "declaration/jdd1.json" positionné dans jdd/fr)
     */
    public static void set(Class pageObject, String dataFile) {
        runDataSet(pageObject, "set", "", dataFile, 1, null, null);
    }

    public static void set(Class pageObject, String dataFile, int rowNum) {
        runDataSet(pageObject, "set", "", dataFile, rowNum, null, null);
    }

    public static void set(Class pageObject, String dataFile, int rowNum, String champStart, String champStop) {
        runDataSet(pageObject, "set", "", dataFile, rowNum, champStart, champStop);
    }

    /**
     * Réalise des assert en masse à partir d'un json de jeu de données positionné dans jdd/langue. Pour chacune des clés "key", invoque la méthode assertKeyValue(value) de la classe "pageObject"
     * @param pageObject : class de la page à laquelle s'applique le jdd
     * @param dataFile : fichier json jeu de donnée (par exemple "declaration/jdd1.json" positionné dans jdd/fr)
     */
    public static void assertValue(Class pageObject, String dataFile) {
        runDataSet(pageObject, "assert", "value", dataFile,1, null, null);
    }

    public static void assertValue(Class pageObject, String dataFile, int rowNum) {
        runDataSet(pageObject, "assert", "value", dataFile,rowNum, null, null);
    }

    /**
     * Réalise des action en masse à partir d'un json de jeu de données positionné dans jdd/langue. Pour chacune des clés "key", invoque la méthode prefixMethodKeySuffixMethod(value) de la classe "pageObject". Si la clé="valider", invoque la méthode value.
     * par exemple si le jdd.json contient {"nom" : "toto", "prenom" : "titi", "valider" : "enregistrer"}, que prefixeMethod="assert" et suffixeMethod="valueContains", on exécutera pageObject.assertNomValueContains("toto") puis pageObject.assertPrenomValueContains("titi") puis pageObject.enregistrer()
     * @param prefixeMethod prefix de la méthode
     * @param suffixMethod prefix de la méthode
     * @param pageObject : class de la page à laquelle s'applique le jdd
     * @param jdd : fichier json jeu de donnée (par exemple "declaration/jdd1.json" positionné dans jdd/fr)
     */
    public static void runDataSet(Class pageObject, String prefixeMethod, String suffixMethod, String jdd, int rowNum, String champStart, String champStop) {
        runDataSet(pageObject, prefixeMethod, suffixMethod, new DataSetUtil(jdd, rowNum), champStart, champStop);
    }
    /**
     * Réalise des action en masse à partir d'un json de jeu de données positionné dans jdd/langue. Pour chacune des clés "key", invoque la méthode prefixMethodKeySuffixMethod(value) de la classe "pageObject". Si la clé="valider", invoque la méthode value.
     * par exemple si le jdd.json contient {"nom" : "toto", "prenom" : "titi", "valider" : "enregistrer"}, que prefixeMethod="assert" et suffixeMethod="valueContains", on exécutera pageObject.assertNomValueContains("toto") puis pageObject.assertPrenomValueContains("titi") puis pageObject.enregistrer()
     * @param prefixeMethod prefix de la méthode
     * @param suffixMethod prefix de la méthode
     * @param pageObject : class de la page à laquelle s'applique le jdd
     * @param dataSet : hash jeu de donnée
     */
    public static void runDataSet(Class pageObject, String prefixeMethod, String suffixMethod, DataSetUtil dataSet, String champStart, String champStop) {
        HashMap<String, String> jddHash = dataSet.getKeyAndValues();
        boolean valoriseChamp = false;
        if (champStart==null || champStart.equals("")) valoriseChamp = true;
        for (String key: jddHash.keySet()
        ) {
            if (champStart!=null && !champStart.equals("") && key.equals(champStart)) valoriseChamp = true;
            if (valoriseChamp) {
                String value = jddHash.get(key);
                if (!value.equals("N/A")) {
                    String method = prefixeMethod + StringUtils.capitalize(key) + StringUtils.capitalize(suffixMethod);
                    try {
                        if (value.equals("true") || value.equals("false")) {
                            pageObject.getMethod(method, boolean.class).invoke(null, Boolean.valueOf(value));
                        } else {
                            pageObject.getMethod(method, String.class).invoke(null, value);
                        }
                    } catch (Exception e) {
                        try {
                            pageObject.getMethod(method).invoke(null);
                        } catch (Exception e2) {
                            try {
                                if (prefixeMethod.equals("set")) {
                                    pageObject.getMethod("click" + StringUtils.capitalize(key)).invoke(null);
                                } else {
                                    System.out.println("INFO no such method >>> " + pageObject.getName() + "." + method);
                                }
                            } catch (Exception e3) {
                                Driver.getReport().log("error", pageObject.getName() + "." + method,null,null,null, "FAIL to call with \"" + value + "\"");
                            }
                        }
                    }
                }
            }
            if (champStop!=null && !champStop.equals("") && key.equals(champStop)) valoriseChamp = false;
        }
    }
}
