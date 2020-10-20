package util;

import driver.Driver;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FtpUtil {

    /**
     * upload un fichier dans un ftp
     * @param ftp FTP défini dans le fichier test_env.properties avec les properties ftp_host_FTP, ftp_port_FTP, ftp_user_FTP, ftp_pwd_FTP
     * @param remotePathRep répertoire du ftp
     * @param fichierLocal fichier local à uploader (dans target/test-classes/jdd/langue)
     */
    public static void upload(String ftp, String remotePathRep, String fichierLocal) {
        String server = Data.get("ftp_host_" + ftp);
        int port = Integer.parseInt(Data.get("ftp_port_" + ftp));
        String user = Data.get("ftp_user_" + ftp);
        String pass = Data.get("ftp_pwd_" + ftp);

        FTPClient ftpClient = new FTPClient();
        try {

            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            File localFile= new File("target/test-classes/DataSet/" + fichierLocal);

            String remoteFile = remotePathRep + "/" + localFile.getName();
            InputStream inputStream = new FileInputStream(localFile);

            boolean done = ftpClient.storeFile(remoteFile, inputStream);
            inputStream.close();
            if (done) {
                Driver.getReport().log("pass", "FTP.upload to " + ftp + " /" + remotePathRep, fichierLocal, null, null, null);
            }

        } catch (IOException ex) {
            Driver.getReport().log("fail", "FTP.upload to " + ftp + " /" + remotePathRep, fichierLocal, null, null, ex.getMessage());
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


    }
}
