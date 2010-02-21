#!/bin/bash

MY_DIR=$(dirname $0)

# TODO: crude ...
cd "${MY_DIR}/../../../"

echo "Setting up hudson in $PWD (press ctrl-c to abort, press enter to continue)"
read

if [ -d hudson ]; then
		cd hudson
else
		mkdir hudson
		cd hudson
		wget http://hudson-ci.org/latest/hudson.war
fi		

echo "running hudson ... (try http://localhost:8080/ )"
echo "install http://wiki.hudson-ci.org/display/HUDSON/Performance+Plugin ..."

java -jar hudson.war

#TODO: hudson config ...
