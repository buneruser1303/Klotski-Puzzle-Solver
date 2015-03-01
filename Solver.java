import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;


public class Solver {
	private Tray initialTray;
	private Tray current;
	private ArrayList<ArrayList<Integer>> finalTray;
	private boolean isBigTray;
	private HashSet<String> alreadySeen;
	private HashSet<HashMap<String, ArrayList<Integer>>> alreadySeen2;
	private LinkedList<Tray> trayList;
	private boolean solutionFound;
	
	//// ************ DEBUGGING FIELDS ************* //// 
	private String debugOptions; 
	private int numTraysDebug; 
	private boolean iAmDebugging = false; 
	//*********************************************///
	
	public Solver(){
		finalTray = new ArrayList<ArrayList<Integer>>();
		trayList = new LinkedList<Tray>();
	}
	
	//			HANDLE COMMAND LINE INPUT			   //
	//		 SET UP THE INITIAL AND FINAL TRAYS		  //
	public void initializeGame(String[] args){
		InputSource initConfig;
		InputSource finConfig;
		//WRITTEN BY GRANT LIN— START//
		if (args.length == 3) {	// for debugging command
			System.out.println("you have entered the debugging mode");
			initConfig = new InputSource(args[1]);
			finConfig = new InputSource(args[2]);
			debugOptions = args[0];
			//Check for errors in the optional user input
			if (!debugOptions.substring(0, 2).equals("-o")){
				System.out.println("First command line argument is formatted incorrectly. Must start input with -o. Input -ooptions to list all options.");
				System.exit(1);
			}
			if (!debugOptions.equals("-ooptions") && !debugOptions.equals("-oinitialtray") && !debugOptions.equals("-ofinaltray") && !debugOptions.equals("-oruntime")
			&& !debugOptions.equals("-omemused") && !debugOptions.equals("-onumtrays") && !debugOptions.equals("-oprinteachmove") && !debugOptions.equals("-ocheckokay")
			&& !debugOptions.equals("-ostopformem") && !debugOptions.equals("-ostopfortime")){
				System.out.println("First command line argument is formatted incorrectly. Not a valid debugging option. Input -ooptions to list all options.");
				System.exit(1);
			}
			iAmDebugging = true;
			System.out.println("debugging option chosen : " + debugOptions.substring(2));
			
		} else {
			initConfig = new InputSource(args[0]);
			finConfig = new InputSource(args[1]);
		}
		String initLine = initConfig.readLine ( );
		Scanner initConfigScan = new Scanner (initLine);
		String row = initConfigScan.next ( );
		String column = initConfigScan.next ( );

		initialTray = new Tray(Integer.parseInt(row), Integer.parseInt(column)); //***MAKE INITIAL TRAY***
    //WRITTEN BY GRANT LIN— END//
		while (true) {
           // Read a line from the initConfig
           String consecutivelines = initConfig.readLine ( );
           if (consecutivelines == null || consecutivelines.length() == 0) {
               break;
           }
           // Put each line into block in tray 
           Scanner configScan = new Scanner (consecutivelines);

           int x1 = Integer.parseInt(configScan.next ( ));
           int y1 = Integer.parseInt(configScan.next ( ));
           int x2 = Integer.parseInt(configScan.next ( ));
           int y2 = Integer.parseInt(configScan.next ( ));
           
           initialTray.addBlock(x1, y1, x2, y2);
       }
	   
		while (true) {
        // Read a line from the finConfig
			String consecutivelines = finConfig.readLine ( );
			if (consecutivelines == null || consecutivelines.length() == 0) {
				break;
			}
			ArrayList<Integer> coords = new ArrayList<Integer>();
			Scanner configScan = new Scanner (consecutivelines);
        
			coords.add(Integer.parseInt(configScan.next ( )));
			coords.add(Integer.parseInt(configScan.next ( )));
			coords.add(Integer.parseInt(configScan.next ( )));
			coords.add(Integer.parseInt(configScan.next ( )));
			coords.add(((coords.get(2)-coords.get(0))+1)*((coords.get(3)-coords.get(1))+1));
			finalTray.add(coords); // add block coordinates to finalTray
		}
		if (initialTray.getTrayColumn()*initialTray.getTrayRow()>1000){	// if tray area greater than 1000, use hashset of hashmaps
			isBigTray = true;
			alreadySeen2 = new HashSet<HashMap<String, ArrayList<Integer>>>();
		} else {
			alreadySeen = new HashSet<String>();	// else use hashset of strings
		}
		
		//Debugging statements. when -o is implemented.
		if (iAmDebugging == true){
			//list all debugging options
			if (debugOptions.equals("-ooptions")){
				System.out.println("--- Debugging options ---");
				System.out.println("initaltray: \tprint out what the initial configuration looks like");
				System.out.println("finaltray: \tprint out what the final configuration looks like whether the game was won or not");
				System.out.println("runtime: \tthe amount of time it took in order to end the game whether it was won or not");
				System.out.println("memused: \tthe amount of memory it took in order to end the game whether it was won or not");
				System.out.println("numtrays: \tthe number of HashMaps created in the game, whether it was won or not");
				System.out.println("printeachmove:\t prints every configuration that is considered, even those not implemented");
				System.out.println("checkokay:\t calls isOK() after every move made"); 
				System.out.println("stopformem: \tstops the game when you've reached 90% of the Java Heap Memory, \n\t\tshows how many instances of tray have been made, and the time that has passed.");
				System.out.println("stopfortime: \tstops the game when you've reached 80 seconds. \n\t\tShow how many instances of Tray have been created, and the percentage of memory used up.");
				System.exit(1);
			}
		}
	}
	
