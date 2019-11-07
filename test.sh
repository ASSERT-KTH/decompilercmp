#!/bin/bash

HOME=$(pwd)

REPOS_DIR="repos"

PROJECTS="Bukkit  DiskLruCache  commons-codec  commons-collections  commons-imaging  commons-lang  dctest  javapoet  joda-time  jsoup  junit4  mimecraft  scribejava  spark"

echo "Start decompiler run"
for p in $PROJECTS
do
	cd $REPOS_DIR/$p
	mvn clean install
	echo "[$p] mvn clean test $?" >> $HOME/test.log
	cd $HOME
done
