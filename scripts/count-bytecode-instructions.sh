#!/bin/zsh

ROOT=$(pwd)
RESULT="$ROOT/bytecode-instructions.csv"

echo "class,nbInstructions" > $RESULT

for r in $(ls repos)
do
	cd $ROOT
	cd repos/$r/target/classes
	for c in $(ls **/*.class)
	do
		cl=$(echo $c | cut -d '.' -f1)
		wp=$(echo $cl | sed 's/\//./g')
		count=$(javap -c $wp 2> /dev/null| grep -c -E "^[[:space:]]+[0-9]+:")
		
		echo "$cl,$count" >> $RESULT
	done
done