	// RUN THE SOLVER PROGRAM 
	public static void main(String[] args) {
		Timer t = new Timer();
		t.start();
		Solver gameSolver = new Solver();
		gameSolver.initializeGame(args); //initialize tray

		/// ************* FOR DEBUGGING ONLY ************* ///
		if (gameSolver.iAmDebugging == true && gameSolver.debugOptions.equals("-oinitialtray")){
			gameSolver.initialTray.printBoard();
			System.out.println();
		}
		/// ******************************************* ///
		
		gameSolver.trayList.add(gameSolver.initialTray); // add the first config to the trayList

		while(!gameSolver.trayList.isEmpty()){ // while trayList is not empty, process moves

			gameSolver.current = gameSolver.trayList.removeFirst(); //pop the first tray from the trayList

			/// ************* FOR DEBUGGING ONLY ************* ///
			if (gameSolver.iAmDebugging == true && gameSolver.debugOptions.equals("-oprinteachmove")){
				gameSolver.current.printBoard();
				System.out.println();
			}
			if (gameSolver.iAmDebugging == true && gameSolver.debugOptions.equals("-ostopfortime")){
				if (t.elapsed() > 80000){
					System.out.println("Reached 80 seconds of time, and created " + gameSolver.numTraysDebug + " instances of Tray.");
					System.out.println("Last config before reaching time : ");
					gameSolver.current.printBoard();
					long memUsed = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
					float memPercent = (float) memUsed / Runtime.getRuntime().totalMemory();
					System.out.println(memPercent + " memory was used");
					System.exit(1);
				}
			}
			if (gameSolver.iAmDebugging == true && gameSolver.debugOptions.equals("-ostopformem")){
				System.out.println("debugging for memory");
				long memUsed = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				float memPercent = (float) memUsed / Runtime.getRuntime().totalMemory();
				String actualResult = MessageFormat.format("{0,number,#.##%}", memPercent);
				System.out.println(actualResult);
				if (memPercent > .9500){
					System.out.print(gameSolver.numTraysDebug);
					System.out.println(" instances of Tray created");
					System.out.println(memPercent + " of memory used.");
					System.out.println(t.elapsed() + " time has passed.");
					System.exit(1);
				}
			}
			/// ******************************************* ///
			
			if (gameSolver.current.checkGoal(gameSolver.finalTray)){ // check if we're at GOAL configuration
				gameSolver.solutionFound = true;
				break;
			}
			if (gameSolver.haveSeen(gameSolver.current)){ // check if we have seen this configuration before
				continue;
			}
			gameSolver.makeMoves(gameSolver.current); // add future configurations to the trayList
		}

		if (gameSolver.solutionFound){	// check if we found a solution
			t.stop(); //for run time
			
			/// ************* FOR DEBUGGING ONLY ************* ///
			if (gameSolver.iAmDebugging == true){
				if (gameSolver.debugOptions.equals("-ofinaltray")){
					gameSolver.current.printBoard();
					System.out.println();
				}
				else if (gameSolver.debugOptions.equals("-oruntime")){
					System.out.println(t.elapsed() + " milliseconds.\n");
				}
				else if (gameSolver.debugOptions.equals("-omemused")){
					long memUsed = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
					float memPercent = (float) memUsed / Runtime.getRuntime().totalMemory();
					String actualResult = MessageFormat.format("{0,number,#.##%}", memPercent);
					System.out.print(actualResult);
					System.out.println(" of free memory used\n");

				}
				else if (gameSolver.debugOptions.equals("-onumtrays")){
					System.out.println(gameSolver.numTraysDebug + " trays were created.\n");
				}
			}
			/// ******************************************* ///
			gameSolver.printTrace(gameSolver.produceTrace(gameSolver.current)); // print out sequence of moves
		}
		else {
			t.stop(); //for run time
			
			/// ************* FOR DEBUGGING ONLY ************* ///
			if (gameSolver.iAmDebugging == true){
				System.out.println("debug");
				System.out.println(gameSolver.debugOptions);
				if (gameSolver.debugOptions.equals("-ofinaltray")){
					gameSolver.current.printBoard();
					System.out.println();
				}
				else if (gameSolver.debugOptions.equals("-oruntime")){
					System.out.println(t.elapsed() + " milliseconds.\n");
				}
				else if (gameSolver.debugOptions.equals("-omemused")){
					long memUsed = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
					double memPercent = (float) memUsed / Runtime.getRuntime().totalMemory();
					String actualResult = MessageFormat.format("{0,number,#.##%}", memPercent);
					System.out.print(actualResult);
					System.out.println(" of free memory used.\n");
				}
				else if (gameSolver.debugOptions.equals("-onumtrays")){
					System.out.println(gameSolver.numTraysDebug + " trays were created.\n");
				}
			}
			/// ******************************************* ///
	
			System.exit(1); // no solution found
		}
	}
	
