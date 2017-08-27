package makao;

import java.util.*;

import processing.core.PApplet;

public class PlayerAI extends PlayerMakao{
	private int posX, posY;
	private boolean offensive; // Likes attacking, uses a lot of fightable cards.
	private boolean mixer; // Likes mess, frequently calls for suit or rank.
	
	public PlayerAI(Card[] cards, int id, int x, int y){
		this.hand = new Hand(cards);
		this.ID = id;
		Random gen = new Random();
		this.offensive = gen.nextBoolean();
		this.mixer = gen.nextBoolean();
		posX = x;
		posY = y;
	}

	public Card playACard(Makao game, PlayerMakao[] players, int playerID){
		Card thrown = null;
		Card taken = null;
		
		if(this.amIStaying()){
			this.stayed();
			return null;
		}
		
		if(this.haveCards()==1 && (hand.showCards()[0].isQueenOfPikes() || game.table.whatsThere().isQueenOfPikes()))
			return hand.throwACard(0);
		
		if(game.table.whatsThere().isFightable() && game.table.getPenaltyAmount()>0){
			if(this.fightableFits(game.table, game.table.whatsThere(), 0)>0)
				thrown = this.playFightable(game.table, players, playerID);
			if(thrown!=null)
				return thrown;
			thrown = this.playQueenOfPikes(game.table, players, playerID);
			if(thrown!=null)
				return thrown;
			taken = game.table.drawACard();
			if(taken==null){
				game.lackOfCards();
				return null;
			}
			if(taken.isFightable()){
				thrown = this.playFightable(game.table, players, playerID, taken);
				if(thrown!=null)
					return thrown;
			}
			if(taken.isQueenOfPikes()){
				thrown = this.playQueenOfPikes(game.table, players, playerID, taken);
				if(thrown!=null)
					return thrown;
			}
			hand.takeACard(taken);
			Card[] temp = game.table.drawACard(game.table.getPenaltyAmount()-1);
				if(temp==null){
					game.lackOfCards();
					return null;
				}
			hand.takeACard(temp);
			game.table.resetPenalty();
			return null;
		}
		
		if(game.table.whatsThere().getRank()==Rank.bl4 && game.table.getStandAmount()>0){
			thrown = this.playFour(game.table, players, playerID);
			if(thrown!=null)
				return thrown;
		
			taken = game.table.drawACard();
			if(taken==null){
				game.lackOfCards();
				return null;
			}
			if(taken.getRank()==Rank.bl4){
				thrown = this.playFour(game.table, players, playerID, taken);
				if(thrown!=null)
					return thrown;
			}
			hand.takeACard(taken);;
			this.stay(game.table.getStandAmount());
			game.table.resetStand();
			return null;
		}
		
		if(game.table.whatsThere().getRank()==Rank.jack && game.table.getCalledRank()!=null){
			if(this.whereIsJack(game.table, game.table.whatsThere())>0){
				if(this.mixer){
					return hand.throwACard(this.whereIsJack(game.table, game.table.whatsThere())-1);
				}
				else {
					for(int i=0; i<hand.showCards().length; i++)
						if(!hand.showCards()[i].isFightable() && !hand.showCards()[i].isQueenOfPikes() 
								&& hand.showCards()[i].getRank()==game.table.getCalledRank() 
								&& hand.showCards()[i].isCovered(hand.showCards(), 0))
							return hand.throwACard(i);
					for(int i=0; i<hand.showCards().length; i++)
						if(!hand.showCards()[i].isFightable() && !hand.showCards()[i].isQueenOfPikes() 
								&& hand.showCards()[i].getRank()==game.table.getCalledRank())
							return hand.throwACard(i);
					return hand.throwACard(this.whereIsJack(game.table, game.table.whatsThere())-1);
				}
			}
			else{
				for(int i=0; i<hand.showCards().length; i++)
					if(!hand.showCards()[i].isFightable() && !hand.showCards()[i].isQueenOfPikes() 
							&& hand.showCards()[i].getRank()==game.table.getCalledRank() 
							&& hand.showCards()[i].isCovered(hand.showCards(), 0))
						return hand.throwACard(i);
				for(int i=0; i<hand.showCards().length; i++)
					if(!hand.showCards()[i].isFightable() && !hand.showCards()[i].isQueenOfPikes() 
							&& hand.showCards()[i].getRank()==game.table.getCalledRank())
						return hand.throwACard(i);
				taken = game.table.drawACard();
				if(taken==null){
					game.lackOfCards();
					return null;
				}
				if(taken.getRank()==game.table.getCalledRank() || taken.getRank()==Rank.jack)
					return taken;
				hand.takeACard(taken);
			}
			return null;
		}
		
		if(thrown==null && this.fightableFits(game.table, game.table.whatsThere(), 0)>0)
			thrown = this.playFightable(game.table, players, playerID);
		if(thrown==null && this.whereIsFour(game.table, game.table.whatsThere())>0)
			thrown = this.playFour(game.table, players, playerID);
		if(thrown==null)
			thrown = this.justPlay(game.table, players, playerID);
		
		if(thrown!=null)
			return thrown;
		
		taken = game.table.drawACard();
		if(taken==null){
			game.lackOfCards();
			return null;
		}
		
		if(taken.isFightable())
			thrown = this.playFightable(game.table, players, playerID, taken);
		if(taken.getRank()==Rank.bl4)
			thrown = this.playFour(game.table, players, playerID, taken);
		if(taken.isSpot() || taken.getRank()==Rank.jack || taken.getRank()==Rank.ace)
			thrown = this.justPlay(game.table, players, playerID, taken);
		
		if(thrown!=null)
			return thrown;
		else{
			hand.takeACard(taken);
			return null;
		}
	}
	
