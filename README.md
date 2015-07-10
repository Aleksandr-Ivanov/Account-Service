##AccountService API, servlet-based server-side and JAX-RS client-side applications with Jersey 2.19 as JAX-RS implementation 

##Installation:

1. Create DB Schema, Table, User and set connection for server-side app. SQL script is located in accountservice\server for help.
Connection parameters is setting by file \accountservice\server\src\main\resources\settings.properties

2. Set client parameters: 
   - readers/writers count 
   - planning service publish address from address to name of deployed app (by default app build name is: accountservice) 
     example: http://localhost:8080/accountservice  
   - ids range

3. Build project with Maven

4. Deploy servlet to Tomcat

5. Start client with command line in \accountservice\client\target folder: java -jar client.jar
Script stub startClient.sh is located in \accountservice\client\ Please, change it if it's needed

##Server-side app navigation:

http://localhost:<port>/accountservice/stats - statistics of 'get' and 'add' requests processing (works through GET requests)
http://localhost:<port>/accountservice/stats/reset - set statistics to 0 (works through GET requests to provide reset by browser navigation)

http://localhost:<port>/accountservice/account/{id} - @GET request - processes getAmount AccountService method
                                                      @POST request - processes addAmount AccountService method

Tested with: Tomcat 7.0.62, MySQL 5.6.25, Maven 3.
