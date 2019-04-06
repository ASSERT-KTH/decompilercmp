#!/bin/zsh

HEADER="\t\t"
#line
HEADER2="                     "
HEADER3="----------------"
for overp in $(ls report | cut -d ':' -f1 | sort | uniq)
do
	overp=$(echo $overp | sed 's/commons-//')
	overp=$(printf "%11s" $overp)
	HEADER="$HEADER\t$overp"
	HEADER2="$HEADER2   javac|eclipse"
	HEADER3="$HEADER3-----------------"
done
echo "$HEADER"
echo "$HEADER2"
echo "$HEADER3"
for overd in $(ls report | cut -d ':' -f2 | sort | uniq)
do
	dname=$(echo $overd | sed 's/-2.5.0.Final//' | sed 's/-pre1//')
	LINE=" $dname\t"
	#column
	for overp in $(ls report | cut -d ':' -f1 | sort | uniq)
	do
		#cell
		REPORT="report/${overp}:${overd}:javac:report.csv"
		JAVAC=$(wc -l $REPORT 2> /dev/null | cut -d ' ' -f1)
		REPORT="report/${overp}:${overd}:eclipse:report.csv"
		ECLIPSE=$(wc -l $REPORT 2> /dev/null | cut -d ' ' -f1)
		CELL=$(printf "%4s | %4s" $JAVAC $ECLIPSE)
		LINE="$LINE\t$CELL"
	done
	echo $LINE
done
echo "$HEADER3"

