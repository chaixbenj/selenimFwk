package util;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONObject;
import org.apache.wink.json4j.OrderedJSONObject;

import java.util.Iterator;
import java.util.LinkedHashMap;

public class JsonUtil {
    private static String myStrJson;
    private static LinkedHashMap<String, String> keyAndValues;

    /**
     * return hashmap from json file or string
     * @param strJson
     * @return
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
