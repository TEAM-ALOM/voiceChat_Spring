package Alom.voiceChat.service.file;

import Alom.voiceChat.dto.FileDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public interface FileService {

    FileDto uploadFile(MultipartFile file, String roomId);

    void deleteFileDir(String roomId);

    default File convertMultipartFileToFile (MultipartFile multipartFile,String tmpPath) throws IOException{
        File file = new File(tmpPath);

        if (file.createNewFile()){
            try(FileOutputStream fos = new FileOutputStream(file)){
                fos.write(multipartFile.getBytes());
            }
            return file;
        }
        throw new IOException();
    }

    ResponseEntity<byte[]> getObject(String fileName, String filePath) throws Exception;

    void uploadFileSizeCheck(MultipartFile file);
}
