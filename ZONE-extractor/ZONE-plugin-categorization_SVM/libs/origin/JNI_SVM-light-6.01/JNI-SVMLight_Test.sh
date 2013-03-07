#!/bin/sh
java -Xmx128m -classpath ./lib/jnisvmlight.jar:. -Djava.library.path=./lib JNI_SVMLight_Test
