#!/bin/bash

ROOT=$(pwd)
OUTPUTDIR="sequences"
#COMPILERS="javac"
#REPOS="commons-lang Bukkit commons-codec commons-collections commons-imaging dctest DiskLruCache javapoet joda-time jsoup junit4 mimecraft scribejava spark"

COMPILERS="eclipse"
REPOS="jsoup"

#repo="dctest"
#for repo in $(ls repos)
for repo in $REPOS
do
	echo "REPO: $repo"
	for c in $COMPILERS
	do
		cd $ROOT
		cd repos/$repo
		echo "COMPILER: $c"
		mvn clean compile -Dmaven.compiler.compilerId=$c
		cd target/classes
		for cl in $(find . -name "*.class" | sed 's/.\///')
		do
			CL_DIR=$(dirname $cl)
			mkdir -p $ROOT/$OUTPUTDIR/$repo/$c/$CL_DIR
			CL_DOT=$(echo $cl | sed 's/\//./g' | sed 's/.class//')
			javap -v -c $CL_DOT 2> /dev/null | grep -E "^[[:space:]]+[0-9]+:" | sed 's/ 0:/ 0: method\n         0:/' | cut -d ':' -f2 | cut -d ' ' -f2 | sed 's/^#[0-9]\+(.\+$/ANNOTATION/' | grep -v -E "^[0-9]+$" > $ROOT/$OUTPUTDIR/$repo/$c/$(echo $cl | sed 's/.class//')
		done
	done
	COMPILERS="javac eclipse"
	
done

cd $ROOT

chmod g+rw -R $OUTPUTDIR
chmod o+rw -R $OUTPUTDIR

