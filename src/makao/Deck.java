package makao;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import processing.core.PApplet;

public class Deck{
	protected Card[] cards;
	
	public Deck(PApplet pa, int decks){
		cards = new Card[52*decks];
		for(int d=0; d<decks; d++){
			for(int i=0+(52*d); i<52+(52*d); i++)
				cards[i] = new Card(i%4, i%13, pa);
		}
		this.sort();
	}
	
	public Deck(Card[] cards){
		this.cards = cards;
	}
	
	public void sort(){
		Arrays.sort(this.cards, Collections.reverseOrder());
		for(int i=0; i<cards.length-1; i++)
			cards[i].makeNotLast();
		cards[cards.length-1].makeLast();
	}
	
	public Card[][] giveOut(int players, int leftOnTable){
		if((this.cards.length-leftOnTable)%players!=0) {
			System.out.println("You can't give out cards this way!");
			return null;
		}
		
		Card[][] table;
		int onHand = (this.cards.length-leftOnTable)/players;
		table = new Card[players+1][];
		
		for(int i=0; i<players; i++)
			table[i] = new Card[onHand];
		table[players] = new Card[leftOnTable];

		int counter=0;
		for(int i=0; i<players; i++)
			for(int j=0; j<onHand; j++, counter++)
				table[i][j] = this.cards[counter];

		if(leftOnTable>0)
		for(int i = counter, j = 0; i<this.cards.length; i++, j++)
			table[players][j] = this.cards[i];
		
		return table;
	}
	
	public void shuffle(int strength){
		Random gen = new Random();
		for(int i=0; i<strength; i++){
			int a = gen.nextInt(this.cards.length);
			int b = gen.nextInt(this.cards.length);
			Card temp = this.cards[a];
			this.cards[a]=this.cards[b];
			this.cards[b]=temp;
		}
	}
	
	public Integer whereIs(Suit s, Rank r){
		Card temp = new Card(s.ordinal(), r.ordinal());
		for(int i=0; i<this.cards.length; i++)
			if(temp.compareTo(this.cards[i])==0) return i;
		return null;
	}
	
	public Card[] showCards(){
		return cards;
	}
}