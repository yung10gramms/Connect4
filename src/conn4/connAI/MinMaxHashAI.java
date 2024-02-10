package conn4.connAI;


import conn4.MainClass;

public abstract class MinMaxHashAI implements Connect4AI {

	protected MainClass mainClass;
	
	public int maxDepth;	
	
	protected int num_hit = 0;
	
	protected int curDepth;
	protected int[][] mat;
	
	protected int player;
	
	public MinMaxHashAI(MainClass mc) {
		this.mainClass = mc;
		mat = mainClass.mat;
		
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}
	
	public int getNumHit() {
		return num_hit;
	}
}
