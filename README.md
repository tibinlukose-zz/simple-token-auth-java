# simple-token-auth-java
Implementing Simple Time Based Web Token For Session Less Authentication.

An alternative  authentication method for Web/Mobile Apps where session_id based authentication cannot satisfy. Like cookie storing issue.
In this , Token will be issued to successful user authentication for username and password. Each Token will have constant expiry time which is defined in the program.  And for every request from app, UserId and Token has to be mentioned and as part of response, new token will be issued. 
