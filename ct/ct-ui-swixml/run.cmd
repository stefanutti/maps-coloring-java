rem java -Xms1200m -XX:+UseParallelGC -jar target\ct-ui-swixml-1.9-jar-with-dependencies.jar
rem java -XX:+UseParallelGC -jar target\ct-ui-swixml-1.9-jar-with-dependencies.jar
rem java -Xmx1600m -jar target\ct-ui-swixml-1.9-jar-with-dependencies.jar
rem java -Dcom.sun.management.jmxremote.port=3333 Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false target\ct-ui-swixml-1.9-jar-with-dependencies.jar
# set JAVA_HOME=..\..\jre\6.0
"%JAVA_HOME%\bin\java" -version
"%JAVA_HOME%\bin\java" -Xmx1200m -XX:+UseParallelGC -jar target\ct-ui-swixml-2.3-jar-with-dependencies.jar

