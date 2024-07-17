package com.itssc.jasperreport.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;



public class FileUtil {
    public static String saveFile(byte[] pdfByteArray) {
        // Example PDF byte array (replace this with your actual byte array)
        final Logger logger = LoggerFactory.getLogger(FileUtil.class);

        try {
            // Get the current working directory (typically the bin directory)
            File binDir = new File("").getAbsoluteFile();
            logger.error("Current Directory: " + binDir);

            // Navigate to the parent directory of bin (assuming standard structure)
            File rootDir = binDir.getParentFile();
            logger.error("Root Directory: " + rootDir);

            // Construct the path to the files directory within webapps
            File filesDir = new File(rootDir, "webapps/files");
            logger.error("Files Directory: " + filesDir);

            if (!filesDir.exists()) {
                boolean dirCreated = filesDir.mkdirs();
                if (!dirCreated) {
                    logger.error("Failed to create directory: " + filesDir);
                    return "error creating directory";
                }
            }
            // Ensure the files directory exists
            Long timestamp = Instant.now().toEpochMilli();
            String fileName = String.valueOf(timestamp) + ".pdf";
            // Define the output file path within the files directory
            File outputFile = new File(filesDir, fileName);
            logger.error("Output File Path: " + outputFile);

            // Write the byte array to the file using BufferedOutputStream
            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                bos.write(pdfByteArray);
                logger.error("PDF file successfully created at " + outputFile.getAbsolutePath());
                return fileName;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error saving file";
    }
}
