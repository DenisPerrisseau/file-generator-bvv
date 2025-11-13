# 🎯 Générateur de Fichiers JSON

Un **système simple et efficace** pour générer des fichiers JSON d'équipements avec erreurs et doublons.

## 🚀 Démarrage Rapide

### Prérequis
- Java 21+
- Maven 3.8+

### Lancer l'Application

**Option 1: Avec Maven (Windows/Linux/Mac)**
```bash
mvn spring-boot:run
```

**Option 2: Avec le script (Windows)**
```bash
./start.bat
```

**Option 3: Jar pré-compilé**
```bash
java -jar target/file-generation-1.0.0.jar
```

### Accéder à l'Interface
```
http://localhost:8080/
```

## 📋 Fonctionnalités

### 1. Génération Configurables
- **Nombre de lignes** : De 1 à N
- **Lignes avec erreurs** : De 0 à N
- **Doublons** : De 0 à N

### 2. Types d'Erreurs (9 types)
- ☑ Valeur NULL
- ☑ Chaîne Vide
- ☑ Date Invalide
- ☑ Hors Limites
- ☑ Type Incorrect
- ☑ Trop Long
- ☑ Trop Court
- ☑ Format Invalide
- ☑ Champ Manquant

### 3. Actions
- 👁️ **Prévisualiser** - Voir le résultat avant téléchargement
- ⬇️ **Télécharger** - Télécharger le fichier JSON

## 📊 Exemple d'Utilisation

### Configuration
```
Total lignes: 10
Lignes avec erreurs: 2
Doublons: 1
Types d'erreurs: [Date Invalide, Champ Vide]
```


## 🏗️ Architecture

### Backend
- **Contrôleur**: `GeneratorController.java`
  - `GET /` - Interface
  - `POST /api/generate` - Générer (prévisualisation)
  - `POST /api/download` - Télécharger

- **Service**: `JsonFileGeneratorService.java`
  - Génération des équipements valides
  - Injection des erreurs
  - Création des doublons

### Frontend
- **Interface**: `index.html` (Bootstrap 5)
  - Formulaire de configuration
  - Cases à cocher pour les erreurs
  - Prévisualisation du JSON
  - Boutons de téléchargement

## 📁 Structure du Projet

```
GenerateFileBVV/
├── src/main/
│   ├── java/com/bvv/filegeneration/
│   │   ├── controller/
│   │   │   └── GeneratorController.java
│   │   └── service/
│   │       └── JsonFileGeneratorService.java
│   └── resources/
│       ├── templates/
│       │   └── index.html
│       └── application.yml
├── pom.xml
├── start.bat (Windows)
└── NOUVEAU_SYSTEME.md
```

## 🛠️ Configuration

Fichier: `src/main/resources/application.yml`

```yaml
spring:
  application:
    name: file-generator
  thymeleaf:
    cache: false

server:
  port: 8080

logging:
  level:
    com.bvv.filegeneration: DEBUG
```

## 🧪 Tests Manuels

### Test 1: Fichier Valide
1. Total: 10
2. Erreurs: 0
3. Doublons: 0
4. Aucune erreur cochée
5. Cliquer "Prévisualiser"

**Résultat attendu**: 10 lignes valides

### Test 2: Avec Erreurs
1. Total: 20
2. Erreurs: 5
3. Doublons: 2
4. Cocher "Date Invalide" et "Champ Vide"
5. Cliquer "Télécharger"

**Résultat attendu**: 15 lignes valides + 5 avec erreurs + 2 doublons = 22 lignes

### Test 3: Tous les Types d'Erreurs
1. Total: 30
2. Erreurs: 10
3. Doublons: 5
4. Cocher tous les types d'erreurs
5. Cliquer "Prévisualiser"

**Résultat attendu**: Mix de différents types d'erreurs

## 🐛 Dépannage

### "Maven command not found"
Installer Maven ou l'ajouter au PATH système.

### Port 8080 déjà utilisé
Changer le port dans `application.yml`:
```yaml
server:
  port: 9090
```

### Erreur lors de la compilation
```bash
# Nettoyer et recompiler
mvn clean install
```

## 📝 Notes Techniques

- **Framework**: Spring Boot 3.3.0
- **Language**: Java 21
- **Template Engine**: Thymeleaf
- **Frontend**: Bootstrap 5 + Vanilla JavaScript
- **JSON Processing**: Jackson

## 📞 Support

Pour toute question ou problème, consultez le fichier `NOUVEAU_SYSTEME.md`.

---

**Générateur de Fichiers JSON v1.0.0** ✨

