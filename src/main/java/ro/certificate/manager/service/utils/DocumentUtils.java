package ro.certificate.manager.service.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class DocumentUtils {

	@Autowired
	private FolderUtils folderUtils;

	public String saveDocumentOnDisk(MultipartFile documentToSign, String userID) throws Exception {
		File documentFile = folderUtils.createDocumentFile(documentToSign, userID);
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(documentFile);
			IOUtils.copyLarge(documentToSign.getInputStream(), outputStream);
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (Exception e) {
				// Do nothing.
			}
		}

		return documentFile.getName();
	}
}
