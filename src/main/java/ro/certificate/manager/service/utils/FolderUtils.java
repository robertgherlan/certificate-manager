package ro.certificate.manager.service.utils;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ro.certificate.manager.utils.ConfigurationUtils;
import ro.certificate.manager.utils.ErrorMessageBundle;
import ro.certificate.manager.utils.ExtensionsUtils;
import ro.certificate.manager.utils.StringGeneratorUtils;

import java.io.File;
import java.io.FileNotFoundException;

@Component
public class FolderUtils {

	@Autowired
	private ConfigurationUtils configurationUtils;

	@Autowired
	private ExtensionsUtils extensionsUtils;

	private static final String DOCUMENTS_FOLDER_NAME = "documents";

	private static final String KEYSTORE_FOLDER_NAME = "keystores";

	private static final String SIGNATURES_FOLDER_NAME = "signatures";

	private File getUserFolder(String userID) {
		File file = new File(configurationUtils.getUserFilesFolder());
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}

		File userFolder = new File(file, userID);
		if (!userFolder.exists() && !userFolder.isDirectory()) {
			userFolder.mkdir();
		}

		return userFolder;
	}

	public File getDocumentsFolderFile(String userID) {
		File userFolder = getUserFolder(userID);
		File documentsFile = new File(userFolder, DOCUMENTS_FOLDER_NAME);
		if (!documentsFile.exists() && !documentsFile.isDirectory()) {
			documentsFile.mkdir();
		}

		return documentsFile;
	}

	public File getDocumentFile(String userID, String documentName) throws FileNotFoundException {
		File userDocumentFolder = getDocumentsFolderFile(userID);
		File documentFile = new File(userDocumentFolder, documentName);
		if (!documentFile.exists()) {
			throw new FileNotFoundException("Not found.");
		}
		return documentFile;
	}

	public File getSignaturesFolderFile(String userID) {
		File userFolder = getUserFolder(userID);
		File signaturesFile = new File(userFolder, SIGNATURES_FOLDER_NAME);
		if (!signaturesFile.exists() && !signaturesFile.isDirectory()) {
			signaturesFile.mkdir();
		}

		return signaturesFile;
	}

	public File getKeystoresFolderFile(String userID) {
		File userFolder = getUserFolder(userID);
		File documentsFile = new File(userFolder, KEYSTORE_FOLDER_NAME);
		if (!documentsFile.exists() && !documentsFile.isDirectory()) {
			documentsFile.mkdir();
		}

		return documentsFile;
	}

	public String getFileNameForWindows(String fileName) {
		fileName = fileName.replace(".", "");
		fileName = fileName.replace("/", "");
		fileName = fileName.replace("\\", "");
		fileName = fileName.replace("*", "");
		fileName = fileName.replace("?", "");
		fileName = fileName.replace("<", "");
		fileName = fileName.replace(">", "");
		fileName = fileName.replace("|", "");

		return fileName;
	}

	private String getExtension(String fileName) throws Exception {
		String extension = FilenameUtils.getExtension(fileName);
		if (!extensionsUtils.isExtensionFileValid(extension)) {
			throw new Exception(ErrorMessageBundle.INVALID_FILE_EXTENSION);
		}

		return extension;
	}

	private String removeExtension(String fileName, String extension) {
		return getFileNameForWindows(fileName.substring(0, fileName.indexOf(extension)));
	}

	public String removeExtension(String fileName) throws Exception {
		String extension = getExtension(fileName);
		return getFileNameForWindows(fileName.substring(0, fileName.indexOf(extension)));
	}

	public String generateFileName(String fileName, boolean removeExtension) throws Exception {
		if (removeExtension) {
			fileName = removeExtension(fileName);
		}

		fileName = getFileNameForWindows(fileName);
		return fileName + "_" + System.currentTimeMillis() + "_" + StringGeneratorUtils.generateRandomFileName();
	}

	public File createDocumentFile(MultipartFile documentToSign, String userID) throws Exception {
		String originalFileName = documentToSign.getOriginalFilename();
		String extension = getExtension(originalFileName);
		String fileName = removeExtension(originalFileName, extension);
		String documentFileName = generateFileName(fileName, false) + "." + extension;
		File documentFile = new File(getDocumentsFolderFile(userID), documentFileName);
		if (documentFile.exists() && documentFile.isFile()) {
			throw new Exception("Document already exist.");
		}

		return documentFile;
	}

}
