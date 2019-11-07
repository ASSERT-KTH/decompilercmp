#!/bin/bash

for f in $(find repo-resources -type f)
do
	dest=$(echo $f | sed -e 's/repo-resources/repos/')
	echo "file $f -> cp $f $dest"
	cp $f $dest
done
