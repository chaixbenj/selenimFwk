package util;

import driver.Driver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CallWS {
    static HashMap<String,String> keyValues = new HashMap<String,String>();
    static Stack<String> key_path = new Stack<String>();

    public static void call(String user, String password, String uri, String method, String payload, String fileNameToSaveBodyResponse) {
        uri = uri.replaceAll(":", "%3A");
        uri = uri.replaceAll("\\+", "%2B");
        Driver.getReport().description = "Appel du webservice " + method + " " + uri;
        System.out.println("call " + method + " " + uri);
        String reponse = "";
        String status = "";
        try {
            URL url = new URL(TestProperties.sistemaUrl + uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            String encoded = Base64.getEncoder().encodeToString((user + ":" + password).getBytes(StandardCharsets.UTF_8));
            conn.setRequestMethod(method);
            conn.setRequestProperty("Authorization", "Basic "+ encoded);
            conn.setDoInput(true);
            if (payload!=null) {
                System.out.println(payload);
                //conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                try (OutputStream output = conn.getOutputStream()) {
                    output.write(payload.getBytes());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            status = String.valueOf(conn.getResponseCode());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = conn.getInputStream().read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            reponse = baos.toString("UTF-8");
            if (fileNameToSaveBodyResponse!=null && !reponse.equals("") && !reponse.equals("[]")) {
                try (FileOutputStream stream = new FileOutputStream(TestProperties.download_rep + "\\" + fileNameToSaveBodyResponse)) {
                    stream.write(baos.toByteArray());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            conn.disconnect();

        } catch (Exception e) {
            System.out.println("Exception in NetClient:- " + e);
        }
        Driver.getReport().log("pass", "WS " + method + " " + uri, "", "", status, null);
    }



    public static void assertStatus(String expectedStatus, String actualStatus) {
        Driver.getReport().description = "Vérifier le status de l'appel du WS =" + expectedStatus;
        boolean assertOK;
        if (!expectedStatus.equals(actualStatus)) {
            Driver.getReport().log("fail", "CallWS check status ", "", expectedStatus,actualStatus, null);
            assertOK = false;
        } else {
            Driver.getReport().log("pass", "CallWS check status ", "", expectedStatus, actualStatus, null);
            assertOK = true;
        }
    }

    public static void assertReponseTxt(String expectedReponse, String actualReponse) {
        Driver.getReport().description = "Vérifier le contenu de la réponse =" + expectedReponse;
        boolean assertOK;
        expectedReponse = getContentIfIsFile(expectedReponse);
        if (!expectedReponse.equals(actualReponse)) {
            Driver.getReport().log("fail", "CallWS check response body ", "", expectedReponse,actualReponse, null);
            assertOK = false;
        } else {
            Driver.getReport().log("pass", "CallWS check response body ", "", expectedReponse, actualReponse, null);
            assertOK = true;
        }
    }

    public static void assertReponseJSON(String expectedReponse, String actualReponse, String jsonKeyToIgnore, String replacementValuesString) {
        Driver.getReport().description = "Vérifier le json de la réponse =" + expectedReponse;
        boolean assertOK;
        String[] jsonActual = new String[] {"","",""};
        String[] jsonExpected = new String[] {"","",""};
        String strJson;
        String strKeys;
        String strKeyValues;
        expectedReponse = getContentIfIsFile(expectedReponse);
        if (replacementValuesString!=null && !replacementValuesString.equals("")) {
            String[] replacementValues = replacementValuesString.split("\\s*\\|\\s*");
            for (String replacementValue : replacementValues) {
                try {
                    String[] repValue = replacementValue.split(":=");
                    String toBeReplaced = repValue[0];
                    String byString = repValue[1];
                    expectedReponse = expectedReponse.replaceAll(toBeReplaced, byString);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        try {
            JSONObject json = new JSONObject(actualReponse);
            jsonActual = loadJson("","","", json, jsonKeyToIgnore, 0);
        } catch (Exception e) {
            try {
                JSONArray jsonarray = new JSONArray(actualReponse);
                strJson = "";
                strKeys = "";
                strKeyValues = "";
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject json = jsonarray.getJSONObject(i);
                    jsonActual = loadJson(strJson, strKeys, strKeyValues, json, jsonKeyToIgnore, i);
                    strJson = jsonActual[0];
                    strKeys = jsonActual[1];
                    strKeyValues = jsonActual[2];
                }
            } catch (Exception e2) {
                jsonActual = new String[] {"not a json","not a json","not a json"};
            }
        }

        try {
            JSONObject json = new JSONObject(expectedReponse);
            jsonExpected = loadJson("","","", json, jsonKeyToIgnore, 0);
        } catch (Exception e) {
            try {
            JSONArray jsonarray = new JSONArray(expectedReponse);
            strJson = "";
            strKeys = "";
            strKeyValues = "";
            for (int i=0;i<jsonarray.length();i++) {
                JSONObject json = jsonarray.getJSONObject(i);
                jsonExpected = loadJson(strJson, strKeys, strKeyValues, json, jsonKeyToIgnore, i);
                strJson = jsonExpected[0];
                strKeys = jsonExpected[1];
                strKeyValues = jsonExpected[2];
            }
            } catch (Exception e2) {
                jsonExpected = new String[] {"not a json","not a json","not a json"};
            }
        }

        if (jsonActual[0].equals(jsonExpected[0])) {
            Driver.getReport().log("pass", "CallWS check reponse ", "", actualReponse , null, null);
            assertOK = true;
        } else {
            String[] keysExpected = jsonExpected[1].split(";");
            String[] keysActual = jsonActual[1].split(";");
            String[] keyValuesExpected = jsonExpected[2].split(";");
            String[] keyValuesActual = jsonActual[2].split(";");
            String jsonDiff = "<br><br><b>Différence(s) constatée(s):</b><br>";
            for (int i=0;i<keysExpected.length;i++) {
                if (Arrays.asList(keysActual).contains(keysExpected[i])) {
                    int j = Arrays.asList(keysActual).indexOf(keysExpected[i]);
                    if (!keyValuesExpected[i].equals(keyValuesActual[j])) {
                        jsonDiff += keysExpected[i] + " attendu=" +  keyValuesExpected[i] + ", constaté=" + keyValuesActual[j] + "<br>";
                    }
                } else {
                    jsonDiff += keysExpected[i] + " absent de la réponse<br>";
                }
            }
            for (int i=0;i<keysActual.length;i++) {
                if (!Arrays.asList(keysExpected).contains(keysActual[i])) {
                    jsonDiff += keysActual[i] + " absent de l'attendu<br>";
                }
            }
            Driver.getReport().log("fail", "CallWS check reponse ", "", expectedReponse , actualReponse, jsonDiff);
            assertOK = false;
        }
    }

    private static String[] loadJson(String strJson, String strKeys, String strKeyValues, JSONObject json, String jsonKeyToIgnore, int arrayIndex){
        Iterator<?> json_keys = json.keys();

        while( json_keys.hasNext() ){
            String json_key = (String)json_keys.next();

            try{
                key_path.push(json_key);
                String[] retLoadJson = loadJson(strJson, strKeys, strKeyValues,  json.getJSONObject(json_key), jsonKeyToIgnore, arrayIndex);
                strJson = retLoadJson[0];
                strKeys = retLoadJson[1];
                strKeyValues = retLoadJson[2];
            }catch (JSONException e){
                String key = "";
                for(String sub_key: key_path){
                    key += sub_key+".";
                }
                key = key.substring(0,key.length()-1);
                String value;
                try {
                    value = json.get(json_key).toString();
                } catch (Exception ex) {
                    value = "null";
                }
                if (jsonKeyToIgnore!=null && Arrays.asList(jsonKeyToIgnore.split(";")).contains(key)) value = "no check";
                strJson += key+": " + value + "<br>";
                strKeys += key + "[" + arrayIndex + "]" + ";";
                strKeyValues += value + ";";
                key_path.pop();
                //keyValues.put(key, value);
            }
        }
        if(key_path.size() > 0){
            key_path.pop();
        }
        return new String[] {String.valueOf(strJson),String.valueOf(strKeys),String.valueOf(strKeyValues)};
    }

    private static String getContentIfIsFile(String expectedResult) {
        String content;
        try
        {
            content = new String ( Files.readAllBytes( Paths.get("target/test-classes/references/" + expectedResult) ) );
        }
        catch (Exception e)
        {
            content = expectedResult;
        }
        return content;
    }


}
