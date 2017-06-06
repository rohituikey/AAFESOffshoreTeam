set path=C:\Program Files\Java\jdk1.8.0_131\bin
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_131
set classpath=.\standalone\deployments\StarGate-1.0-SNAPSHOT.war\WEB-INF\classes\com\aafes\stargate\gateway\svs

java -cp $classpath\ Scheduler

if [grep -ic "exception" -ne 0]

mail -s "your subject" your@email.com