package makao;

import java.util.Locale;

import processing.core.PApplet;
import processing.core.PImage;

public class Card implements Comparable<Card> {
	
	private boolean isLast = false;
	private boolean isHovered = false;
	private boolean isPlayable = false;
	
	public void setPlayable(boolean p){
		isPlayable = p;
	}
	
	public void setHovered(boolean p){
		isHovered = p;
	}
	
	private int posX;
	private int posY;
	
	public final int cardWidth = 17;
	public final int cardHeight = 97;
	public final int cardWidthLast = 72;
	
	private PImage cardImg;
	
	private Suit suit;
	private Rank rank;
	
	public Card(int s, int r, PApplet pa){
		Suit[] suitArr = Suit.values();
		suit = suitArr[s];
		
		Rank[] rankArr = Rank.values();
		rank = rankArr[r];
		
		String separator;
		if(System.getProperty("os.name").toLowerCase(Locale.ENGLISH).indexOf("win")>=0)
			separator = "\\";
		else
			separator = "/";
		
		cardImg = pa.loadImage("cards"+separator+suit.name()+rank.name()+".png", "png");
	}
	
	public Card(int s, int r){
		Suit[] suitArr = Suit.values();
		suit = suitArr[s];
		
		Rank[] rankArr = Rank.values();
		rank = rankArr[r];
		
		cardImg = null;
	}
	
	public void setXY(int a, int b){
		posX = a;
		posY = b;
	}
	
	public void draw(int x, int y, PApplet pa, boolean onHand){
		if(onHand){
			posX = x;
			posY = y;
		}
		
		pa.image(cardImg, posX, posY);
		
		if(onHand && isHovered){
				pa.strokeWeight(3);
				pa.noFill();
				
				if(isPlayable)
					pa.stroke(0,255,0);
				else
					pa.stroke(255,0,0);
				
				if(isLast)
					pa.rect(x, y, cardWidthLast+2, cardHeight+2);
				else
					pa.rect(x, y, cardWidth+2, cardHeight+2);
		}
	}
	
	public boolean isInside(int mX, int mY){
		if(isLast && mX>=posX && mX<posX+cardWidthLast && mY>=posY && mY<posY+cardHeight)
			return true;
		else if(mX>=posX && mX<posX+cardWidth && mY>=posY && mY<posY+cardHeight)
			return true;
		else
			return false;
	}
	
	public void makeNotLast(){
		isLast = false;
	}
	
	public void makeLast(){
		isLast = true;
	}
	
	public String toString(){
			return rank.name() + " of " + suit.name();
	}
	
	public Suit getSuit(){
		return suit;
	}
	
	public Rank getRank(){
		return rank;
	}
	
	public int compareTo(Card other){
		if(this.suit.compareTo(other.suit)!=0) return this.suit.compareTo(other.suit);
		if(this.rank.compareTo(other.rank)!=0) return this.rank.compareTo(other.rank);
		else return 0;
	}
	
	public boolean isFightable(){
		if(this.getRank() == Rank.bl2 || this.getRank() == Rank.bl3
				|| (this.getRank()==Rank.king && (this.getSuit()==Suit.pikes || this.getSuit()==Suit.hearts)))
			return true;
		else
			return false;
			
	}
	
	public boolean isQueenOfPikes(){
		if(this.getRank()==Rank.queen && this.getSuit()==Suit.pikes)
			return true;
		else
			return false;
	}
	
	public boolean fits(Card other){
		if(other.isQueenOfPikes())
			return true;
		if(this.fitsSuit(other) || this.fitsRank(other))
			return true;
		else
			return false;
	}
	
	public boolean fitsSuit(Card other){
		if(this.getSuit() == other.getSuit())
			return true;
		else
			return false;
	}
	
	public boolean fitsRank(Card other){
		if(this.getRank() == other.getRank())
			return true;
		else
			return false;
	}
	
	public boolean isCovered(Card[] cards, int mode){
		int covers=mode;
		for(int i=0; i<cards.length; i++)
			if(this.fitsSuit(cards[i]))
				covers++;
		
		if((cards.length<=5 && covers>1) || covers>2)
			return true;
		else
			return false;
	}
	
	public boolean isMultiple(Card[] cards, int mode){
		int covers=mode;
		for(int i=0; i<cards.length; i++)
			if(this.fitsRank(cards[i]))
				covers++;
		if(covers>1)
			return true;
		else
			return false;
	}
	
	public boolean isSpot(){
		if(!this.isFightable() && !this.isQueenOfPikes() && this.getRank()!=Rank.bl4 && 
				this.getRank()!=Rank.ace && this.getRank()!=Rank.jack)
			return true;
		else
			return false;
	}
	
	public int safetyClass(Card[] cardsA, Card[] cardsB, int mode){
		int gone = mode;
		for(int i=0; i<cardsA.length; i++)
			if(cardsA[i].isFightable() && cardsA[i].fits(this)) 
				gone++;
		for(int i=0; i<cardsB.length; i++)
			if(cardsB[i].isFightable() && cardsB[i].fits(this))
				gone++;
		
		//isQueenOfSpikes() check in fits() attempts only for sent argument, so QofP at the hand won't affect safety class. 
		
		if(this.getRank()==Rank.king && gone>=4) return 0;
		if(this.getRank()==Rank.king && gone>=3) return 1;
		if(this.getRank()==Rank.king && gone>=2) return 2;
		if(this.getRank()==Rank.king && gone>=0) return 3;
		
		if(this.getRank()!=Rank.king && (this.getSuit()==Suit.hearts || this.getSuit()==Suit.pikes) && gone>=6) return 0;
		if(this.getRank()!=Rank.king && (this.getSuit()==Suit.hearts || this.getSuit()==Suit.pikes) && gone>=4) return 1;
		if(this.getRank()!=Rank.king && (this.getSuit()==Suit.hearts || this.getSuit()==Suit.pikes) && gone>=2) return 2;
		if(this.getRank()!=Rank.king && (this.getSuit()==Suit.hearts || this.getSuit()==Suit.pikes) && gone>=0) return 3;
		
		if((this.getSuit()==Suit.tiles || this.getSuit()==Suit.clovers) && gone>=5) return 0;
		if((this.getSuit()==Suit.tiles || this.getSuit()==Suit.clovers) && gone>=3) return 1;
		if((this.getSuit()==Suit.tiles || this.getSuit()==Suit.clovers) && gone>=1) return 2;
		if((this.getSuit()==Suit.tiles || this.getSuit()==Suit.clovers) && gone>=0) return 3;
		
		return 3;
	}
}