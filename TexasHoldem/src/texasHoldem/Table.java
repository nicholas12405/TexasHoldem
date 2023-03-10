package texasHoldem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class Table {
	private int bb;
	private int sb;
	
	private boolean preflop;
	private int goesFirst;
	private int bbPlayerIndex;
	private int sbPlayerIndex;
	
	private int currentHighestBet;
	private int pot;
	private int minimumRaise;
	
	private ArrayList<Player> allPlayers;
	private ArrayList<Card> tableCards;
	private ArrayList<Card> deck;
	private int numPlayersInPlay;
	
	private Scanner in;
	
	public Table(int bb, int sb, ArrayList<Player> players, Scanner in, ArrayList<Card> deck) {
		this.bb = bb;
		this.sb = sb;
		allPlayers = players;
		this.in = in;
		tableCards = new ArrayList<>();
		this.deck = deck;
		firstActions();
	}
	
	//This runs once before the game starts
	private void firstActions() {
		//Randomly generate bb, sb, and first positions
		bbPlayerIndex = (int)(Math.random() * allPlayers.size());
		sbPlayerIndex = bbPlayerIndex + 1;
		if(sbPlayerIndex == allPlayers.size()) {
			sbPlayerIndex = 0;
		}		
	}
	
	public int getPot() {
		return pot;
	}
	
	//This runs after a round
	//Returns cards
	public void cleanupAfterRound() {
		//Return table cards to deck
		while(tableCards.size() > 0) {
			deck.add(tableCards.remove(0));
		}
		
		//Return player hands to deck
		for(Player player : allPlayers) {
			deck.addAll(player.returnCards());
		}
		
		//Kill the dead players
		for(Player player : allPlayers) {
			if(player.getMoney() == 0) {
				allPlayers.remove(player);
			}
		}
	}
	
	//This runs before the preflop
	public void beginRound() {
		preflop = true;
		
		//Shuffle the deck
		Collections.shuffle(deck);
		
		//Deal out cards
		for(Player player : allPlayers) {
			for(int c = 0; c < 2; c++) {
				player.addCard(deck.remove(0));
			}
		}
		
		//Set pot, currentHighestBet, and minimumRaise
		pot = sb + bb;
		currentHighestBet = bb;
		minimumRaise = bb;
		
		//Rotate big blind
		bbPlayerIndex = bbPlayerIndex - 1;
		if(bbPlayerIndex == -1) {
			bbPlayerIndex = allPlayers.size() - 1;
		}
		
		//Rotate small blind
		sbPlayerIndex = sbPlayerIndex - 1;
		if(sbPlayerIndex == -1) {
			sbPlayerIndex = allPlayers.size() - 1;
		}
		
		//Set first player back to left of big blind
		if(allPlayers.size() > 2) {
			goesFirst = bbPlayerIndex - 1;
			if(goesFirst == -1) {
				goesFirst = allPlayers.size() - 1;
			}
		} else {
			//First player = btn = small blind if two player
			goesFirst = sbPlayerIndex;
		}
		
		//Big blinds and small blinds
		Player sbPlayer = allPlayers.get(sbPlayerIndex);
		System.out.println(sbPlayer.getId() + " pays the small blind");
		sbPlayer.call(sb);
		
		Player bbPlayer = allPlayers.get(bbPlayerIndex);
		System.out.println(bbPlayer.getId() + " pays the big blind");
		bbPlayer.call(bb);
		
		//Reset numPlayersInPlay
		numPlayersInPlay = allPlayers.size();
	}
	
	private void printChoices(ArrayList<?> choices, String word) {
		
		if(choices.size() == 2) {
			System.out.println("\"" + choices.get(0) + "\" " + word + " \"" + choices.get(1) + "\"");
		} else {
			for(int i = 0; i < choices.size(); i++) {
				System.out.print("\"" + choices.get(i) + "\"");
				
				if(i < choices.size() - 2) {
					System.out.print(", ");
				} else if(i == choices.size() - 2){
					System.out.print(", " + word + " ");
				} else {
					System.out.println("");
				}
			}
		}
		
	}
	
	public int getPlayersInPlay() {
		return numPlayersInPlay;
	}
	
	public void runRound() {
		int endPoint = goesFirst;
		int index = goesFirst;
		
		if(preflop) {
			preflop = false;
			//move goesFirst for postflop rounds
			//sb goes first unless it's two players, in which case bb goes first
			if(allPlayers.size() > 2) {
				goesFirst = sbPlayerIndex;
			} else {
				goesFirst = bbPlayerIndex;
			}
		}
		do {
			Player player = allPlayers.get(index);
			
			if(numPlayersInPlay == 1) {
				break;
			}
			System.out.println("---------------------------------------");
			if(player instanceof AiOpponent) {
				System.out.println(player.getId() + " has $" + player.getMoney());
				if(player.getMoney() == 0) {
					System.out.println(player.getId() + " is all in and cannot check, fold, or raise!");
				} else if(player.isFolded()) {
					System.out.println(player.getId() + " has folded.");
				} else {
					int num = ((AiOpponent) player).getChoice(currentHighestBet, minimumRaise);
					if(num == -1) {
						numPlayersInPlay--;
						System.out.println(player.getId() + " folded!");
					} else if (player.getCurrentBet() > currentHighestBet) {
						endPoint = index;
						int raisedby = player.getCurrentBet() - currentHighestBet;
						//System.out.println("num: " + num + ", player.getcurrentbet: " + player.getCurrentBet() + ", raisedBy: " + raisedby);
						minimumRaise = raisedby;
						System.out.print(player.getId() + " raised by $" + raisedby);
						currentHighestBet = player.getCurrentBet();
						System.out.println(" for a total bet of " + currentHighestBet + "!");
						pot = pot + num;
					} else {
						if(num == 0) {
							System.out.println(player.getId() + " checks");
						} else {
							System.out.println(player.getId() + " calls with $" + num + " for a total bet of $" + currentHighestBet);
						}
						
						pot = pot + num;
					}
					
					if(player.getMoney() == 0) {
						System.out.println(player.getId() + " is all in!");
					}
				}
			} else {
				if(player.getMoney() == 0) {
					System.out.println("You are all in and cannot call, fold, or raise!");
				} else if(player.isFolded()) {
					System.out.println("You have folded and cannot take any actions.");
				} else {
					ArrayList<String> validChoices = new ArrayList<>();
					validChoices.add("raise");
					
					//Make a distinction between check and call, and don't let the player fold if a check is available
					if(currentHighestBet == player.getCurrentBet()) {
						validChoices.add("check");
					} else {
						validChoices.add("fold");
						validChoices.add("call");
					}
					
					System.out.println("---------------------------------------");
					System.out.println("Your turn, " + player.getId() + ", you have " + player.getMoney());
					System.out.println("---------------------------------------");
					System.out.println("The current bet is at $" + currentHighestBet);
					System.out.println("Minimum raise is $" + minimumRaise);
					System.out.print("Enter your choice, type ");
					printChoices(validChoices, "or");
					System.out.println("---------------------------------------");
					
					String choice = "";
					
					while(choice.equals("")) {
						choice = in.nextLine();
						choice = choice.toLowerCase();
						
						if(!validChoices.contains(choice)) {
							System.out.print("Enter something valid: ");
							printChoices(validChoices, "or");
							choice = "";
						}
					}
					
					if(choice.equals("call")) {
						int amount = player.call(currentHighestBet);
						System.out.println("You call with $" + amount + " for a total bet of $" + currentHighestBet);
						
						pot = pot + amount;
					} else if(choice.equals("check")) {
						System.out.println("You check");
					} else if(choice.equals("raise")) {
						//raiseBy here is the amount from the currentHighestBet that the player wants to raise
						int raiseBy = -1;
						int maxRaise = player.getMoney() - (currentHighestBet - player.getCurrentBet());
						while(raiseBy == -1) {
							try {
								System.out.println("Minimum raise: " + minimumRaise);
								System.out.println("Maximum raise: " + maxRaise);
								System.out.println("Enter a valid number to raise by: ");
								raiseBy = in.nextInt();
							} catch(Exception e) {
								raiseBy = -1;
								System.out.println("You need to enter a number");
							}
							
							if(raiseBy < minimumRaise || raiseBy > maxRaise) {
								System.out.println("Enter a valid raise");
								raiseBy = -1;
							}
						}
						
						currentHighestBet = currentHighestBet + raiseBy;
						pot = pot + currentHighestBet;
						System.out.println("You raise by " + raiseBy + " for a total bet of " + currentHighestBet);
						minimumRaise = raiseBy;
						player.call(currentHighestBet);
						endPoint = index;
					} else {
						System.out.println("You fold.");
						numPlayersInPlay--;
						player.fold();
					}
					
					if(player.getMoney() == 0) {
						System.out.println("You are all in");
					}
				}
			}
			
			index--;
			if(index == -1) {
				index = allPlayers.size() - 1;
			}
		} while(index != endPoint);
	}
	
	//This runs between preflop-flop, flop-turn, and turn-river
	public void addTableCards(int numCards) {
		for(int i = 0; i < numCards; i++) {
			tableCards.add(deck.remove(0));
		}
	}
	
	public void printTable() {
		for(Card card : tableCards) {
			System.out.println(card);
		}
	}
	
	public void resolveWinner(HashMap<Integer, String> handTypeToString, HashMap<Integer, String> numsToString) {
		//Get the players that haven't folded
		ArrayList<Player> playersInPlay = new ArrayList<>();
		ArrayList<Hand> playersStrongestHand = new ArrayList<>();

		for(Player player : allPlayers) {
			if(!player.isFolded()) {
				playersInPlay.add(player);
			}
		}
		
		if(playersInPlay.size() > 1) {
			for(int i = 0; i < playersInPlay.size(); i++) {
				//For each player, create a list of all their possible hands
				ArrayList<Hand> playerHands = new ArrayList<>();
				
				//The table hand is a potential hand
				playerHands.add(new Hand(tableCards, handTypeToString, numsToString));
				
				//For each of the players two cards, an additional 10 hands can be created by replacing one of the table cards
				for(Card pCard : playersInPlay.get(i).getHand()) {
					
					for(int j = 0; j < 5; j++) {
						ArrayList<Card> potentialHand = new ArrayList<>(tableCards);
						potentialHand.set(j, pCard);
						playerHands.add(new Hand(potentialHand, handTypeToString, numsToString));
					}
				}
				
				//By replacing two of the table cards with the player's two cards, an additional 10 possible hands can be created
				for(int p = 0; p < 4; p++) {
					for(int j = p + 1; j < 5;  j++) {
						ArrayList<Card> potentialHand = new ArrayList<>(tableCards);
						potentialHand.set(p, playersInPlay.get(i).getHand().get(0));
						potentialHand.set(j, playersInPlay.get(i).getHand().get(1));
						playerHands.add(new Hand(potentialHand, handTypeToString, numsToString));
					}
				}
				
				Collections.sort(playerHands);
				playersStrongestHand.add(playerHands.get(playerHands.size() - 1));
			}
			
			//Now just eliminate players until the strongest hand remains
			int n = 1;
			
			//Keep eliminating players until 1 remains, or more if there are tied players
			while(playersInPlay.size() > n) {
				int result = playersStrongestHand.get(n - 1).compareTo(playersStrongestHand.get(n));
				if(result > 0) {
					System.out.println(playersInPlay.get(n).getId() + " with " + playersStrongestHand.get(n).getWholeHand() + " lost with " + playersStrongestHand.get(n).getResString());
					playersStrongestHand.remove(n);
					playersInPlay.remove(n);
				} else if(result < 0) {
					while(n > 1) {
						System.out.println(playersInPlay.get(n - 1).getId() + " with " + playersStrongestHand.get(n - 1).getWholeHand() + " and ");
						playersStrongestHand.remove(n - 1);
						playersInPlay.remove(n - 1);
						n--;
					}
					
					System.out.println(playersInPlay.get(n - 1).getId() + " with " + playersStrongestHand.get(n - 1).getWholeHand() + " lost with " +  playersStrongestHand.get(n - 1).getResString());
					playersStrongestHand.remove(n - 1);
					playersInPlay.remove(n - 1);
					
				} else {
					n++;
				}
			}
			
			if(playersInPlay.size() > 1) {
				System.out.print("Tie between: ");
				printChoices(playersInPlay, "and");
				System.out.println("\nWith a hand of: " + playersStrongestHand.get(0).getResString());
			} else {
				System.out.println(playersInPlay.get(0).getId() + " with " + playersStrongestHand.get(0).getWholeHand() + " won with " + playersStrongestHand.get(0).getResString());
			}
			
			//Pay out all the players, split pot if tie
			for(Player player : playersInPlay) {
				player.wonMoney(pot/playersInPlay.size());
			}
			
		} else {
			System.out.println(playersInPlay.get(0) + " won because everyone else folded!");
			playersInPlay.get(0).wonMoney(pot);
		}
	}
}
