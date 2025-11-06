@echo off
REM Quick build helper script for PXL Game Engine
REM Provides shortcuts for common Gradle tasks

if "%1"=="" goto help
if "%1"=="help" goto help
if "%1"=="setup" goto setup
if "%1"=="build" goto build
if "%1"=="clean" goto clean
if "%1"=="rebuild" goto rebuild
if "%1"=="run" goto run
if "%1"=="editor" goto editor
if "%1"=="deps" goto deps
if "%1"=="tasks" goto tasks
goto unknown

:help
echo ========================================
echo PXL Game Engine - Build Helper
echo ========================================
echo.
echo Usage: build.bat [command]
echo.
echo Commands:
echo   setup      - Check Java and setup requirements
echo   build      - Build all modules
echo   clean      - Clean all build artifacts
echo   rebuild    - Clean and build everything
echo   run        - Run the Editor application
echo   editor     - Same as run
echo   deps       - Show project dependencies
echo   tasks      - List all available Gradle tasks
echo   help       - Show this help message
echo.
echo Examples:
echo   build.bat build
echo   build.bat run
echo   build.bat rebuild
echo.
echo For more details, see BUILD.md
echo.
goto end

:setup
echo Running setup check...
call setup.bat
goto end

:build
echo Building PXL...
call gradlew.bat build -x test
goto end

:clean
echo Cleaning build artifacts...
call gradlew.bat clean
goto end

:rebuild
echo Rebuilding PXL...
call gradlew.bat clean build -x test
goto end

:run
echo Running PXL Editor...
call gradlew.bat :Editor:run
goto end

:editor
echo Running PXL Editor...
call gradlew.bat :Editor:run
goto end

:deps
echo Showing dependencies...
call gradlew.bat dependencies
goto end

:tasks
echo Listing all tasks...
call gradlew.bat tasks --all
goto end

:unknown
echo Unknown command: %1
echo Run 'build.bat help' for usage information
goto end

:end
