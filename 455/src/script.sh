#!/bin/bash
#Bash script to set up the program for cs455 HW2.
#Starts new terminals, sshes to specified computers, and runs Server and Client.
#The clients' computers’ names are read from a file named comps.

SERVER="kitaro"
PORTNUM=63094
NUMTHREADS=10

#recompile in case there are any changes
echo 'compiling things'
make all
echo '---------------------------'
echo''

#set up the server
echo 'sshing to '$SERVER
echo 'setting up the server'
gnome-terminal -t 'server - '$SERVER -x bash -c "ssh -t $SERVER 'cd ~/git/cs455/455/src; java cs455.scaling.server.Server $PORTNUM $NUMTHREADS; bash'" &
echo '---------------------------'
echo''

#give server time to start
sleep 1s

#set up the clients
for i in `cat ./comps`
do
	echo 'sshing to '${i}
	echo 'setting up a client'
	gnome-terminal -t ${i} -x bash -c "ssh ${i} 'bash -s' < client.sh" &
done