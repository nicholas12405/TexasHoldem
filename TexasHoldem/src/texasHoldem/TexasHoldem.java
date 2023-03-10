package texasHoldem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class TexasHoldem {
	
	static final int SB = 1;
	static final int BB = 2;
	static final int STARTMONEY = 100;
	
	public static void printCards(ArrayList<Card> cards) {
		for(Card card : cards) {
			System.out.println(card.toString());
		}
	}

	public static void main(String[] args) {
		HashMap<Integer, String> handTypeToString = new HashMap<>();
		HashMap<Integer, String> numsToString = new HashMap<>();
		
		//For toString of hand type
		handTypeToString.put(1, "High Card");
		handTypeToString.put(2, "One Pair");
		handTypeToString.put(3, "Two Pair");
		handTypeToString.put(4, "Three of a Kind");
		handTypeToString.put(5, "Straight");
		handTypeToString.put(6, "Flush");
		handTypeToString.put(7, "Full House");
		handTypeToString.put(8, "Four of a Kind");
		handTypeToString.put(9, "Straight Flush");
		handTypeToString.put(10, "Royal Flush");
		
		//For toString of number
		numsToString.put(2, "2");
		numsToString.put(3, "3");
		numsToString.put(4, "4");
		numsToString.put(5, "5");
		numsToString.put(6, "6");
		numsToString.put(7, "7");
		numsToString.put(8, "8");
		numsToString.put(9, "9");
		numsToString.put(10, "T");
		numsToString.put(11, "J");
		numsToString.put(12, "Q");
		numsToString.put(13, "K");
		numsToString.put(14, "A");
		
		ArrayList<Card> deck = new ArrayList<>();
		
		//Generate deck
		for(int i = 0; i < 52; i++) {
			String suit = "diamonds";
			if(i < 13) {
				suit = "clubs";
			} else if(i < 26) {
				suit = "spades";
			} else if(i < 39) {
				suit = "hearts";
			}
			
			int cardNum = i%13 + 2;
			deck.add(new Card(cardNum, suit, numsToString));
		}
		
		//Get number of players
		Scanner in = new Scanner(System.in);
		int numPlayers = -1;
		
		System.out.println("Enter your name: ");
		String name = in.nextLine();
		
		//Input checking
		while(numPlayers == -1) {
			try {
				System.out.print("Enter the number of players, between 2 and 9: ");
				numPlayers = in.nextInt();
			} catch(Exception e) {
				numPlayers = -1;
				System.out.println("You need to enter a number");
			}
			
			if(numPlayers < 2 || numPlayers > 9) {
				System.out.println("Number of players should be between 2 and 10");
				numPlayers = -1;
			}
		}
		
		System.out.println("---------------------------------------");
		System.out.println("Small blind of $" + SB + ", big blind of $" + BB + ", you start with $" + STARTMONEY);
		
		Player you = new Player(name, STARTMONEY);
		
		ArrayList<AiOpponent> opponents = new ArrayList<>();
		
		//Initialize AIs
		for(int i = 1; i < numPlayers; i++) {
			opponents.add(new AiOpponent(i, STARTMONEY));
		}
		
		int numRounds = 0;
		
		//Add all players to a list
		ArrayList<Player> allPlayers = new ArrayList<>();
		allPlayers.addAll(opponents);
		allPlayers.add(you);
		Collections.shuffle(allPlayers);
		
		System.out.print("Seating order: ");
		
		for(Player player : allPlayers) {
			System.out.print(player.getId() + " -> ");
		}
		
		System.out.println(allPlayers.get(0).getId());
		
		//Instantiate Table here
		Table table = new Table(BB, SB, allPlayers, in, deck);
		
		//Infinite game loop
		//Ends when player is out or beats all the AIs
		while(true) {
			numRounds++;
			System.out.println("---------------------------------------");
			System.out.println("Round " + numRounds);
			System.out.println("---------------------------------------");
			
			table.beginRound();
			
			System.out.println("---------------------------------------");
			System.out.println("Your hand: ");
			printCards(you.getHand());
			System.out.println("");
			
			System.out.println("Your money: " + you.getMoney());
			System.out.println("---------------------------------------");
			
			for(AiOpponent Ai : opponents) {
				System.out.println(Ai.getId() + "'s money: " + Ai.getMoney());
				System.out.println("---------------------------------------");
			}
			
			System.out.println("---------------------------------------");
			System.out.println("Pre-flop begins!");
			System.out.println("---------------------------------------");
			
			table.runRound();
			
			if(table.getPlayersInPlay() != 1) {
				table.addTableCards(3);
				
				System.out.println("---------------------------------------");
				System.out.println("Your hand: ");
				printCards(you.getHand());
				System.out.println("");
				
				System.out.println("Your money: " + you.getMoney());
				System.out.println("---------------------------------------");
				
				for(AiOpponent Ai : opponents) {
					System.out.println(Ai.getId() + "'s money: " + Ai.getMoney());
					System.out.println("---------------------------------------");
				}
				
				System.out.println("---------------------------------------");
				System.out.println("The pot: " + table.getPot());
				System.out.println("---------------------------------------");
				
				System.out.println("The flop: ");
				table.printTable();
				table.runRound();
			}
			
			if(table.getPlayersInPlay() != 1) {
				table.addTableCards(1);
				
				System.out.println("---------------------------------------");
				System.out.println("Your hand: ");
				printCards(you.getHand());
				System.out.println("");
				
				System.out.println("Your money: " + you.getMoney());
				System.out.println("---------------------------------------");
				
				for(AiOpponent Ai : opponents) {
					System.out.println(Ai.getId() + "'s money: " + Ai.getMoney());
					System.out.println("---------------------------------------");
				}
				
				System.out.println("---------------------------------------");
				System.out.println("The pot: " + table.getPot());
				System.out.println("---------------------------------------");
				
				System.out.println("The turn: ");
				table.printTable();
				table.runRound();
			}
			
			if(table.getPlayersInPlay() != 1) {
				table.addTableCards(1);
				
				System.out.println("---------------------------------------");
				System.out.println("Your hand: ");
				printCards(you.getHand());
				System.out.println("");
				
				System.out.println("Your money: " + you.getMoney());
				System.out.println("---------------------------------------");
				
				for(AiOpponent Ai : opponents) {
					System.out.println(Ai.getId() + "'s money: " + Ai.getMoney());
					System.out.println("---------------------------------------");
				}
				
				System.out.println("---------------------------------------");
				System.out.println("The pot: " + table.getPot());
				System.out.println("---------------------------------------");
				
				System.out.println("The river: ");
				table.printTable();
				table.runRound();
			}
			
			table.resolveWinner(handTypeToString, numsToString);
			
			table.cleanupAfterRound();
			
			System.out.println("Quit?");
			String answer = in.nextLine();
			if(answer.toLowerCase().equals("quit")) {
				break;
			}
			
			if(you.getMoney() == 0) {
				System.out.println("You lost!");
				break;
			} else if(allPlayers.size() == 1) {
				System.out.println("You won!");
				break;
			}
		}
		
		in.close();
	}

}
