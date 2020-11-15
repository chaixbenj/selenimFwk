package element;

import driver.Driver;
import driver.Reporter;
import util.CsvUtil;
import util.DataSetUtil;
import util.JsonUtil;
import org.apache.commons.lang.StringUtils;
import java.util.HashMap;

public class Form {
    /**
     * read a json or csv file containing keys and values
     * for each keys "key" and "value", call the method prefixeMethod<Key>SuffixMethod(<value>) of the class pageObject
     * for example jdd.json = {"firstname" : "toto", "lastname" : "titi"}, prefixeMethod="assert", suffixeMethod="valueContains"
     * will call pageObject.assertFirstnameValueContains("toto")
     * and pageObject.assertLastnameValueContains("titi")
     * @param pageObject class containing elements and method of the page
     * @param prefixeMethod prefix method
     * @param suffixMethod suffix method
     * @param dataSet path to the json or csv
     * @param rowNum line to read if jdd is a csv
     */
    public static void runDataSet(Class pageObject, String prefixeMethod, String suffixMethod, String dataSet, int rowNum) {
        runDataSet(pageObject, prefixeMethod, suffixMethod, new DataSetUtil(dataSet, rowNum));
    }

    /**
     * read a DateSetUtil object containing keys and values
     * for each keys "key" and "value", call the method prefixeMethod<Key>SuffixMethod(<value>) of the class pageObject
     * for example jdd.json = {"firstname" : "toto", "lastname" : "titi"}, prefixeMethod="assert", suffixeMethod="valueContains"
     * will call pageObject.assertFirstnameValueContains("toto")
     * and pageObject.assertLastnameValueContains("titi")
     * @param pageObject class containing elements and method of the page
     * @param prefixeMethod prefix method
     * @param suffixMethod suffix method
     * @param dataSet object DataSetUtil that contains an HashMap with the Keys (element) and values
     */
    public static void runDataSet(Class pageObject, String prefixeMethod, String suffixMethod, DataSetUtil dataSet) {
        HashMap<String, String> jddHash = dataSet.getKeyAndValues();
        for (String key: jddHash.keySet()
        ) {
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
    }
}
