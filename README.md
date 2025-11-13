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

### Résultat
```json
{
  "equipments": [
    { "name": "DVIFRPNO171", "file": "B804001085", "deployDateTime": "2025-12-12T13:34:00.000Z" },
    { "name": "DVIFRPNO241", "file": "B804001086", "deployDateTime": "2025-13-32T13:35:00.000Z" },
    ... (8 lignes supplémentaires)
    { "name": "DVIFRPNO171", "file": "B804001085", "deployDateTime": "2025-12-12T13:34:00.000Z" }  // Doublon
  ]
}
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

---

## 📚 Documentation Complète

### 🚀 Démarrage Rapide
- **[QUICK_START_API.md](./QUICK_START_API.md)** - Test en 2 minutes
- **[INDEX_DOCUMENTATION.md](./INDEX_DOCUMENTATION.md)** - Index de toute la documentation

### 📤 Envoi API
- **[ENVOI_API.md](./ENVOI_API.md)** - Documentation complète envoi API
- **[ENVOI_API_RESUME.md](./ENVOI_API_RESUME.md)** - Résumé visuel

### 🔧 Technique
- **[RECAPITULATIF_MODIFICATIONS.md](./RECAPITULATIF_MODIFICATIONS.md)** - Toutes les modifications
- **[GUIDE_MIGRATION.md](./GUIDE_MIGRATION.md)** - Guide de migration API
- **[CORRECTIONS_COHERENCE.md](./CORRECTIONS_COHERENCE.md)** - Détails des corrections

### 🧪 Tests
- **[TESTS_VALIDATION.md](./TESTS_VALIDATION.md)** - Suite de tests complète
- **[GUIDE_COHERENCE.md](./GUIDE_COHERENCE.md)** - Guide des validations

---

## 🐛 Dépannage

### Application

| Problème | Solution |
|----------|----------|
| "Maven command not found" | Installer Maven ou utiliser le JAR |
| Port 8080 déjà utilisé | Changer dans `application.yml`: `server.port: 9090` |
| Erreur compilation | `mvn clean install` |

### Envoi API

| Problème | Solution |
|----------|----------|
| Section envoi pas visible | Sélectionner ACQ_MTBORNE ou ACQ_PE |
| "URL invalide" | Vérifier le format: https://example.com |
| "Failed to fetch" | Vérifier que l'API est accessible |
| "401 Unauthorized" | Vérifier le Bearer Token |

**Plus de détails:** [ENVOI_API.md § Dépannage](./ENVOI_API.md#dépannage)

---

## 📝 Notes Techniques

### Stack Technique
- **Backend**: Spring Boot 3.3.0
- **Language**: Java 21
- **Template Engine**: Thymeleaf
- **Frontend**: Bootstrap 5 + Vanilla JavaScript
- **JSON Processing**: Jackson
- **Build**: Maven 3.8+

### Validations Implémentées
- ✅ Total >= 1
- ✅ Erreurs <= Total
- ✅ Doublons >= 0
- ✅ Types d'erreurs <= Lignes en erreur
- ✅ Checkboxes limitées dynamiquement
- ✅ Distribution circulaire des erreurs

### Sécurité
- 🔒 Bearer Token supporté
- 🔒 Token masqué par défaut
- 🔒 Validation côté client + serveur
- 🔒 HTTPS recommandé pour production

---

## 🎯 Changelog

### v1.1 (2025-11-13)
- ✨ **NOUVEAU:** Envoi API automatique avec Bearer Token
- ✨ Support ACQ MTBORNE et ACQ PE pour envoi API
- 🐛 Correction du bug de dégrissage des checkboxes
- ✅ Validations cohérentes renforcées
- 📚 Documentation complète ajoutée

### v1.0 (Initial)
- ✅ Génération de fichiers JSON
- ✅ Gestion des erreurs et doublons
- ✅ Interface web Bootstrap

---

## 📞 Support

### Pour les Utilisateurs
- 📖 Consultez [QUICK_START_API.md](./QUICK_START_API.md)
- 📋 Voir la [FAQ dans ENVOI_API.md](./ENVOI_API.md#faq)

### Pour les Développeurs
- 🔧 Consultez [GUIDE_MIGRATION.md](./GUIDE_MIGRATION.md)
- 📊 Tests dans [TESTS_VALIDATION.md](./TESTS_VALIDATION.md)

---

## 🤝 Contribution

Les contributions sont les bienvenues! Veuillez:
1. Fork le projet
2. Créer une branche (`git checkout -b feature/amazing-feature`)
3. Commit les changements (`git commit -m 'Add amazing feature'`)
4. Push vers la branche (`git push origin feature/amazing-feature`)
5. Ouvrir une Pull Request

---

## 📄 License

Ce projet est sous license MIT - voir le fichier LICENSE pour plus de détails.

---

<div align="center">

[Documentation](./INDEX_DOCUMENTATION.md) • [Quick Start](./QUICK_START_API.md) • [Tests](./TESTS_VALIDATION.md)

</div>

