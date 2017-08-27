package makao;

import processing.core.PApplet;
import processing.core.PImage;

public enum Suit {
	clovers, tiles, hearts, pikes;
	
	public PImage getSign(PApplet pa){
		return pa.loadImage("cards\\"+this.name()+".png", "png");
	}
}