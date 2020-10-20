package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class CsvUtil {
    public static LinkedHashMap<String, String> recordToHash(String dataSetName) {
        return recordToHash(dataSetName, 1, ";");
    }

    public static LinkedHashMap<String, String> recordToHash(String dataSetName, int rowNumber) {
        return recordToHash(dataSetName, rowNumber, ";");
    }

    public static LinkedHashMap<String, String> recordToHash(String dataSetName, int rowNumber, String separator) {
        LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
        try {
            InputStream stream = new FileInputStream("target/test-classes/DataSet/" + dataSetName);
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader buff = new BufferedReader(reader);
            ArrayList<String> entetes = new ArrayList<String>(Arrays.asList(buff.readLine().split(separator)));
            String ligne = "";
            int i = 0;
            while (i< rowNumber) {
                ligne = buff.readLine();
                i++;
            }
            ArrayList<String> datas = new ArrayList<String>(Arrays.asList(ligne.split(separator)));
            for (String entete:entetes
                 ) {
                record.put(entete.trim(), datas.get(entetes.indexOf(entete)).trim());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return record;
    }
}
