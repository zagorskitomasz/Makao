package makao;

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PImage;

public class Table extends Hand{
	private Card[] pile;
	private Card takenCard;
	private Suit calledSuit;
	private Rank calledRank;
	private int penaltyAmount;
	private int standAmount;
	private PImage deck;
	private boolean deckHovered=false;
	private boolean showTaken=false;
	private Makao g;
	
	public Table(Card[] cards, PApplet pa){
		super(cards);
		
		String separator;
		if(System.getProperty("os.name").toLowerCase(Locale.ENGLISH).indexOf("win")>=0)
			separator = "\\";
		else
			separator = "/";
		
		deck = pa.loadImage("cards"+separator+"deck.png", "png");
		pile = new Card[0];
		calledSuit = null;
		calledRank = null;
		penaltyAmount = 0;
		standAmount = 0;
		g = (Makao) pa;
	}
	
	public void setCalledSuit(Suit s){
		calledSuit = s;
	}
	
	public void setCalledRank(Rank r){
		calledRank = r;
	}
	
	public Suit getCalledSuit(){
		return calledSuit;
	}
	
	public Rank getCalledRank(){
		return calledRank;
	}
	
	public void increasePenalty(int p){
		penaltyAmount += p;
	}
	
	public int getPenaltyAmount(){
		return penaltyAmount;
	}
	
	public void resetPenalty(){
		penaltyAmount = 0;
	}
	
	public void increaseStand(int s){
		standAmount += s;
	}
	
	public int getStandAmount(){
		return standAmount;
	}
	
	public void resetStand(){
		standAmount = 0;
	}
	
	public void hoverDeck(boolean h){
		deckHovered=h;
	}
	
	public void showTaken(boolean s){
		showTaken=s;
		if(s==true)
			hoverDeck(false);
	}
	
	public Card getTaken(){
		return takenCard;
	}
	
	public void draw(Makao pa){
		pa.image(deck, 580, 210);
		if(deckHovered){
			pa.noFill();
			pa.stroke(0,255,0);
			pa.rect(580, 210, 72+2, 97+2);
		}
		if(showTaken){
			takenCard.makeLast();
			takenCard.draw(610, 250, pa, true);
		}
		for(int i=0; i<pile.length; i++)
			pile[i].draw(0, 0, pa, false);
		
		pa.textSize(30);
		pa.stroke(255,255,255);
		pa.fill(255,255,255);
		if(!pa.mode.equals("takingCard") && !pa.mode.equals("staying") && calledSuit!=null){
			pa.text("Called card suit: ", 500, 610);
			pa.image(calledSuit.getSign(pa), 650, 590);
		}
		
		String cR = "";
		if(calledRank!=null){
			if(calledRank==Rank.bl5)
				cR = "5";
			if(calledRank==Rank.bl6)
				cR = "6";
			if(calledRank==Rank.bl7)
				cR = "7";
			if(calledRank==Rank.bl8)
				cR = "8";
			if(calledRank==Rank.bl9)
				cR = "9";
			if(calledRank==Rank.bl10)
				cR = "10";
			if(calledRank==Rank.queen)
				cR = "Queen";
			if(calledRank==Rank.king)
				cR = "King";
		}
		if(!pa.mode.equals("takingCard") && !pa.mode.equals("staying") && calledRank!=null) pa.text("Called card rank: " + cR, 500, 610);
		if(!pa.mode.equals("takingCard") && !pa.mode.equals("staying") && penaltyAmount>0) pa.text("Total penalty: " + penaltyAmount, 500, 610);
		if(!pa.mode.equals("takingCard") && !pa.mode.equals("staying") && standAmount>0) pa.text("Possible loss of turns: " + standAmount, 500, 610);
	}
	
	public Card drawACard(){
		if(this.cards.length>0){
			takenCard = throwACard(this.cards.length-1);
			g.take.rewind();
			g.take.play();
			return takenCard;
		}
		else{
			if((this.cards.length+this.pile.length)>1){
				Card temp = this.pile[this.pile.length-1];
				this.cards = Arrays.copyOfRange(this.pile, 0, this.pile.length-1);
				this.shuffle(1000);
				this.pile = new Card[1];
				pile[0] = temp;
				takenCard = throwACard(this.cards.length-1);
				g.take.rewind();
				g.take.play();
				return takenCard;
			}
			else{
				return null;
			}
		}
	}
	
	public Card[] drawACard(int amount){
		Card[] temp = new Card[amount];
		for(int i=0; i<amount; i++){
			temp[i] = this.drawACard();
			if(temp[i]==null) return null;
		}
		return temp;
	}
	
	public Card[] cardsGone(){
		return pile;
	}
	
	public Card whatsThere(){
		return this.pile[this.pile.length-1];
	}
	
	public void putACard(Card c){
		Random gen = new Random();
		c.setXY(390+gen.nextInt(60), 170+gen.nextInt(80));
		this.pile = Arrays.copyOfRange(this.pile, 0, this.pile.length+1);
		this.pile[this.pile.length-1] = c;
		g.put.rewind();
		g.put.play();
	}
	
	public void putACard(Card[] cArr){
		for(Card c : cArr)
			this.putACard(c);
	}
	
	public void layOff(){
		do {
			this.putACard(this.drawACard());
		}
		while(this.whatsThere().isFightable() || this.whatsThere().getRank()==Rank.bl4 
				|| this.whatsThere().getRank()==Rank.ace || this.whatsThere().isQueenOfPikes());		
	}
	
	
	public void cardEffect(Card thrown, PlayerMakao player, Makao game){
		this.setCalledSuit(null);
		this.setCalledRank(null);
		
		if(thrown.getRank()==Rank.bl2) this.increasePenalty(2);
		else if(thrown.getRank()==Rank.bl3) this.increasePenalty(3);
		else if(thrown.getRank()==Rank.king && thrown.getSuit()==Suit.hearts) this.increasePenalty(5);
		else if(thrown.getRank()==Rank.king && thrown.getSuit()==Suit.pikes) this.increasePenalty(5);
		else if(thrown.getRank()==Rank.bl4) this.increaseStand(1);
		else if(thrown.isQueenOfPikes()){
			this.resetPenalty();
			this.resetStand();
			this.setCalledSuit(null);
			this.setCalledRank(null);
		}
		else if(thrown.getRank()==Rank.ace) this.setCalledSuit(player.callForSuit(game));
		else if(thrown.getRank()==Rank.jack) this.setCalledRank(player.callForRank(game));
	}
}