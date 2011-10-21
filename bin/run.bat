@echo off

setlocal
setlocal enabledelayedexpansion

set BASE=%~dp0
if %BASE:~-4%==bin\ set BASE=%BASE:~0,-4%

set CLASSPATH=%BASE%target\classes
for %%i in (%BASE%target\dependency\*.jar) do (
	set CLASSPATH=!CLASSPATH!;%%i
)

java -classpath !CLASSPATH!; withgod.lingr.Mention2Lingr %*

endlocal
