#!/bin/bash

set -e

PROJECT_PATH=$HOME/projects/Kingdoms
JAR=kingdoms-1.0.jar
SERVER_PATH=$(pwd)
DATAPACK=kingdoms-datapack

cd $PROJECT_PATH
mvn clean install

zip -r $DATAPACK.zip $DATAPACK
cp $DATAPACK.zip $SERVER_PATH/plugins/$DATAPACK.zip
rm $DATAPACK.zip

cp -r $DATAPACK $SERVER_PATH/plugins/$DATAPACK

echo $PROJECT_PATH/target/$JAR -> $SERVER_PATH/plugins/$JAR
cp target/$JAR $SERVER_PATH/plugins/$JAR
cp update_kingdoms.sh $SERVER_PATH/update_kingdoms.sh

true