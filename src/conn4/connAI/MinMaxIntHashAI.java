package conn4.connAI;

import java.util.ArrayList;
import java.util.HashMap;

import conn4.MainClass;

/**
 * This class implements a min max algorithm that plays 
 * the game connect 4 and for each depth, it stores all 
 * evaluated positions into a HashMap, using a 
 * <b> non-fixed-length-coding </b> approach for the Hash key.
 * */
public class MinMaxIntHashAI extends MinMaxHashAI {

	/**
	 * HashMap to store previously evaluated positions. It is
	 * supposed to be cleared before every time the AI plays a move 
	 * */
	protected HashMap<String, Integer> map;

	public long timeWaistedInComparing;
	public long timeWaistedInHashing;
	int maxArrLen = 0;
	
	int numberOfCalls = 0;
	
	
	/**
	 * buffer used to hash the game matrix
	 * */
	private char[] buffer;
	private final int bufferSize = 15;
	
	/**
	 * @brief Constructor of MinMaxIntHashAI class. This class 
	 * implements a min max algorithm that plays the game connect 4
	 * and for each depth, it stores all evaluated positions into 
	 * a HashMap, using a non-fixed-length-coding approach for the
	 * Hash key.
	 * 
	 * @param mc instnce of MainClass
	 * */
	public MinMaxIntHashAI(MainClass mc) {
		super(mc);
		map = new HashMap<>();
		timeWaistedInComparing = 0L;
		timeWaistedInHashing = 0L;
		buffer = new char[bufferSize];

	}
	

	/**
	 * @brief function that hashes a position using a non fixed
	 * length string. The function iterates the position matrix
	 * and represents 1 to bit '0', -1 to bits '10' and a space,
	 * which means next line as '11'. These are grouped into bytes,
	 * padded with zeros at the right-most side. The final byte of
	 * the String represents the number of information bits in the
	 * String.
	 * 
	 * @return string representation of the game matrix
	 * */
	public String hashBoard() {
		
		int quad = 0, bitcount = 0, stringIndex = 0;
		
		for(int j = 0; j < mat[0].length; j ++) {
			for(int i = 0; i < mat.length; i ++) {			
				if(mat[i][j] == 1) {

					bitcount ++;
					if(bitcount == 8) {
						buffer[stringIndex] = (char) quad;
						stringIndex ++;
						bitcount = 0;
						quad = 0;
					} 

				} else if(mat[i][j] == -1) {

					quad +=  1 << (bitcount + 1);
					
					bitcount += 2;
					if(bitcount == 8) {
						buffer[stringIndex] = (char) quad;
						stringIndex ++;
						bitcount = 0;
						quad = 0;
					} else if(bitcount == 9) {
						buffer[stringIndex] = (char) quad;
						stringIndex ++;
						bitcount = 1;
						quad = 1;
					}
				} else if(i < mainClass.numRows && j < mainClass.numCols) {

					quad +=  1 << (bitcount + 1);
					
					bitcount ++;
					
					if(bitcount == 8) {
						buffer[stringIndex] = (char) quad;
						stringIndex ++;
						bitcount = 0;
						quad = 0;
					} 

					quad +=  1 << (bitcount + 1);
					
					bitcount ++;
					if(bitcount == 8) {
						buffer[stringIndex] = (char) quad;
						stringIndex ++;
						bitcount = 0;
						quad = 0;
					} 
					i = mat.length;
				}
				
			}
		}
		if(quad != 0) {
			buffer[stringIndex] = (char) quad;
			stringIndex ++;
		}
		
		buffer[stringIndex] = (char) bitcount;
		stringIndex ++;
		
		if(stringIndex < bufferSize) {
			for(int i = stringIndex; i < bufferSize; i ++) {
				buffer[i] = (char) 0;
			}
		}

		return new String(buffer, 0, stringIndex);
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
			if(hb.length() > maxArrLen) {
				maxArrLen = hb.length();
			}
			long t0 = System.nanoTime();
			Integer mtmp = (Integer) map.get(hb);
			
			timeWaistedInComparing += System.nanoTime() - t0;
			if(mtmp == null) {
				evalTmp = minMaxMap(-player);
				map.put(hb, (Integer) evalTmp);
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
		System.out.println("max arrlen " +maxArrLen+ " max depth = " +maxDepth+ " num of elements in map " +map.size()+ ", number of calls " +numberOfCalls+", eval = " +eval);
		maxArrLen = 0;		
	}
	
	public void setPlayer(int p) {
		player = p;
	}
		

}
