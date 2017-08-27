package makao;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import processing.core.PApplet;

public class Makao extends PApplet{
	int gameWidth = 1000;
	int gameHeight = 650;
	String mode = "hello";
	int lastTime;
	
	Table table;
	Deck deck;
	PlayerMakao[] players;
	
	int playersNb;
	int activePlayer;
	int place;
	
	Minim minim;
	AudioPlayer put, take, shuffle, fanfare;
	boolean sound = true;
	
	public static void main (String[] args) {
		PApplet.main(new String[] {"makao.Makao"});
	}
	
	public void settings() {
		  size(gameWidth, gameHeight, "processing.opengl.PGraphics3D");
	}

	public void setup(){
		minim = new Minim(this);
		put = minim.loadFile("sounds\\put.mp3");
		take = minim.loadFile("sounds\\take.mp3");
		shuffle = minim.loadFile("sounds\\shuffle.mp3");
		fanfare = minim.loadFile("sounds\\fanfare.mp3");
	}
	
	public void draw(){
		switch(mode){
		case "hello":
			background(100,50,0);
			textAlign(CENTER);
			textSize(30);
			stroke(255,255,255);
			fill(255,255,255);
			text("Hello in Makao game!", 500, 80);
			textSize(20);
			text("---------------------------------------------------------------------------------\n"
				+ "Game rules:\n"
				+ "1. Throw card which matches (by suit or rank) recently played card on table.\n"
				+ "2. If you can't play any card (or don't want to), take one from table (click on deck).\n"
				+ "3. Who will throw all cards - wins.\n"
				+ "4. Function cards (fightable included):\n"
				+ " a) two (fightable): next player takes 2 cards from table, or throw matching fightable card,\n"
				+ " b) three (fightable): next player takes 3 cards from table, or throw matching fightable card,\n"
				+ " c) king of hearts (fightable): next player takes 5 cards from table, or throw matching fightable card,\n"
				+ " d) king of pikes (fightable): previous player takes 5 cards from table, or throw matching fightable card,\n"
				+ " e) four: next player loses turn, or throw another four,\n"
				+ " f) jack: call for card rank (can't call fightable cards),\n"
				+ " g) ace: call for card suit,\n"
				+ " h) queen of pikes: cancels all penalties, calls etc. You can always throw queen of pikes when in troubles.\n"
				+ "---------------------------------------------------------------------------------\n"
				+ "Click to continue.\n", 500, 130);
			break;
		case "playersChoose":
			background(100,50,0);
			textAlign(CENTER);
			textSize(40);
			stroke(255,255,255);
			fill(255,255,255);
			text("Choose number of players:", 500, 170);
			stroke(0);
			fill(255,255,0);
			rect(175,300,50,50);
			rect(275,300,50,50);
			rect(375,300,50,50);
			rect(475,300,50,50);
			rect(575,300,50,50);
			rect(675,300,50,50);
			rect(775,300,50,50);
			fill(0);
			text("2", 200, 338);
			text("3", 300, 338);
			text("4", 400, 338);
			text("5", 500, 338);
			text("6", 600, 338);
			text("7", 700, 338);
			text("8", 800, 338);
			fill(255,255,255);
			textSize(25);
			text("Press 'S' to turn sound on/off.", 500, 450);
			textSize(20);
			text("Author: Tomasz Zagórski (zagorskitomasz@gmail.com)", 500, 530);
			break;
		case "decksChoose":
			background(100,50,0);
			textAlign(CENTER);
			textSize(40);
			stroke(255,255,255);
			fill(255,255,255);
			text("Choose number of card decks:", 500, 150);
			stroke(0);
			fill(255,255,0);
			rect(250,250,100,150);
			rect(450,250,100,150);
			rect(650,250,100,150);
			fill(0);
			textSize(60);
			text("1", 300, 345);
			text("2", 500, 345);
			text("3", 700, 345);
			break;
		case "gameOver":
			background(100,50,0);
			textAlign(CENTER);
			textSize(40);
			stroke(255,255,255);
			fill(255,255,255);
			text("Game over! You take " + place + " place.", 500, 200);
			if(place==1)
				text("Congratulations!", 500, 350);
			textSize(20);
			text("(click to continue)", 500, 550);
			break;
		case "noCards":
			background(100,50,0);
			textAlign(CENTER);
			textSize(40);
			fill(255,255,255);
			text("Lack of cards :(", 500, 200);
			text("Try to use more card decks!", 500, 350);
			textSize(20);
			text("(click to continue)", 500, 550);
			break;
		case "normalGame":
			drawGameBasics();
			textAlign(CENTER);
			table.draw(this);
			/*for(PlayerMakao p : players)
				p.draw(this);*/
			for(int i=0; i<players.length; i++){
				if(activePlayer==i && activePlayer!=0){
					PlayerAI temp = (PlayerAI) players[i];
					temp.draw(this, "active");
				}
				else
					players[i].draw(this);
			}
			
			if(activePlayer>=players.length) activePlayer = 0;
			if(activePlayer<0) activePlayer = players.length-1;
			
			if(activePlayer==0 && players[0].amIStaying()){
				mode="staying";
			}
			
			if(activePlayer==0 && !players[0].amIStaying()){
				fill(0);
				textSize(30);
				text("Your turn!", 500, 390);
			}
			
			if(!mode.equals("gameOver") && !mode.equals("noCards") && activePlayer!=0 && millis() - lastTime > 800){
				Card thrown = players[activePlayer].playACard(this, players, activePlayer);
				if(mode.equals("noCards"))
					return;
				if(thrown!=null){
					table.putACard(thrown);
					table.cardEffect(thrown, players[activePlayer], this);
					
					boolean removed = false;
					if(players[activePlayer].haveCards()<=0){
						removed = true;
						if(players.length==2){
							place++;
							mode="gameOver";
						}
						else{
							place++;
							players = removePlayer(players, activePlayer);
							activePlayer--;
						}
					}
					
					if(thrown.getSuit()==Suit.pikes && thrown.getRank()==Rank.king){
						if(removed)
							activePlayer-=1;
						else
							activePlayer-=2;
					}
				}
				activePlayer++;
				lastTime=millis();
			}
			break;
		case "staying":
			drawGameBasics();
			textAlign(CENTER);
			table.draw(this);
			for(PlayerMakao p : players)
				p.draw(this);
			textSize(30);
			stroke(0);
			fill(255,255,255);
			text("You lose your turn! (click to continue)", 500, 610);
			break;
		case "callingSuit":
			drawGameBasics();
			textAlign(CENTER);
			table.draw(this);
			for(PlayerMakao p : players)
				p.draw(this);
			textSize(30);
			stroke(0);
			fill(255,255,255);
			text("What suit would you like to call for:", 300, 610);
			image(Suit.pikes.getSign(this), 590, 590);
			image(Suit.hearts.getSign(this), 640, 590);
			image(Suit.tiles.getSign(this), 690, 590);
			image(Suit.clovers.getSign(this), 740, 590);
			fill(255,255,0);
			rect(790,590,50,21);
			textSize(15);
			fill(0);
			text("None", 815, 607);
			break;
		case "callingRank":
			drawGameBasics();
			textAlign(CENTER);
			table.draw(this);
			for(PlayerMakao p : players)
				p.draw(this);
			textSize(30);
			stroke(0);
			fill(255,255,255);
			text("What rank would you like to call for:", 300, 610);
			fill(255,255,0);
			rect(590,590,30,21);
			rect(630,590,30,21);
			rect(670,590,30,21);
			rect(710,590,30,21);
			rect(750,590,30,21);
			rect(790,590,30,21);
			rect(830,590,30,21);
			rect(870,590,30,21);
			rect(910,590,50,21);
			textSize(15);
			fill(0);
			text("5", 605, 607);
			text("6", 645, 607);
			text("7", 685, 607);
			text("8", 725, 607);
			text("9", 765, 607);
			text("10", 805, 607);
			text("Q", 845, 607);
			text("K", 885, 607);
			text("None", 935, 607);
			break;
		case "takingCard":
			drawGameBasics();
			textAlign(CENTER);
			table.draw(this);
			for(PlayerMakao p : players)
				p.draw(this);
			textSize(30);
			stroke(0);
			fill(255,255,255);
			text("Do you want to play this card?", 300, 610);
			fill(255,255,0);
			rect(600,590,50,21);
			rect(700,590,50,21);
			textSize(15);
			fill(0);
			text("YES", 625, 607);
			text("NO", 725, 607);
			break;
		}
		
	}
	
