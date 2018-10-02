
public class Name {

	public static void main(String[] args) {
		int actual = 120;
		String lol = "amount < 100";


		if (lol.contains("amount")) {
		    String[] parts = lol.split(" ");
		    int value = Integer.parseInt(parts[2]);
		    helper(parts[1], actual, value);
		}

	}
	
	public static boolean helperString(String x, String country1, String country2) {
		switch(x) {
		    case "==":
		        return country1.equals(country2);
		    case "!=":
		        return !country1.equals(country2);
	    }
		return false;   
		
	}
	
	public static boolean helper(String x, int actual, int value) {
	    switch(x) {
	    case "<":
	        return actual < value;
	    case ">":
	        return actual > value;
	    case "==":
	        return actual == value;
	    case "!=":
	        return actual != value;
	    case "<=":
	    	return actual <= value;
	    case ">=":
	    	return actual >= value;
	    }
		return false;   
	}

}
