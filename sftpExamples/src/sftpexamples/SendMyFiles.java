/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sftpexamples;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

/**
 *
 * @author salim file to download: 32868.jpg
 * 
 * http://www.mysamplecode.com/2013/06/sftp-apache-commons-file-download.html
 */
public class SendMyFiles {

    static Properties props;

    public static void main(String[] args) {

        SendMyFiles sendMyFiles = new SendMyFiles();
//        if (args.length < 1) {
//            System.err.println("Usage: java " + sendMyFiles.getClass().getName()
//                    + " Properties_file File_To_FTP ");
//            System.exit(1);
//        }

        String propertiesFile;
        String fileToFTP;

        propertiesFile = "/sftpProp";
        fileToFTP = "fcb.jpeg";//32868.jpg";
        sendMyFiles.startFTP(propertiesFile, fileToFTP);

    }

    public boolean startFTP(String propertiesFilename, String fileToFTP) {

        props = new Properties();
        StandardFileSystemManager manager = new StandardFileSystemManager();
        try {
            props.load(new FileInputStream(System.getProperty("user.home") + propertiesFilename));
            //props.setProperty("serverAddress", "127.0.0.1");
            String serverAddress = props.getProperty("serverAddress").trim();
            String userId = props.getProperty("userId").trim();
            String password = props.getProperty("password").trim();
            String remoteDirectory = props.getProperty("remoteDirectory").trim();
            String localDirectory = props.getProperty("localDirectory").trim();
            System.out.println("properties are fetched:serverAddress=" + serverAddress);
            System.out.println("userId=" + userId);
            System.out.println("password=" + password);
            System.out.println("remoteDirectory=" + remoteDirectory);
            System.out.println("localDirectory=" + localDirectory);
            //check if the file exists
            String filepath = localDirectory + fileToFTP;
            System.out.println("filepath:" + filepath);
            File file = new File(filepath);
            if (!file.exists()) {
                throw new RuntimeException("Error. Local file not found");
            }

            //Initializes the file manager
            manager.init();

            //Setup our SFTP configuration
            FileSystemOptions opts = new FileSystemOptions();
            SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(
                    opts, "no");
            SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true);
            SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 10000);

            //Create the SFTP URI using the host name, userid, password,  remote path and file name
            String sftpUri = "sftp://" + userId + ":" + password + "@" + serverAddress + "/" + fileToFTP;
            System.out.println("uri: " + sftpUri);
            // Create local file object
            FileObject localFile = manager.resolveFile(file.getAbsolutePath());

            // Create remote file object
            FileObject remoteFile = manager.resolveFile(sftpUri, opts);

            // Copy local file to sftp server
            remoteFile.copyFrom(localFile, Selectors.SELECT_SELF);
            System.out.println("File upload successful");
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            manager.close();
        }
        return true;
    }
}
