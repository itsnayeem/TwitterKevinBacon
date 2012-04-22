# Twitter Six Degrees of Kevin Bacon #

This program will find the degree of separation between two twitter users. 
Based on [Six Degrees of Kevin Bacon](http://en.wikipedia.org/wiki/Six_Degrees_of_Kevin_Bacon)

-----

To run this program:

	$ java -cp argo-2.23.jar:. Crawler -source <user_id> -target <user_id>

Where user_id is the numeric user_id for the twitter accounts.

To find a numeric **user_id**, use this api call:
`https://api.twitter.com/users/lookup.json?screen_name=<username>`

Libraries used: **argo-2.23.jar**

-----

## Details ##

### Design ###

This program is a web crawler with the specific purpose of connecting Twitter's JSON API. There is
a thread pool with n amount of worker threads. When started the program, there is a source **user_id**
and a target **user_id**. When the source **user_id** is processed, it will produce either an id that matches
the target **user_id* or a list of ids.

### Hazards ###

As with any graph walking process, there is the hazard of there being a cycle (especially with Twitter). 
To prevent a cycle from causing problems, each id that is processed is stored in a static concurrent 
HashMap. Before and id is processed, it is checked against the map of processed ids.

Twitter's API has a limit on the number of requests that can be made per app per hour. Since this is
not a commercial app, there is maximum number of ids that will be processed. This can be set at `FriendCrawler.MAX_SEARCHES` 

### Crawler.java ###

The `Crawler` class is the driver for the program. It does a few simple tasks: 
- parses the arguments
- adds a `FriendCrawler` to the `WorkQueue` with the seed friend
- waits for the processes to finish
- prints out the results

### FriendCrawler.java ###

The `FriendCrawler` (for lack of a better name), is the meat of the project. Its tasks are:
- to query Twitter's API for a list of friends for the given user_id
- for each of those friends check to see if it's the target user_id
- if it is, start the shutdown process
- if it is not, create a new `FriendCrawler` for each of those user_id and let the process continue

### WorkQueue.java ###

The `WorkQueue` is a wrapper for Java's ThreadPoolExecutor class. It has the added functionality
of having an `awaitShutdown()` method to allow for a thread to wait for all the `WorkQueue's` tasks
to finish.
 