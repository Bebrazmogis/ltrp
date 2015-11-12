@echo off

IF "%JRE_HOME%" == "" (
  IF "%JAVA_HOME%" == "" (
    echo JRE_HOME or JAVA_HOME is not set.
    pause
    exit
  )

  set "JRE_HOME=%JAVA_HOME%\jre"
)

set "JVM_SERVER_LIB_PATH=%JRE_HOME%\bin\server"
set "JVM_CLIENT_LIB_PATH=%JRE_HOME%\bin\client"

IF EXIST "%JVM_SERVER_LIB_PATH%" (
  set "PATH=%JVM_SERVER_LIB_PATH%;%PATH%"
) ELSE IF EXIST "%JVM_CLIENT_LIB_PATH%" (
  set "PATH=%JVM_CLIENT_LIB_PATH%;%PATH%"
) ELSE (
  echo JVM not found.
  pause
  exit
)

samp-server.exe
