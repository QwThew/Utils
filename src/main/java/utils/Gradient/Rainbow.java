package utils.Gradient;

import java.util.ArrayList;
import java.util.Hashtable;

public class Rainbow {
	
	private double minNum;
	private double maxNum;
	private String[] colours;
	private ArrayList<ColourGradient> colourGradients;

	public static ArrayList<String> createGradient(int count, String[] colours) {
		Rainbow rainbow = new Rainbow();

		try {
			rainbow.setNumberRange(1, count);
			rainbow.setSpectrum(colours);
		} catch (HomogeneousRainbowException | InvalidColourException | NumberRangeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();

		}

		ArrayList<String> hexCodes = new ArrayList<>();
		for (int i = 1; i <= count; i++) {
			hexCodes.add("#" + rainbow.colourAt(i));
		}
		return hexCodes;
	}

	/**
	 * Constructor. By default, the number range is from 0 to 100, and the spectrum is a rainbow.
	 */
	public Rainbow() {
		try {
			minNum = 0;
			maxNum = 100;
			colours = new String[]{"red", "yellow", "lime", "blue"};
			setSpectrum(colours);
		}
		// These exceptions are theoretically impossible, so rethrow as unchecked exceptions
		catch (HomogeneousRainbowException e) {
			throw new AssertionError(e);					
		} catch (InvalidColourException e) {
			throw new AssertionError(e);
		}

	}
	
	/**
	 * Returns the hex colour corresponding to the number. If number is out of range, 
	 * it returns the appropriate hex colour corresponding to either the minNumber or maxNumber.
	 * @param number The number for which you want to find the corresponding colour
	 * @return The corresponding colour represented as a HTML RGB hexidecimal String
	 */
	public String colourAt(double number) {
		if (colourGradients.size() == 1) {
			return colourGradients.get(0).colourAt(number);
		} else {
			double segment = (maxNum - minNum)/(colourGradients.size());
			int index = (int) Math.min(Math.floor((Math.max(number, minNum) - minNum)/segment), colourGradients.size() - 1);
			return colourGradients.get(index).colourAt(number);
		}		
	}	
	
	/**
	 * Sets the spectrum of the Rainbow object. By default, the spectrum is a rainbow. 
	 * You must have a minimum of two colours, but you can specify more than two colours. 
	 * Colours can be in the form "red", "ff0000", or "#ff0000". 
	 * For example, <code>rainbow.setSpectrum("red", "yellow", "white");</code>
	 * makes the "Rainbow" a colour gradient from red to yellow to white. 
	 * @param spectrum Two or more Strings representing HTML colours,
	 * or pass in a whole String array of length 2 or greater
	 * @throws HomogeneousRainbowException if there is less than two arguments
	 * @throws InvalidColourException if one of the arguments is an invalid colour
	 */
	public void setSpectrum (String ... spectrum) throws HomogeneousRainbowException, InvalidColourException {
		try {
			if (spectrum.length < 2) {
				throw new HomogeneousRainbowException();
			} else {
				double increment = (maxNum - minNum)/(spectrum.length - 1);
				ColourGradient firstGradient = new ColourGradient();
				firstGradient.setGradient(spectrum[0], spectrum[1]);
				firstGradient.setNumberRange(minNum, minNum + increment);
				
				colourGradients = new ArrayList<ColourGradient>();
				colourGradients.add(firstGradient);
				
				for (int i = 1; i < spectrum.length - 1; i++) {
					ColourGradient colourGradient = new ColourGradient();
					colourGradient.setGradient(spectrum[i], spectrum[i + 1]);
					colourGradient.setNumberRange(minNum + increment * i, minNum + increment * (i + 1)); 
					colourGradients.add(colourGradient); 
				}
	
				colours = spectrum;
			}
		}
		// This exception is theoretically impossible, so rethrow as unchecked exception			
		catch (NumberRangeException e) {
			throw new RuntimeException(e);
		}		
	}
	
	/**
	 * Sets the number range of the Rainbow object. By default, it is 0 to 100.
	 * @param minNumber The minimum number of the number range
	 * @param maxNumber The maximum number of the number range
	 * @throws NumberRangeException if minNumber is greater than maxNumber
	 */
	public void setNumberRange(double minNumber, double maxNumber) throws NumberRangeException
	{
		try {
			if (maxNumber > minNumber) {
				minNum = minNumber;
				maxNum = maxNumber;				
				setSpectrum(colours); 
			} else {
				throw new NumberRangeException(minNumber, maxNumber);
			}
		}
		// These exceptions are theoretically impossible, so rethrow as unchecked exceptions			
		catch (HomogeneousRainbowException e) {
			throw new RuntimeException(e);
		} catch (InvalidColourException e) {
			throw new RuntimeException(e);
		}		
	}	
	
