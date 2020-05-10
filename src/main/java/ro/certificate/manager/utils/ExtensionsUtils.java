package ro.certificate.manager.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExtensionsUtils {

    @Value("#{'${file.supportedExtensions}'.split(';')}")
    protected List<String> supportedExtensionsFiles;

    public boolean isExtensionFileValid(String extension) {
        if (supportedExtensionsFiles != null && !supportedExtensionsFiles.isEmpty()) {
            return supportedExtensionsFiles.stream().anyMatch(e -> e.equalsIgnoreCase(extension));
        }
        return false;
    }
}
