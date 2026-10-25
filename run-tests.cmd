@echo off
cd /d "%~dp0"
javac -d out src/Scheduler.java src/ScheduleLab.java tests/SchedulerTests.java
if errorlevel 1 goto end
java -cp out SchedulerTests
:end
pause
