package mew.blindmaze.game;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

/*
 * 
 * TODO: To Add the idea of tunnels which transport the user from one location to another.
 */
public class SourceToDestination {
	public static int board[][];
	public static int board_size=25;
	public static int wall_count = 100;
	public static int mine_count = 25;
	
	public static Point start_point;
	public static Point end_point;
	public static Point current_point;

	public static int move_count;
	public static int choice_count;
	public static boolean debug_on = false;
	public static final Scanner read = new Scanner(System.in);

	public static HashMap<Point, Point> path = new HashMap<Point, Point>();

	/*
	 * To Help to Fill the board
	 */
	private static int getNextPoint(){
		Random rand = new Random();		
		return rand.nextInt(board_size);
	}
		
	/*
	 * To Help to set the board and Generate the required type of point on the board
	 */
	private static void setPoint(int pt_type){
		int x, y;
		Point p;
		while(true){
			x = getNextPoint();
			y = getNextPoint();

			if(board[x][y] == Point_Type.EMPTY_POINT){				
				board[x][y] = pt_type;
				break;
			}
		}
		p = new Point(x, y);

		switch(pt_type){
		case Point_Type.START_POINT:
			start_point = p;
			break;

		case Point_Type.END_POINT:
			end_point = p;
			break;
		}
	}

	/*
	 * Board is a blank square
	 * To fill the board with desired entities
	 */
	private static void init(){
		setPoint(Point_Type.START_POINT);
		setPoint(Point_Type.END_POINT);

		for(int i=0 ; i<wall_count ; ++i){
			setPoint(Point_Type.WALL_POINT);
		}
		for(int i=0 ; i<mine_count ; ++i){
			setPoint(Point_Type.MINE_POINT);
		}
		
		current_point = start_point;
	}

	private static char read(){		
		return read.nextLine().trim().charAt(0);
	}


	/*
	 * To Check if we can move to given co-ordinate on the board ?
	 */
	private static boolean isValid(int x, int y){
		boolean ret_value = false;

		if(x >= 0 && x<board_size && y >= 0 && y<board_size){
			ret_value = true;
		}

		if(debug_on) System.out.println("Point ("+x+", "+y+") is : "+ret_value);
		return ret_value;
	}
	
	private static boolean isSafe(int x, int y){
		boolean ret_value = false;
		
		if(isValid(x, y)){
			 ret_value = (board[x][y] == Point_Type.START_POINT ||
				board[x][y] == Point_Type.END_POINT || 
					board[x][y] == Point_Type.EMPTY_POINT);
		}
		
		return ret_value;
	}

	/*
	 * To check all the neighbors and return a list of all the reachable neighbors
	 */
	private static List<Point> readNeighbour(Point curr){
		int x = curr.x;
		int y = curr.y;
		List<Point> lst = new ArrayList<Point>();
		// top
		if(isSafe(x-1, y)){
			lst.add(new Point(x-1, y));
		}

		//bottom
		if(isSafe(x+1, y)){
			lst.add(new Point(x+1, y));
		}

		//right
		if(isSafe(x, y+1)){
			lst.add(new Point(x, y+1));
		}

		//left
		if(isSafe(x, y-1)){
			lst.add(new Point(x, y-1));
		}

		return lst;
	}

	/*
	 * To check if its valid (x, y) on board and move and return whether move was successful
	 */
	private static boolean move(int x, int y){
		if(isSafe(x, y)){
			current_point = new Point(x, y);
			return true;
		}else if(isValid(x, y)){
			if(isMine(x, y)){
				print_lose("You Step on Mine. :D");
				abort();
				return false;
			}
		}
		return false;
	}

	private static void print_lose(String msg) {
		System.out.println(msg);
		System.out.println("You Lose !!! Game Over");
	}

	private static boolean isMine(int x, int y) {
		if(isValid(x, y)){
			return board[x][y] == Point_Type.MINE_POINT;
		}
		return false;
	}

	/*
	 * Calculates the total number of steps to shortest path to end point
	 */
	private static int find_Steps() {
		int step_count=0;
		HashMap<Point, Point> map = new HashMap<Point, Point>();
		Queue<Point> queue = new ArrayDeque<Point>();

		if(current_point.equals(end_point)){
			return step_count;
		}
		
		map.put(start_point, null);
		queue.add(current_point);

		while(!queue.isEmpty()){
			Point pt = queue.remove();

			if(pt.equals(end_point)){
				break;
			}
			List<Point> neigh = readNeighbour(pt);
			for(Point p : neigh ){

				if(!map.containsKey(p)){
					map.put(p, pt);
					queue.add(p);
				}
			}
		}

		// clear previous map entry
		path.clear();
		
		if(map.containsKey(end_point)){
			Point curr_point = end_point;

			while(!curr_point.equals(current_point)){
				++step_count;				
				path.put(map.get(curr_point), curr_point);
				curr_point = map.get(curr_point);
			}
		}
		
		return step_count;
	}

