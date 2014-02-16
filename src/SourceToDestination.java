import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

class Point {
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public int x;
	public int y;
	
	@Override
	public boolean equals(Object obj) {
		if(! (obj instanceof Point)){
			return false;
		}
		Point temp = (Point) obj;
		
		return (temp.x == this.x && temp.y == this.y);
	}
	
	@Override
	public int hashCode() {		
		return this.x+1011+y;
	}
}

public class SourceToDestination {
	public static int board[][];
	public static int board_size=10;
	
	public static final int EMPTY_POINT = 0;
	public static final int START_POINT = 1;
	public static final int END_POINT = 2;
	public static final int WALL_POINT = 3; 
	
	public static Point start_point;
	public static Point end_point;
	public static Point current_point;
	
	public static int wall_count = 10;
	
	public static int move_count;
	public static int choice_count;
	public static boolean debug_on = false;
	
	/*
	 * To Help to Fill the board
	 */
	public static int getNextPoint(){
		Random rand = new Random();
		
		return rand.nextInt(board_size);
	}
	
	/*
	 * To Help to set the board and Generate the required type of point on the board
	 */
	public static void setPoint(int pt_type){
		int x, y;
		Point p;
		while(true){
			x = getNextPoint();
			y = getNextPoint();
			
			if(board[x][y] == EMPTY_POINT){				
				board[x][y] = pt_type;
				break;
			}
		}
		p = new Point(x, y);
		
		switch(pt_type){
		case START_POINT:
			start_point = p;
			break;
			
		case END_POINT:
			end_point = p;
			break;
		}
	}
	
	/*
	 * Board is a blank square
	 * To fill the board with desired entities
	 */
	public static void init(){
		setPoint(START_POINT);
		setPoint(END_POINT);
		
		for(int i=0 ; i<wall_count ; ++i){
			setPoint(WALL_POINT);
		}
		current_point = start_point;
	}
	
	public static char read(){
		Scanner read = new Scanner(System.in);
		return read.nextLine().trim().charAt(0);
	}
	
	
	public static void showChoice(){
		
	}
	
	@Deprecated
	public static int getValue(int x, int y){
		return x*board_size+y;		
	}
	
	@Deprecated
	public static Point readValue(int val){
		return new Point(val / board_size,  val % board_size);
	}
	
	/*
	 * To Check if we can move to given co-ordinate on the board ?
	 */
	public static boolean isValid(int x, int y){
		boolean ret_value = false;
		
		if(x >= 0 && x<board_size && y >= 0 && y<board_size){
			ret_value = (board[x][y] == START_POINT || board[x][y] == END_POINT || board[x][y] == EMPTY_POINT);
		}
		
		if(debug_on) System.out.println("Point ("+x+", "+y+") is : "+ret_value);
		return ret_value;
	}
	
	/*
	 * To check all the neighbors and return a list of all the reachable neighbors
	 */
	public static List<Point> readNeighbour(Point curr){
		int x = curr.x;
		int y = curr.y;
		List<Point> lst = new ArrayList<Point>();
		// left
		if(isValid(x-1, y)){
			lst.add(new Point(x-1, y));
		}
		
		//right
		if(isValid(x+1, y)){
			lst.add(new Point(x+1, y));
		}
		
		//top
		if(isValid(x, y+1)){
			lst.add(new Point(x, y+1));
		}
		
		//bottom
		if(isValid(x, y-1)){
			lst.add(new Point(x, y-1));
		}
		
		return lst;
	}
	
	/*
	 * To check if its valid (x, y) on board and move and return whether move was successful
	 */
	public static boolean move(int x, int y){
		if(isValid(x, y)){
			current_point = new Point(x, y);
			return true;
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
		
		//int start_key = getValue(curr_x, curr_y); 
		map.put(start_point, null);
		queue.add(current_point);
		
		while(!queue.isEmpty()){
			Point pt = queue.remove();
			 
			if(pt.equals(end_point)){
				break;
			}
			List<Point> neigh = readNeighbour(pt);
			for(Point p : neigh ){
				//int key = getValue(p.x, p.y);
				
				if(!map.containsKey(p)){
					map.put(p, pt);
					queue.add(p);
				}
			}
		}
		
		//int end_key = getValue(en_x, en_y);
		if(map.containsKey(end_point)){
			Point curr_point = end_point;
			
			while(!curr_point.equals(current_point)){
				++step_count;				
				curr_point = map.get(curr_point);
			}
			return step_count;
		
		}else {
			return 0;
		}
	}
	
	public static void main(String[] args) {
		// To make the board empty place
		board = new int[board_size][];
		char choice;
		
		for(int i=0 ; i<board_size ; ++i){
			board[i] = new int[board_size];
			
			for(int j=0 ; j<board_size ; ++j){
				board[i][j] = EMPTY_POINT;
			}
		}// end of init
		
		// initialize it with 1 Start, 1 End & 10 Walls on 10X10 board
		init();
		
		say_welcome();
		while(true){
			
			int no_steps = find_Steps();
			// check if you are at destination ?
			if(no_steps == 0){
				System.out.println("Congratulations!! you have reached the destination. :-)");
				System.out.println("No of moves = "+move_count);
				break;
			}
			
			System.out.println("Shortest path to destiation is "+no_steps+" steps away. ");
			
			boolean valid=true;
			do {
				System.out.println("Which way you want to go ?? (L, R, U, D)");
				choice = read();
				
				switch(choice){
				case 'L': case 'l':
					valid = move(current_point.x-1, current_point.y);
					break;
					
				case 'R': case 'r':
					valid = move(current_point.x+1, current_point.y);
					break;
					
				case 'U': case 'u':
					valid = move(current_point.x, current_point.y+1);
					break;
					
				case 'D': case 'd':
					valid = move(current_point.x, current_point.y-1);
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
				}
			}while(!valid);
			
			++move_count;			
		}
	}

	private static void say_welcome() {
		System.out.println("****************************************************************************");
		System.out.println("Welcome to Unknown World Hunting Game !!!");
		System.out.println("The Fun part is there is no GUI no intension of showing you the board");
		System.out.println("From the given information how quickly can you find the destination ??");
		System.out.println("NOTE:: Game is case insensitive so don't bother of pressing SHIFT key :P");
		System.out.println("Feel free to send feedback @ mahesh.msg.24@gmail.com");
		System.out.println("****************************************************************************");
	}

	/**
	 * To Debug the game
	 */
	private static void print_status() {
		
		System.out.println("You are currently at ("+current_point.x+", "+current_point.y+")");
		for(int i=0 ; i<board_size ; ++i){
			for(int j=0 ; j<board_size; ++j){
				
				if(current_point.x == i && current_point.y == j){
					System.out.print("C");
				
				}else {		
					switch(board[i][j]){
					case EMPTY_POINT:
						System.out.print("-");
						break;
					
					case START_POINT:
						System.out.print("S");
						break;
					
					case END_POINT:
						System.out.print("E");
						break;
					case WALL_POINT:
						System.out.print("W");
						break;
					}
				}
				System.out.print(" ");
			}
			System.out.println();
		}
	}

}
