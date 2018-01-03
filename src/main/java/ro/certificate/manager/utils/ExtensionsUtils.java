package ro.certificate.manager.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ExtensionsUtils {

	@Value("#{'${file.supportedExtensions}'.split(';')}")
	protected List<String> supportedExtensionsFiles;

	public boolean isExtensionFileValid(String extension) {
		boolean validExtension = false;
		if (supportedExtensionsFiles != null && !supportedExtensionsFiles.isEmpty()) {
			for (String definedExtension : supportedExtensionsFiles) {
				if (definedExtension.equalsIgnoreCase(extension)) {
					validExtension = true;
					break;
				}
			}
		}
		return validExtension;
	}

	public List<String> getSupportedExtensionsFiles() {
		return supportedExtensionsFiles;
	}

	public void setSupportedExtensionsFiles(List<String> supportedExtensionsFiles) {
		this.supportedExtensionsFiles = supportedExtensionsFiles;
	}

}
