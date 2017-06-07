#!/bin/bash
echo "NOTICE FOR SOME INSTALLATIONS ADD SUDO IN FRONT OF THE CP AND DOCKER COMMANDS"
echo "====================Removing target before makingassembly================================"
rm -fr ../target && rm -fr docker/*
echo "====================Entering to Hello-Akka directory and creating assembly================"
cd ../ && sbt assembly
echo "====================Coming back to dockerApp directory===================================="
cd -
echo "====================Copying application jar to docker folder=============================="
cp ../target/scala-2.11/Ndb-assembly-0.0.1-SNAPSHOT.jar docker/
echo "====================Building docker image================================================="
docker build -t akkaapp .
echo "====================Running docker container=============================================="
docker run akkaapp