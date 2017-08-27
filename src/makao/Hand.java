package makao;

import java.util.Arrays;

public class Hand extends Deck{
	public Hand(Card[] cards){
		super(cards);
	}
	
	public Card throwACard(int index){
		if(index>=this.cards.length){
			return null;
		}
		Card playCard = this.cards[index];
		this.cards = removeCard(this.cards, index);
		return playCard;
	}
	
	public void takeACard(Card takeCard){
		this.cards = Arrays.copyOfRange(this.cards, 0, this.cards.length+1);
		this.cards[this.cards.length-1] = takeCard;
	}
	
	public void takeACard(Card[] takeCardArr){
		for(Card card : takeCardArr)
			this.takeACard(card);
	}
	
	public int length(){
		return this.cards.length;
	}
	
	public Card[] removeCard(Card[] cards, int item){
		if(item>cards.length-1 || item<0)
			return cards;
		else {
			
			int lengthNew = cards.length-1;
			Card[] temp = new Card[lengthNew];
			
			if(item==lengthNew)
				for(int i=0; i<lengthNew; i++)
					temp[i] = cards[i];
			
			else if(item==0)
				for(int i=0; i<lengthNew; i++)
					temp[i] = cards[i+1];
			
			else{
				for(int i=0; i<item; i++)
					temp[i] = cards[i];
				for(int i=item; i<lengthNew; i++)
					temp[i] = cards[i+1];
			}
			return temp;
		}
	}
}