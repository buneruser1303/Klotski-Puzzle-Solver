import java.util.ArrayList;
import java.util.HashMap;

public class Tray {
	private int[][] board;
	private int blockCount;
	private int trayRow, trayColumn;
	private String stringRep;
	HashMap<String, ArrayList<Integer>> coordinates;
	Tray parent = null;
	
	// Tray constructor - pass in row and column amounts to initialize board
	public Tray(int row, int column){
		trayRow = row;
		trayColumn = column;
		board = new int[trayRow][trayColumn];
		coordinates = new HashMap<String, ArrayList<Integer>>();
		blockCount = 1;
		stringRep="";
	}
	
	// Tray constructor to make a copy of a Tray
	public Tray(Tray copy){
		trayRow = copy.trayRow;
		trayColumn = copy.trayColumn;
		board = new int[copy.trayRow][copy.trayColumn];
		copy(copy.board);
		coordinates = new HashMap<String, ArrayList<Integer>>(copy.coordinates);
		blockCount = copy.blockCount;
		stringRep="";
	}
	
	// Add block to the Tray - pass in coordinates
	// Give each block a unique number (blockCount)
	// Use the blockCount number as the key, and an ArrayList of the block coordinates as the value, and put into coordinate hashmap
	// Add the block to the "board" grid of arrays. Fill in the top left block as the area of the block, and fill in the rest as -1
	public void addBlock(int row1, int column1, int row2, int column2){
		ArrayList<Integer> coords = new ArrayList<Integer>();
		coords.add(row1);
		coords.add(column1);
		coords.add(row2);
		coords.add(column2);
		coords.add(((row2-row1)+1) * ((column2-column1)+1));
		coordinates.put(""+blockCount, coords);

		for (int row = row1; row <= row2; row++){
			for (int column = column1; column <= column2; column++) {
				board[row][column] = -1;
			}
		}
		board[row1][column1] = coords.get(4);
		blockCount++;
	}
	
	// Visual representation of the board in Tray - for debugging ONLY!
	public void printBoard(){ 
		for (int rowB = 0; rowB < trayRow; rowB++){
			for (int columnB = 0; columnB < trayColumn; columnB++) {
				System.out.print(board[rowB][columnB] + " ");
			}
			System.out.println("");
		}
	}
	
	// String representation of the board in Tray
	public String toString(){
		String toReturn="";
		for (int rowB = 0; rowB < trayRow; rowB++){
			for (int columnB = 0; columnB < trayColumn; columnB++) {
				toReturn+=board[rowB][columnB] + "";
			}
			toReturn+="";
		}
		return toReturn;
	}
	
	// Get the string representation of the board if already created; if not, create it
	public String getString(){
		if (stringRep==""){
			stringRep = toString();
			return stringRep;
		}
		return stringRep;
	}
	
	//Getter methods
	public int getTrayRow(){
		return trayRow;
	}
	
	public int getTrayColumn(){
		return trayColumn;
	}
	
	public int getBlockCount(){
		return blockCount;
	}
	
	// Method to make copy of the board in Tray
	public void copy(int[][] toCopy){
		for (int rowB = 0; rowB < trayRow; rowB++){
			for (int columnB = 0; columnB < trayColumn; columnB++) {
				board[rowB][columnB] = toCopy[rowB][columnB];
			}
		}
	}
	
	// Check if a given block can be moved UP, DOWN, LEFT, or RIGHT
	// Returns true if all the array positions adjacent to the given block in the given direction are 0 (empty)
	public boolean isAdjacent(ArrayList<Integer> blockPos, String direction){
		int row1 = blockPos.get(0);
		int column1 = blockPos.get(1);
		int row2 = blockPos.get(2);
		int column2 = blockPos.get(3);
		
		if (direction.equals("up")){
			if (row1 <= 0){
				return false;
			}
			for (int column = column1; column <= column2; column++){
				if (board[row1-1][column] != 0){
					return false;
				}
			}
			return true;
		}
		else if (direction.equals("down")){
			if (row2 >= trayRow-1){
				return false;
			}
			for (int column = column1; column <= column2; column++){
				if (board[row2+1][column] != 0){
					return false;
				}
			}
			return true;
		}
		else if (direction.equals("left")){
			if (column1 <= 0){
				return false;
			}
			for (int row = row1; row <= row2; row++){
				if (board[row][column1-1] != 0){
					return false;
				}
			}
			return true;
		}
		else {
			if (column2 >= trayColumn-1){
				return false;
			}
			for (int row = row1; row <= row2; row++){
				if (board[row][column2+1] != 0){
					return false;
				}
			}
			return true;
		}
	}
	
