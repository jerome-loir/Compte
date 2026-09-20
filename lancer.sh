#!/bin/bash
# Lance l'API et l'application web en parallèle.
# Usage : ./lancer.sh [h2|postgres]
# Si aucun profil n'est précisé, "h2" est utilisé par défaut.

PROFIL=${1:-h2}

echo "Lancement de l'API avec le profil '$PROFIL'..."
cd api
mvn spring-boot:run -Dspring-boot.run.profiles=$PROFIL -Dspring-boot.run.fork=false &
API_PID=$!
cd ..

echo "Lancement de l'application web..."
cd webapp
mvn spring-boot:run -Dspring-boot.run.fork=false &
WEBAPP_PID=$!
cd ..

trap "echo 'Arrêt des applications...'; kill $API_PID $WEBAPP_PID 2>/dev/null" INT TERM

echo "API démarrée (PID $API_PID), webapp démarrée (PID $WEBAPP_PID)."
wait
