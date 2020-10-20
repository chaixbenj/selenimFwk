package util;

import driver.Driver;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class XmlUtil
{
    public static void xmlNodeTextEquals(String actualXML, String key, String value) {
        String actualvalue;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(new InputSource(new StringReader(Data.getContentIfIsFile(actualXML))));
            XPathFactory xpf = XPathFactory.newInstance();
            XPath xp = xpf.newXPath();
            String expression = "//" + key + "[1]" ;
            XPathExpression expr = xp.compile(expression);
            if (((NodeList) expr.evaluate(document, XPathConstants.NODESET)).getLength()==1) {
                expression = "//" + key + "[1]/text()";
                actualvalue = xp.evaluate(expression, document.getDocumentElement());
            } else {
                actualvalue = "element " + key + " absent du xml";
            }
        } catch (Exception e) {
            actualvalue = "erreur " + e.getMessage();
        }
        Driver.getReport().assertEquals("vérification de la valeur de " + key, value, actualvalue.trim());
    }

    /**
     * vérifie l'égalité de 2 xml
     * Le résultat est tracé dans le rapport.
     * @param expectedXML xml attendu
     * @param actualXML xml actuel
     */
    public static void assertXMLEquals(String expectedXML, String actualXML) {
        try {
            System.out.println(expectedXML);
            System.out.println(actualXML);
            XMLUnit.setIgnoreWhitespace(true);
            XMLUnit.setIgnoreAttributeOrder(true);

            DetailedDiff diff = new DetailedDiff(XMLUnit.compareXML(expectedXML, actualXML));

            List<?> allDifferences = diff.getAllDifferences();
            if (allDifferences.size() > 0) {
                Driver.getReport().log("fail", "assertXMLEquals", null, expectedXML, null, diff.toString());
            } else {
                Driver.getReport().log("pass", "assertXMLEquals", null, null, null, null);
            }
        } catch (Exception e) {
            Driver.getReport().log("error", "assertXMLEquals", "", expectedXML, actualXML, e.getMessage());
        }
    }

    /**
     * crée un nouvel xml destFile à partir de initFile en remplaçant tous les champs entre {} par la valeur retournée par le Data.get du champs entre {}
     * @param initFile fichier xml initial avec les champs non valorisées
     * @param destFile fichier résultat avec les champs valorisées
     */
    public static void variabilize(String initFile, String destFile) {
        try {
            String content = Data.get(new String(Files.readAllBytes(Paths.get(initFile))));
            try (FileOutputStream fos = new FileOutputStream(destFile)) {
                fos.write(content.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
