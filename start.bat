@echo off
REM Script de démarrage du générateur de fichiers JSON

echo ========================================
echo Générateur de Fichiers JSON
echo ========================================
echo.

REM Chercher Maven
for /f "tokens=*" %%i in ('where mvn 2^>nul') do set MAVEN_CMD=%%i

if "%MAVEN_CMD%"=="" (
    echo ERREUR: Maven n'est pas installé ou pas dans le PATH
    echo.
    echo Veuillez installer Maven ou l'ajouter au PATH
    pause
    exit /b 1
)

echo Maven trouvé: %MAVEN_CMD%
echo.

REM Compiler et démarrer
echo Compilation du projet...
call mvn clean package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo Erreur lors de la compilation
    pause
    exit /b 1
)

echo.
echo ========================================
echo Démarrage de l'application...
echo ========================================
echo.
echo Ouvrez votre navigateur sur: http://localhost:8080/
echo.

call mvn spring-boot:run

pause

