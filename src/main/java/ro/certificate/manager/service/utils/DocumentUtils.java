package ro.certificate.manager.service.utils;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

@Component
public class DocumentUtils {

    @Autowired
    private FolderUtils folderUtils;

    public String saveDocumentOnDisk(MultipartFile documentToSign, String userID) throws Exception {
        File documentFile = folderUtils.createDocumentFile(documentToSign, userID);
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(documentFile))) {
            IOUtils.copyLarge(documentToSign.getInputStream(), outputStream);
        }

        return documentFile.getName();
    }
}
