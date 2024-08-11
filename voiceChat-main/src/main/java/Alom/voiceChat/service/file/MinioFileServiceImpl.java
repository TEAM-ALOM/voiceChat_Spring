package Alom.voiceChat.service.file;

import Alom.voiceChat.dto.FileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioFileServiceImpl implements FileService{

    private final Minio
    @Override
    public FileDto uploadFile(MultipartFile file, String roomId) {
        return null;
    }

    @Override
    public void deleteFileDir(String roomId) {

    }

    @Override
    public ResponseEntity<byte[]> getObject(String fileName, String filePath) throws Exception {
        return null;
    }

    @Override
    public void uploadFileSizeCheck(MultipartFile file) {

    }
}
