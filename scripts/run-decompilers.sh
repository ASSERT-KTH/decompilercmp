#!/bin/bash


PROJECTS="Bukkit commons-collections commons-lang DiskLruCache jsoup mimecraft spark commons-codec commons-imaging dctest joda-time junit4 scribejava"

DECOMPILERS="Procyon-0.5.34 Jode-1.1.2-pre1 CFR-0.141 Fernflower-2.5.0.Final Krakatau JD-GUI-1.4.1 Dava-3.3.0 JADX-0.9.0"

COMPILERS="javac eclipse"

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
        for c in $COMPILERS
        do
		    echo "Start decompiler run on $p with $d compiled with $c"
		    java -cp $CLASSPATH se.kth.DecompilerComparator -p $REPOS_DIR/$p -d $d -j $c 1> ${LOG_DIR}/${p}-${d}-${c}.log 2> ${LOG_DIR}/${p}-${d}-${c}.log
	    done
	done
done