	public void drawGameBasics(){
		background(0,150,0);
		fill(100,50,0);
		rect(0, gameHeight-100, gameWidth, 100);
	}
	
	public void mouseClicked(){
		switch(mode){
		case "hello":
			mode = "playersChoose";
			break;
		case "playersChoose":
			if(mouseX>=175 && mouseY>=300 && mouseX<=225 && mouseY<=350){
				playersNb = 2;
				mode = "decksChoose";
			}
			if(mouseX>=275 && mouseY>=300 && mouseX<=325 && mouseY<=350){
				playersNb = 3;
				mode = "decksChoose";
			}
			if(mouseX>=375 && mouseY>=300 && mouseX<=425 && mouseY<=350){
				playersNb = 4;
				mode = "decksChoose";
			}
			if(mouseX>=475 && mouseY>=300 && mouseX<=525 && mouseY<=350){
				playersNb = 5;
				mode = "decksChoose";
			}
			if(mouseX>=575 && mouseY>=300 && mouseX<=625 && mouseY<=350){
				playersNb = 6;
				mode = "decksChoose";
			}
			if(mouseX>=675 && mouseY>=300 && mouseX<=725 && mouseY<=350){
				playersNb = 7;
				mode = "decksChoose";
			}
			if(mouseX>=775 && mouseY>=300 && mouseX<=825 && mouseY<=350){
				playersNb = 8;
				mode = "decksChoose";
			}
			break;
		case "decksChoose":
			if(mouseX>=250 && mouseY>=250 && mouseX<=350 && mouseY<=400){
				newGame(1);
			}
			if(mouseX>=450 && mouseY>=250 && mouseX<=550 && mouseY<=400){
				newGame(2);
			}
			if(mouseX>=650 && mouseY>=250 && mouseX<=750 && mouseY<=400){
				newGame(3);
			}
			break;
		case "normalGame":
			if(activePlayer==0){
				for(int i=0; i<players[0].haveCards(); i++){
					if(players[0].hand.cards[i].isInside(mouseX, mouseY)){
						if(players[0].canIPlayIt(table, players[0].hand.cards[i], table.whatsThere())){
							Card thrown = players[0].hand.throwACard(i);
							if(players[0].haveCards()>0)
								players[0].hand.sort();
							table.cardEffect(thrown, players[0], this);
							table.putACard(thrown);
							if(thrown.getSuit()==Suit.pikes && thrown.getRank()==Rank.king)
								activePlayer -= 2;
							activePlayer++;
							lastTime=millis();
						}
					}
				}
				if(mouseX>=580 && mouseY>=210 && mouseX<=652 && mouseY<=307){
					Card taken = table.drawACard();
					if(taken==null){
						lackOfCards();
						return;
					}
					if(players[0].canIPlayIt(table, taken, table.whatsThere())){
						table.showTaken(true);
						mode="takingCard";
					}
					else{
						players[0].taking(table, taken, this);
						activePlayer++;
						lastTime=millis();
					}
				}
				if(players[0].haveCards()<=0){
					if(place==1){
						fanfare.rewind();
						fanfare.play();
					}
					mode="gameOver";
				}
			}
			break;
		case "gameOver":
			mode="playersChoose";
			break;
		case "noCards":
			mode="playersChoose";
			break;
		case "staying":
			players[0].stayed();
			activePlayer++;
			mode="normalGame";
			lastTime=millis();
		case "takingCard":
			if(mouseX>=600 && mouseY>=590 && mouseX<=650 && mouseY<=611){
				Card thrown = table.getTaken();
				table.cardEffect(thrown, players[0], this);
				table.putACard(thrown);
				if(thrown.getSuit()==Suit.pikes && thrown.getRank()==Rank.king)
					activePlayer -= 2;
				table.showTaken(false);
				activePlayer++;
				if(mode.equals("takingCard"))
					mode="normalGame";
				lastTime=millis();
			}
			else if(mouseX>=700 && mouseY>=590 && mouseX<=750 && mouseY<=611){
				players[0].taking(table, table.getTaken(), this);
				table.showTaken(false);
				activePlayer++;
				mode="normalGame";
				lastTime=millis();
			}
			break;
		case "callingSuit":
			if(mouseX>=590 && mouseY>=590 && mouseX<=611 && mouseY<=611){
				table.setCalledSuit(Suit.pikes);
				mode="normalGame";
				lastTime=millis();
			}
			else if(mouseX>=640 && mouseY>=590 && mouseX<=661 && mouseY<=611){
				table.setCalledSuit(Suit.hearts);
				mode="normalGame";
				lastTime=millis();
			}
			else if(mouseX>=690 && mouseY>=590 && mouseX<=711 && mouseY<=611){
				table.setCalledSuit(Suit.tiles);
				mode="normalGame";
				lastTime=millis();
			}
			else if(mouseX>=740 && mouseY>=590 && mouseX<=761 && mouseY<=611){
				table.setCalledSuit(Suit.clovers);
				mode="normalGame";
				lastTime=millis();
			}
			else if(mouseX>=790 && mouseY>=590 && mouseX<=840 && mouseY<=611){
				table.setCalledSuit(null);
				mode="normalGame";
				lastTime=millis();
			}
			break;
		case "callingRank":
			if(mouseX>=590 && mouseY>=590 && mouseX<=620 && mouseY<=611){
				table.setCalledRank(Rank.bl5);
				mode="normalGame";
				lastTime=millis();
			}
			else if(mouseX>=630 && mouseY>=590 && mouseX<=660 && mouseY<=611){
				table.setCalledRank(Rank.bl6);
				mode="normalGame";
				lastTime=millis();
			}
			else if(mouseX>=670 && mouseY>=590 && mouseX<=700 && mouseY<=611){
				table.setCalledRank(Rank.bl7);
				mode="normalGame";
				lastTime=millis();
			}
			else if(mouseX>=710 && mouseY>=590 && mouseX<=740 && mouseY<=611){
				table.setCalledRank(Rank.bl8);
				mode="normalGame";
				lastTime=millis();
			}
			else if(mouseX>=750 && mouseY>=590 && mouseX<=780 && mouseY<=611){
				table.setCalledRank(Rank.bl9);
				mode="normalGame";
				lastTime=millis();
			}
			else if(mouseX>=790 && mouseY>=590 && mouseX<=820 && mouseY<=611){
				table.setCalledRank(Rank.bl10);
				mode="normalGame";
				lastTime=millis();
			}
			else if(mouseX>=830 && mouseY>=590 && mouseX<=860 && mouseY<=611){
				table.setCalledRank(Rank.queen);
				mode="normalGame";
				lastTime=millis();
			}
			else if(mouseX>=870 && mouseY>=590 && mouseX<=900 && mouseY<=611){
				table.setCalledRank(Rank.king);
				mode="normalGame";
				lastTime=millis();
			}
			else if(mouseX>=910 && mouseY>=590 && mouseX<=960 && mouseY<=611){
				table.setCalledRank(null);
				mode="normalGame";
				lastTime=millis();
			}
		}
	}
	
