#!/bin/sh

APPDIR=`dirname $0`;
GWT_DIR="../../lib/gwt/"
GWT_EXT="./gwt_module"

APP_CP="$GWT_EXT/src:$APPDIR/src:$APPDIR/bin:$GWT_DIR/gwt-user.jar:$GWT_DIR/gwt-dev-mac.jar"
java -XstartOnFirstThread -Xmx256M -cp $APP_CP com.google.gwt.dev.GWTCompiler -logLevel INFO -style DETAILED -out "$APPDIR/www" "$@" com.asemantics.LightParser;
