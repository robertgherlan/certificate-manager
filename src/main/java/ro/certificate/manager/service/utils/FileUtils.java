package ro.certificate.manager.service.utils;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import ro.certificate.manager.exceptions.FileAlreadyExistException;
import ro.certificate.manager.exceptions.InternalServerError;
import ro.certificate.manager.utils.ErrorMessageBundle;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileUtils {

    private FileUtils() {
    }

    private static final Logger logger = Logger.getLogger(FileUtils.class);


    public static void checkIfExist(File file) throws Exception {
        if (file.exists() && file.isFile()) {
            throw new FileAlreadyExistException(ErrorMessageBundle.FILE_ALREADY_EXIST);
        }
    }

    public static void checkIfNotExist(File file) throws Exception {
        if (!file.exists()) {
            throw new FileNotFoundException(ErrorMessageBundle.FILE_NOT_FOUND);
        }
    }

    public static void downloadFile(HttpServletResponse response, InputStream inputStream, String fileName) {
        try {
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            logger.error(e);
            throw new InternalServerError(e.getMessage());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e2) {
                // Do nothing.
            }

        }
    }
}
