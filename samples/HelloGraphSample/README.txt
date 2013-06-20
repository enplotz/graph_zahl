******************************************************************************
*                                                                            *
*                   InfiniteGraph Sample: HelloGraph!                        *
*                                                                            *
*                   ===============================                          *
*                   COPYRIGHT AND DISCLAIMER NOTICE                          *
*                   ===============================                          *
*                                                                            *
* The following copyright and disclaimer notice applies to all files         *
* included in this application.                                              *
*                                                                            *
* Objectivity, Inc. grants you a nonexclusive copyright license to use all   *
* programming code examples from which you can generate similar function     *
* tailored to your own specific needs.                                       *
*                                                                            *
* All sample code is provided by Objectivity, Inc. for illustrative          *
* purposes only. These examples have not been thoroughly tested under all    *
* conditions. Objectivity, Inc., therefore, cannot guarantee or imply        *
* reliability, serviceability, or function of these programs.                *
*                                                                            *
* All programs contained herein are provided to you "AS IS" without any      *
* warranties or indemnities of any kind. The implied warranties of           *
* non-infringement, merchantability and fitness for a particular purpose     *
* are expressly disclaimed.                                                  *
*                                                                            *
******************************************************************************

Overview
--------
This example creates a simple graph database that includes two vertices
connected by a single edge. One of the vertices is named so that it can be
used as the starting point (root vertex) for searching in the graph database.

With each execution, the program removes the existing graph database (if
applicable) and creates a new one.

Compiling and Running
---------------------
You must have already installed and configured InfiniteGraph as described in
the installation instructions on the InfiniteGraph Developer Site:

http://wiki.infinitegraph.com

Eclipse Java IDE
----------------
1. Create a new Java project with the following settings:

   a. Name the project HelloGraphSample.
   b. For the default project location, navigate to the HelloGraphSample
      directory you extracted.
   c. For the Java build settings, add the
      <installDir>/lib/InfiniteGraph.jar and
      <installDir>/lib/slf4j-simple-1.6.1.jar to your
      libraries as external JARs.

2. Run the project.

Command-Line
------------
For compiling and running on the command line, include InfiniteGraph.jar and
slf4j-simple-1.6.1.jar in your CLASSPATH environment variable. You also need
to include the src directory in your CLASSPATH using the command line.

If your CLASSPATH is complete, compile and run the program as follows:

  cd HelloGraphSample

  javac -cp "%CLASSPATH%";.\src .\src\com\infinitegraph\samples\hellograph\HelloGraph.java
  java -cp "%CLASSPATH%";.\src com.infinitegraph.samples.hellograph.HelloGraph

If your CLASSPATH is not complete, pass the needed entries as options.

Windows example:

javac -cp ".;C:\Program Files\Infinitegraph\3.0\lib\InfiniteGraph.jar;
  C:\Program Files\Infinitegraph\3.0\lib\slf4j-simple-1.6.1.jar;"
  .\src\com\infinitegraph\samples\hellograph\HelloGraph.java

java -cp ".;.\src;C:\Program Files\Infinitegraph\3.0\lib\InfiniteGraph.jar;
  C:\Program Files\Infinitegraph\3.0\lib\slf4j-simple-1.6.1.jar;"
  com.infinitegraph.samples.hellograph.HelloGraph

UNIX example:

javac -cp .:/opt/InfiniteGraph/3.0/lib/InfiniteGraph.jar:
  /opt/InfiniteGraph/3.0/lib/slf4j-simple-1.6.1.jar
  ./src/com/infinitegraph/samples/hellograph/HelloGraph.java

java -cp .:./src:/opt/InfiniteGraph/3.0/lib/InfiniteGraph.jar:
  /opt/InfiniteGraph/3.0/lib/slf4j-simple-1.6.1.jar
  com.infinitegraph.samples.hellograph.HelloGraph

Viewing Results
---------------
Use the InfiniteGraph Visualizer to view the graph database.

* Start IG Visualizer and connect to the graph database through its bootfile,
  HelloGraph.boot.
* Search for the named vertex, John.

For information about using IG Visualizer, refer to the section about
viewing data at the end of the HelloGraphDB tutorial on the InfiniteGraph
Developer Site.







