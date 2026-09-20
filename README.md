# Application de simulation de tenue de compte – Java Spring Boot

Projet personnel développé en Java avec Spring Boot, visant à mettre en œuvre une architecture backend propre, maintenable et facilement déployable.

Ce projet m’a permis de consolider mes compétences en développement Java, en conception d’API REST, en gestion de base de données et en bonnes pratiques de structuration applicative.

---

## Stack technique

- Java 17+
- Spring Boot
- Maven
- JPA / Hibernate
- PostgreSQL
- Base embarquée H2 (mode développement)
- JUnit
- Git / GitHub

---

## Architecture

- API REST
- Architecture en couches :
  - Controller
  - Service
  - Repository
  - Model

---

## Prérequis

- Java 17 (LTS)
- Maven 3.8+
- PostgreSQL 14+ (uniquement pour le profil `postgres`)

---

## Lancement de l'application web / Profils disponibles pour l'API

- h2 (par défaut) - démarrage immédiat sans configuration préalable.

	-> sous Windows,  lancer.bat  avec cmd (.\lancer.bat avec PowerShell)
	-> sous Linux, sh lancer.sh
	
- postgres

	-> sous Windows, lancer.bat postgres  avec cmd (.\lancer.bat avec PowerShell)
	-> sous Linux, sh lancer.sh postgres


Résultat dans un navigateur web : http://localhost:9001/
