#!/bin/bash

# Shell script which populates the docker image with built sources

# Create destination
rm -rf jars
mkdir -p jars/plugins
mkdir -p jars/bungee

# Multi-module
for dir in */**/target
do
    cp ${dir}/*.jar jars/plugins
done

# Single-module
for dir in */target
do
    cp ${dir}/*.jar jars/plugins
done

# Only keep shaded
rm jars/plugins/original-*
rm jars/plugins/utilities-*

# Components
mkdir jars/plugins/components
mv jars/plugins/games-* jars/plugins/components
mv jars/plugins/components/games-core-* jars/plugins

mv jars/plugins/*-bungee*.jar jars/bungee
mv jars/plugins jars/bukkit

cp /data/.m2/repository/network/walrus/sportpaper/1.8.8-R0.1-SNAPSHOT/sportpaper-1.8.8-R0.1-SNAPSHOT.jar jars/bukkit.jar
cp /data/.m2/repository/io/github/waterfallmc/glymur-bootstrap/1.14-SNAPSHOT/glymur-bootstrap-1.14-SNAPSHOT.jar jars/bungee.jar
