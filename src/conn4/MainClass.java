package conn4;

import java.util.ArrayList;
import java.util.Random;

import conn4.connAI.MinMaxIntHashAI;
import conn4.connAI.MinMaxStringHashAI;

public class MainClass {

	/**
	 * game matrix of the connect 4 game
	 * */
	public int[][] mat;

	/**
	 * array with the lowest free cell of each column. If an
	 * element of this variable is 0, that means that the 
	 * corresponding column is free. In the same way, if an
	 * element is equal to numRows, it means that the
	 * column in question is full
	 * */
	public int[] topIndex;

	public int numRows = 6, numCols = 7;
	
	/**
	 * every AI will output its best move here
	 * */
	private int topMove;
	
	private int numOfEmptyCells;

	public Random rand;
	private StandardInputRead sir;
	
	public static final char p1 = 'X';
	public static final char pm1 = 'O';
	public static final char empty = ' ';

	
	public MainClass() {
		sir = new StandardInputRead();
		resetBoard();
		rand = new Random();
		
	}
	
	public void resetBoard() {
		mat = new int[numRows][numCols];
		topIndex = new int[numCols];
		numOfEmptyCells = numRows * numCols;
	}
	
	public void setTopMove(int tm) {
		topMove = tm;
	}
	
	public int checkRow(int i, int j) {
		try {
			if(mat[i][j] == mat[i][j+1] && mat[i][j] == mat[i][j+2] && mat[i][j] == mat[i][j+3]) {
				return mat[i][j];
			}
		} catch (ArrayIndexOutOfBoundsException ar) {
			return 0;
		}
		return 0;
	}
	
	public int checkCol(int i, int j) {
		try {
			if(mat[i][j] == mat[i+1][j] && mat[i][j] == mat[i+2][j] && mat[i][j] == mat[i+3][j]) {
				return mat[i][j];
			}
		} catch (ArrayIndexOutOfBoundsException ar) {
			return 0;
		}
		return 0;
	}
	
	public int checkDiag(int i, int j) {
		try {
			if(mat[i][j] == mat[i+1][j+1] && mat[i][j] == mat[i+2][j+2] && mat[i][j] == mat[i+3][j+3]) {
				return mat[i][j];
			}
		} catch (ArrayIndexOutOfBoundsException ar) {
			return 0;
		}
		return 0;
	}
	
	public int checkCounterDiag(int i, int j) {
		try {
			if(mat[numRows-i-1][j] == mat[numRows-i-2][j+1] && mat[numRows-i-1][j] == mat[numRows-i-3][j+2] && 
					mat[numRows-i-1][j] == mat[numRows-i-4][j+3]) {
				return mat[numRows-i-1][j];
			} 
		}
		catch (ArrayIndexOutOfBoundsException ar) {
			return 0;
		}
		return 0;
	}
	
	public int checkFor4() {
		for ( int i = 0; i < numRows; i ++ ) {
			for ( int j = 0; j <  numCols; j ++ ) {
				if(checkRow(i, j) != 0) 
					return checkRow(i, j);
				if(checkCol(i, j) != 0) 
					return checkCol(i, j);
				if(checkDiag(i, j) != 0)
					return checkDiag(i, j);
				if(checkCounterDiag(i, j) != 0) 
					return checkCounterDiag(i, j);
			}
		}
		return 0;
	}
	
	
	public ArrayList<Integer> availableMoves() {
		ArrayList<Integer> out = new ArrayList<>();		
		for (int i = 0; i < numCols; i ++) {
			if(topIndex[i] < numRows) {
				if(topIndex[i] >= 6 || i >= 7)
					System.out.println("why?");
				
				Integer tmp = (Integer) i;
				out.add(tmp);
			}
		}		
		return out;
	}
	
	public void playMove(int index, int player) {
		mat[topIndex[index]][index] = player;
		topIndex[index]++;
	}
	
	public void unplayMove(int index, int player) {
		topIndex[index]--;
		mat[topIndex[index]][index] = 0;
		
	}
	
	
	public void printBoard() {
		System.out.println("-------------------------------------");
		for(int i = numRows-1; i >=0 ; i --) {
			System.out.print("| ");
			for(int j = 0; j < numCols; j ++) {
				String printSt = empty + " ";
				if(mat[i][j] == 1)
					printSt = p1 +" ";
				if(mat[i][j] == -1)
					printSt = pm1 + " ";
				
				System.out.print(printSt + " | ");
			}
			System.out.println("");
		}
		System.out.println("-------------------------------------");
		System.out.print("| ");
		int idx;
		for(int j = 0; j < numCols; j ++) {
			idx = j + 1;
			String printSt = idx + " ";		
			System.out.print(printSt + " | ");
		}
		System.out.println("");
		System.out.println("-------------------------------------");
	}
	
	
	public int humanPlayMove() {

		int num = sir.readPositiveInt("Play a move in the i-th column (1 to 7): ") - 1;
		
		if(num < 0 || num > 6) {
			System.out.println("\nFalse input. Number must be between 1 and 7, please try again.");
			
			return humanPlayMove();
		}
		if(topIndex[num] >= numRows) {
			System.out.println("\nFalse input. Column is full, please try again.");
			
			return humanPlayMove();
		}
		
		return num;
	}
	
	
	public void playgame(boolean useMap) {
		
		MinMaxStringHashAI conn4AI = new MinMaxStringHashAI(this);
		conn4AI.setPlayer(1);
		conn4AI.setMaxDepth(12);
		
		int turn = 1;
		
		if (rand.nextInt() % 2 == 0) {
			System.out.println("Player 1 (AI) plays first!");
		} else {
			System.out.println("Player -1 (human) plays first!");	
			turn = -1;
		}
		
		while(checkFor4() == 0 && numOfEmptyCells > 0) {
			System.out.println("Now playing : " + turn +" (" + ((turn == 1) ? "X" : "O") + ")");
			printBoard();
			
			if(turn == 1)
				conn4AI.playAI();
			else
				topMove = humanPlayMove();
			
//			mat = cpy;
			playMove(topMove, turn);
			turn = - turn;
			numOfEmptyCells--;
		}
		printBoard();
		System.out.println(checkFor4() + " player won");
	}
	
