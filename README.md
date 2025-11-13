# File Generator BVV

## 📋 Description

Application Spring Boot pour générer des fichiers à partir de gabarits avec support pour JSON, XML et TXT. Permet d'importer des fichiers exemple, générer automatiquement des gabarits, créer des fichiers de données valides ou avec erreurs, et envoyer les fichiers via HTTP POST sécurisé avec token HMAC-SHA256.

## 🛠️ Technologies

- **Java 21** - Langage de programmation
- **Spring Boot 3.3.0** - Framework web
- **Spring Data JPA** - ORM et accès aux données
- **PostgreSQL** - Base de données
- **Liquibase** - Gestion des migrations de schéma
- **Lombok** - Réduction de code boilerplate
- **Thymeleaf** - Moteur de templates web
- **Jackson** - Sérialisation JSON et XML
- **Maven** - Gestionnaire de dépendances

## 📁 Structure du projet

```
src/main/java/com/bvv/filegeneration/
├── controller/              # Contrôleurs REST
│   ├── TemplateController.java
│   ├── GenerationJobController.java
│   └── GenerationLogController.java
├── service/                 # Logique métier
│   ├── TemplateService.java
│   ├── GenerationJobService.java
│   └── GenerationLogService.java
├── repository/              # Accès aux données (JPA)
│   ├── TemplateRepository.java
│   ├── GenerationJobRepository.java
│   ├── GenerationLogRepository.java
│   └── TemplateFieldRepository.java
├── entity/                  # Entités JPA
│   ├── Template.java
│   ├── TemplateField.java
│   ├── GenerationJob.java
│   └── GenerationLog.java
├── dto/                     # Data Transfer Objects
│   ├── TemplateDTO.java
│   ├── FieldDTO.java
│   ├── GenerationJobDTO.java
│   ├── GenerationLogDTO.java
│   └── ...
├── mapper/                  # Mappers DTO ↔ Entity
│   ├── TemplateMapper.java
│   ├── FieldMapper.java
│   ├── GenerationJobMapper.java
│   └── GenerationLogMapper.java
├── utils/                   # Utilitaires
│   ├── TemplateNamingRules.java
│   ├── HmacTokenGenerator.java
│   └── FileParser.java
├── common/
│   ├── enums/              # Énumérations
│   ├── constants/          # Constantes globales
│   └── exceptions/         # Exceptions personnalisées
└── FileGeneratorApplication.java
```

## 🚀 Démarrage rapide

### Prérequis

- Java 21+
- Maven 3.8+
- PostgreSQL 12+

### Installation

1. **Cloner le projet**
```bash
git clone <repo-url>
cd GenerateFileBVV
```

2. **Configurer PostgreSQL**
```sql
CREATE DATABASE filegeneration_db;
```

3. **Modifier application.yml**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/filegeneration_db
    username: votre_utilisateur
    password: votre_mot_de_passe
```

4. **Compiler et lancer**
```bash
mvn clean install
mvn spring-boot:run
```

L'application démarre sur `http://localhost:8080`

## 🔌 Endpoints REST

### Templates
| Méthode | URL | Description |
|---------|-----|-------------|
| POST | `/api/templates/import` | Importer un fichier et générer un gabarit |
| GET | `/api/templates` | Lister tous les gabarits |
| GET | `/api/templates/{id}` | Détail d'un gabarit |
| PUT | `/api/templates/{id}` | Mettre à jour un gabarit |
| DELETE | `/api/templates/{id}` | Supprimer un gabarit |

### Tâches de génération
| Méthode | URL | Description |
|---------|-----|-------------|
| POST | `/api/jobs/generate` | Créer une tâche de génération |
| GET | `/api/jobs` | Lister toutes les tâches |
| GET | `/api/jobs/{id}` | Statut d'une tâche |
| GET | `/api/jobs/{id}/preview` | Aperçu des premières lignes |
| POST | `/api/jobs/{id}/send` | Envoyer via HTTP POST avec HMAC-SHA256 |

### Historique
| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/api/logs` | Tous les logs |
| GET | `/api/logs/job/{jobId}` | Logs d'une tâche |

## 💡 Exemples d'utilisation

### 1. Générer un fichier JSON

**Request:**
```bash
curl -X POST http://localhost:8080/api/jobs/generate \
  -H "Content-Type: application/json" \
  -d '{
    "templateId": 1,
    "totalLines": 100,
    "errorLines": 5,
    "outputFormat": "JSON"
  }'
```

**Response:**
```json
{
  "id": 1,
  "templateId": 1,
  "totalLines": 100,
  "errorLines": 5,
  "outputFormat": "JSON",
  "status": "PENDING",
  "createdAt": "2025-11-12T10:30:00"
}
```

### 2. Envoyer un fichier via HTTP POST

```bash
curl -X POST http://localhost:8080/api/jobs/1/send \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://api.example.com/upload",
    "secret": "your-secret-key"
  }'
```

## 🔐 Sécurité - Token HMAC-SHA256

L'application utilise HMAC-SHA256 pour signer les requêtes HTTP :

```java
String token = HmacTokenGenerator.generateToken(secret, payload);
// Résultat: "Bearer <token_base64>"
```

## 📝 Nomenclature des fichiers

Format standardisé : `PREFIX_TYPE_yyyyMMdd_vN.json`

Exemples :
- `CLIENTS_JSON_20251112_v1.json` - Gabarit de clients en JSON
- `INVOICES_XML_20251112_v1.json` - Gabarit de factures en XML

## 🗄️ Base de données

La structure est gérée par **Liquibase** via `/src/main/resources/db/changelog/`.

**Tables principales:**
- `templates` - Définition des gabarits
- `template_fields` - Champs de chaque gabarit
- `generation_jobs` - Tâches de génération
- `generation_logs` - Historique des exécutions

## 📌 À implémenter

- [ ] Endpoint `/api/templates/import` - Import de fichiers
- [ ] Génération de fichiers JSON avec injection d'erreurs
- [ ] Génération de fichiers XML
- [ ] Génération de fichiers TXT
- [ ] Endpoint `/api/jobs/{id}/preview`
- [ ] Endpoint `/api/jobs/{id}/send` avec HTTP POST sécurisé
- [ ] Interface Thymeleaf
- [ ] Validateurs personnalisés
- [ ] Global Exception Handler

## 🧪 Tests

```bash
mvn test
```

## 📚 Documentation supplémentaire

- **Architecture:** Consulter `ARCHITECTURE.md`
- **API Specification:** Consulter `API_SPEC.md`


## 📄 Licence

Apache License 2.0

