package tpvinh.config.aop;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import tpvinh.config.exception.BusinessException;
import tpvinh.config.exception.ResourceNotFoundException;
import tpvinh.config.pagination.PagingException;
import tpvinh.payload.ERROR;

@Controller
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private String uri = "";

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    protected ResponseEntity<Object> handleNotFound(ResourceNotFoundException exception, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        uri = getRequestUri(request);
        return ResponseEntity.status(status).body(new ERROR(exception, status, uri));
    }

    @ExceptionHandler(value = {PagingException.class})
    protected ResponseEntity<Object> handlePaging(PagingException exception, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        uri = getRequestUri(request);
        return ResponseEntity.status(status).body(new ERROR(exception, status, uri));
    }

    @ExceptionHandler(value = {BusinessException.class})
    protected ResponseEntity<Object> handleBussinessException(Exception exception, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        uri = getRequestUri(request);
        return ResponseEntity.status(status).body(new ERROR(exception, status, uri));
    }

    private String getRequestUri(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI().toString();
    }
}
