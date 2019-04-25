# Overview
ProgramOne is used to post a new status update on Twitter. ProgramOne will output whether a status has been successfully posted.

ProgramTwo is used to retrieve and print statuses from the home timeline. The status author and text will be displayed for each post retrieved.

This codebase depends on the [Twitter4J API](http://twitter4j.org/). 

## How To Run

Begin by cloning the repository using ```git clone https://github.com/Vadifire/tweeting.git```

To build the project, open build.gradle and set the ```mainClassName``` attribute to ```main.ProgramOne``` or ```main.ProgramTwo```, depending on which project you want to build and run.

Next, run the build command: ```./gradlew build```

To use the Twitter API, authentication is required.  API keys and tokens for authentication can be generated here: https://apps.twitter.com. Please follow the configuration instructions here: http://twitter4j.org/en/configuration.html. The twitter4j.properties should be placed in project's root directory.

To run ProgramOne run: ```./gradlew run --args 'Hello World'```
Note: The string argument must be unique from any previous Twitter Status posted. Change 'Hello World' to a unique message if necessary.

To run ProgramTwo run: ```./gradlew run```
