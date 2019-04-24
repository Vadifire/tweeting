# Overview
ProgramOne is used to post a new status update on Twitter. ProgramOne will output whether a status has been successfully posted.

ProgramTwo is used to retrieve and print statuses from the home timeline. The status author and text will be displayed for each post retrieved.

## How To Run

Begin by cloning the repository using ```git clone https://github.com/Vadifire/tweeting.git```

This codebase depends on the [Twitter4J API](http://twitter4j.org/). You can download the API here: http://twitter4j.org/en/index.html#download. Create a /lib/ folder and place the twitter4j-core.x.x.x.jar file inside of it. This jar file can be found in the lib directory of the aforementioned download.

Create the /out/main directory and run the following command to compile:

```
javac -cp lib/twitter4j-core-x.x.x.jar src/main/*.java -d out/
```

CD into the /out/ directory and create a MANIFEST.MF file for the Program(s) you want to run. The Main-Class must be specified as ProgramOne or ProgramTwo. The Class-Path must include the twitter4j-core-x.x.x.jar file. 

For example:
```
Manifest-Version: 1.0
Main-Class: main.ProgramOne
Class-Path: ../lib/twitter4j-core-4.0.7.jar

```

Next run the following command to build the jar file(s):
```
jar cvfm [jar-file] [manifest-file] [main-directory]
```

For example: 

```
jar cvfm ProgramOne.jar MANIFEST.MF main
```

To use the Twitter API, authentication is required.  API keys and tokens for authentication can be generated here: https://apps.twitter.com. Please follow the configuration instructions here: http://twitter4j.org/en/configuration.html. The twitter4j.properties should be placed in the same directory as the jar files if used.

To execute the jar file, run ``` "java -jar JarName.jar". ```

Note: ProgramOne requires at least one argument which denotes the status(es) to be posted. 

