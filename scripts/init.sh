#!/bin/bash

CONFIG=$(<$1)
YAJTA_DIR=$2
YAJTA="$YAJTA_DIR/script/tie-maven.sh $YAJTA_DIR/target/yajta-2.0.0-jar-with-dependencies.jar"


for o in $(echo $CONFIG | jq -c '.[]')
do
	URL=$(echo $o | jq -r '.url')
	REPO=$(echo $o | jq -r '.repo')
	PACKAGES=$(echo $o | jq -r '.packages')
	COMMIT=$(echo $o | jq -r '.commit')
	
	git clone $URL
	cd $REPO
	git reset --hard $COMMIT
	mvn install
	eval $YAJTA
	cd ..
done
