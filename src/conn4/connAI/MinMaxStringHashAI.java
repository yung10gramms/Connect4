package conn4.connAI;

import java.util.ArrayList;
import java.util.HashMap;

import conn4.MainClass;

/**
 * <p>
 * This class implements a min max algorithm that plays 
 * the game connect 4 and for each depth, it stores all 
 * evaluated positions into a HashMap, using a 
 * <b> fixed-length-coding </b> approach for the Hash key.
 * </p>
 * */
public class MinMaxStringHashAI extends MinMaxHashAI {

	/**
	 * HashMap to store previously evaluated positions. It is
	 * supposed to be cleared before every time the AI plays a move 
	 * */
	protected HashMap<String, Integer> map;
	
	public long timeWaistedInComparing;
	public long timeWaistedInHashing;
	
	/**
	 * buffer used to hash the game matrix
	 * */
	private char[] string;
	
	int numberOfCalls = 0;
	
	/**
	 * @brief Constructor of MinMaxStringHashAI class. This class 
	 * implements a min max algorithm that plays the game connect 4
	 * and for each depth, it stores all evaluated positions into 
	 * a HashMap, using a <b> fixed-length-coding </b> approach 
	 * for the Hash key.
	 * 
	 * @param mc instnce of MainClass
	 * */
	public MinMaxStringHashAI(MainClass mc) {
		super(mc);
		map = new HashMap<String, Integer>();
		timeWaistedInComparing = 0L;
		timeWaistedInHashing = 0L;
		string = new char[42];
	}
	
	/**
	 * @brief function that hashes the game matrix by passing 
	 * each cell to a string.
	 * */
	public String hashBoard() {

		int k = 0;
		for(int i = 0; i < mat.length; i ++)
			for(int j = 0; j < mat[0].length; j ++) { 
				if(mat[i][j] == 1)
					string[k] = MainClass.p1;
				else if(mat[i][j] == -1)
					string[k] = MainClass.pm1;
				else
					string[k] = MainClass.empty;
				k++;
			}
			
		return new String(string);
	}
	
	/**
	 * @brief implementation of min max algorithm with
	 * alpha beta pruning, using a HashMap to store previously
	 * evaluated positions. The top move that this function 
	 * calculates is stored at the MainClass instance using
	 * the setTopMove function.
	 * 
	 * @param player which player is playing
	 * 
	 * @return the evaluation of the position
	 * */
	protected int minMaxMap(int player) {

		numberOfCalls ++;
		
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
			long t1 = System.nanoTime();
			String hb = hashBoard();
			timeWaistedInHashing += System.nanoTime() - t1;
			long t0 = System.nanoTime();
			Integer mtmp = map.get(hb);
			timeWaistedInComparing += System.nanoTime() - t0;
			if(mtmp == null) {
				evalTmp = minMaxMap(-player);
				map.put(hb, evalTmp);
			} else {
				num_hit++;
				evalTmp = (int) mtmp;
			}
			
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
			else if(player*evalTmp == player*eval_top) {
				if(mainClass.rand.nextInt() % 2 == 0) {
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
	
	public void playAI() {
		numberOfCalls = 0;
		map.clear();
		int eval = minMaxMap(player);
		System.out.println(" max depth = " +maxDepth+ " num of elements in map " +map.size()+ ", number of calls " +numberOfCalls+ ", eval = " +eval );
		
	}
	
	public void setPlayer(int p) {
		player = p;
	}
}
