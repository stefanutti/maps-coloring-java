#!/bin/bash
java --add-exports java.desktop/com.apple.eawt=ALL-UNNAMED \
     --patch-module java.desktop=../ct-maven/file_repository/it/tac/com/apple/ui/1.2/ui-1.2.jar \
     -Xmx1200m -XX:+UseParallelGC \
     -jar target/ct-ui-swixml-2.3-jar-with-dependencies.jar
