@echo off
set CP=target/classes
for /f %%i in ('dir /s /b "%USERPROFILE%\.m2\repository\org\postgresql\postgresql\42.7.1\*.jar" 2^>nul') do set CP=%CP%;%%i
java -cp "%CP%" org.example.Main
pause