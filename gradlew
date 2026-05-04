#!/bin/sh
APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")
APP_HOME=$(cd "$(dirname "$0")" && pwd -P)
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

set_java_home() {
    if [ -n "$JAVA_HOME" ]; then
        if [ -x "$JAVA_HOME/jre/sh/java" ]; then
            JAVACMD="$JAVA_HOME/jre/sh/java"
        else
            JAVACMD="$JAVA_HOME/bin/java"
        fi
        if [ ! -x "$JAVACMD" ]; then
            die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME"
        fi
    else
        JAVACMD=java
        which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found."
    fi
}

die() {
    echo
    echo "$*"
    echo
    exit 1
}

set_java_home
exec "$JAVACMD" $DEFAULT_JVM_OPTS -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
