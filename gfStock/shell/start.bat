@echo off
 setLocal EnableDelayedExpansion
 set CLASSPATH="
 for /R ./libs %%a in (*.jar) do (
   set CLASSPATH=!CLASSPATH!;%%a
 )
 set CLASSPATH=!CLASSPATH!"
 echo %CLASSPATH%
 java -cp classes;%CLASSPATH% job.gf.stock.Runner