	public static void main(String[] args) {
		char choice;
		
		read_configuration();
		// Initialize the Game with all the entities		
		init_board();
		init(); // initialize it with 1 Start, 1 End & 10 Walls on 10X10 board		
		say_welcome();
		
		// Game Begins
		while(true){

			int no_steps = find_Steps();
			
			// check if you are at destination ?
			if(no_steps == 0){
				say_won();
				abort();
				break;
			}

			System.out.println("Shortest path to destiation is "+no_steps+" steps away.");

			boolean valid=true;
			do {
				System.out.println("Which way you want to go ?? (L, R, U, D)");
				System.out.println("Enter y for suggestion");
				choice = read();

				switch(choice){				
				case 'U': case 'u':
					valid = move(current_point.x-1, current_point.y);
					break;

				case 'D': case 'd':
					valid = move(current_point.x+1, current_point.y);
					break;

				case 'R': case 'r':
					valid = move(current_point.x, current_point.y+1);
					break;

				case 'L': case 'l':
					valid = move(current_point.x, current_point.y-1);
					break;

				case 'Y': case 'y':
					showChoice();
					++choice_count;
					break;
					
				case '#' :
					print_status();
					break;

				default:
					if(debug_on) System.out.println("no option matched.");
					break;
				}
				
				if(debug_on) print_status();
				if(debug_on) System.out.println("is valid ? "+valid);
				if(!valid){
					System.out.println("Cannot go there :-/");
				}else {
					++move_count;
				}
			}while(!valid);
					
		}
	}

	private static void read_configuration() {
		//TODO: Read Configuration from external file
		wall_count = 100;
		board_size = 25;
		mine_count = 25;
	}

	private static void abort() {
		System.exit(0);
		
	}

	private static void say_won() {
		System.out.println("Congratulations!! you have reached the destination. :-)");
		System.out.println("No of moves = "+move_count);
		System.out.println("No of Suggestions = "+choice_count);		
	}

	private static void init_board() {
		// To make the board empty place
		board = new int[board_size][];

		for(int i=0 ; i<board_size ; ++i){
			board[i] = new int[board_size];

			for(int j=0 ; j<board_size ; ++j){
				board[i][j] = Point_Type.EMPTY_POINT;
			}
		}
	}

	private static void showChoice() {
		Point curr = current_point;
		Point next = path.get(curr);

		if(debug_on){
			System.out.println("Going from "+curr+" to "+next);
		}
		
		
		String msg = "";
		if(curr.x == next.x){
			if(curr.y > next.y){
				msg = "Go Left";				
			}else{
				msg = "Go Right";				
			}
		}else {
			if(curr.x > next.x){
				msg = "Go Up";
			}else {
				msg = "Go Down";
			}
		}
		System.out.println(msg);
	}

	private static void say_welcome() {
		System.out.println(
				"****************************************************************************\n" +
				"               Welcome to Unknown World Hunting Game !!!           \n" +
				"   The Fun part is there is no GUI no intension of showing you the board    \n" +
				"   From the given information how quickly can you find the destination ??   \n" +
				"   NOTE:: Game is case Insensitive so don't bother of pressing SHIFT key :P   \n" +
				" **************************************************************************** \n" +
				"");
	}

	/**
	 * To Debug the game
	 */
	private static void print_status() {

		char ch='*';
		System.out.println("You are currently at ("+current_point.x+", "+current_point.y+")");
		
		for(int i=0 ; i<board_size ; ++i){
			for(int j=0 ; j<board_size; ++j){

				if(current_point.x == i && current_point.y == j){
					ch = 'C';

				}else {
					switch(board[i][j]){
					case Point_Type.EMPTY_POINT:
						ch = '-';
						break;

					case Point_Type.START_POINT:
						ch = 'S';
						break;

					case Point_Type.END_POINT:
						ch = 'E';
						break;
					
					case Point_Type.WALL_POINT:
						ch = 'W';
						break;
					case Point_Type.MINE_POINT:
						ch='M';
						break;
					}							
				}
				System.out.print(ch+" ");
			}
			System.out.println();
		}
	}

}