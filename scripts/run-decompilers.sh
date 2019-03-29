#!/bin/bash

PROJECTS=""

DECOMPILERS=""

CLASSPATH=""

LOG_DIR=""

echo "Start decompiler run"
for p in $PROJECTS
do
	echo "Start decompiler run on $p"
	for d in $DECOMPILERS
	do
		echo "Start decompiler run on $p with $d"
		java -cp $CLASSPATH se.kth.DecompilerComparator -p $p -d $d 1> ${LOG_DIR}/${p}-${d}.log 2> ${LOG_DIR}/${p}-${d}.log
	done
done
