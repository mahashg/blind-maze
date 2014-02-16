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
}

public class SourceToDestination {
	public static int board[][];
	public static int board_size=10;
	
	public static final int EMPTY_POINT = 0;
	public static final int START_POINT = 1;
	public static final int END_POINT = 2;
	public static final int WALL_POINT = 3; 
	
	public static int st_x, st_y;
	public static int en_x, en_y;
	public static int wall_count = 10;
	
	public static int curr_x, curr_y;
	
	public static int move_count;
	public static int choice_count;
	public static boolean debug_on = false;
	
	public static int getNextPoint(){
		Random rand = new Random();
		
		return rand.nextInt(board_size);
	}
	
	public static void setPoint(int pt_type){
		int x, y;
		
		while(true){
			x = getNextPoint();
			y = getNextPoint();
			
			if(board[x][y] == EMPTY_POINT){				
				board[x][y] = pt_type;
				break;
			}
		}
		
		switch(pt_type){
		case START_POINT:
			st_x = x; st_y = y;
			break;
			
		case END_POINT:
			en_x = x; en_y = y;
			break;
		}
	}
	
	public static void init(){
		setPoint(START_POINT);
		setPoint(END_POINT);
		
		for(int i=0 ; i<wall_count ; ++i){
			setPoint(WALL_POINT);
		}

		curr_x = st_x;
		curr_y = st_y;
	}
	
	public static char read(){
		Scanner read = new Scanner(System.in);
		return read.nextLine().trim().charAt(0);
	}
	
	public static void showChoice(){
		
	}
	
	public static int getValue(int x, int y){
		return x*board_size+y;		
	}
	public static Point readValue(int val){
		return new Point(val / board_size,  val % board_size);
	}
	
	public static boolean isValid(int x, int y){
		boolean ret_value = false;
		
		if(x >= 0 && x<board_size && y >= 0 && y<board_size){
			ret_value = (board[x][y] == START_POINT || board[x][y] == END_POINT || board[x][y] == EMPTY_POINT);
		}
		
		if(debug_on) System.out.println("Point ("+x+", "+y+") is : "+ret_value);
		return ret_value;
	}
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
	
	public static boolean move(int x, int y){
		if(isValid(x, y)){
			curr_x = x;
			curr_y = y;
			return true;
		}
		return false;
	}
	
	private static int find_Steps() {
		int step_count=0;
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		Queue<Integer> queue = new ArrayDeque<Integer>();
		
		if(curr_x == en_x && curr_y == en_y){
			return step_count;
		}
		
		int start_key = getValue(curr_x, curr_y); 
		map.put(start_key, -1);
		queue.add(getValue(curr_x, curr_y));
		
		while(!queue.isEmpty()){
			int val = queue.remove();
			Point pt = readValue(val);
			if(pt.x == en_x && pt.y == en_y){
				break;
			}
			List<Point> neigh = readNeighbour(pt);
			for(Point p : neigh ){
				int key = getValue(p.x, p.y);
				
				if(!map.containsKey(key)){
					map.put(key, val);
					queue.add(key);
				}
			}
		}
		
		int end_key = getValue(en_x, en_y);
		if(map.containsKey(end_key)){
			int curr_key = end_key;
			
			while(curr_key != start_key){
				++step_count;				
				curr_key = map.get(curr_key);
			}
			return step_count;
		
		}else {
			return 0;
		}
	}
	
	public static void main(String[] args) {
		board = new int[board_size][];
		char choice;
		
		for(int i=0 ; i<board_size ; ++i){
			board[i] = new int[board_size];
			
			for(int j=0 ; j<board_size ; ++j){
				board[i][j] = EMPTY_POINT;
			}
		}// end of init
		
		init();
		
		while(true){
			int no_steps = find_Steps();
			
			if(no_steps == 0){
				System.out.println("Congratulations!! you have reached the destination. :-)");
				System.out.println("No of moves = "+move_count);
				break;
			}
			
			System.out.println("Shortest path to destiation is "+no_steps+" steps away. ");
	/*		System.out.println("Wanna take Hint ? (y/n)");
			choice = read();
			
			switch(choice){
			case 'Y': case 'y':
				showChoice();
				++choice_count;
				break;
	
			case 'N': case 'n':
				break;			
			}
	*/
			
			boolean valid=true;
			do {
				System.out.println("Which way you want to go ?? (L, R, U, D)");
				choice = read();
				
				switch(choice){
				case 'L': case 'l':
					valid = move(curr_x-1, curr_y);
					break;
					
				case 'R': case 'r':
					valid = move(curr_x+1, curr_y);
					break;
					
				case 'U': case 'u':
					valid = move(curr_x, curr_y+1);
					break;
					
				case 'D': case 'd':
					valid = move(curr_x, curr_y-1);
					break;
					
				default:
					System.out.println("no option matched.");
					break;
				}
				if(debug_on) print_status();
				if(debug_on) System.out.println("is valid ? "+valid);
			}while(!valid);
			
			++move_count;
			
		}
	}

	private static void print_status() {
		
		System.out.println("You are currently at ("+curr_x+", "+curr_y+")");
		for(int i=0 ; i<board_size ; ++i){
			for(int j=0 ; j<board_size; ++j){
		
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
				System.out.print(" ");
			}
			System.out.println();
		}
	}

}
