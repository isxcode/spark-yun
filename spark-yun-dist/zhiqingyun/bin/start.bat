@echo off
setlocal EnableExtensions EnableDelayedExpansion

rem Move to project root (../ from bin)
set "SCRIPT_DIR=%~dp0"
pushd "%SCRIPT_DIR%.." >nul

set "PRINT_LOG=true"

rem Parse args: only supports --print-log=true|false
:parse_args
if "%~1"=="" goto args_done
set "ARG=%~1"
if /I "!ARG:~0,12!"=="--print-log=" (
  set "PRINT_LOG=!ARG:~12!"
) else (
  echo Unknown parameter: %~1
  popd >nul
  exit /b 1
)
shift
goto parse_args

:args_done
rem Load optional environment variables for Windows
if exist "conf\zhiqingyun-env.bat" (
  call "conf\zhiqingyun-env.bat"
)

rem If PID exists and process is alive, exit
if exist "zhiqingyun.pid" (
  set /p PID=<"zhiqingyun.pid"
  if defined PID (
    tasklist /FI "PID eq !PID!" | findstr /R /C:" !PID! " >nul 2>&1
    if not errorlevel 1 (
      echo [zhiqingyun]: HAS RUNNING
      popd >nul
      exit /b 0
    )
  )
)

rem Ensure logs directory and file exist
if not exist "logs" mkdir "logs"
if not exist "logs\spark-yun.log" type nul > "logs\spark-yun.log"

rem Decide Java executable
set "JAVA_CMD=java"
if defined JAVA_HOME (
  if exist "%JAVA_HOME%\bin\java.exe" set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
)

rem Start app in background and capture PID
powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $p = Start-Process -FilePath '%JAVA_CMD%' -ArgumentList '-jar','-Xmx2048m','lib/zhiqingyun.jar','--spring.profiles.active=local','--spring.config.additional-location=conf/' -WorkingDirectory (Get-Location).Path -WindowStyle Hidden -PassThru; Set-Content -Path 'zhiqingyun.pid' -Value $p.Id -NoNewline"
if errorlevel 1 (
  echo [zhiqingyun]: START FAILED
  popd >nul
  exit /b 1
)

echo [zhiqingyun]: STARTING
if /I "%PRINT_LOG%"=="true" (
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Get-Content -Path 'logs/spark-yun.log' -Wait"
)

popd >nul
exit /b 0