	// Checks each block in the current configuration to see if it may be moved LEFT, RIGHT, UP and/or DOWN
	// If the move is valid, create a new Tray object, and make that move. 
	// Add new configurations on to the trayList
	public void makeMoves(Tray currentTray){
		Tray futureMove = null;
		for (int i = 1 ; i < currentTray.getBlockCount(); i ++){
			ArrayList<Integer> blockMove = currentTray.coordinates.get("" + i);
			
			if (currentTray.isAdjacent(blockMove, "left")){
				futureMove = new Tray(currentTray);
				futureMove.move(blockMove, "" + i + "", "left");
				addTotrayList(futureMove);
				futureMove.parent = currentTray; 
				
				/// ************* FOR DEBUGGING ONLY ************* ///
				if (iAmDebugging == true && debugOptions.equals("-ocheckokay")){
					futureMove.isOk();
				}
				numTraysDebug++;
				/// ******************************************* ///
			}
			if (currentTray.isAdjacent(blockMove, "right")){
				futureMove = new Tray(currentTray);
				futureMove.move(blockMove, "" + i + "", "right");
				addTotrayList(futureMove);
				futureMove.parent = currentTray; 
				
				/// ************* FOR DEBUGGING ONLY ************* ///
				if (iAmDebugging == true && debugOptions.equals("-ocheckokay")){
					futureMove.isOk();
				}
				numTraysDebug++;
				/// ******************************************* ///
			}
			if (currentTray.isAdjacent(blockMove, "up")){
				futureMove = new Tray(currentTray);
				futureMove.move(blockMove, "" + i + "", "up");
				addTotrayList(futureMove);
				futureMove.parent = currentTray; 
				
				/// ************* FOR DEBUGGING ONLY ************* ///
				if (iAmDebugging == true && debugOptions.equals("-ocheckokay")){
					futureMove.isOk();
				}
				numTraysDebug++;
				/// ******************************************* ///
			}
			if (currentTray.isAdjacent(blockMove, "down")){
				futureMove = new Tray(currentTray);
				futureMove.move(blockMove, "" + i + "", "down");
				addTotrayList(futureMove);
				futureMove.parent = currentTray; 
				
				/// ************* FOR DEBUGGING ONLY ************* ///
				if (iAmDebugging == true && debugOptions.equals("-ocheckokay")){
					futureMove.isOk();
				}
				numTraysDebug++;
				/// ******************************************* ///
			}
		}
	}
	
