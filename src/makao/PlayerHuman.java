package makao;

import processing.core.*;

public class PlayerHuman extends PlayerMakao{
	public PlayerHuman(Card[] cards, int id){
		super(cards, id);
		hand.sort();
	}
	
	public Card playACard(Makao game, PlayerMakao[] players, int playerID){
		if(this.amIStaying()){
			game.fill(100,50,0);
			game.rect(0, game.gameHeight-100, game.gameWidth, 100);
			game.textSize(30);
			game.stroke(255,255,255);
			game.fill(255,255,255);
			game.text("You lose your turn!", 500, 610);
			game.delay(2000);
		}
		return null;
	}

	public void taking(Table table, Card taken, Makao game){
		this.hand.takeACard(taken);
		if(table.getPenaltyAmount()>0){
			Card[] penalty = new Card[table.getPenaltyAmount()-1];
			penalty = table.drawACard(table.getPenaltyAmount()-1);
				if(penalty==null){
					game.lackOfCards();
					return;
				}
			this.hand.takeACard(penalty);
			table.resetPenalty();
		}
		if(table.getStandAmount()>0){
			this.stay(table.getStandAmount());
			table.resetStand();
		}
		this.hand.sort();
	}
	
	public boolean canIPlayIt(Table table, Card my, Card onTable){
		
		if(onTable.getRank()==Rank.bl4 && table.getStandAmount()>0){
			if(my.fitsRank(onTable) || my.isQueenOfPikes())
				return true;
			else
				return false;
		}

		else if(onTable.isFightable() && table.getPenaltyAmount()>0){
			if((my.isFightable() && my.fits(onTable)) || my.isQueenOfPikes())
				return true;
			else
				return false;
		}
		
		else if(onTable.getRank()==Rank.jack && table.getCalledRank()!=null){
			if((my.getRank()==table.getCalledRank() && !my.isFightable()) || my.fitsRank(onTable) || my.isQueenOfPikes())
				return true;
			else
				return false;
		}
		
		else if(onTable.getRank()==Rank.ace && table.getCalledSuit()!=null){
			if(my.getSuit()==table.getCalledSuit() || my.fitsRank(onTable) || my.isQueenOfPikes())
				return true;
			else
				return false;
		}
		
		else if(onTable.isQueenOfPikes())
			return true;
		
		else if(my.isQueenOfPikes())
			return true;
		
		else{
			if(my.fits(onTable))
				return true;
			else
				return false;
		}
	}
	
	public Suit callForSuit(Makao game){
		game.mode = "callingSuit";
		return null;
	}
	
	public Rank callForRank(Makao game){
		game.mode = "callingRank";
		return null;
	}
	
	public void draw(PApplet pa){
		int posX = 464 - ((int)hand.length()/2)*17;
		for(int i=0; i<hand.length(); i++, posX += 17){
			hand.cards[i].draw(posX, 420, pa, true);
		}
	}
}