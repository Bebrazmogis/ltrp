#!/bin/bash
#export JAVA_HOME=Path to JDK8 (32-bit)
export LD_LIBRARY_PATH=.:$JAVA_HOME/jre/lib/i386/client:$JAVA_HOME/jre/lib/i386/server:/usr/local/lib
$JAVA_HOME/jre/bin/java -jar shoebill-updater.jar