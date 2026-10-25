@echo off
cd /d "%~dp0"
javac -d out src/Scheduler.java src/ScheduleLab.java tests/SchedulerTests.java
if errorlevel 1 goto failed
java -cp out ScheduleLab samples/processes.csv reports 2
if errorlevel 1 goto failed
start "" "reports\report.html"
goto end
:failed
echo Demo failed. Check that a JDK is installed and java and javac are available.
:end
pause
