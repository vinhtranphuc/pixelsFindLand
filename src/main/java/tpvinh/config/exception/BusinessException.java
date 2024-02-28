package tpvinh.config.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;
import lombok.Setter;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 4103740739658448375L;

    @Getter
    @Setter
    private String errorCode = "";

    public BusinessException() {
        this("BusinessException occurred");
    }

    public BusinessException(String message) {
        this(message, "", null);
    }

    public BusinessException(String message, String errorCode) {
        this(message, errorCode, null);
    }

    public BusinessException(String message, String errorCode, Throwable cause) {
        super(message);
        this.errorCode = errorCode;
    }
}
