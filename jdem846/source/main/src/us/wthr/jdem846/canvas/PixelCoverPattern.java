package us.wthr.jdem846.canvas;

public class PixelCoverPattern
{
	public static final byte NULL_COVER =        (byte) 0x00;	// 00000000		Null (0.0%) Cover
	public static final byte FULL_COVER =        (byte) 0x81;	// 10000001		Full (100.0%) Cover
	
	public static final byte LEFT_TOP_BIAS =     (byte) 0x80;	// 10000000		Left Bias
	public static final byte RIGHT_BOTTOM_BIAS = (byte) 0x01;	// 00000001		Right Bias
	
	public static final byte LEFT_TOP_0125 =     (byte) 0x80;	// 10000000		Left/Top Bias, 12.5%
	public static final byte LEFT_TOP_0250 =     (byte) 0xC0;	// 11000000		Left/Top Bias, 25.0%
	public static final byte LEFT_TOP_0375 =     (byte) 0xA0;	// 10100000		Left/Top Bias, 37.5%
	public static final byte LEFT_TOP_0500 =     (byte) 0x90;	// 10010000		Left/Top Bias, 50.0%
	public static final byte LEFT_TOP_0625 =     (byte) 0x88;	// 10001000		Left/Top Bias, 62.5%
	public static final byte LEFT_TOP_0750 =     (byte) 0x84;	// 10000100		Left/Top Bias, 75.0%
	public static final byte LEFT_TOP_0875 =     (byte) 0x82;	// 10000010		Left/Top Bias, 87.5%
	
	public static final byte RIGHT_BOTTOM_0125 = (byte) 0x01;	// 00000001		Right/Bottom Bias, 12.5%
	public static final byte RIGHT_BOTTOM_0250 = (byte) 0x03;	// 00000011		Right/Bottom Bias, 25.0%
	public static final byte RIGHT_BOTTOM_0375 = (byte) 0x05;	// 00000101		Right/Bottom Bias, 37.5%
	public static final byte RIGHT_BOTTOM_0500 = (byte) 0x09;	// 00001001		Right/Bottom Bias, 50.0%
	public static final byte RIGHT_BOTTOM_0625 = (byte) 0x11;	// 00010001		Right/Bottom Bias, 62.5%
	public static final byte RIGHT_BOTTOM_0750 = (byte) 0x21;	// 00100001		Right/Bottom Bias, 75.0%
	public static final byte RIGHT_BOTTOM_0875 = (byte) 0x41;	// 01000001		Right/Bottom Bias, 87.5%
	
	
	public static double getCoverage(byte sideBias)
	{
		switch (sideBias) {
		case NULL_COVER:
			return 0.0;
		case LEFT_TOP_0125:
		case RIGHT_BOTTOM_0125:
			return 0.125;
		case LEFT_TOP_0250:
		case RIGHT_BOTTOM_0250:
			return 0.250;
		case LEFT_TOP_0375:
		case RIGHT_BOTTOM_0375:
			return 0.375;
		case LEFT_TOP_0500:
		case RIGHT_BOTTOM_0500:
			return 0.500;
		case LEFT_TOP_0625:
		case RIGHT_BOTTOM_0625:
			return 0.625;
		case LEFT_TOP_0750:
		case RIGHT_BOTTOM_0750:
			return 0.750;
		case LEFT_TOP_0875:
		case RIGHT_BOTTOM_0875:
			return 0.875;
		case FULL_COVER:
		default:
			return 1.0;
		}
	}
	
	public static byte getPattern(byte sideBias, double cover)
	{
		byte pattern = NULL_COVER;
		
		
		/*if (cover <= 0.0625) {
			pattern = NULL_COVER;
		} else */if (cover > 0/*(0.0625*/ && cover <= 0.1875) {
			pattern = (sideBias == LEFT_TOP_BIAS) ? LEFT_TOP_0125 : RIGHT_BOTTOM_0125;
		} else if (cover > 0.1875 && cover <= 0.3125) {
			pattern = (sideBias == LEFT_TOP_BIAS) ? LEFT_TOP_0250 : RIGHT_BOTTOM_0250;
		} else if (cover > 0.3125 && cover <= 0.4375) {
			pattern = (sideBias == LEFT_TOP_BIAS) ? LEFT_TOP_0375 : RIGHT_BOTTOM_0375;
		} else if (cover > 0.4375 && cover <= 0.5625) {
			pattern = (sideBias == LEFT_TOP_BIAS) ? LEFT_TOP_0500 : RIGHT_BOTTOM_0500;
		} else if (cover > 0.5625 && cover <= 0.6875) {
			pattern = (sideBias == LEFT_TOP_BIAS) ? LEFT_TOP_0625 : RIGHT_BOTTOM_0625;
		} else if (cover > 0.6875 && cover <= 0.8125) {
			pattern = (sideBias == LEFT_TOP_BIAS) ? LEFT_TOP_0750 : RIGHT_BOTTOM_0750;
		} else if (cover > 0.8125 && cover <= 0.9575) {
			pattern = (sideBias == LEFT_TOP_BIAS) ? LEFT_TOP_0875 : RIGHT_BOTTOM_0875;
		} else {
			pattern = FULL_COVER;
		}
		
		return pattern;
	}
	
	
	
	
}
