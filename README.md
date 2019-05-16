# Overview
This application exposes a RESTful API that supports tweeting and retrieval of the home timeline.

This codebase depends on the [Twitter4J API](http://twitter4j.org/). 

## How To Run

Begin by cloning the repository using ```git clone https://github.com/Vadifire/tweeting.git``` 

To build the project, cd into the downloaded repository and run: ```./gradlew build```. 
Building will automatically perform unit testing. Use ```./gradlew test --rerun-tasks``` to manually run unit testing.

#### Authentication

To use the Twitter API, authentication is required. 
API keys and tokens for authentication can be generated here: https://apps.twitter.com. Create a new app if
necessary, then access its "Keys and tokens" tab. Copy the config-example.yml file (in the project's root directory) 
into a new file called config.yml. Replace API_KEY, API_SECRET_KEY, ACCESS_TOKEN, and ACCESS_TOKEN_SECRET in the config 
file with the respective values from Twitter.

To start the application, run ```./gradlew run```. The application will start running locally on port 8080.

#### Posting Tweets
 
To post a tweet, run the following command, replacing ```Hello World``` with your desired tweet:
```curl -i http://localhost:8080/api/1.0/twitter/tweet -d 'message=Hello World'```

#### Retrieving Home Timeline

To retrieve the home timeline , run the following command:
 ```curl -i http://localhost:8080/api/1.0/twitter/timeline```
 
Alternatively, you can simply access ```http://localhost:8080/api/1.0/twitter/timeline``` in browser.
 
##### Filtering Home Timeline

To retrieve a filtered version of the home timeline, run the following command, replacing ```world``` with your desired 
keyword:  ```curl -i -G http://localhost:8080/api/1.0/tweet/filter --data-urlencode "keyword=world"```
 
Alternatively, you can simply access ```http://localhost:8080/api/1.0/tweet/filter?keyword=world``` in browser.

## How To Check Code Coverage

To generate a code coverage report, build the project using ```./gradlew build``` 
and run ```./gradlew jacocoTestReport```. The test report is called index.html and is located in the
build/reports/jacoco/test/html/ directory.

## Logging

By default, the latest log file is stored as /log/tweeting.log.