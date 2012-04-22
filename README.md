# Twitter Six Degrees of Kevin Bacon #

This program will find the degree of separation between two twitter users. 
Based on [Six Degrees of Kevin Bacon](http://en.wikipedia.org/wiki/Six_Degrees_of_Kevin_Bacon)

-----

To run this program:

	$ java -cp argo-2.23.jar:. Crawler -source _<user_id>_ -target _<user_id>_

Where user_id is the numeric user_id for the twitter accounts.

To find a numeric _user_id_, use this api call:
https://api.twitter.com/users/lookup.json?screen_name=<username>

Libraries used: _argo-2.23.jar_

-----

## Details ##
