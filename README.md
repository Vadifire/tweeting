# Overview
ProgramOne is used to post a new status update on Twitter. ProgramOne will output whether a status has been successfully posted.

ProgramTwo is used to retrieve and print statuses from the home timeline. The status author and text will be displayed for each post retrieved.

## How To Run
First, compile and build jar files for Programs One and Two if necessary.

This codebase uses the [Twitter4J API](http://twitter4j.org/). To use the API, Twitter authentication is required.  API keys and tokens for authentication can be generated here: https://apps.twitter.com. Please follow the configuration instructions here: http://twitter4j.org/en/configuration.html. The twitter4j.properties should be placed in the same directory as the jar files if used.

To run either program from a jar file in command line, cd into the directory containing the jar files and run "java -jar JarName.jar". 

ProgramOne requires at least one argument which denotes the status(es) to be posted. 

