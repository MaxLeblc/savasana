#!/bin/bash

# Script for backend Spring Boot

cd "$(dirname "$0")/back"

export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export PATH=$JAVA_HOME/bin:$PATH

mvn spring-boot:run
