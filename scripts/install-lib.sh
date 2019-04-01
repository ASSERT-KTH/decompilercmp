#!/bin/bash

cd ../lib

echo "dl jadx-0.9.0 ..."
wget https://github.com/skylot/jadx/releases/download/v0.9.0/jadx-0.9.0.zip

mkdir jadx-0.9.0

unzip jadx-0.9.0.zip -d jadx-0.9.0

echo "dl Krakatau ..."
wget https://github.com/Storyyeller/Krakatau/archive/master.zip
mkdir Krakatau
unzip master.zip -d Krakatau

echo "dl jode-1.1.2-pre1 ..."
wget https://downloads.sourceforge.net/project/jode/jode/1.1.2-pre1/jode-1.1.2-pre1.jar?r=https%3A%2F%2Fsourceforge.net%2Fprojects%2Fjode%2Ffiles%2Flatest%2Fdownload&ts=1554126208


echo "dl jd-gui-1.4.1 ..."
wget https://github.com/java-decompiler/jd-gui/releases/download/v1.4.1/jd-gui-1.4.1.jar

echo "dl soot ..."
wget https://soot-build.cs.uni-paderborn.de/nexus/repository/soot-release/ca/mcgill/sable/soot/3.3.0/soot-3.3.0-jar-with-dependencies.jar