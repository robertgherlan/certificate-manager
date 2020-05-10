package ro.certificate.manager.service.utils;


public class StringUtils {

    private StringUtils() {
    }

    public static String substring(String content, String startMark, String endMark) {
        String substring = null;
        if (content != null && !content.trim().isEmpty()) {
            int start = content.indexOf(startMark);
            if (start != -1) {
                start = start + startMark.length();
                int end = content.indexOf(endMark);
                if (end > 0 && end > start) {
                    substring = content.substring(start, end);
                }
            }
        }

        return substring;
    }
}
