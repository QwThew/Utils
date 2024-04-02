package utils.Gradient;

import java.io.Serial;

public class HomogeneousRainbowException extends RainbowException {

	@Serial
	private static final long serialVersionUID = -3883632693158928681L;
	
	public String getMessage() {
		return "Rainbow must have two or more colours.";
	}

}