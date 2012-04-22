<h1>Twitter Six Degrees of Kevin Bacon<h1>
<p>This program will find the degree of separation between two twitter users. 
Based on: http://en.wikipedia.org/wiki/Six_Degrees_of_Kevin_Bacon</p> 
<p>To run this program:
<br />
<blockquote><pre>
java -cp argo-2.23.jar:. Crawler -source <user_id> -target <user_id>
</pre></blockquote>
<br />	
Where user_id is the numeric user_id for the twitter accounts.
<br /><br />
To find a numeric user_id, use this api call:
https://api.twitter.com/users/lookup.json?screen_name=<username>
<br /><br />
Libraries used: argo-2.23.jar
</p>
<h3>Details</h3>
<p></p>