#!/bin/bash

mkdir lib
cd lib

echo "dl jadx-0.9.0 ..."
wget https://github.com/skylot/jadx/releases/download/v0.9.0/jadx-0.9.0.zip
mkdir jadx-0.9.0
unzip jadx-0.9.0.zip -d jadx-0.9.0
rm jadx-0.9.0.zip

echo "dl Krakatau ..."
wget https://github.com/Storyyeller/Krakatau/archive/258ae9a931e0c403a79a737e7f41d0fb0cbac051.zip
unzip 258ae9a931e0c403a79a737e7f41d0fb0cbac051.zip -d .
mv Krakatau-258ae9a931e0c403a79a737e7f41d0fb0cbac051 Krakatau
rm 258ae9a931e0c403a79a737e7f41d0fb0cbac051.zip

echo "dl jode-1.1.2-pre1 ..."
wget https://downloads.sourceforge.net/project/jode/jode/1.1.2-pre1/jode-1.1.2-pre1.jar?r=https%3A%2F%2Fsourceforge.net%2Fprojects%2Fjode%2Ffiles%2Flatest%2Fdownload&ts=1554126208


echo "dl jd-gui-1.4.1 ..."
wget https://github.com/java-decompiler/jd-gui/releases/download/v1.4.1/jd-gui-1.4.1.jar

echo "dl jd-core-1.0.0"
wget https://github.com/java-decompiler/mvn-repo/raw/1bfc0b146ee07bcf316c2c9af5a1c917cb73012e/org/jd/jd-core/1.0.0/jd-core-1.0.0.jar

echo "dl soot ..."
wget https://soot-build.cs.uni-paderborn.de/nexus/repository/soot-release/ca/mcgill/sable/soot/3.3.0/soot-3.3.0-jar-with-dependencies.jar

echo "dl jardiff ..."
wget https://github.com/scala/jardiff/releases/download/v1.2.0/jardiff.jar

cd ..
