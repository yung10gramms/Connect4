package conn4.connAI;

public interface Connect4AI {

	/**
	 * @brief function that sets the MainClass member variable topMove to the move to play
	 *  
     */
	void playAI();
	
	/** 
	 * @brief setter function for variable player. Player can be set to either 1 or -1
	 * 
	 * */
	void setPlayer(int playerNo);
	
}