	// Checks whether we have seen the current configuration before
	// If the isBigTray field is true, we will check the alreadySeen hashset of hashmaps for previous configurations
	// If the isBigTray field is false, we will check the alreadySeen hashset of strings for previous configurations
	// If we have not seen this configuration yet, add it to the appropriate hashset
	public boolean haveSeen(Tray currentConfig){
		if (isBigTray){
			if (alreadySeen2.contains(currentConfig.coordinates)){	// configuration has been seen before
				return true;
			}
			else{
				alreadySeen2.add(currentConfig.coordinates);
				return false;
			}
		} else {
			
		if (alreadySeen.contains(currentConfig.getString())){ // configuration has been seen before
			return true;
		} else{ 
			alreadySeen.add(currentConfig.getString());
			return false;
			}
		}	
	}
	
    // Add the current configuration to trayList
    // If a block's position from its goal configuration is farther than half of the maximum distance
    // in the board (trayRow+trayColumn), add it to the end of the trayList. Otherwise, add the configuration
    // to the front of trayList
	public void addTotrayList(Tray currentConfig){	
			for (ArrayList<Integer> coords : currentConfig.coordinates.values()) {
				for (int i = 0; i < finalTray.size(); i++){
			    	if (finalTray.get(i).get(4) == coords.get(4)) {
			    		if(!((Math.abs(finalTray.get(i).get(0) - coords.get(0))) + (Math.abs(finalTray.get(i).get(1) - coords.get(1))) < (initialTray.getTrayRow()+initialTray.getTrayColumn())/2)){
			    			trayList.addLast(currentConfig);
			    			return;
			    		}
			    	}
				}
			}
		trayList.addFirst(currentConfig);
	}
//WRITTEN BY GRANT LIN— START//
	// The input should be the solution configuration.
	// Create an arrayList that has the reverse order of tray steps
	public ArrayList<Tray> produceTrace (Tray currTray) { 
		ArrayList<Tray> toBePrint = new ArrayList<Tray>();  
		Tray curr = currTray;
		for (;curr.parent != null; curr = curr.parent) { 
			toBePrint.add(curr); //add itself to the ArrayList
		} 
		if (curr.parent == null && curr == initialTray) { //if there is no parent and curr is the InitialConfig
			toBePrint.add(curr);
			return toBePrint;
		} 	
		return null;
	}
	
	// Prints out the sequence of moves from the initial configuration to the solution
	public void printTrace (ArrayList<Tray> trace) {
		for (int i = trace.size()-1; i > 0; i--) {
			System.out.println(((Tray) trace.get(i)).moveToTray((Tray) trace.get(i-1)));
		}
	}
	//WRITTEN BY GRANT LIN— END//
	
}
