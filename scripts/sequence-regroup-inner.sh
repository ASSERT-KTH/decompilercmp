#!/bin/zsh

regroupInnerClasses() {
	#get direct children
	local main=$1
	#echo "main: ${main}"
	local mainEscape="s/"$(echo "./${main}\$" | sed 's/\//\\\//g' | sed 's/\$/\\$/g')"//"
	#echo "mainEscape: ${mainEscape}"
	local prefix="./"$(echo $main | sed 's/\$/\\$/g')"\\\$*"
	#echo "prefix: $prefix"
	#find . -wholename "$prefix"
	for child in $(find . -wholename $(echo "$prefix") | sed $mainEscape | grep -v "\\$")
	do
		echo "$child"
		local tmp=$main
		local param=$(echo "${tmp}\$$child")
		#echo "param: $param"
		regroupInnerClasses $param
		echo "inner_start" >> $tmp
		cat ${param} >> $tmp
		echo "inner_end" >> $tmp
	done
	#echo "done"
}

ROOT=$(pwd)
OUTPUTDIR="sequences"
COMPILERS=('javac')
REPOS=('commons-lang' 'Bukkit' 'commons-codec' 'commons-collections' 'commons-imaging' 'dctest' 'DiskLruCache' 'javapoet' 'joda-time' 'jsoup' 'junit4' 'mimecraft' 'scribejava' 'spark')

#repo="dctest"
#for repo in $(ls repos)
for repo in $REPOS
do
	echo "REPO: $repo"
	for c in $COMPILERS
	do
		cd $ROOT
		cd $OUTPUTDIR/$repo/$c
		echo "COMPILER: $c"
		#for mainClass in $(find . -type f | grep "\\$" | cut -d '$' -f1 | sort | uniq | sed 's/.\/sequences\/'"$repo"'\/'"$c"'//')
		for mainClass in $(find . -type f | grep "\\$" | cut -d '$' -f1 | sort | uniq | sed 's/.\///')
		do
			#echo "mainClass: "$(echo $mainClass | sed 's/\$/\\$/g')
			regroupInnerClasses $mainClass
		done
	done
	COMPILERS=('javac' 'eclipse')
	
done

cd $ROOT
cd $OUTPUTDIR
rm **/*\$*
