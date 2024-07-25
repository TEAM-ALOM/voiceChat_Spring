package Alom.voiceChat.utils;

import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

public class StringUtil {

    public static String nullToEmptyString (String value){
        return value != null ? value : "";
    }

    public static Boolean isNullOrEmpty(String value) {
        if (Objects.isNull(value) || "".equals(value)){
            return true;
        }
        return false;
    }
    public  static String getExtension(MultipartFile file){
        String fileName = file.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return extension;
    }
}
