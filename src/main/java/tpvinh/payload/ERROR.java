package tpvinh.payload;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import lombok.Data;
import tpvinh.config.exception.BusinessException;

@Data
public class ERROR {

    private long timestamp;
    private int status;
    private String error;
    private String errorCode;
    private String path;

    public ERROR(Exception exception, HttpStatus httpStatus, String path) {
        this.timestamp = System.currentTimeMillis();
        this.status = httpStatus.value();
        this.error = exception.getMessage();
        if(exception instanceof BusinessException) {
            String errorCode = ((BusinessException) exception).getErrorCode();
            this.errorCode = StringUtils.isEmpty(errorCode)?exception.getClass().getSimpleName():errorCode;
        } else {
            this.errorCode = exception.getClass().getSimpleName();
        }
        this.path = path;
    }

    public ERROR(Exception exception, String errorCode, HttpStatus httpStatus, String path) {
        this.timestamp = System.currentTimeMillis();
        this.status = httpStatus.value();
        this.error = exception.getMessage();
        this.errorCode = errorCode;
        this.path = path;
    }

    public ERROR(String error, HttpStatus httpStatus, String path) {
        this.timestamp = System.currentTimeMillis();
        this.status = httpStatus.value();
        this.error = error;
        this.path = path;
    }

    public ERROR(String path) {
        this.timestamp = System.currentTimeMillis();
        this.status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        this.error = "INTERNAL ERROR";
        this.path = path;
    }
}
