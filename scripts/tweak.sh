#!/bin/bash

for f in $(ls repo-resources/*/*)
do
	dest=$(echo $f | sed -e 's/repo-resources/repos/')
	cp $f $dest
done
