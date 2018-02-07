package data;

public class RootedTriplet {
	public final int a, b, c; 
	
	public RootedTriplet(int a, int b, int c) {
		this.c = c; 
		
		if (a < b) {
			this.a = a;
			this.b = b; 
		}
		else {
			this.b = a; 
			this.a = b; 
		}
	}
	
	public String toString() {
		return "{" + a + "," + b + "|" + c + "}"; 
	}
	

}
