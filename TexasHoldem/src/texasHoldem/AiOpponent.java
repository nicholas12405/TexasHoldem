package texasHoldem;

//AiOpponent object has everything a player object does but also needs AI logic
public class AiOpponent extends Player {
	
	public AiOpponent(int id, int startMoney) {
		super("Computer player " + Integer.toString(id), startMoney);
	}
	
	//For now the AIs are completely random
	//1-40 = Call
	//41-70 = Raise
	//71-100 = Fold
	//Returns amount the AI is contributing to the pot
	public int getChoice(int currentAmount, int minRaise) {
		int num = 1 + (int)(Math.random() * 100);
		
		if(num < 41) {
			
			return call(currentAmount);
			
		} else if(num < 71) {
			int maxRaise = getMoney() - (currentAmount - getCurrentBet());
			if(minRaise <= maxRaise) {
				return call(currentAmount + minRaise);
			} else {
				return call(currentAmount);
			}
		}
		
		//In fold numbers still check if available
		if(currentAmount == getCurrentBet()) {
			return call(getCurrentBet());
		}
		fold();
		return -1;
	}
	
}
