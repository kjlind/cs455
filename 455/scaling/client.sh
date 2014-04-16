#!/bin/bash
# starts a bunch of Clients in a single terminal

SERVER="kitaro"
PORTNUM=63094
NUMTHREADS=10
RATE=3

cd ~/git/cs455/455/src

for i in {1..5}
do
	java cs455.scaling.client.Client $SERVER $PORTNUM $RATE &
done