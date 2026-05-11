@echo off
setlocal EnableExtensions EnableDelayedExpansion

rem Move to project root (../ from bin)
set "SCRIPT_DIR=%~dp0"
pushd "%SCRIPT_DIR%.." >nul

if exist "zhiqingyun.pid" (
  set /p PID=<"zhiqingyun.pid"
  if defined PID (
    taskkill /PID !PID! /F >nul 2>&1
    if not errorlevel 1 (
      del /f /q "zhiqingyun.pid" >nul 2>&1
      echo [zhiqingyun]: CLOSED
      popd >nul
      exit /b 0
    )
  )
)

echo [zhiqingyun]: HAS CLOSED
popd >nul
exit /b 0
