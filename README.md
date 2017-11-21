# SimpleLanguage

A simple implementation of Brainf&*k built using Truffle for the GraalVM.

This repository is licensed under the permissive UPL licence. Fork it to begin
your own Truffle language.

## Prerequisites
* JDK 8
* maven3 

## Installation

* Clone graal-bf repository using
* Download Graal VM Development Kit from 
  http://www.oracle.com/technetwork/oracle-labs/program-languages/downloads
* Unpack the downloaded `graalvm_*.tar.gz` into `simplelanguage/graalvm`. 
* Verify that the file `graal-bf/graalvm/bin/java` exists and is executable
* Execute `mvn package`

* Execute `./bf tests/HelloWorld.bf` to run a simple language source file.
* Execute `./bf -disassemble tests/HelloWorld.bf` to see assembly code for Truffle compiled functions.

## IGV

* Download the Ideal Graph Visualizer (IGV) from
  https://lafo.ssw.uni-linz.ac.at/pub/idealgraphvisualizer/
* Unpack the downloaded `.zip` file  
* Execute `bin/idealgraphvsiualizer` to start IGV
* Execute `./bf -dump tests/HelloWorld.bf` to dump graphs to IGV.

## Debugging

* Execute `./bf -debug tests/HelloWorld.bf`.
* Attach a Java remote debugger (like Eclipse) on port 8000.

## Tested Compatibility

Simple language is compatible to:

* Truffle-Version: 0.28
* GraalVM-Version: 0.28


## Further information

* [Truffle JavaDoc](http://lafo.ssw.uni-linz.ac.at/javadoc/truffle/latest/)
* [Truffle on Github](http://github.com/graalvm/truffle)
* [Graal on Github](http://github.com/graalvm/graal-core)
* [Truffle Tutorials and Presentations](https://wiki.openjdk.java.net/display/Graal/Publications+and+Presentations)
* [Truffle FAQ and Guidelines](https://wiki.openjdk.java.net/display/Graal/Truffle+FAQ+and+Guidelines)
* [Graal VM]( http://www.oracle.com/technetwork/oracle-labs/program-languages/overview) on the Oracle Technology Network
* [Papers on Truffle](http://ssw.jku.at/Research/Projects/JVM/Truffle.html)
* [Papers on Graal](http://ssw.jku.at/Research/Projects/JVM/Graal.html)

## License

The Truffle framework is licensed under the [GPL 2 with Classpath exception](http://openjdk.java.net/legal/gplv2+ce.html).
The SimpleLanguage is licensed under the [Universal Permissive License (UPL)](http://opensource.org/licenses/UPL).


