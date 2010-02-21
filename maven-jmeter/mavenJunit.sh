#!/bin/bash
#
# Script to checkout jmeter svn rel 2.3.2, build it and install the
# required maven artifacts in your local repo.
#
# You need the jmeter artifacts to build the Samplers.
#

DIR=$(dirname $0)

echo "Going to build and install JMeter maven artifacts."
echo "Using $DIR as working directory (press ctrl-c to quit, press return to continue)"
read 


echo -e "\nGetting JMeter 2.3.2 source\n"
svn co  http://svn.apache.org/repos/asf/jakarta/jmeter/tags/v2_3_2 jmeter-2.3.2


echo -e "\nBuilding JMeter\n"
cd jmeter-2.3.2/
ant


echo -e "\nInstalling maven artifacts\n"
mvn install:install-file -Dfile=lib/ext/ApacheJMeter_core.jar \
    -DpomFile=../ApacheJMeter_core-2.3.2.pom -Dpackaging=jar \
    -DgroupId=org.apache.jmeter -DartifactId=ApacheJMeter_core \
    -Dversion=2.3.2

mvn install:install-file -Dfile=lib/ext/ApacheJMeter_components.jar \
    -DpomFile=../ApacheJMeter_components-2.3.2.pom -Dpackaging=jar \
    -DgroupId=org.apache.jmeter -DartifactId=ApacheJMeter_components \
    -Dversion=2.3.2

mvn install:install-file -Dfile=lib/ext/ApacheJMeter_java.jar \
    -DpomFile=../ApacheJMeter_java-2.3.2.pom -Dpackaging=jar \
    -DgroupId=org.apache.jmeter -DartifactId=ApacheJMeter_java \
    -Dversion=2.3.2

mvn install:install-file -Dfile=lib/ext/ApacheJMeter_junit.jar \
    -DpomFile=../ApacheJMeter_junit-2.3.2.pom -Dpackaging=jar \
    -DgroupId=org.apache.jmeter -DartifactId=ApacheJMeter_junit \
    -Dversion=2.3.2

# Note: use the jorhan.jar from jmeter!

mvn install:install-file -Dfile=lib/jorphan.jar \
    -DpomFile=../jorphan-2.3.2.pom -Dpackaging=jar \
    -DgroupId=org.apache.jmeter -DartifactId=jorphan -Dversion=2.3.2
