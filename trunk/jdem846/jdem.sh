#!/bin/bash


CLASSPATH=${CLASSPATH}:lib/dom4j-1.6.1.jar
CLASSPATH=${CLASSPATH}:lib\javassist.jar
CLASSPATH=${CLASSPATH}:lib\jaxen-1.1-beta-6.jar
CLASSPATH=${CLASSPATH}:lib\scannotation-1.0.2.jar
CLASSPATH=${CLASSPATH}:lib\reflections-0.9.5-RC2.one-jar.jar
CLASSPATH=${CLASSPATH}:jdem846.jar
export CLASSPATH

echo $CLASSPATH
java -classpath "$CLASSPATH" us.wthr.jdem846.JDemMain -no-debug