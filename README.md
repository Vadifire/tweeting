# Overview
ProgramOne is used to post a new status update on Twitter. ProgramOne will output whether a status has been successfully posted.

ProgramTwo is used to retrieve and print statuses from the home timeline. The status author and text will be displayed for each post retrieved.

This codebase depends on the [Twitter4J API](http://twitter4j.org/). 

## How To Run

Begin by cloning the repository using ```git clone https://github.com/Vadifire/tweeting.git```

Create the /out/ directory and run the following command to compile:

```
javac -cp lib/twitter4j-core-4.0.7.jar src/main/*.java -d out/
```

CD into the /out/ directory and run the following commands to build the jar files:
```
jar cvfm ProgramOne.jar ProgramOne.mf main
jar cvfm ProgramTwo.jar ProgramTwo.mf main
```

To use the Twitter API, authentication is required.  API keys and tokens for authentication can be generated here: https://apps.twitter.com. Please follow the configuration instructions here: http://twitter4j.org/en/configuration.html. The twitter4j.properties should be placed in the same directory as the jar files if used.

To execute ProgramOne, run ```java -jar ProgramOne.jar 'Hello World'```. To execute ProgramTwo, run ```java -jar ProgramTwo.jar```. 

Note: ProgramOne requires at least one argument which denotes the status(es) to be posted. 

