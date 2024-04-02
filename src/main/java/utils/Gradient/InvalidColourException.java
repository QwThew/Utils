package utils.Gradient;

import java.io.Serial;

public class InvalidColourException extends RainbowException {

	@Serial
	private static final long serialVersionUID = 5801441252925805756L;
	
	private final String nonColor;
	
	public InvalidColourException(String nonColour){
		super();
		nonColor = nonColour;
	}
	
	public String getMessage() {
		return nonColor + " is not a valid colour.";
	}
	
}