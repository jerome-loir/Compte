@echo off
REM Lance l'API et l'application web en parallèle.
REM Usage : lancer.bat [h2|postgres]
REM Si aucun profil n'est précisé, "h2" est utilisé par défaut.

set PROFIL=%1
if "%PROFIL%"=="" set PROFIL=h2

echo Lancement de l'API avec le profil '%PROFIL%'...
start "API" cmd /k "cd api && mvn spring-boot:run -Dspring-boot.run.profiles=%PROFIL% -Dspring-boot.run.fork=false"

echo Lancement de l'application web...
start "Webapp" cmd /k "cd webapp && mvn spring-boot:run -Dspring-boot.run.fork=false"

echo Les deux applications démarrent dans des fenêtres séparées.