	private Card justPlay(Table table, PlayerMakao[] players, int playerID){
		if(table.getCalledSuit()!=null){
			if(this.mixer){
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].getRank()==Rank.ace && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere())))
						return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].getRank()==Rank.jack && hand.showCards()[i].getSuit()==table.getCalledSuit())
						return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isSpot() && hand.showCards()[i].isMultiple(hand.showCards(), 0) 
							&& hand.showCards()[i].getSuit()==table.getCalledSuit())
						return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isSpot() && hand.showCards()[i].getSuit()==table.getCalledSuit())
						return hand.throwACard(i);
				}
			}
			else{
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isSpot() && hand.showCards()[i].isMultiple(hand.showCards(), 0) 
							&& hand.showCards()[i].getSuit()==table.getCalledSuit())
						return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isSpot() && hand.showCards()[i].getSuit()==table.getCalledSuit())
						return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].getRank()==Rank.ace && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere())))
						return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].getRank()==Rank.jack && hand.showCards()[i].getSuit()==table.getCalledSuit())
						return hand.throwACard(i);
				}	
			}
		}
		else{
			if(this.mixer){
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].getRank()==Rank.jack && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere())))
						return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].getRank()==Rank.ace && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere())))
						return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isSpot() && hand.showCards()[i].fitsRank(table.whatsThere()))
						return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isSpot() && hand.showCards()[i].isMultiple(hand.showCards(), 0) 
							&& (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere())))
						return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isSpot() && hand.showCards()[i].isCovered(hand.showCards(), 0) 
							&& (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere())))
						return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isSpot() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere())))
						return hand.throwACard(i);
				}
			}
			else{
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isSpot() && hand.showCards()[i].isMultiple(hand.showCards(), 0) 
							&& (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere())))
						return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isSpot() && hand.showCards()[i].isCovered(hand.showCards(), 0) 
							&& (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere())))
						return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isSpot() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere())))
						return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].getRank()==Rank.jack && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere())))
						return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].getRank()==Rank.ace && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere())))
						return hand.throwACard(i);
				}
			}
		}
		return null;
	}
	
	private Card justPlay(Table table, PlayerMakao[] players, int playerID, Card taken){
		if(table.getCalledSuit()!=null){
			if(taken.getRank()==Rank.ace && taken.fits(table.whatsThere()))
				return taken;
			if(taken.getRank()==Rank.jack && taken.getSuit()==table.getCalledSuit())
				return taken;
			if(taken.isSpot() && taken.getSuit()==table.getCalledSuit())
				return taken;
		}
		else{
			if(taken.getRank()==Rank.ace && taken.fits(table.whatsThere()))
				return taken;
			if(taken.getRank()==Rank.jack && taken.fits(table.whatsThere()))
				return taken;
			if(taken.isSpot() && taken.fits(table.whatsThere()))
				return taken;
		}
		return null;
	}
	
	private Card playQueenOfPikes(Table table, PlayerMakao[] players, int playerID){
		if(this.offensive){
			if((table.getPenaltyAmount()>5 && players[(playerID+1)%players.length].haveCards()>2 && this.haveCards()<4) ||
					(table.getPenaltyAmount()>8 && players[(playerID+1)%players.length].haveCards()>1)){
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isQueenOfPikes())
						return hand.throwACard(i);
				}
			}
		}
		else{
			if((table.getPenaltyAmount()>6 && players[(playerID+1)%players.length].haveCards()>3 && this.haveCards()<3) ||
					(table.getPenaltyAmount()>10 && players[(playerID+1)%players.length].haveCards()>1)){
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isQueenOfPikes())
						return hand.throwACard(i);
				}
			}
		}
		return null;
	}
	
	private Card playQueenOfPikes(Table table, PlayerMakao[] players, int playerID, Card taken){
		if(taken.isQueenOfPikes()){
			if(this.offensive){
				if((table.getPenaltyAmount()>5 && players[(playerID+1)%players.length].haveCards()>2 && this.haveCards()<4) ||
						(table.getPenaltyAmount()>8 && players[(playerID+1)%players.length].haveCards()>1)){
					return taken;
				}
			}
			else{
				if((table.getPenaltyAmount()>6 && players[(playerID+1)%players.length].haveCards()>3 && this.haveCards()<3) ||
						(table.getPenaltyAmount()>10 && players[(playerID+1)%players.length].haveCards()>1)){
					return taken;
				}
			}
		}
		return null;
	}
	
	private Card playFightable(Table table, PlayerMakao[] players, int playerID){
		if(table.getCalledSuit()!=null){
			if(this.offensive){
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && hand.showCards()[i].getSuit()==table.getCalledSuit()
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==0
							&& hand.showCards()[i].isCovered(hand.showCards(), 0))
								return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && hand.showCards()[i].getSuit()==table.getCalledSuit()
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==1
							&& hand.showCards()[i].isCovered(hand.showCards(), 0))
								return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && hand.showCards()[i].getSuit()==table.getCalledSuit()
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==0)
								return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && hand.showCards()[i].getSuit()==table.getCalledSuit()
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==1)
								return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && hand.showCards()[i].getSuit()==table.getCalledSuit()
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==2
							&& hand.showCards()[i].isCovered(hand.showCards(), 0))
								return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && hand.showCards()[i].getSuit()==table.getCalledSuit()
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)>2
							&& hand.showCards()[i].isCovered(hand.showCards(), 0))
								return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && hand.showCards()[i].getSuit()==table.getCalledSuit()
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==2)
								return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && hand.showCards()[i].getSuit()==table.getCalledSuit()
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)>2)
								return hand.throwACard(i);
				}
			}
			else{
				if(this.amountFightable(0)>2 || players[(playerID+1)%players.length].haveCards()<=2 || this.haveCards()<=2){
					for(int i=0; i<hand.showCards().length; i++){
						if(hand.showCards()[i].isFightable() && hand.showCards()[i].getSuit()==table.getCalledSuit()
								&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==0
								&& hand.showCards()[i].isCovered(hand.showCards(), 0))
									return hand.throwACard(i);
					}
					for(int i=0; i<hand.showCards().length; i++){
						if(hand.showCards()[i].isFightable() && hand.showCards()[i].getSuit()==table.getCalledSuit()
								&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==1
								&& hand.showCards()[i].isCovered(hand.showCards(), 0))
									return hand.throwACard(i);
					}
					for(int i=0; i<hand.showCards().length; i++){
						if(hand.showCards()[i].isFightable() && hand.showCards()[i].getSuit()==table.getCalledSuit()
								&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==0)
									return hand.throwACard(i);
					}
					for(int i=0; i<hand.showCards().length; i++){
						if(hand.showCards()[i].isFightable() && hand.showCards()[i].getSuit()==table.getCalledSuit()
								&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==1)
									return hand.throwACard(i);
					}
					for(int i=0; i<hand.showCards().length; i++){
						if(hand.showCards()[i].isFightable() && hand.showCards()[i].getSuit()==table.getCalledSuit()
								&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==2
								&& hand.showCards()[i].isCovered(hand.showCards(), 0))
									return hand.throwACard(i);
					}
					for(int i=0; i<hand.showCards().length; i++){
						if(hand.showCards()[i].isFightable() && hand.showCards()[i].getSuit()==table.getCalledSuit()
								&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)>2
								&& hand.showCards()[i].isCovered(hand.showCards(), 0))
									return hand.throwACard(i);
					}
					for(int i=0; i<hand.showCards().length; i++){
						if(hand.showCards()[i].isFightable() && hand.showCards()[i].getSuit()==table.getCalledSuit()
								&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==2)
									return hand.throwACard(i);
					}
					for(int i=0; i<hand.showCards().length; i++){
						if(hand.showCards()[i].isFightable() && hand.showCards()[i].getSuit()==table.getCalledSuit()
								&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)>2)
									return hand.throwACard(i);
					}
				}
				else
					return null;
			}
		}
		else if(table.getPenaltyAmount()>0){
			if(this.offensive){
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==0
							&& hand.showCards()[i].isCovered(hand.showCards(), 0))
								return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==1
							&& hand.showCards()[i].isCovered(hand.showCards(), 0))
								return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==0)
								return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==1)
								return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==2
							&& hand.showCards()[i].isCovered(hand.showCards(), 0))
								return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)>2
							&& hand.showCards()[i].isCovered(hand.showCards(), 0))
								return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==2)
								return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)>2)
								return hand.throwACard(i);
				}
			}
			else{
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==0
							&& hand.showCards()[i].isCovered(hand.showCards(), 0))
								return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==1
							&& hand.showCards()[i].isCovered(hand.showCards(), 0))
								return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==0)
								return hand.throwACard(i);
				}
				if(this.amountFightable(0)>2 || players[(playerID+1)%players.length].haveCards()<=2 || this.haveCards()<=2 || table.getPenaltyAmount()>3){
					for(int i=0; i<hand.showCards().length; i++){
						if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
								&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==1)
									return hand.throwACard(i);
					}
					for(int i=0; i<hand.showCards().length; i++){
						if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
								&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==2
								&& hand.showCards()[i].isCovered(hand.showCards(), 0))
									return hand.throwACard(i);
					}
					for(int i=0; i<hand.showCards().length; i++){
						if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
								&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)>2
								&& hand.showCards()[i].isCovered(hand.showCards(), 0))
									return hand.throwACard(i);
					}
					for(int i=0; i<hand.showCards().length; i++){
						if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
								&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==2)
									return hand.throwACard(i);
					}
					for(int i=0; i<hand.showCards().length; i++){
						if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
								&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)>2)
									return hand.throwACard(i);
					}
				}
				else
					return null;
			}
		}
		else{
			if(this.offensive){
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==0
							&& hand.showCards()[i].isCovered(hand.showCards(), 0))
								return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==1
							&& hand.showCards()[i].isCovered(hand.showCards(), 0))
								return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==0)
								return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==1)
								return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==2
							&& hand.showCards()[i].isCovered(hand.showCards(), 0))
								return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)>2
							&& hand.showCards()[i].isCovered(hand.showCards(), 0))
								return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==2)
								return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
							&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)>2)
								return hand.throwACard(i);
				}
			}
			else{
				if(this.amountFightable(0)>2 || players[(playerID+1)%players.length].haveCards()<=2 || this.haveCards()<=2){
					for(int i=0; i<hand.showCards().length; i++){
						if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
								&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==0
								&& hand.showCards()[i].isCovered(hand.showCards(), 0))
									return hand.throwACard(i);
					}
					for(int i=0; i<hand.showCards().length; i++){
						if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
								&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==1
								&& hand.showCards()[i].isCovered(hand.showCards(), 0))
									return hand.throwACard(i);
					}
					for(int i=0; i<hand.showCards().length; i++){
						if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
								&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==0)
									return hand.throwACard(i);
					}
					for(int i=0; i<hand.showCards().length; i++){
						if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
								&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==1)
									return hand.throwACard(i);
					}
					for(int i=0; i<hand.showCards().length; i++){
						if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
								&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==2
								&& hand.showCards()[i].isCovered(hand.showCards(), 0))
									return hand.throwACard(i);
					}
					for(int i=0; i<hand.showCards().length; i++){
						if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
								&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)>2
								&& hand.showCards()[i].isCovered(hand.showCards(), 0))
									return hand.throwACard(i);
					}
					for(int i=0; i<hand.showCards().length; i++){
						if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
								&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)==2)
									return hand.throwACard(i);
					}
					for(int i=0; i<hand.showCards().length; i++){
						if(hand.showCards()[i].isFightable() && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere()))
								&& hand.showCards()[i].safetyClass(hand.showCards(), table.cardsGone(), 0)>2)
									return hand.throwACard(i);
					}
				}
				else
					return null;
			}
		}
		return null;
	}
	
	private Card playFightable(Table table, PlayerMakao[] players, int playerID, Card taken){
		if(table.getCalledSuit()!=null){
			if(this.offensive){
				if(taken.isFightable() && taken.getSuit()==table.getCalledSuit())
					return taken;
			}
			else{
				if(taken.isFightable() && taken.getSuit()==table.getCalledSuit() &&
						(this.amountFightable(1)>2 || players[(playerID+1)%players.length].haveCards()<=2 || this.haveCards()<=2))
					return taken;
			}
		}
		else if(table.getPenaltyAmount()>0){
			if(this.offensive){
				if(taken.isFightable() && taken.fits(table.whatsThere()))
					return taken;
			}
			else{
				if(taken.isFightable() && taken.fits(table.whatsThere())){
					if(taken.safetyClass(hand.showCards(), table.cardsGone(), 1)==0 ||
							(taken.safetyClass(hand.showCards(), table.cardsGone(), 1)==1 && taken.isCovered(hand.showCards(), 1)))
						return taken;
					else{
						if(this.amountFightable(1)>2 || players[(playerID+1)%players.length].haveCards()<=2 || this.haveCards()<=2 || table.getPenaltyAmount()>3)
							return taken;
					}
				}
			}
		}
		else{
			if(this.offensive){
				if(taken.isFightable() && taken.fits(table.whatsThere()))
					return taken;
			}
			else{
				if((taken.isFightable() && taken.fits(table.whatsThere())) && 
						(this.amountFightable(1)>2 || players[(playerID+1)%players.length].haveCards()<=2 || this.haveCards()<=2))
					return taken;
			}
		}
		return null;
	}
	
	private Card playFour(Table table, PlayerMakao[] players, int playerID){
		if(table.getCalledSuit()!=null){
			if(this.offensive){
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].getRank()==Rank.bl4 && hand.showCards()[i].getSuit()==table.getCalledSuit())
						return hand.throwACard(i);
				}
			}
				
			else{
				if(this.amountFour(0)>=2 || players[(playerID+1)%players.length].haveCards()<=2 || this.haveCards()<=2 || table.getStandAmount()>=2){
					for(int i=0; i<hand.showCards().length; i++){
						if(hand.showCards()[i].getRank()==Rank.bl4 && hand.showCards()[i].getSuit()==table.getCalledSuit())
							return hand.throwACard(i);
					}
				}
			}
			return null;
		}
		else{
			if(this.offensive){
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].getRank()==Rank.bl4 && hand.showCards()[i].isCovered(hand.showCards(), 0) && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere())))
						return hand.throwACard(i);
				}
				for(int i=0; i<hand.showCards().length; i++){
					if(hand.showCards()[i].getRank()==Rank.bl4 && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere())))
						return hand.throwACard(i);
				}
			}
				
			else{
				if(this.amountFour(0)>=2 || players[(playerID+1)%players.length].haveCards()<=2 || this.haveCards()<=2 || table.getStandAmount()>=2){
					for(int i=0; i<hand.showCards().length; i++){
						if(hand.showCards()[i].getRank()==Rank.bl4 && hand.showCards()[i].isCovered(hand.showCards(), 0) && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere())))
							return hand.throwACard(i);
					}
					for(int i=0; i<hand.showCards().length; i++){
						if(hand.showCards()[i].getRank()==Rank.bl4 && (table.whatsThere().isQueenOfPikes() || hand.showCards()[i].fits(table.whatsThere())))
							return hand.throwACard(i);
					}
				}
			}
			return null;
		}
	
	}
	
	private Card playFour(Table table, PlayerMakao[] players, int playerID, Card taken){
		if(table.getCalledSuit()!=null){
			if(this.offensive){
				if(taken.getSuit()==table.getCalledSuit())
					return taken;
				else
					return null;
			}
			else{
				if(taken.getSuit()==table.getCalledSuit() &&
						(this.amountFour(0)>=2 || players[(playerID+1)%players.length].haveCards()<=2 || this.haveCards()<=2 || table.getStandAmount()>=2))
					return taken;
				else
					return null;
			}
		}
		else{
			if(this.offensive){
				if(taken.fits(table.whatsThere()))
					return taken;
				else
					return null;
			}
			else{
				if(taken.fits(table.whatsThere()) &&
						(this.amountFour(0)>=2 || players[(playerID+1)%players.length].haveCards()<=2 || this.haveCards()<=2 || table.getStandAmount()>=2))
					return taken;
				else
					return null;
			}
		}
	}
	
	private Suit longestSuit(){
		Suit[] sArr = Suit.values();
		int[] iArr = new int[4];
		for(int i=0; i<hand.showCards().length; i++)
			iArr[hand.showCards()[i].getSuit().ordinal()]++;
		
		int length=0;
		int top=0;
		for(int i=0; i<iArr.length; i++)
			if(iArr[i]>length){
				length = iArr[i];
				top=i;
			}
		return sArr[top];
	}
	
	public Suit callForSuit(Makao game){
		return this.longestSuit();
	}
	
	public Rank callForRank(Makao game){
		for(int i=0; i<hand.showCards().length; i++)
			if(hand.showCards()[i].isSpot() && hand.showCards()[i].isMultiple(hand.showCards(), 0))
				return hand.showCards()[i].getRank();
		for(int i=0; i<hand.showCards().length; i++)
			if(hand.showCards()[i].isSpot() && hand.showCards()[i].isCovered(hand.showCards(), 0))
				return hand.showCards()[i].getRank();
		for(int i=0; i<hand.showCards().length; i++)
			if(hand.showCards()[i].isSpot())
				return hand.showCards()[i].getRank();
		return null;
	}
	
	private int amountFour(int mode){
		int counter=mode;
		for(int i=0; i<hand.showCards().length; i++)
			if(hand.showCards()[i].getRank()==Rank.bl4)
				counter++;
		return counter;
	}
	
	private int amountFightable(int mode){
		int counter=mode;
		for(int i=0; i<hand.showCards().length; i++)
			if(hand.showCards()[i].isFightable())
				counter++;
		return counter;
	}
	
	private int fightableFits(Table table, Card k, int mode){
		int counter=mode;
		for(int i=0; i<hand.showCards().length; i++)
			if(hand.showCards()[i].isFightable() && (hand.showCards()[i].fits(k) || hand.showCards()[i].getSuit()==table.getCalledSuit()))
				counter++;
		return counter;
	}
	
	private int whereIsJack(Table table, Card k){
		for(int i=0; i<hand.showCards().length; i++)
			if(hand.showCards()[i].getRank()==Rank.jack && (hand.showCards()[i].fits(k) || hand.showCards()[i].getSuit()==table.getCalledSuit()))
				return i+1;
		return 0;
	}
	
	private int whereIsFour(Table table, Card k){
		for(int i=0; i<hand.showCards().length; i++)
			if(hand.showCards()[i].getRank()==Rank.bl4 && (hand.showCards()[i].fits(k) || hand.showCards()[i].getSuit()==table.getCalledSuit()))
				return i+1;
		return 0;
	}
	
	public void draw(PApplet pa){
		pa = (Makao) pa;
		pa.strokeWeight(3);
		
		pa.stroke(0);
		pa.fill(100,50,0);
		pa.ellipse(posX, posY, 75, 75);
		pa.fill(255,255,255);
		pa.textSize(40);
		pa.text(hand.length(), posX, posY+15);
	}
	
	public void draw(PApplet pa, String active){
		pa = (Makao) pa;
		pa.strokeWeight(3);
		
		pa.stroke(255,255,0);
		pa.fill(100,50,0);
		pa.ellipse(posX, posY, 75, 75);
		pa.fill(255,255,255);
		pa.stroke(0);
		pa.textSize(40);
		pa.text(hand.length(), posX, posY+15);
	}
}