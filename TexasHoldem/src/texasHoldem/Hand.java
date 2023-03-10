package texasHoldem;

import java.util.HashMap;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Collections;

//Compares two hands
//Used to figure out hand is strongest during showdown
public class Hand implements Comparable<Hand> {
	private int handType; //Higher number = stronger hand, 1-10
	private int[] kickers; //Organized by most important to least important
	private ArrayList<Card> cards; //Always 5
	private HashMap<Integer, String> handTypeToString;
	private HashMap<Integer, String> numsToString;
	private boolean wonByType;
	private int numThatWon;
	
	public Hand(ArrayList<Card> cards, HashMap<Integer, String> handTypeToString, HashMap<Integer, String> numsToString) {
		this.cards = cards;
		this.handTypeToString = handTypeToString;
		this.numsToString = numsToString;
		calcRanking();
	}
	
	private boolean isSameSuit() {
		String reqSuit = cards.get(0).getSuit();
		for(int i = 1; i < cards.size(); i++) {
			if(!cards.get(i).getSuit().equals(reqSuit)) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean isStraight() {
		int reqNum = cards.get(0).getNum();
		for(int i = 1; i < cards.size(); i++) {
			reqNum--;
			if(cards.get(i).getNum() != reqNum) {
				return false;
			}
		}
		
		return true;
	}
	
	//Maps card number to number of occurrences
	private HashMap<Integer, Integer> getPairs (){
		HashMap<Integer, Integer> answer = new HashMap<>();
		
		for(Card card : cards) {
			answer.put(card.getNum(), answer.getOrDefault(card.getNum(), 0) + 1);
		}
		
		return answer;
	}
	
	private void calcRanking (){
		//Highest card first
		Collections.sort(cards, Collections.reverseOrder());
		boolean flush = isSameSuit();
		boolean straight = isStraight();
		HashMap<Integer, Integer> pairs = getPairs();
		
		//Royal Flush
		if(cards.get(4).getNum() == 10 && flush && straight) {
			System.out.println("Got here");
			handType = 10;
			kickers = null;
		} else if(flush && straight) { //Straight Flush
			kickers = new int[1];
			kickers[0] = cards.get(0).getNum();
			
			handType = 9;
		} else if(pairs.size() == 2) { //Four of a kind and Full House
			kickers = new int[2];
			
			for(int suitnum : pairs.keySet()) {
				if(pairs.get(suitnum) == 4) {
					kickers[0] = suitnum;
				} else {
					kickers[1] = suitnum;
				}
			}
			
			if(kickers[0] != 0) {
				handType = 8;
			} else {
				for(int suitnum : pairs.keySet()) {
					if(pairs.get(suitnum) == 3) {
						kickers[0] = suitnum;
					} else {
						kickers[1] = suitnum;
					}
				}
				
				handType = 7;
			}
			
		} else if(flush) { //Flush
			kickers = new int[5];
			for(int i = 0; i < 5; i++) {
				kickers[i] = cards.get(i).getNum();
			}
			handType = 6;
		} else if(straight) { //Straight
			kickers = new int[1];
			kickers[0] = cards.get(0).getNum();
			handType = 5;
		} else if(pairs.size() == 3) { //Three of a Kind and Two pair
			kickers = new int[3];
			//keySet is sorted by lowest first, not highest first
			//Highest card needs to have precedent, so kickIndex starts at 2 and is decremented
			int kickIndex = 2;
			
			for(int suitnum : pairs.keySet()) {
				if(pairs.get(suitnum) == 3) {
					kickers[0] = suitnum;
				} else {
					if(kickIndex == 0) {
						continue;
					}
					kickers[kickIndex] = suitnum;
					kickIndex--;
				}
			}
			
			if(kickers[0] != 0) {
				handType = 4;
			} else {
				kickIndex = 1;
				for(int suitnum : pairs.keySet()) {
					if(pairs.get(suitnum) == 2) {
						kickers[kickIndex] = suitnum;
						kickIndex--;
					} else {
						kickers[2] = suitnum;
					}
				}
				
				handType = 3;
			}
			
		} else if(pairs.size() == 4) { //One pair
			kickers = new int[4];
			int kickIndex = 3;
			
			for(int suitnum : pairs.keySet()) {
				if(pairs.get(suitnum) == 2) {
					kickers[0] = suitnum;
				} else {
					kickers[kickIndex] = suitnum;
					kickIndex--;
				}
			}
			
			handType = 2;
		} else { //High card
			kickers = new int[5];
			for(int i = 0; i < 5; i++) {
				kickers[i] = cards.get(i).getNum();
			}
			handType = 1;
		}
	}
	
	public int compareTo(Hand otherHand) {
		if(handType != otherHand.handType) {
			wonByType = true;
			otherHand.wonByType = true;
			return handType - otherHand.handType;
		}
		
		wonByType = false;
		otherHand.wonByType = false;
		numThatWon = 0;
		otherHand.numThatWon = 0;
		
		for(int i = 0; i < kickers.length; i++) {
			if(kickers[i] != otherHand.kickers[i]) {
				numThatWon = kickers[i];
				otherHand.numThatWon = otherHand.kickers[i];
				return kickers[i] - otherHand.kickers[i];
			}
		}
		
		return 0;
	}
	
	public String getWholeHand() {
		StringBuilder str = new StringBuilder();
		
		for(int i = 0; i < 5; i++) {
			str.append(cards.get(i).toString());
			if(i < 4) {
				str.append(", ");
			}
		}
		
		return str.toString();
	}
	
	public String getResString() {
		StringBuilder retString = new StringBuilder(handTypeToString.get(handType));
		if(wonByType) {
			return retString.toString();
		}
		
		retString.append(" with high card of ");
		retString.append(numsToString.get(numThatWon));
		return retString.toString();
	}
	
	public String toString() {
		StringBuilder buildString = new StringBuilder("Hand type: ");
		buildString.append(handTypeToString.get(handType));

		for(int i = 0; i < 5; i++) {
			buildString.append("\nCard ");
			buildString.append(i + 1);
			buildString.append(": ");
			buildString.append(cards.get(i).toString());
		}
		
		return buildString.toString();
	}
}
