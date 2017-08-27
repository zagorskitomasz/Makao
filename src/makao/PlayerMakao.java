package makao;

import processing.core.PApplet;

abstract public class PlayerMakao{
	protected int ID;
	protected Hand hand;
	private int staying;
	
	public PlayerMakao(Card[] cards, int id){
		this.hand = new Hand(cards);
		this.ID = id;
		this.hand.sort();
		this.staying = 0;
	}
	
	public PlayerMakao(){};
	
	public int getID(){
		return ID;
	}
	
	public boolean amIStaying(){
		return staying>0;
	}
	
	abstract public Suit callForSuit(Makao game);
	
	abstract public Rank callForRank(Makao game);
	
	abstract public void draw(PApplet pa);
	
	public void stayed(){
		staying--;
	}
	
	public void stay(int turns){
		staying += turns;
	}
	
	abstract Card playACard(Makao game, PlayerMakao[] players, int playerID);
	
	public boolean canIPlayIt(Table table, Card my, Card onTable){
		return false;
	}
	
	public void taking(Table table, Card taken, Makao game){}
	
	public int haveCards(){
		return hand.length();
	}
}