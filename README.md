# Overview
This application exposes a RESTful API that supports tweeting and retrieval of the home timeline.

This codebase depends on the [Twitter4J API](http://twitter4j.org/). 

## How To Run

Begin by cloning the repository using ```git clone https://github.com/Vadifire/tweeting.git``` 

To build the project, cd into the downloaded repository and run: ```./gradlew build```. 
Building will automatically perform unit testing. Use ```./gradlew test --rerun-tasks``` to manually run unit testing.

To use the Twitter API, authentication is required. 
API keys and tokens for authentication can be generated here: https://apps.twitter.com. 
Once your credentials have been generated, copy the config-example.yml file into a new file called config.yml. 
Replace the placeholder values with your consumer token, consumer key, access token, and access token secret. 

To start the application, run ```./gradlew run```. The application will start running locally on port 8080.

#### Retrieving Home Timeline

To retrieve the home timeline , run the following command:
 ```curl -i http://localhost:8080/api/1.0/twitter/timeline```
 
Alternatively, you can simply access ```http://localhost:8080/api/1.0/twitter/timeline``` in browser.
 
 #### Retrieving Tweets
 
To post a tweet, run the following command, replacing ```Hello World``` with your desired tweet:
```curl -i http://localhost:8080/api/1.0/twitter/tweet -d 'message=Hello World'```


## How To Check Code Coverage

To generate a code coverage report, build the project using ```./gradlew build``` 
and run ```./gradlew jacocoTestReport```. The test report is called index.html and is located in the
build/reports/jacoco/test/html/ directory. To ensure 100% line and path coverage of required classes, run 
```./gradlew jacocoTestCoverageVerification```