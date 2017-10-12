#!/bin/bash
# Runs the MUp JRE profile command line. 

LP_VER=0.9
MUP_VER=0.7

DIST_DIR=../dist
LIB_PARENT=../../..
LIB_DIR=$LIB_PARENT/lib
LP_DIR=../../../lightparser/trunk/

[ "$JAVA_HOME"   = "" ] && { echo "JAVA_HOME unset" 1>&2 ; exit 1 ; }

if [ ! -e $LP_DIR/dist/lightparser-$LP_VER.jar ]
then
    echo "Light Parser not yet distributed: try doing * ant dist * first in $LP_DIR" 1>&2
    exit 1
fi

if [ ! -e $DIST_DIR/mup-jre-profile-$MUP_VER.jar ]
then
    echo "JRE profile not yet distributed: try doing * ant dist * first" 1>&2
    exit 1
fi

CP=`./make_classpath $LIB_PARENT`:$LP_DIR/dist/lightparser-$LP_VER.jar:$DIST_DIR/mup-jre-profile-$MUP_VER.jar
#echo classpath $CP

JAVA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

exec java $JAVA_OPTS -Xmx256m -cp "$CP" com.asemantics.mashup.console.MUCommandLine "$@" 2>&1