	/**
	 * Same as colourAt(double number)
	 */
	public String colorAt(double number) {
		return colourAt(number);
	}

}

class ColourGradient {
	
	private int[] startColour = {0xff, 0x00, 0x00};
	private int[] endColour = {0x00, 0x00, 0xff};
	private double minNum = 0;
	private double maxNum = 100;

	private static Hashtable<String, int[]> htmlColors;
	static {
			htmlColors = new Hashtable<String, int[]>();
			htmlColors.put("black", new int[]{0x00, 0x00, 0x00});
			htmlColors.put("navy", new int[]{0x00, 0x00, 0x80});		
			htmlColors.put("blue", new int[]{0x00, 0x00, 0xff});			
			htmlColors.put("green", new int[]{0x00, 0x80, 0x00});
			htmlColors.put("teal", new int[]{0x00, 0x80, 0x80});			
			htmlColors.put("lime", new int[]{0x00, 0xff, 0x00});			
			htmlColors.put("aqua", new int[]{0x00, 0xff, 0xff});			
			htmlColors.put("maroon", new int[]{0x80, 0x00, 0x00});			
			htmlColors.put("purple", new int[]{0x80, 0x00, 0x80});				
			htmlColors.put("olive", new int[]{0x80, 0x80, 0x00});			
			htmlColors.put("grey", new int[]{0x80, 0x80, 0x80});
			htmlColors.put("gray", new int[]{0x80, 0x80, 0x80});
			htmlColors.put("silver", new int[]{0xc0, 0xc0, 0xc0});				
			htmlColors.put("red", new int[]{0xff, 0x00, 0x00});			
			htmlColors.put("fuchsia", new int[]{0xff, 0x00, 0xff});		
			htmlColors.put("orange", new int[]{0xff, 0x80, 0x00});				
			htmlColors.put("yellow", new int[]{0xff, 0xff, 0x00});			
			htmlColors.put("white", new int[]{0xff, 0xff, 0xff});				
	}

	public String colourAt(double number) {
		return 	formatHex(calcHex(number, startColour[0], endColour[0]))
			+	formatHex(calcHex(number, startColour[1], endColour[1]))
			+	formatHex(calcHex(number, startColour[2], endColour[2]));
	}
	
	private int calcHex(double number, int channelStart, int channelEnd) {
		double num = number;
		if (num < minNum) {
			num = minNum;
		}
		if (num > maxNum) {
			num = maxNum;
		} 
		double numRange = maxNum - minNum; 
		double cPerUnit = (channelEnd - channelStart)/numRange;
		return (int) Math.round(cPerUnit * (num - minNum) + channelStart);
	}
	
	private String formatHex (int val) 
	{
		String hex = Integer.toHexString(val);
		if (hex.length() == 1) {
			return '0' + hex;
		} else {
			return hex;
		}
	} 
	
	public void setNumberRange(double minNumber, double maxNumber) throws NumberRangeException{
		if (maxNumber > minNumber) {
			minNum = minNumber;
			maxNum = maxNumber;
		} else {
			throw new NumberRangeException(minNumber, maxNumber);
		}
	}
	
	public void setGradient (String colourStart, String colourEnd) throws InvalidColourException {
		startColour = getHexColour(colourStart);
		endColour = getHexColour(colourEnd);		
	}
	
	private int[] getHexColour(String s) throws InvalidColourException {
		if (s.matches("^#?[0-9a-fA-F]{6}$")){
			return rgbStringToArray(s.replace("#", ""));
		} else {
			int[] rgbArray = htmlColors.get(s.toLowerCase());
			if (rgbArray == null) {
				throw new InvalidColourException(s);
			} else {
				return rgbArray;
			}
		}
	}
	
	private int[] rgbStringToArray(String s){
		int red = Integer.parseInt(s.substring(0,2), 16);
		int green = Integer.parseInt(s.substring(2,4), 16);
		int blue = Integer.parseInt(s.substring(4,6), 16);
		return new int[]{red, green, blue};		
	}
	
}