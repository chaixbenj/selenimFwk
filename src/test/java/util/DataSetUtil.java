package util;

import java.util.Date;
import java.util.LinkedHashMap;

public class DataSetUtil {
    private LinkedHashMap<String, String> keyAndValues;

    public DataSetUtil() {
        keyAndValues = new LinkedHashMap<String, String>();
    }

    public DataSetUtil (String jdd, int rowNum) {
        if (jdd.endsWith(".json")) {
            keyAndValues = JsonUtil.jsonFileToHash(jdd);
        } else {
            keyAndValues = CsvUtil.recordToHash(jdd + ".csv", rowNum);
        }
    }

    public DataSetUtil(LinkedHashMap<String, String> keyAndValues) {
        this.keyAndValues = keyAndValues;
    }

    public void add(String key, String value) {
        if (keyAndValues.containsKey(key)) {
            keyAndValues.replace(key, value);
        } else {
            keyAndValues.put(key, value);
        }
    }

    public void clear() {
        keyAndValues.clear();
    }

    public LinkedHashMap<String, String> getKeyAndValues() {
        return keyAndValues;
    }
}
