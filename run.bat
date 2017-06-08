%echo off

cd .

REM Compile all source files
echo Compiling source files...
javac -d ./bin -sourcepath ./src ./src/ConnectionWindow.java
javac -d ./bin -sourcepath ./src ./src/IMClient.java
javac -d ./bin -sourcepath ./src ./src/IMConnectable.java
javac -d ./bin -sourcepath ./src ./src/IMServer.java
javac -d ./bin -sourcepath ./src ./src/IMWindow.java
javac -d ./bin -sourcepath ./src ./src/SimpleIM.java
echo Done.

REM Run program.
echo Running application...
java -classpath ./bin SimpleIM
echo Done.

pause