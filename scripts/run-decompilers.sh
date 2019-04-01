#!/bin/bash

PROJECTS="Bukkit commons-collections commons-lang mimecraft commons-codec commons-imaging DiskLruCache jsoup scribejava joda-time"
#PROJECTS="joda-time mimecraft"
#PROJECTS="Bukkit"

#DECOMPILERS="Procyon-0.5.34 Jode-1.1.2-pre1 CFR-0.141 Fernflower-2.5.0.Final Krakatau JD-GUI-1.4.1 Dava-3.3.0"
DECOMPILERS="JADX-0.9.0"

CLASSPATH="target/decompiler-cmp-1.0-SNAPSHOT-jar-with-dependencies.jar:lib/jd-gui-1.4.1.jar:lib/jode-1.1.2-pre1.jar:lib/sootclasses-trunk-jar-with-dependencies.jar"

LOG_DIR="log"

REPOS_DIR="repos"

echo "Start decompiler run"
for p in $PROJECTS
do
	echo "Start decompiler run on $p"
	for d in $DECOMPILERS
	do
		echo "Start decompiler run on $p with $d"
		java -cp $CLASSPATH se.kth.DecompilerComparator -p $REPOS_DIR/$p -d $d 1> ${LOG_DIR}/${p}-${d}.log 2> ${LOG_DIR}/${p}-${d}.log
	done
done