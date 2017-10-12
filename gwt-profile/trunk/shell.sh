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
GWT_CP="$GWT_DIR/gwt-user.jar:$GWT_DIR/gwt-dev-mac.jar"

# GWT-Ext.
GWT_EXT_CP="$LIB_DIR/gwtext-2.0.5/gwtext.jar"

# Apache Commons HTTP.
CN_HTTP_CP="$LIB_DIR/commons-codec-1.3.jar:$LIB_DIR/commons-logging-1.1.1.jar:$LIB_DIR/commons-httpclient-3.1.jar:$LIB_DIR/commons-logging-adapters-1.1.1.jar:$LIB_DIR/commons-logging-api-1.1.1.jar"

# Application complete class path.
APP_CP="$LP_CP:$MU_CP:$APPDIR/src:$APPDIR/bin:$GWT_CP:$CN_HTTP_CP:$GWT_EXT_CP"

# Compile server classes.
echo -n "Compiling server classes... "
javac -cp $APP_CP -d war/WEB-INF/classes src/com/asemantics/integration/server/proxy/HttpProxyServiceImpl.java
echo "Done"

#  -style should be followed by one of
#  OBF, PRETTY, or DETAILED
#java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -XstartOnFirstThread -Xmx512M -cp $APP_CP:classes com.google.gwt.dev.GWTShell -ea -style OBF -out "$APPDIR/www" "$@" com.asemantics.integration.MUpConsole/MUpConsole.html
java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -XstartOnFirstThread -Xmx512M -cp $APP_CP com.google.gwt.dev.HostedMode -style PRETTY -startupUrl MUpConsole.html com.asemantics.integration.MUpConsole




