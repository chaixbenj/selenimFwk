package util;

import java.util.LinkedHashMap;

/**
 * use this class for creating data set used by Form class
 */
public class DataSetUtil {
    private LinkedHashMap<String, String> keyAndValues;

    public DataSetUtil() {
        keyAndValues = new LinkedHashMap<String, String>();
    }

    /**
     * create a DataSetUtil object from a json a csv
     * @param jdd
     * @param rowNum
     */
    public DataSetUtil (String jdd, int rowNum) {
        if (jdd.endsWith(".json")) {
            keyAndValues = JsonUtil.jsonFileToHash(jdd);
        } else {
            keyAndValues = CsvUtil.recordToHash(jdd + ".csv", rowNum);
        }
    }

    /**
     * create a DataSetUtil object from a LinkedHashMap
     * @param keyAndValues
     */
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

    /**
     * clear datasetutil values
     */
    public void clear() {
        keyAndValues.clear();
    }

    /**
     * return datasetutil object LinkedHashMap
     * @return
     */
    public LinkedHashMap<String, String> getKeyAndValues() {
        return keyAndValues;
    }
}
