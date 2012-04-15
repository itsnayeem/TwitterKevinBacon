import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.saj.InvalidSyntaxException;

public class FriendCrawler implements Runnable {
	/**
	 * Max number of friends to search and counter
	 */
	public static final int MAX_SEARCHES = 10;
	public static final AtomicInteger count = new AtomicInteger(0);

	/**
	 * Stores value of step when target is found
	 */
	public static final AtomicInteger foundStep = new AtomicInteger(0);

	/**
	 * Keeps track of users who have been processed
	 */
	private static final ConcurrentHashMap<Integer, Boolean> checked = new ConcurrentHashMap<Integer, Boolean>();

	/**
	 * The id to search
	 */
	private int myId;

	/**
	 * The steps down the tree this id is
	 */
	private int myStep;

	public FriendCrawler(int myId, int myStep) {
		this.myId = myId;
		this.myStep = myStep;
	}

	public void run() {
		// System.out.println("Running: " + myId + ":" + myStep + ":" + count +
		// ":" + WorkQueue.inQueue.get());
		// update job counter

		WorkQueue wq = WorkQueue.getInstance();

		// if work queue is shutting down, return to allow shutdown to happen
		if (wq.isShutdown()) {
			return;
		}

		// if friend limit is hit, shutdown queue and return
		if (count.getAndIncrement() > MAX_SEARCHES) {
			wq.shutdown();
			return;
		}

		String path = "https://api.twitter.com/1/friends/ids.json?cursor=-1&user_id="
				+ myId;
		StringBuffer webContent = new StringBuffer();
		String line, language, urlpath;
		Socket socket = null;
		PrintWriter output = null;
		BufferedReader input = null;

		try {
			URL url = new URL(path);
			socket = new Socket(InetAddress.getByName(url.getHost())
					.getHostAddress(), 80);

			output = new PrintWriter(new OutputStreamWriter(
					socket.getOutputStream()));

			if (!url.getPath().trim().equals("")) {
				urlpath = url.getPath() + "?" + url.getQuery();
			} else {
				urlpath = "/";
			}

			language = " HTTP/1.1\nHost: " + url.getHost() + "\n";

			// write proper get request
			// System.out.println("GET " + urlpath + language);
			output.println("GET " + urlpath + language);
			output.flush();

			input = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			// skip headers
			while ((line = input.readLine()) != null && !line.contains("{"))
				;

			// save content
			do {
				webContent.append(line + " ");
			} while ((line = input.readLine()) != null);

		} catch (UnknownHostException e) {
			System.out.println("Unknown Host: " + path);
		} catch (IOException e) {
		} finally {
			output.close();
			try {
				input.close();
			} catch (IOException e) {
			}
		}

		String rawContent = new String(webContent);
		// System.out.println("Response: " + rawContent);

		// parse json
		JsonRootNode json = null;
		try {
			json = new JdomParser().parse(rawContent);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			return;
		}
		List<JsonNode> results = null;

		// if the response doesn't contain "ids" it hit the rate limit
		try {
			results = json.getArrayNode("ids");
		} catch (IllegalArgumentException e) {
			foundStep.set(-1);
			wq.shutdown();
			return;
		}

		// get value in results
		for (JsonNode j : results) {
			// System.out.println("friend of " + myId + ": " + j.getText());
			int friend = Integer.parseInt(j.getText());

			// if target found, set step and shutdown queue
			if (Crawler.target == friend) {
				foundStep.set(myStep);
				wq.shutdown();
				return;
			}

			// if friend not already checked, mark checked and run job
			if (checked.get(friend) == null) {
				checked.put(friend, true);
				if (!wq.isShutdown()) {
					wq.execute(new FriendCrawler(friend, myStep + 1));
				}
			}
		}
		// if no friends are being added and queue is empty, shutdown
		if (wq.getTaskCount() <= 1 && wq.getActiveCount() <= 1) {
			wq.shutdown();
			return;
		}
	}
}