	public void newGame(int decksNb){
		shuffle.rewind();
		shuffle.play();
		mode = "normalGame";
		deck = new Deck(this, decksNb);
		deck.shuffle(10000);
		place = 1;
		
		Card[][] stacks = deck.giveOut(playersNb, deck.showCards().length-playersNb*5);
		table = new Table(stacks[stacks.length-1], this);
		table.layOff();
		
		players = new PlayerMakao[playersNb];
		players[0] = new PlayerHuman(stacks[0], 1);
		players[0].hand.sort();
		
		if(playersNb==2){
			players[1] = new PlayerAI(stacks[1], 2, 500, 100);
		}
		if(playersNb==3){
			players[1] = new PlayerAI(stacks[1], 2, 250, 160);
			players[2] = new PlayerAI(stacks[2], 2, 750, 160);
		}
		if(playersNb==4){
			players[1] = new PlayerAI(stacks[1], 2, 200, 225);
			players[2] = new PlayerAI(stacks[2], 2, 500, 80);
			players[3] = new PlayerAI(stacks[3], 2, 800, 225);
		}
		if(playersNb==5){
			players[1] = new PlayerAI(stacks[1], 2, 200, 250);
			players[2] = new PlayerAI(stacks[2], 2, 400, 100);
			players[3] = new PlayerAI(stacks[3], 2, 600, 100);
			players[4] = new PlayerAI(stacks[4], 2, 800, 250);
		}
		if(playersNb==6){
			players[1] = new PlayerAI(stacks[1], 2, 200, 270);
			players[2] = new PlayerAI(stacks[2], 2, 300, 140);
			players[3] = new PlayerAI(stacks[3], 2, 500, 80);
			players[4] = new PlayerAI(stacks[4], 2, 700, 140);
			players[5] = new PlayerAI(stacks[5], 2, 800, 270);
		}
		if(playersNb==7){
			players[1] = new PlayerAI(stacks[1], 2, 200, 280);
			players[2] = new PlayerAI(stacks[2], 2, 300, 140);
			players[3] = new PlayerAI(stacks[3], 2, 420, 80);
			players[4] = new PlayerAI(stacks[4], 2, 580, 80);
			players[5] = new PlayerAI(stacks[5], 2, 700, 140);
			players[6] = new PlayerAI(stacks[6], 2, 800, 280);
		}
		if(playersNb==8){
			players[1] = new PlayerAI(stacks[1], 2, 200, 300);
			players[2] = new PlayerAI(stacks[2], 2, 280, 200);
			players[3] = new PlayerAI(stacks[3], 2, 380, 120);
			players[4] = new PlayerAI(stacks[4], 2, 500, 90);
			players[5] = new PlayerAI(stacks[5], 2, 620, 120);
			players[6] = new PlayerAI(stacks[6], 2, 720, 200);
			players[7] = new PlayerAI(stacks[7], 2, 800, 300);
		}
		activePlayer=0;
		lastTime=millis();
	}
	
