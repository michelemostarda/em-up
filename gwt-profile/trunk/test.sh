#!/bin/bash
APPDIR=`dirname $0`;

# Light Parser.
LP_DIR="../../lightparser/trunk"
LP_CP="$LP_DIR/src/:$LP_DIR/gwt_module/src"

# MU Core.
MU_DIR="../../core/trunk"
MU_CP="$MU_DIR/src"

# External libraries.
LIB_DIR="../../lib"

# GWT.
GWT_DIR="$LIB_DIR/gwt"
GWT_CP="$GWT_DIR/gwt-user.jar:$GWT_DIR/gwt-dev-mac.jar:$GWT_DIR/gwt-servlet.jar:$GWT_DIR/gwt-benchmark-viewer.jar"

# GWT-Ext.
GWT_EXT_CP="$LIB_DIR/gwtext-2.0.5/gwtext.jar"

# Apache Commons HTTP.
CN_HTTP_CP="$LIB_DIR/commons-codec-1.3.jar:$LIB_DIR/commons-logging-1.1.1.jar:$LIB_DIR/commons-httpclient-3.1.jar:$LIB_DIR/commons-logging-adapters-1.1.1.jar:$LIB_DIR/commons-logging-api-1.1.1.jar"

# JUnit.
TEST_CP="$LIB_DIR/junit.jar"

# Out dirs
OUT_DIR="../MashUp/out/"
OTHER_MODULES_CP="$OUT_DIR/production/LightParser:$OUT_DIR/production/MashUp:$OUT_DIR/test/LightParser:$OUT_DIR/test/MashUp"

# Application complete class path.
APP_CP="$LP_CP:$MU_CP:$APPDIR/src:$APPDIR/bin:$GWT_CP:$GWT_EXT_CP:$CN_HTTP_CP:$TEST_CP:$OTHER_MODULES_CP:classes"

rm -fr classes
mkdir classes

echo -n "Compiling..."
find src  -name "*.java" | xargs javac -cp $APP_CP -d classes
find test -name "*.java" | xargs javac -cp $APP_CP -d classes
echo "Done."

cp src/com/asemantics/integration/MUpConsole.gwt.xml classes/com/asemantics/integration/MUpConsole.gwt.xml

java -XstartOnFirstThread -Xmx512M  -Dgwt.args="-out www-test" -cp $APP_CP junit.textui.TestRunner com.asemantics.integration.client.gui.GWTUIGlobalTest "$@";