package texasHoldem;

import java.util.ArrayList;

public class Player {
	private ArrayList<Card> hand;
	private String id;
	private int money;
	private int currentBet;
	private boolean folded;
	
	public Player(String id, int startMoney) {
		this.id = id;
		money = startMoney;
		currentBet = 0;
		hand = new ArrayList<>();
		folded = false;
	}
	
	public ArrayList<Card> getHand() {
		return hand;
	}


	public String getId() {
		return id;
	}

	public int getMoney() {
		return money;
	}

	public int getCurrentBet() {
		return currentBet;
	}

	public boolean isFolded() {
		return folded;
	}

	public void addCard(Card card) {
		hand.add(card);
	}
	
	//Covers call, check, and raise
	//Returns the amount of money the player is able to contribute to the pot
	public int call(int callBy) {
		int difference = callBy - currentBet;
		int returnVal = Math.min(difference, money);
		//System.out.println("Old money value for " + id + ": " + money);
		money -= returnVal;
		//System.out.println("New money value for " + id + ": " + money);
		currentBet = callBy;
		
		return returnVal;
	}
	
	public void fold() {
		folded = true;
	}
	
	public ArrayList<Card> returnCards() {
		folded = false;
		ArrayList<Card> temp = hand;
		hand = new ArrayList<Card>();
		currentBet = 0;
		return temp;
	}
	
	public void wonMoney(int amount) {
		money += amount;
	}
	
	public String toString() {
		return id;
	}
}
