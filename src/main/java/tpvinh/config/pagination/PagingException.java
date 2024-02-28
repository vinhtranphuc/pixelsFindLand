package tpvinh.config.pagination;

public class PagingException extends Exception {

	private static final long serialVersionUID = 1L;

	public PagingException(String message) {
    	this(message, null);
    }

    public PagingException(String message, Throwable cause) {
        super(message, cause);
    }
}
