package functions;

public class MessageTrackCheck {
	private int offset;
	private int instance;
	private int runningSum;

	public MessageTrackCheck(Integer offset) {
		// Constructor, with given offset to initialise the running sum

		// TODO
		this.offset = offset;
		runningSum = offset;
		
		
	}
	
	public void add(Integer n) {
		// PRE: -
		// POST: Adds n to the running sum

		// TODO
		this.runningSum += n;
	}
	
	public char check() {
		// PRE: -
		// POST: Returns the character that corresponds to the running sum mod 26;
		//       0..25 correspond to a..z
		
		// TODO
		int result = this.runningSum % 26;
		result += 97;
		return (char)result;
		
	}
	
	public void reset(Integer offset) {
		// PRE: -
		// POST: Re-initialises the running sum to the given offset

		// TODO
		this.runningSum = offset;
	}

	public static void main(String[] args) {
		MessageTrackCheck m = new MessageTrackCheck(5);
		
		System.out.println(m.check());
		m.add(24);
		System.out.println(m.check());
	}
	
}
