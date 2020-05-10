package ro.certificate.manager.service.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import ro.certificate.manager.exceptions.FileAlreadyExistException;
import ro.certificate.manager.exceptions.InternalServerError;
import ro.certificate.manager.utils.ErrorMessageBundle;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Log4j2
public class FileUtils {

    private FileUtils() {
    }
    
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
            log.error(e);
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
