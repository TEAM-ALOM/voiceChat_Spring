package Alom.voiceChat.controller;

import jakarta.servlet.http.HttpServletRequest;
import jdk.jfr.Frequency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.management.relation.RelationNotFoundException;
import java.net.UnknownHostException;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
public class ExceptionController {

    private static final Logger log = LoggerFactory.getLogger(ExceptionController.class);

    @ResponseStatus(HttpStatus.UNAUTHORIZED) //401
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    public String unauthorizedException(Exception e){
        this.printErrorLog(e);

        Map<String, String> result = new HashMap<>();
        result.put("code", "500");
        result.put("message","Required header is missing :: " +e.getMessage());
        return "error/403";
    }


    public static class UnauthorizedException extends RuntimeException{
        public UnauthorizedException(String message){
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
    public @ResponseBody String internalServerError(Exception e){
        this.printErrorLog(e);
        return "error/500";
    }

    public static class InternalServerError extends RuntimeException{
        public InternalServerError(String message){
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(RelationNotFoundException.class)
    @ResponseBody
    public Map<String,String> resourceNotFoundException(Exception e){
        this.printErrorLog(e);

        Map<String ,String> result = new HashMap<>();
        result.put("code","404");
        result.put("message", "there is no resource");
        return result;
    }

    public static class ResourceNotFoundException extends RuntimeException{
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    @ExceptionHandler({AccessForbiddenException.class, AccessDeniedException.class , UnknownHostException.class})
    public String handleAccessException(Exception e, HttpServletRequest request){
        this.printErrorLog(e);
        request.setAttribute("error_message", e.getMessage());
        return "error/403";
    }

    public static class AccessForbiddenException extends RuntimeException{
        public AccessForbiddenException(String message){
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public @ResponseBody Map<String, String> fileSizeException(Exception e){
        this.printErrorLog(e);
        Map<String , String > result =new HashMap<>();
        result.put("code", "40013");
        result.put("message", "File Extension Error");
        return result;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FileExtensionException.class)
    public @ResponseBody Map<String,String> fileExtensionException (Exception e){
        this.printErrorLog(e);
        Map<String, String> result = new HashMap<>();
        result.put("code", "40022");
        result.put("message", "File Extension");
        return result;
    }

    public static class FileExtensionException extends RuntimeException{
        public FileExtensionException(String message){
            super(message);
        }
    }


    public static class AccessDeniedException extends RuntimeException {
        public AccessDeniedException(String message) {
            super(message);
        }
    }



    private void printErrorLog(Exception e){
        log.error(">>>>>>> "+e.getMessage());
        if (Objects.nonNull(e.getCause())){
            log.error(">>>>>>> "+ e.getCause().toString());
        }
        e.printStackTrace();
    }
}