	// Move a given block a given direction (precondition: the block CAN be moved in the given direction)
	// Change the state of the board grid, and change the coordinates corresponding to the block in the coordinates hashmap
	public void move(ArrayList<Integer> blockPos, String num, String direction) {
		int row1 = blockPos.get(0);
		int column1 = blockPos.get(1);
		int row2 = blockPos.get(2);
		int column2 = blockPos.get(3);
		int blockNum = blockPos.get(4);
		ArrayList<Integer> newBlockPos = new ArrayList<Integer>(blockPos);
		
		if (direction.equals("up")){
			for (int column = column1; column <= column2; column++){
				board[row1-1][column] = -1;
			}
			for (int column = column1; column <= column2; column++){
				board[row2][column] = 0;
			}
			board[row1-1][column1] = blockNum;
			if ((row2-row1) > 0){
				board[row1][column1] = -1;
			}
			newBlockPos.set(0, (row1-1));
			newBlockPos.set(2, (row2-1));
			coordinates.put(num, newBlockPos);
		}
		else if (direction.equals("down")){
			for (int column = column1; column <= column2; column++){
				board[row2+1][column] = -1;
			}
			for (int column = column1; column <= column2; column++){
				board[row1][column] = 0;
			}
			board[row1+1][column1] = blockNum;
			newBlockPos.set(0, (row1+1));
			newBlockPos.set(2, (row2+1));
			coordinates.put(num, newBlockPos);
		}
		else if (direction.equals("right")){
			for (int row = row1; row <= row2; row++){
				board[row][column2+1] = -1;
			}
			for (int row = row1; row <= row2; row++){
				board[row][column1] = 0;
			}
			board[row1][column1+1] = blockNum;
			board[row1][column1] = 0;
			newBlockPos.set(1, (column1+1));
			newBlockPos.set(3, (column2+1));
			coordinates.put(num, newBlockPos);
		}
		else {
			for (int row = row1; row <= row2; row++){
				board[row][column1-1] = -1;
			}
			for (int row = row1; row <= row2; row++){
				board[row][column2] = 0;
			}
			board[row1][column1-1] = blockNum;
			if (Math.abs(column1-column2) > 0){
				board[row1][column1] = -1;
			}
			newBlockPos.set(1, (column1-1));
			newBlockPos.set(3, (column2-1));
			coordinates.put(num, newBlockPos);
		}
	}
	
	// Checks whether the tray contains all the block positions of the goal configuration
	public boolean checkGoal(ArrayList<ArrayList<Integer>> goalConfig){	
		for (int i = 0 ; i < goalConfig.size(); i ++){
			if (!coordinates.containsValue(goalConfig.get(i))){
				return false;
			}
		}
			return true;
	}
	
	// For debugging - checks that the tray is valid
	public void isOk(){
		if (trayRow != board.length || trayRow <= 0){
			throw new IllegalStateException("There is something wrong with the width of the board.");
		}
		if (trayColumn != board[0].length || trayColumn <= 0){
			throw new IllegalStateException("There is something wrong with the length of the board.");
		}
		if (blockCount - 1 != coordinates.size()){
			throw new IllegalStateException("The blocks in the tray were not initialized properly.");
		}
		int countBlocks = 0;
		
		for (int i = 0; i < board.length; i ++){
			for (int n = 0; n < board[i].length; n ++){
				if (board[i][n]!=0 && board[i][n] != -1){
					countBlocks++;
				}
			}
		}
		if (countBlocks != blockCount -1){
			throw new IllegalStateException("There are discrepancies with the current blocks in this tray.");
		}
		for (ArrayList<Integer> coords : coordinates.values()) {
			int checkRow = coords.get(0);
			int checkCol = coords.get(1);
			if (board[checkRow][checkCol] == 0 || board[checkRow][checkCol] == -1){
				throw new IllegalStateException("There are discrepancies with the positions of the blocks within the HashMap and within the board array");
			}
		}		
	}
	
	// Checks the "difference" between two trays and returns the top left block positions of the first differing blocks
	// To be used for tracing through the sequence of moves
	public String moveToTray(Tray t) { //t is the resulting Tray
		if (t.trayRow == this.trayRow && t.trayColumn ==this.trayColumn) {
			for (int i = 1; i < blockCount; i++) {
				if (!this.coordinates.get(""+ i).equals(t.coordinates.get(""+ i))) {
					int m1 = this.coordinates.get(""+ i).get(0);
					int m2 = this.coordinates.get(""+ i).get(1);
					int m3 = t.coordinates.get(""+ i).get(0);
					int m4 = t.coordinates.get(""+ i).get(1);
					return m1 + " " + m2 + " " + m3 + " " + m4;
				}
			}
		} else {
			return "no moves possible";
		}
		return null;
	}
}