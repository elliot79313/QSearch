package fbvideo;

public class CheckEqual {
	public static boolean equal( int... input) {
		for ( int x=0; x<input.length;x++){
		for(int y =x ;y<input.length;y++)
			if (input[x]!=input[y]){
				return false;
			}
		}
		return true;
	}
}