	public void lackOfCards(){
		mode="noCards";
	}
	
	public void mouseMoved(){
		if(mode.equals("normalGame")){
			for(int i=0; i<players[0].haveCards(); i++){
				if(players[0].hand.cards[i].isInside(mouseX, mouseY)){
					if(players[0].canIPlayIt(table, players[0].hand.cards[i], table.whatsThere()))
						players[0].hand.cards[i].setPlayable(true);
					else
						players[0].hand.cards[i].setPlayable(false);
					players[0].hand.cards[i].setHovered(true);
				}
				else
					players[0].hand.cards[i].setHovered(false);
			}
			if(mouseX>=580 && mouseY>=210 && mouseX<=652 && mouseY<=307)
				table.hoverDeck(true);
			else
				table.hoverDeck(false);
		}
	}
	
	public PlayerMakao[] removePlayer(PlayerMakao[] players, int item){
		if(item>players.length-1 || item<0){
			return players;
		}
		else {
			int lengthNew = players.length-1;
			PlayerMakao[] temp = new PlayerMakao[lengthNew];
			
			if(item==lengthNew)
				for(int i=0; i<lengthNew; i++)
					temp[i] = players[i];
			
			else if(item==0)
				for(int i=0; i<lengthNew; i++)
					temp[i] = players[i+1];
			
			else{
				for(int i=0; i<item; i++)
					temp[i] = players[i];
				for(int i=item; i<lengthNew; i++)
					temp[i] = players[i+1];
			}
			return temp;
		}
	}
	
	public void keyPressed(){
		if(key=='s' || key=='S'){
			if(sound){
				sound=false;
				put.mute();
				take.mute();
				shuffle.mute();
				fanfare.mute();
			}
			else{
				sound=true;
				put.unmute();
				take.unmute();
				shuffle.unmute();
				fanfare.unmute();
			}
		}
	}
}
