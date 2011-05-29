#!/bin/bash

# bootstraps a test version of this application using apache-servicemix

if [ 2 != $# ] 
then
	echo "Usage $0 path-to-servicemix-tar.gz http-port"
	exit 1
fi

TAR=$1
HTTP_PORT=$2

mvn clean install
rm -rf running/
mkdir running
cd running
tar xfvz ../$TAR
cd $(basename $(basename $1 .tar.gz) .tar.bzip2)
./bin/start
sleep 5
echo "org.osgi.service.http.port=$HTTP_PORT" > etc/org.ops4j.pax.web.cfg 
../../runcmd localhost smx smx "features:addUrl mvn:net.sheenobu/net.sheenobu.osgi.tutorial.modules.karaf.features/1.0/xml/features"
../../runcmd localhost smx smx "features:install net.sheenobu.osgi.springmvc.example"
touch etc/org.ops4j.pax.web.cfg 

echo "Navigate to http://localhost:$HTTP_PORT/example/ to see it running!"

#./bin/stop

#echo "run $PWD/bin/start or $PWD/bin/servicemix to run"


