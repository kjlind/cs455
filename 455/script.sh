#!/bin/bash
#Bash script to set up the program for cs455 HW1.
#Starts new terminals, sshes to specified computers, and runs Registry and MessagingNode.
#The messaging nodes' computers’ names are read from a file named comps.

REGISTRY="kitaro"
PORTNUM=61000

#set up the registry
echo 'sshing to '$REGISTRY
echo 'setting up the registry'
gnome-terminal -t 'registry - '$REGISTRY -x bash -c "ssh -t $REGISTRY 'cd ~/git/cs455/455/src; java cs455.overlay.nodes.Registry $PORTNUM; bash'" &

#give registry time to start
sleep 1s

#set up the messaging nodes
for i in `cat ./comps`
do
	echo 'sshing to '${i}
	echo 'setting up a messaging node'
	gnome-terminal -t ${i} -x bash -c "ssh -t ${i} 'cd ~/git/cs455/455/src; java cs455.overlay.nodes.MessagingNode $REGISTRY $PORTNUM; bash'" &
done