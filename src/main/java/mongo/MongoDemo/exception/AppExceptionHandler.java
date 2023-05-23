package mongo.MongoDemo.exception;

import mongo.MongoDemo.dto.CustomError;
import mongo.MongoDemo.dto.ServerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ServerResponse<Void>> handleException(final Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ServerResponse.error(e.getMessage(),"NA"));
    }

    @ExceptionHandler(value = MongoDemoException.class)
    public ResponseEntity<ServerResponse<Void>> handleMongoDemoException(final MongoDemoException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ServerResponse.error(e.getMessage(),e.getCode()));
    }
}
