package sec;

//represents two related pieces of data
public class Tuple<T> {
	private T tup1 = null;
	private T tup2 = null;
	
	//constructor
	public Tuple(T tup1, T tup2) {
		this.tup1 = tup1;
		this.tup2 = tup2;
	}
	
	//getter for first component in tuple
	public T getTup1() {
		return tup1;
	}
	
	//getter for second component in tuple
	public T getTup2() {
		return tup2;
	}

}
