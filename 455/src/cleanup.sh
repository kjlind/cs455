#!/bin/bash
# cs455 HW2
# bash script to clean up lingering java processes after the test has been run

for i in `cat ./comps`
do
	echo 'sshing to '${i}
	echo 'killing ALLLL the java mwahahaha'
	ssh ${i} 'killall java'
done
