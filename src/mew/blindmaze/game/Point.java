package mew.blindmaze.game;

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
	@Override
	public String toString() {
		
		return "("+this.x+", "+this.y+")";
	}
}