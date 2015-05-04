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
 * @author salim
 */
public class GetMyFiles {

    static Properties props;

    public static void main(String[] args) {

        GetMyFiles getMyFiles = new GetMyFiles();
        /*
        if (args.length < 1) {
            System.err.println("Usage: java " + getMyFiles.getClass().getName()
                    + " Properties_filename File_To_Download ");
            System.exit(1);
        }

        String propertiesFilename = args[0].trim();
        String fileToDownload = args[1].trim();
                */
        String propertiesFilename = "/sftpProp";
        String fileToDownload = "fcb";//32868.jpg";
        getMyFiles.startFTP(propertiesFilename, fileToDownload);

    }

    public boolean startFTP(String propertiesFilename, String fileToDownload) {

        props = new Properties();
        StandardFileSystemManager manager = new StandardFileSystemManager();

        try {

            props.load(new FileInputStream(System.getProperty("user.home") + propertiesFilename));
            String serverAddress = props.getProperty("serverAddress").trim();
            String userId = props.getProperty("userId").trim();
            String password = props.getProperty("password").trim();
            String remoteDirectory = props.getProperty("remoteDirectory").trim();
            String localDirectory = props.getProperty("localDirectory").trim();

            //Initializes the file manager
            manager.init();

            //Setup our SFTP configuration
            FileSystemOptions opts = new FileSystemOptions();
            SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(
                    opts, "no");
            SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true);
            SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 10000);

            //Create the SFTP URI using the host name, userid, password,  remote path and file name
            //String sftpUri = "sftp://" + userId + ":" + password + "@" + serverAddress 
            //      + "/" + remoteDirectory + fileToDownload;
            String sftpUri = "sftp://" + userId + ":" + password + "@" + serverAddress 
                 + "/" + fileToDownload;
            // Create local file object
            String filepath = localDirectory + fileToDownload;
            File file = new File(filepath);
            FileObject localFile = manager.resolveFile(file.getAbsolutePath());

            // Create remote file object
            FileObject remoteFile = manager.resolveFile(sftpUri, opts);

            // Copy local file to sftp server
            localFile.copyFrom(remoteFile, Selectors.SELECT_SELF);
            System.out.println("File download successful");

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            manager.close();
        }

        return true;
    }

}
