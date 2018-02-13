package utils;

public class PhysicsException extends Exception {

	private final String message;
	private final Throwable cause;
	
	public PhysicsException(){
		this.message = null;
		this.cause = null;
	}
	
	public PhysicsException(String message){
		this.message = message;
		this.cause = null;
	}
	
	public PhysicsException(Throwable cause){
		this.message = null;
		this.cause = cause;
	}
	
	public PhysicsException(String message, Throwable cause){
		this.message = message;
		this.cause = cause;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public Throwable getCause() {
		return this.cause;
	}
	
	private static final long serialVersionUID = 1L;

}
