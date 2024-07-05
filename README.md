# Projet Etudiant Openclassrooms n°3 – Créer une application avec Spring Boot

<img src="/preview.jpg" alt="Logo de l'application">

<h1 align="center">SafetyNet - Alert</h1>

SafetyNet Alerts est une application Spring Boot conçue pour fournir des informations essentielles aux services d'urgence. L'application permet de gérer et d'accéder rapidement à des données telles que les personnes vivant à une adresse donnée, les informations de contact en cas d'urgence, et bien plus encore.

## Fonctionnalités

- **Gestion des personnes** : Permet d'ajouter, de modifier, de supprimer et de rechercher des informations sur les résidents.
- **Alertes** : Fournit des alertes pour les situations d'urgence, telles que la localisation des enfants dans une adresse spécifique.
- **Informations sur les urgences** : Donne accès à des informations cruciales telles que les numéros de téléphone des résidents d'une station de pompiers spécifique.

## Technologies Utilisées

- **Java** : Langage de programmation.
- **Spring Boot** : Framework pour simplifier le développement d'applications.
- **Gradle** : Outil de build automatisé.

## Prérequis

- Java JDK 11 ou supérieur.
- Gradle 6.3 ou supérieur (si vous souhaitez construire le projet manuellement).

## Installation

1. Clonez le dépôt GitHub :

```bash
git clone https://github.com/Xenophee/safetynet-alerts.git
```

2. Allez dans le répertoire du projet et construisez-le avec Gradle :

```bash
./gradlew build
```

3. Exécutez l'application :

```bash
./gradlew bootRun
```

## Documentation

La documentation de l'API est générée avec Swagger. Vous pouvez y accéder à la racine de l'application : [http://localhost:8080](http://localhost:8080)


## Tests

Pour exécuter les tests, utilisez la commande suivante :

```bash
./gradlew test
```

Le rapport de tests est généré dans le répertoire `build/reports/tests/test`. Vous pouvez ouvrir le fichier `index.html` dans un navigateur pour consulter les résultats.

Le rapport de couverture de code est généré dans le répertoire `build/reports/jacoco/test`. Vous pouvez ouvrir le fichier `index.html` dans un navigateur pour consulter les résultats.