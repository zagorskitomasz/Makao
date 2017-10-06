package makao;

import java.util.Locale;

import processing.core.PApplet;
import processing.core.PImage;

public enum Suit {
	clovers, tiles, hearts, pikes;
	
	public PImage getSign(PApplet pa){
		String separator;
		if(System.getProperty("os.name").toLowerCase(Locale.ENGLISH).indexOf("win")>=0)
			separator = "\\";
		else
			separator = "/";
		
		return pa.loadImage("cards"+separator+this.name()+".png", "png");
	}
}