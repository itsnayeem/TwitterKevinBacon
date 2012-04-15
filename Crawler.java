
public class Crawler {

	public static int target;
	
	public static void main(String[] args) {
		if (args.length != 4) {
			System.err.println("Incorrect number of arguments\n");
			System.exit(1);
		}
		
		String s = null, t = null;
		
		for (int i = 0; i < args.length; i += 2) {
			
			if (args[i].equals("-source")) {
				s = args[i+1];
			} else if (args[i].equals("-target")) {
				t = args[i+1];
			} else {
				System.err.println("Invalid arguments");
				System.exit(1);
			}
		}
		
		System.out.println("Searching for degree of separation between " + s + " and " + t);
		
		target = Integer.parseInt(t);
		
		// start seed friend
		WorkQueue.getInstance().execute(new FriendCrawler(Integer.parseInt(s), 1));
		
		// wait for work queue to shutdown
		WorkQueue.getInstance().awaitShutdown();
		
		// print results
		if (FriendCrawler.foundStep.get() > 0 ) {
			System.out.println("The degree of separation is: " + FriendCrawler.foundStep);
		} else if (FriendCrawler.foundStep.get() < 0) {
			System.out.println("Rate Limit Exceeded");
		} else {
			System.out.println("The degree of separation was not found.");
			if (FriendCrawler.count.get() >= FriendCrawler.MAX_SEARCHES) {
				System.out.println("\tReason: MAX SEARCHES (" + FriendCrawler.MAX_SEARCHES + ") limit hit.");
				System.out.println("\tTo increase, change FriendCrawler.MAX_SEARCHES. Beware, Twitter limits to 150 queries/hour");
			} else {
				System.out.println("\tReason: No more friends to search.");
			}
		}
		
	}

}
