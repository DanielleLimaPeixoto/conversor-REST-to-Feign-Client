package exceptions;

public class ProductNotFoundException extends Exception{
	private static final long serialVersionUID = 1L;
	
	public ProductNotFoundException(String pesel) {
		super("No such customer: " + pesel);
	}
}
