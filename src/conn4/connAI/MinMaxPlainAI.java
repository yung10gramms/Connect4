package conn4.connAI;

import java.util.ArrayList;

import conn4.MainClass;

/**
 * <p>
 * This class implements a min max algorithm that plays 
 * the game connect 4. This class <b> does not </b> store 
 * evaluated positions, resulting in being less efficient than 
 * other approaches that implement the same algorithm.
 * </p>
 * 
 * <p>
 * However, it is included in this project as it is interesting to 
 * compare running time efficiency to other approaches.
 * </p>
 * */
public class MinMaxPlainAI implements Connect4AI {

	/**
	 * MainClass instance
	 * */
	protected MainClass mainClass;
	
	/**
	 * maximum depth for the min max algorithm. A suggested value
	 * is 8.
	 * */
	private int maxDepth;	
	
	protected int curDepth;
	
	/**
	 * reference to mat variable of the MainClass instance
	 * */
	protected int[][] mat;
	
	/**
	 * player num, can be 1 or -1
	 * */
	protected int player;

	/**
	 * @brief function to set maximum depth for the min max 
	 * algorithm. A suggested value is 8.
	 * */
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}
	
	/**
	 * constructor for class MinMaxPlainAI. This class implements a basic min max algorithm with
	 * alpha-beta prunning, without storing any past calculations. 
	 * 
	 *  @param mc MainClass instance
	 *  @param player can be either 1 or -1
	 *  @param maxDepth suggested value 8
	 * */
	public MinMaxPlainAI(MainClass mc, int player, int maxDepth) {
		this.mainClass = mc;
		mat = mainClass.mat;
		this.player = player;
		this.maxDepth = maxDepth;
	}

	/**
	 * @brief a simple implementation of the min max algorithm
	 * using alpha beta pruning.
	 * 
	 * @param player which player is playing
	 * 
	 * @return evaluation of the position
	 * */
	protected int minMax(int player) {
		
		int eval = mainClass.checkFor4();
		
		if(eval != 0) {
			return eval;
		}
		
		ArrayList<Integer> avail = mainClass.availableMoves(); 
		
		
		if (avail.size() == 0) {
			return 0;
		}
		
		
		int topMove = avail.get(0);
		int eval_top = 0;
		if (curDepth >= maxDepth)
			return eval;
		
		for(int i = 0; i < avail.size(); i ++) {
			
			int move = avail.get(i);
			if(mainClass.topIndex[move] >= mainClass.numRows)
				continue;
			
			curDepth++;
			mainClass.playMove(move, player);
			
			
			int evalTmp;
			
			evalTmp = minMax(-player);
			
			if (i == 0) {
				eval_top = evalTmp;
				eval = evalTmp;
			}
		
			if(player == -evalTmp) {
				mainClass.unplayMove(move, player);
				curDepth--;
				continue;
			}
			
			if(player == evalTmp) {
				eval = evalTmp;
				mainClass.setTopMove(move);
				mainClass.unplayMove(move, player);
				curDepth--;
				return evalTmp;
			}

			if(player*evalTmp > player*(eval_top))
			{
				eval_top = evalTmp;
				eval = eval_top;
				topMove = move;
			}
			// add this if you want some randomization
			else if(player*evalTmp == player*eval_top) {
				if(mainClass.rand.nextInt()%2 == 0) {
					eval_top = evalTmp;
					topMove = move;	
				}	
			}
			
			mainClass.unplayMove(move, player);
			curDepth--;
		}
		
		mainClass.setTopMove(topMove);
		return eval;
	}
	
	@Override
	public void playAI() {
		minMax(player);
		
	}

	@Override
	public void setPlayer(int playerNo) {
		player = playerNo;
	}

}
