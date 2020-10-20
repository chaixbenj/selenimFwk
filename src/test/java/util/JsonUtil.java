package util;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONObject;
import org.apache.wink.json4j.OrderedJSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonUtil {
    private static String myStrJson;
    private static LinkedHashMap<String, String> keyAndValues;

    /**
     * renvoi une hasmap avec clé et valeur du json (fichier ou contenu)
     * @param strJson json file or content
     * @return valeur de la clé
     */
    public static LinkedHashMap<String, String> jsonFileToHash(String strJson) {
        try {
            strJson = Data.getContentIfIsFile(strJson);
            if (keyAndValues == null) {
                keyAndValues = new LinkedHashMap<String, String>();
            } else {
                keyAndValues.clear();
            }
            try {
                OrderedJSONObject json = new OrderedJSONObject(strJson);
                loadJson(json, "", null, 0);
            } catch (Exception e) {
                try {
                    JSONArray jsonarray = new JSONArray(strJson);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        OrderedJSONObject json = new OrderedJSONObject(jsonarray.getString(i));
                        loadJson(json, "", null, i);
                    }
                } catch (Exception e2) {
                    //e2.printStackTrace();
                    keyAndValues.put("not a json", "not a json");
                }
            }
            return keyAndValues;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * récupére un objet json dans un tableau de json en fonction de son id
     * @param strJson tableau de json
     * @param id de l'objet recherché
     * @return json de l'objet id en string
     */
    public static String getJsonObjectWithIdFromJsonArray(String strJson, String id) {
        try {
            JSONArray jsonarray = new JSONArray(strJson);
            JSONObject jsonObject = null;
            JSONObject jsonObjectFound = null;
            for (int i = 0; i < jsonarray.length(); i++) {
                jsonObject = jsonarray.getJSONObject(i);
                if (jsonObject.get("id").toString().equals(id)) {
                    jsonObjectFound = jsonObject;
                    break;
                }
            }
            if (jsonObjectFound == null) {
                return "null";
            } else {
                return jsonObjectFound.toString();
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * récupére le nieme objet json dans un tableau de json
     * @param strJson tableau de json
     * @param pos de l'objet recherché
     * @return json de l'objet id en string
     */
    public static String getJsonObjectFromJsonArray(String strJson, int pos) {
        try {
            JSONArray jsonarray = new JSONArray(strJson);
            return jsonarray.getString(pos-1);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * récupére la valeur de clé dans un json
     * @param strJson json
     * @param key clé recherché
     * @return valeur de la clé
     */
    public static String getJsonKeyValue(String strJson, String key) {
        try {
            strJson = Data.getContentIfIsFile(strJson);
            if (keyAndValues == null) {
                keyAndValues = new LinkedHashMap<String, String>();
            } else {
                keyAndValues.clear();
            }
            try {
                OrderedJSONObject json = new OrderedJSONObject(strJson);
                loadJson(json, "", null, 0);
            } catch (Exception e) {
                try {
                    JSONArray jsonarray = new JSONArray(strJson);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        OrderedJSONObject json = new OrderedJSONObject(jsonarray.getString(i));
                        loadJson(json, "", null, i);
                    }
                } catch (Exception e2) {
                    keyAndValues.put("not a json", "not a json");
                }
            }
            String keyValue;
            if (keyAndValues.containsKey(key)) {
                keyValue = keyAndValues.get(key);
            } else {
                JSONObject json;
                try {
                    json = new JSONObject(strJson);
                } catch (Exception e) {
                    JSONArray jsonarray = new JSONArray(strJson);
                    json = jsonarray.getJSONObject(0);
                }
                keyValue = json.get(key).toString();
            }
            return keyValue;
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * vérifie l'égalité de 2 json
     * Le résultat est tracé dans le rapport.
     * @param expectedReponse json attendu
     * @param actualReponse json actual
     */
    public static void assertJsonEquals(String expectedReponse, String actualReponse) {
        assertJsonEquals(expectedReponse, actualReponse, "", "");
    }

    /**
     * vérifie l'égalité de 2 json en ignorant des clés et en remplaçant certaines valeur
     * Le résultat est tracé dans le rapport.
     * @param expectedReponse json attendu
     * @param actualReponse json actual
     * @param jsonKeyToIgnore clés dont on ignore les différences de valeurs (String séparé par des ;)
     * @param replacementValuesString clés dont on rempalce les valeurs (String du type : key1:=valeur1|key2:=valeur2|key3:=valeur3...)
     */
    public static void assertJsonEquals(String expectedReponse, String actualReponse, String jsonKeyToIgnore, String replacementValuesString) {
        if (expectedReponse.equals("") && !actualReponse.equals("")) {
            //Reporter.log("fail", "assertJsonEquals ", "", expectedReponse, actualReponse, null);
        } else {
            if (expectedReponse.equals(actualReponse)) {
                //Reporter.log("pass", "assertJsonEquals ", "", actualReponse, null, null);
            } else {
                String jsonActual;
                HashMap<String, String> keysAndValuesActual = new HashMap<String, String>();
                String jsonExpected;
                HashMap<String, String> keysAndValuesExpected = new HashMap<String, String>();
                expectedReponse = Data.getContentIfIsFile(expectedReponse);
                if (replacementValuesString != null && !replacementValuesString.equals("")) {
                    String[] replacementValues = replacementValuesString.split("\\s*\\|\\s*");
                    for (String replacementValue : replacementValues) {
                        try {
                            String[] repValue = replacementValue.split(":=");
                            String toBeReplaced = repValue[0];
                            String byString = repValue[1];
                            expectedReponse = expectedReponse.replaceAll(toBeReplaced, byString);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (keyAndValues==null) {
                    keyAndValues = new LinkedHashMap<String, String>();
                } else {
                    keyAndValues.clear();
                }

                myStrJson = "";
                try {
                    OrderedJSONObject json = new OrderedJSONObject(actualReponse);
                    loadJson(json, "", jsonKeyToIgnore, 0);
                } catch (Exception e) {
                    try {
                        JSONArray jsonarray = new JSONArray(actualReponse);
                        for (int i = 0; i < jsonarray.length(); i++) {
                            OrderedJSONObject json = new OrderedJSONObject(jsonarray.getString(i));
                            loadJson(json, "", jsonKeyToIgnore, i);
                        }
                    } catch (Exception e2) {
                        keyAndValues.put("not a json", "not a json");
                        myStrJson = "not a json";
                    }
                }
                jsonActual = myStrJson;
                keysAndValuesActual.putAll(keyAndValues);

                keyAndValues.clear();
                myStrJson = "";
                try {
                    OrderedJSONObject json = new OrderedJSONObject(expectedReponse);
                    loadJson(json, "", jsonKeyToIgnore, 0);
                } catch (Exception e) {
                    try {
                        JSONArray jsonarray = new JSONArray(expectedReponse);
                        for (int i = 0; i < jsonarray.length(); i++) {
                            OrderedJSONObject json = new OrderedJSONObject(jsonarray.getString(i));
                            loadJson(json, "", jsonKeyToIgnore, i);
                        }
                    } catch (Exception e2) {
                        keyAndValues.put("not a json", "not a json");
                        myStrJson = "not a json";
                    }
                }
                jsonExpected = myStrJson;
                keysAndValuesExpected.putAll(keyAndValues);

                if (jsonActual.toUpperCase().equals(jsonExpected.toUpperCase())) {
                    //Reporter.log("pass", "assertJsonEquals ", "", actualReponse, null, null);
                } else {
                    String jsonDiffHeader = "<br><br><b>Différence(s) constatée(s):</b><br>";
                    String jsonDiff = jsonDiffHeader;

                    for(Map.Entry<String, String> keyAndValueExpected : keysAndValuesExpected.entrySet()) {
                        String key = keyAndValueExpected.getKey();
                        String value = keyAndValueExpected.getValue();
                        if (keysAndValuesActual.containsKey(key)) {
                            if (!value.toUpperCase().equals(keysAndValuesActual.get(key).toUpperCase())) {
                                jsonDiff += key + " attendu=" + value + ", constaté=" + keysAndValuesActual.get(key) + "<br>";
                            }
                        } else {
                            boolean logDiff = true;
                            if (jsonKeyToIgnore != null) {
                                for (String keytoignore : jsonKeyToIgnore.split(";")
                                ) {
                                    if (key.startsWith(keytoignore)) {
                                        logDiff = false;
                                        break;
                                    }
                                }
                            }
                            if (logDiff) jsonDiff += key + " absent de la réponse<br>";
                        }
                    }

                    for(Map.Entry<String, String> keyAndValueActual : keysAndValuesActual.entrySet()) {
                        String key = keyAndValueActual.getKey();
                        if (!keysAndValuesExpected.containsKey(key)) {
                            boolean logDiff = true;
                            if (jsonKeyToIgnore != null) {
                                for (String keytoignore : jsonKeyToIgnore.split(";")
                                ) {
                                    if (key.startsWith(keytoignore)) {
                                        logDiff = false;
                                        break;
                                    }
                                }
                            }
                            if (logDiff) jsonDiff += key + " absent de l'attendu<br>";
                        }
                    }

                    System.out.println(jsonDiff);
                    if (!jsonDiff.equals(jsonDiffHeader)) {
                        //Reporter.log("failnext", "assertJsonEquals ", "", expectedReponse, actualReponse, jsonDiff);
                    } else {
                        //Reporter.log("pass", "assertJsonEquals ", "", actualReponse, null, null);
                    }
                }
            }
        }
    }

    /**
     * vérifie l'égalité de 2 json dont l'un dans un tableau de json en ignorant des clés
     * Le résultat est tracé dans le rapport.
     * @param id id de l'objet json contenu dans le tableau de json actualReponse
     * @param expectedReponse json attendu
     * @param actualReponse json actual
     * @param jsonKeyToIgnore clés dont on ignore les différences de valeurs (String séparé par des ;)
     */
    public static void assertJsonArrayContainsJsonElementEquals(String id, String expectedReponse, String actualReponse, String jsonKeyToIgnore) {
        actualReponse = getJsonObjectWithIdFromJsonArray(actualReponse, id);
        assertJsonEquals(expectedReponse, actualReponse, jsonKeyToIgnore, "");
    }



    private static void loadJson(OrderedJSONObject json, String preKeys, String jsonKeyToIgnore, int arrayIndex) {
        Iterator order = json.getOrder();
        while (order.hasNext()) {
            String json_key = (String) order.next();
            boolean isJSONarray = true;
            boolean isJSONobject = true;
            try {
                JSONArray jsonarray = json.getJSONArray(json_key);
                if (jsonarray.length()==0) {
                    isJSONarray = false;
                } else {
                    JSONObject jsonobject =jsonarray.getJSONObject(0);
                }
            } catch (Exception e) {
                isJSONarray = false;
            }
            try {
                JSONObject jsonobject = json.getJSONObject(json_key);
            } catch (Exception e) {
                isJSONobject = false;
            }
            String key = preKeys + (preKeys.equals("")?"":".") + json_key + (arrayIndex>0?"[" + arrayIndex + "]":"");
            //System.out.println("key" + key);
            String value;
            try {
                value = json.get(json_key).toString();
            } catch (Exception ex) {
                value = "null";
            }
            if (jsonKeyToIgnore!=null && jsonKeyToIgnore.length()>0) {
                for (String keytoignore:jsonKeyToIgnore.split(";")
                ) {
                    if (key.startsWith(keytoignore)) {
                        value = "no check";
                        break;
                    }
                }
            }
            value = (value.startsWith("Data.get=>")?Data.get(value.replace("Data.get=>","")):value);
            if (!isJSONarray && ! isJSONobject) {
                keyAndValues.put(key, value);
                myStrJson += key+": " + value + "<br>";
            }
            if (isJSONobject) {
                try {
                    loadJson(new OrderedJSONObject(json.getString(json_key)), preKeys + (preKeys.equals("") ? "" : ".") + json_key, jsonKeyToIgnore, arrayIndex);
                } catch (Exception e) {
                    //
                }
            }
            if (isJSONarray) {
                try {
                    JSONArray jsonarray = json.getJSONArray(json_key);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        try {
                            loadJson(new OrderedJSONObject(jsonarray.getString(i)), preKeys + (preKeys.equals("") ? "" : ".") + json_key, jsonKeyToIgnore, arrayIndex);
                        } catch (Exception e) {
                            //
                        }
                    }
                } catch (Exception e) {
                    keyAndValues.put(key, value);
                    myStrJson += key+": " + value + "<br>";
                }

            }
        }
    }

}