	public void ai_against_ai() {
		
		MinMaxIntHashAI conn4AI_p1 = new MinMaxIntHashAI(this);
		
		conn4AI_p1.setPlayer(1);
		conn4AI_p1.setMaxDepth(11);
		
		MinMaxStringHashAI conn4AI_pm1 = new MinMaxStringHashAI(this);
		conn4AI_pm1.setPlayer(-1);
		conn4AI_pm1.setMaxDepth(11);
		
		Random rand = new Random();
		int turn = 1;
		
		if (rand.nextInt() % 2 == 0) {
			System.out.println("Player 1 (first AI) plays first!");
		} else {
			System.out.println("Player -1 (other AI) plays first!");	
			turn = -1;
		}
		
		while(checkFor4() == 0 && numOfEmptyCells > 0) {
			System.out.println("Now playing : " + turn +" (" + ((turn == 1) ? "X" : "O") + ")");
			printBoard();
			

			if(turn == 1)
				conn4AI_p1.playAI();
			else
				conn4AI_pm1.playAI();
			
			
			playMove(topMove, turn);
			turn = - turn;
			numOfEmptyCells--;
		}
		printBoard();
		int result = checkFor4();
		if(result != 0)
			System.out.println(" player " + result + " won");
		else
			System.out.println("draw");
		
		double p1hashmili = (double) conn4AI_p1.timeWaistedInHashing/1000000;
		double p1hashsec = (double) p1hashmili/1000;
		double p1compmili = (double) conn4AI_p1.timeWaistedInComparing/1000000;
		double p1compsec = (double) p1compmili/1000;
		System.out.println("Stats for player 1:");
		System.out.println("Hit nums: " + conn4AI_p1.getNumHit());
		System.out.println("Time waisted in hashing: " +conn4AI_p1.timeWaistedInHashing +" ns or " +p1hashmili +" ms or "+p1hashsec +" sec");
		System.out.println("Time waisted in comparing: " +conn4AI_p1.timeWaistedInComparing +" ns or "+p1compmili+" ms or "+p1compsec+" sec");
		
		double pm1hashmili = (double) conn4AI_pm1.timeWaistedInHashing/1000000;
		double pm1hashsec = (double) pm1hashmili/1000;
		double pm1compmili = (double) conn4AI_pm1.timeWaistedInComparing/1000000;
		double pm1compsec = (double) pm1compmili/1000;
		System.out.println("\nStats for player -1:");
		System.out.println("Hit nums: " + conn4AI_pm1.getNumHit());
		System.out.println("Time waisted in hashing: " +conn4AI_pm1.timeWaistedInHashing +" ns or " +pm1hashmili +" ms or "+pm1hashsec +" sec");
		System.out.println("Time waisted in comparing: " +conn4AI_pm1.timeWaistedInComparing +" ns or "+pm1compmili+" ms or "+pm1compsec+" sec");
		
	}
	
	public void ai_against_ai_championship() {
		Random rand = new Random();
		int turn = 1;
		int num_of_games = 100, num_of_p1_wins = 0, num_of_draws = 0;

		MinMaxIntHashAI conn4AI_p1 = new MinMaxIntHashAI(this);
		
		conn4AI_p1.setPlayer(1);
		conn4AI_p1.setMaxDepth(11);
		
		MinMaxStringHashAI conn4AI_pm1 = new MinMaxStringHashAI(this);
		conn4AI_pm1.setPlayer(-1);
		conn4AI_pm1.setMaxDepth(11);
		
		
		for(int l = 0; l < num_of_games; l ++) {
			System.out.println("game no: " +l);
			resetBoard();
			
			if (rand.nextInt() % 2 == 0) {
				System.out.println("Player 1 (first AI) plays first!");
			} else {
				System.out.println("Player -1 (other AI) plays first!");	
				turn = -1;
			}
			while(checkFor4() == 0 && numOfEmptyCells > 0) {
				

				if(turn == 1)
					conn4AI_p1.playAI();
				else
					conn4AI_pm1.playAI();
				
				playMove(topMove, turn);
				turn = - turn;
				numOfEmptyCells--;
			}
			int result = checkFor4();
			if(result == 1)
				num_of_p1_wins ++;
			else if(result == 0)
				num_of_draws ++;
		}
		System.out.println("num of p1 wins " +num_of_p1_wins);
		System.out.println("num of draws " +num_of_draws);
	}
	
	
	
	
	public static void main(String[] args) {
		MainClass m = new MainClass();

		// human against ai
//		m.playgame(true);
		
		//ai against itself
		
		m.ai_against_ai();
//		m.ai_against_ai_championship();
	}
	
}
