package texasHoldem;

import java.util.HashMap;
import java.lang.StringBuilder;

public class Card implements Comparable<Card> {
	private int num;
	private String suit;
	private HashMap<Integer, String> numsToString;
	
	public Card(int num, String suit, HashMap<Integer, String> numsToString) {
		this.num = num;
		this.suit = suit;
		this.numsToString = numsToString;
	}
	
	public int getNum() {
		return num;
	}

	public String getSuit() {
		return suit;
	}

	public HashMap<Integer, String> getNumsToString() {
		return numsToString;
	}



	public int compareTo(Card otherCard) {
		if(num == otherCard.num) {
			return 0;
		} else if(num > otherCard.num) {
			return 1;
		} else {
			return -1;
		}
	}
	
	public String toString() {
		StringBuilder buildString = new StringBuilder(numsToString.get(num));
		buildString.append(" of ");
		buildString.append(suit);
		
		return buildString.toString();
	}
}
