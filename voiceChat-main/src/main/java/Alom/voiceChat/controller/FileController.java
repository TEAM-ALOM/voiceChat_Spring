package Alom.voiceChat.controller;

import Alom.voiceChat.dto.FileDto;
import Alom.voiceChat.service.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@Slf4j
public class FileController {
    private final FileService fileService;


    @PostMapping("/upload")
    public FileDto uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam("roomId") String roomId) {
        FileDto uploadFile = fileService.uploadFile(file, roomId);
        log.info("최종 upload Data {}", uploadFile);

        return uploadFile;
    }

    @PostMapping("/download/{fileName}")
    public ResponseEntity<byte[]> download(
            @RequestParam("fileName") String fileName,
            @RequestParam("filePath") String filePath
    ){
        log.info("fileDir : fileName [{} : {}]", filePath, fileName);
        try{
            return fileService.getObject(fileName, filePath);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
