package driver;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import org.apache.commons.codec.binary.Base64;
import org.apache.maven.shared.utils.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import util.TestProperties;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * this class manage the test report. Log actions and assertions
 */
public class Reporter {
    private String pageObjetMethod = "";
    private SoftAssert softAssert = new SoftAssert();
    public String description = "";
    public static int nbStepFuncError;
    public static int nbStepFuncFail;
    public static int nbStepFuncPass;
    private static int nbTestError;
    public static String currentScenarioFailMessage;
    ExtentReports extent;
    ExtentTest logger;

    /**
     * constructor
     * @param suiteName
     */
    public Reporter(String suiteName) {
        extent = new ExtentReports (Paths.get("").toAbsolutePath().toString() + "\\target\\test-reports\\" + suiteName + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_hhmm")) + ".html", true);
        //extent.addSystemInfo("Environment","Environment Name")
        extent
                .addSystemInfo("Environment", TestProperties.environnement);
        //loading the external xml file (i.e., extent-config.xml) which was placed under the base directory
        //You could find the xml file below. Create xml file in your project and copy past the code mentioned below
        extent.loadConfig(new File(Paths.get("").toAbsolutePath().toString() + "\\target\\test-classes\\extent-config.xml"));
        nbStepFuncPass = 0;
        nbStepFuncFail = 0;
        nbStepFuncError = 0;
        nbTestError = 0;
    }

    /**
     * publish report
     */
    public void publish() {
        extent.flush();
    }

    public void initTest(String test) {
        logger = extent.startTest(test);
    }

    /**
     * called after test
     * @param errorMessage
     */
    public void endTest(String errorMessage) {
        if (nbTestError!=0) logger.log(LogStatus.FAIL, "[TEST FAILED] <b>" + errorMessage + "</b>");
        Assert.assertEquals(nbTestError, 0);
        nbTestError = 0;
    }

    /**
     * log equality assertion between 2 int
     * @param expected
     * @param actual
     */
    public void assertEquals(int expected, int actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * log equality assertion between 2 int with an information in the log
     * @param expected
     * @param actual
     */
    public void assertEquals(String info, int expected, int actual) {
        assertEquals(info, String.valueOf(expected), String.valueOf(actual));
    }

    /**
     * log equality assertion between 2 booleans
     * @param expected
     * @param actual
     */
    public void assertEquals(boolean expected, boolean actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * log equality assertion between 2 booleans with an information in the log
     * @param expected
     * @param actual
     */
    public void assertEquals(String info, boolean expected, boolean actual) {
        assertEquals(info, String.valueOf(expected), String.valueOf(actual));
    }

    /**
     * log equality assertion between 2 doubles
     * @param expected
     * @param actual
     */
    public void assertEquals(Double expected, Double actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * log equality assertion between 2 doubles
     * @param expected
     * @param actual
     */
    public void assertEquals(String info, Double expected, Double actual) {
        assertEquals(info, String.valueOf(expected), String.valueOf(actual));
    }

    /**
     * log equality assertion between 2 strings
     * @param expected
     * @param actual
     */
    public void assertEquals(String expected, String actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * log equality assertion between 2 strings with an information in the log
     * @param expected
     * @param actual
     */
    public void assertEquals(String info, String expected, String actual) {
        String status = (expected==actual?"pass":"failnext");
        log(status, (info!=null?info + " ":"") + "assertEquals", null, expected, actual, null);
    }

    /**
     * log an assertion that one int is greater than another
     * @param num1
     * @param num2
     */
    public void assertGreater(int num1, int num2) {
        assertGreater(null, num1, num2);
    }

    /**
     * log an assertion that one int is greater than another with an information in the log
     * @param num1
     * @param num2
     */
    public void assertGreater(String info, int num1, int num2) {
        assertGreater(info, Double.valueOf(num1), Double.valueOf(num2));
    }

    /**
     * log an assertion that one double is greater than another
     * @param num1
     * @param num2
     */
    public void assertGreater(Double num1, Double num2) {
        assertGreater(null, num1, num2);
    }

    /**
     * log an assertion that one double is greater than another with an information in the log
     * @param num1
     * @param num2
     */
    public void assertGreater(String info, Double num1, Double num2) {
        String status = (num1>num2?"pass":"failnext");
        log(status, (info!=null?info + " ":"") + "assertGreater", null, String.valueOf(num1), String.valueOf(num2), null);
    }

    /**
     * log an assertion that a string is not null
     * @param actual
     */
    public void assertNotNull(String actual) {
        assertNotNull(null, actual);
    }

    /**
     * log an assertion that a string is not null with an information in the log
     * @param actual
     */
    public void assertNotNull(String info, String actual) {
        String status = (actual!=null?"pass":"failnext");
        log(status, (info!=null?info + " ":"") + "assertNotNull", null, null, actual, null);
    }

    /**
     * log an assertion that 2 dates are equals with an average allowed
     * @param date
     * @param format
     * @param minuteEcartAcceptable
     */
    public void assertDateEqualsLocalDateTime(String date, String format, int minuteEcartAcceptable) {
        assertDateEqualsLocalDateTime(null, date, format, minuteEcartAcceptable);
    }

    /**
     * log an assertion that 2 dates are equals with an average allowed and an information in the log
     * @param date
     * @param format
     * @param minuteEcartAcceptable
     */
    public void assertDateEqualsLocalDateTime(String info, String date, String format, int minuteEcartAcceptable) {
        LocalDateTime dateFormatee;
        String status = "pass";
        String message = null;
        LocalDateTime now = LocalDateTime.now();
        try {
            dateFormatee = LocalDateTime.parse(date, DateTimeFormatter.ofPattern(format));
            if (dateFormatee.plusMinutes(minuteEcartAcceptable).isBefore(now) || dateFormatee.minusMinutes(minuteEcartAcceptable).isAfter(now)) {
                status = "failnext";
            }
        } catch (Exception e) {
            status = "errornext";
            message = e.getMessage();
        }
        log(status, (info!=null?info + " ":"") + "assertDateEqualsLocalDateTime", "", String.valueOf(now) + " +/- " + String.valueOf(minuteEcartAcceptable) + " minutes" , String.valueOf(date), message);
    }

    /**
     * write a log in the report
     * @param status
     * @param action
     * @param element
     * @param expected
     * @param actual
     * @param message
     */
    public void log(String status, String action, String element, String expected, String actual, String message) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        int i = 0;
        StackTraceElement stackTraceElement = null;
        for (StackTraceElement stack:stackTraceElements
             ) {
            if (stack.getClassName().contains("pageObject")) {stackTraceElement=stack;}
        }
        if (stackTraceElement!=null) {
            String actualPageObjetMethod = stackTraceElement.getClassName()+ " " + stackTraceElement.getMethodName();
            if (!actualPageObjetMethod.equals(pageObjetMethod)) {
                logger.log(LogStatus.INFO, "<h5>" + stackTraceElement.getMethodName() + " [" + stackTraceElement.getClassName() + "]</h5>");
            }
            pageObjetMethod = actualPageObjetMethod;
        }
        message = (message!=null?message:"");
        String sExpected = action + (element!=null?" sur " + element:"") + (expected!=null?", attendu: " + expected:"");
        String sActual = action + (element!=null?" sur " + element:"") + (actual!=null||message!=null?", constaté: " + actual + " " + message:"");
        switch (status) {
            case "pass":
                logger.log(LogStatus.PASS,  action + (element!=null?" sur " + element:"") + (expected!=null?"<br>attendu: " + expected:""));
                nbStepFuncPass += 1;
                break;
            case "info":
                logger.log(LogStatus.INFO,  action + (element!=null?" sur " + element:"") + (expected!=null?"<br>attendu: " + expected:"") + (actual!=null||message!=null?"<br><br>constaté: " + actual + " " + message:""));
                break;
            case "fail":
                nbStepFuncFail += 1;
                nbTestError += 1;
                logger.log(LogStatus.FAIL, "sous-step " + action + (element!=null?" sur " + element:"") + (expected!=null?"<br>attendu: " + expected:"") + (actual!=null||message!=null?"<br><br>constaté: " + actual + " " + message:""));
                currentScenarioFailMessage = String.valueOf(currentScenarioFailMessage) + "<br>sous-step " + action + (element!=null?" sur " + element:"") + (expected!=null?"<br>attendu: " + expected:"") + (actual!=null||message!=null?"<br><br>constaté: " + actual + " " + message:"");
                screenShotFail();
                Assert.assertEquals(sExpected,sActual);
                break;
            case "error":
                nbStepFuncError += 1;
                nbTestError += 1;
                logger.log(LogStatus.ERROR, "sous-step " + action + (element!=null?" sur " + element:"") + (expected!=null?"<br>attendu: " + expected:"") + (actual!=null||message!=null?"<br><br>constaté: " + actual + " " + message:""));
                currentScenarioFailMessage = String.valueOf(currentScenarioFailMessage) + "<br>sous-step " + action + (element!=null?" sur " + element:"") + (expected!=null?"<br>attendu: " + expected:"") + (actual!=null||message!=null?"<br><br>constaté: " + actual + " " + message:"");
                screenShotFail();
                Assert.assertEquals(sExpected,sActual);
                break;
            case "failnext":
                nbStepFuncFail += 1;
                nbTestError += 1;
                logger.log(LogStatus.FAIL, "sous-step " + action + (element!=null?" sur " + element:"") + (expected!=null?"<br>attendu: " + expected:"") + (actual!=null||message!=null?"<br><br>constaté: " + actual + " " + message:""));
                currentScenarioFailMessage = String.valueOf(currentScenarioFailMessage) + "<br>sous-step " + action + (element!=null?" sur " + element:"") + (expected!=null?"<br>attendu: " + expected:"") + (actual!=null||message!=null?"<br><br>constaté: " + actual + " " + message:"");
                screenShotFail();
                softAssert.assertEquals("fail : " + sExpected,sActual);
                break;
            case "errornext":
                nbStepFuncError += 1;
                nbTestError += 1;
                logger.log(LogStatus.ERROR, "sous-step " + action + (element!=null?" sur " + element:"") + (expected!=null?"<br>attendu: " + expected:"") + (actual!=null||message!=null?"<br><br>constaté: " + actual + " " + message:""));
                currentScenarioFailMessage = String.valueOf(currentScenarioFailMessage) + "<br>sous-step " + action + (element!=null?" sur " + element:"") + (expected!=null?"<br>attendu: " + expected:"") + (actual!=null||message!=null?"<br><br>constaté: " + actual + " " + message:"");
                screenShotFail();
                softAssert.assertEquals("error : " + sExpected,sActual);
                break;
        }
    }

    /**
     * compare a screenshot of the current driver with a png reference from repository resources/references
     * @param reference
     * @param pourcentageDiffAdmissible
     */
    public void compareScreenshotRef(String reference, double pourcentageDiffAdmissible) {
        reference = Paths.get("").toAbsolutePath().toString() + "\\target\\test-classes\\references\\ref" + TestProperties.browser + TestProperties.browser_width + TestProperties.browser_heigth + reference;
        File SrcFile= ((TakesScreenshot) Driver.getCurrentDriver()).getScreenshotAs(OutputType.FILE);
        File DestFile=new File(reference.replace(".png",".current.png"));
        try {
            FileUtils.copyFile(SrcFile, DestFile);
        } catch (Exception e) {
            System.out.println("screenshot error copyFile");
        }
        try {
            compareImage(reference.replace(".png",".current.png"), reference, pourcentageDiffAdmissible);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * compare 2 image files
     * @param file1
     * @param file2
     * @param pourcentageDiffAdmissible
     * @return
     * @throws IOException
     */
    private boolean compareImage(String file1, String file2, double pourcentageDiffAdmissible) throws IOException
    {
        boolean hasDiff = true;
        double difference = -1;
        long nbPixelTotal = 1;
        String refBase64 = null;
        String diffBase64 = null;
        if (!new File(file1).exists()) throw new java.io.FileNotFoundException("Fil 1 not found: " + file1);
        if (new File(file2).exists()) {
            // extraction du nombre de pixel dans l'image
            String[] cmd = new String[]{"identify", "-format", "'%wx%h'", file1};
            Process compProces = Runtime.getRuntime().exec(cmd, new String[0]);
            InputStream std = compProces.getInputStream();
            InputStream err = compProces.getErrorStream();
            StringBuffer outsb = new StringBuffer(20);
            StringBuffer errsb = new StringBuffer(20);
            do {
                int ch = std.read();
                if (ch == -1) break;
                outsb.append((char) ch);
            } while (true);
            do {
                int ch = err.read();
                if (ch == -1) break;
                errsb.append((char) ch);
            } while (true);
            compProces.getOutputStream().close();
            std.close();
            err.close();
            String ret = outsb.toString() + errsb.toString();
            String[] dimImage = ret.split("x");
            nbPixelTotal = Integer.parseInt(dimImage[0].replace("'","")) * Integer.parseInt(dimImage[1].replace("'",""));


            // extraction du nombre de pixel en écart
            cmd = new String[]{"compare", "-metric", "AE", "-fuzz", "10%", file1, file2, "tempcompareImage.png"};
            compProces = Runtime.getRuntime().exec(cmd, new String[0]);
            std = compProces.getInputStream();
            err = compProces.getErrorStream();
            outsb = new StringBuffer(20);
            errsb = new StringBuffer(20);
            do {
                int ch = std.read();
                if (ch == -1) break;
                outsb.append((char) ch);
            } while (true);
            do {
                int ch = err.read();
                if (ch == -1) break;
                errsb.append((char) ch);
            } while (true);
            compProces.getOutputStream().close();
            std.close();
            err.close();
            ret = outsb.toString() + errsb.toString();
            // parse the first number in output
            int n = ret.indexOf(" ");
            try {
                difference = Double.parseDouble(n > 0 ? ret.substring(0, n) : ret);
            } catch (NumberFormatException ex) {
                System.err.println("Output of command " + Arrays.asList(cmd) + " could not be parsed.");
                System.err.println("Perhaps you need to give full path to IMs 'compare' command?");
                System.err.println(cmd[0] + " gave stdout: '" + outsb.toString().trim() + "'");
                System.err.println(cmd[0] + " gave stderr: '" + errsb.toString().trim() + "'");
            }
            File f_ref =  new File(file2);
            refBase64 = encodeFileToBase64Binary(f_ref);
            File f_diff =  new File("tempcompareImage.png");
            diffBase64 = encodeFileToBase64Binary(f_diff);
            f_diff.delete();

        }
        File f_current =  new File(file1);
        String currentBase64 = encodeFileToBase64Binary(f_current);

        difference = difference *100 / nbPixelTotal;
        if (Math.abs(difference)>0.0001) {
            if (difference < pourcentageDiffAdmissible) {
                logger.log(LogStatus.WARNING, "screenshot diff " + file1 + " = " + difference + " %<BR> (actual/ref/diff)" + logger.addBase64ScreenShot("data:image/png;base64," + currentBase64) + logger.addBase64ScreenShot("data:image/png;base64," + refBase64) + logger.addBase64ScreenShot("data:image/png;base64," + diffBase64));
            } else {
                logger.log(LogStatus.ERROR, "screenshot diff " + file1 + " = " + difference + " %<BR> (actual/ref/diff)" + logger.addBase64ScreenShot("data:image/png;base64," + currentBase64) + logger.addBase64ScreenShot("data:image/png;base64," + refBase64) + logger.addBase64ScreenShot("data:image/png;base64," + diffBase64));
            }
        } else {
            logger.log(LogStatus.PASS, "Image identique à la réf" + logger.addBase64ScreenShot("data:image/png;base64," + currentBase64));
            hasDiff = false;
            f_current.delete();
        }
        return hasDiff;
    }

    /**
     * make a comparison between 2 pdf after converting them in png
     * @param filepdf
     * @param fileref
     * @param pourcentageDiffAdmissible
     * @return
     */
    public boolean pdfToPNG(String filepdf, String fileref, double pourcentageDiffAdmissible) {
        boolean hasDiff = true;
        try {
            String [] listefichiers;
            int i;
            listefichiers=new File(TestProperties.image_temp_rep).list();
            for(i=0;i<listefichiers.length;i++){
                if(listefichiers[i].startsWith(filepdf.replace(".pdf", "")) || listefichiers[i].startsWith(fileref.replace(".pdf", ""))){
                    new File(TestProperties.image_temp_rep + "/" + listefichiers[i]).delete();
                }
            }
            String cmd = "magick convert -density 300 -trim " + TestProperties.download_rep + "/" + filepdf + " -quality 100 " + TestProperties.image_temp_rep + "/" + filepdf.replace(".pdf", ".png");
            Process compProces =Runtime.getRuntime().exec(cmd, new String[0]);
            System.out.println(cmd);
            InputStream std = compProces.getInputStream();
            InputStream err = compProces.getErrorStream();
            StringBuffer outsb = new StringBuffer(20);
            StringBuffer errsb = new StringBuffer(20);
            do {
                int ch = std.read();
                if (ch == -1) break;
                outsb.append((char) ch);
            } while (true);

            do {
                int ch = err.read();
                if (ch == -1) break;
                errsb.append((char) ch);
            } while (true);
            compProces.getOutputStream().close();
            std.close();
            err.close();

            cmd = "magick convert -density 300 -trim " + Paths.get("").toAbsolutePath().toString() + "/target/test-classes/references/" + fileref + " -quality 100 " + TestProperties.image_temp_rep + "/ref" + fileref.replace(".pdf", ".png");
            System.out.println(cmd);
            compProces = Runtime.getRuntime().exec(cmd, new String[0]);
            std = compProces.getInputStream();
            err = compProces.getErrorStream();
            outsb = new StringBuffer(20);
            errsb = new StringBuffer(20);
            do {
                int ch = std.read();
                if (ch == -1) break;
                outsb.append((char) ch);
            } while (true);

            do {
                int ch = err.read();
                if (ch == -1) break;
                errsb.append((char) ch);
            } while (true);
            compProces.getOutputStream().close();
            std.close();
            err.close();

            listefichiers=new File(TestProperties.image_temp_rep).list();
            int nbImageDiff = 0;
            for(i=0;i<listefichiers.length;i++){
                if(listefichiers[i].startsWith(filepdf.replace(".pdf", "")) && listefichiers[i].endsWith(".png")){
                    if (compareImage(TestProperties.image_temp_rep + "/" + listefichiers[i],TestProperties.image_temp_rep + "/" +  listefichiers[i].replace(filepdf.replace(".pdf", "") ,"ref" + fileref.replace(".pdf", "") ), pourcentageDiffAdmissible)) nbImageDiff+=1;
                }
            }
            if (nbImageDiff==0) hasDiff = false;

        } catch (Exception e) {
            System.out.println("!!!!" + e.getMessage());
        }
        return hasDiff;
    }

    /**
     * called when an assertion or an action fail in order to add a screenshot in the report
     */
    private void screenShotFail() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String callingFunc = stackTraceElements[3].getClassName() + "." +  stackTraceElements[3].getMethodName();
        if (!callingFunc.contains("CallWS")) {
            String png = ((TakesScreenshot)Driver.getCurrentDriver()).getScreenshotAs(OutputType.BASE64);
            logger.log(LogStatus.FAIL, logger.addBase64ScreenShot("data:image/png;base64," + png));
        }
    }

    /**
     * encode file in 64binary
     * @param file
     * @return
     */
    private static String encodeFileToBase64Binary(File file){
        String encodedfile = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int)file.length()];
            fileInputStreamReader.read(bytes);
            encodedfile = new String(Base64.encodeBase64(bytes), "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodedfile;
    }

}
