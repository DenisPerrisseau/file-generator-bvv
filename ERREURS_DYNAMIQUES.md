# ✅ ERREURS DYNAMIQUES PAR TYPE - IMPLÉMENTÉES

## 🎯 Résumé

Les **types d'erreurs changent maintenant dynamiquement** selon le type de fichier sélectionné. Chaque type a ses propres erreurs spécifiques avec ses propres validations.

## 📋 Erreurs par Type

### 1️⃣ ACQ MTBORNE (JSON Équipements)

**Erreurs Spécifiques :**
- ❌ **deployDateTime Invalide** - Date invalide (jour 32, mois 13, etc.)
- ❌ **file Trop Court** - Moins de 3 caractères (validation minimale)
- ❌ **name Vide** - Champ name vide
- ❌ **file Vide** - Champ file vide
- ❌ **deployDateTime Vide** - Champ deployDateTime vide

### 2️⃣ ACQ PE (JSON Multi-clés)

**Erreurs Spécifiques :**
- ❌ **fetchDateTime Invalide** - Date invalide (jour 32, mois 13, etc.)
- ❌ **version Invalide** - Pas exactement 4 chiffres (validation stricte)
- ❌ **fetchDateTime Vide** - Champ fetchDateTime vide
- ❌ **file Vide** - Champ file vide
- ❌ **version Vide** - Champ version vide

### 3️⃣ ACQ MTCAB (XML Acquittements)

**Erreurs Spécifiques :**
- ❌ **HorodateReceptionEquipement Invalide** - Date invalide
- ❌ **NumeroFichierFluxC2 Invalide** - Pas exactement 3 chiffres (validation stricte)
- ❌ **IdEqpt Vide** - Champ IdEqpt vide
- ❌ **HorodateReceptionEquipement Vide** - Champ vide
- ❌ **NumeroFichierFluxC2 Vide** - Champ vide

## 🔧 Implémentation

### Frontend (HTML/JavaScript)

**Changements :**
- 3 panneaux d'erreurs distincts (un par type)
- Affichage/masquage dynamique selon le type sélectionné
- Erreurs spécifiques à chaque type

**Fonctions JavaScript :**
```javascript
updateErrorsDisplay()      // Affiche les erreurs pour le type sélectionné
setupOutputTypeListeners() // Écoute les changements de type
styleErrorCards()          // Stylise les cartes d'erreurs
```

### Backend (Java)

**Services Modifiés :**

1. **JsonFileGeneratorService.java** (ACQ MTBORNE)
   - `generateEquipmentWithErrors()` - Gère 5 types d'erreurs spécifiques

2. **XmlFileGeneratorService.java** (ACQ MTCAB)
   - `generateAcquittementWithErrors()` - Gère 5 types d'erreurs spécifiques

3. **MultiKeysJsonGeneratorService.java** (ACQ PE)
   - `generateParameterLineWithErrors()` - Gère 5 types d'erreurs spécifiques

## 🎨 Interface Utilisateur

### Sélection du Type
```
📄 Type de fichier de sortie

[✓] ACQ MTBORNE (JSON équipements)
[ ] ACQ MTCAB (XML acquittements)  
[ ] ACQ PE (JSON multi-clés)
```

### Erreurs pour ACQ MTBORNE (Affichées dynamiquement)
```
⚠️ Types d'erreurs à injecter

[✓] ❌ deployDateTime Invalide (Date invalide - jour 32, mois 13, etc.)
[ ] ❌ file Trop Court (Moins de 3 caractères)
[ ] ❌ name Vide (Champ name vide)
[ ] ❌ file Vide (Champ file vide)
[ ] ❌ deployDateTime Vide (Champ deployDateTime vide)
```

### Erreurs pour ACQ PE (Affichées dynamiquement)
```
⚠️ Types d'erreurs à injecter

[✓] ❌ fetchDateTime Invalide (Date invalide - jour 32, mois 13, etc.)
[ ] ❌ version Invalide (Pas exactement 4 chiffres)
[ ] ❌ fetchDateTime Vide (Champ fetchDateTime vide)
[ ] ❌ file Vide (Champ file vide)
[ ] ❌ version Vide (Champ version vide)
```

### Erreurs pour ACQ MTCAB (Affichées dynamiquement)
```
⚠️ Types d'erreurs à injecter

[✓] ❌ HorodateReceptionEquipement Invalide (Date invalide)
[ ] ❌ NumeroFichierFluxC2 Invalide (Pas exactement 3 chiffres)
[ ] ❌ IdEqpt Vide (Champ IdEqpt vide)
[ ] ❌ HorodateReceptionEquipement Vide (Champ vide)
[ ] ❌ NumeroFichierFluxC2 Vide (Champ vide)
```

## 🚀 Utilisation

### Exemple 1 : ACQ MTBORNE avec erreurs

1. Sélectionner **ACQ MTBORNE**
   - Les 5 erreurs spécifiques s'affichent
2. Configurer :
   - Total: 10, Erreurs: 2, Doublons: 1
3. Cocher :
   - ✓ deployDateTime Invalide
   - ✓ file Trop Court
4. Générer
   - 2 lignes auront : date invalide OU file < 3 caractères

### Exemple 2 : ACQ PE avec erreurs

1. Sélectionner **ACQ PE**
   - Les 5 erreurs spécifiques s'affichent
2. Configurer :
   - Total: 15, Erreurs: 3, Doublons: 2
3. Cocher :
   - ✓ fetchDateTime Invalide
   - ✓ version Invalide
4. Générer
   - 3 lignes auront : fetchDateTime invalide OU version ≠ 4 chiffres

### Exemple 3 : ACQ MTCAB avec erreurs

1. Sélectionner **ACQ MTCAB**
   - Les 5 erreurs spécifiques s'affichent
2. Configurer :
   - Total: 8, Erreurs: 2, Doublons: 0
3. Cocher :
   - ✓ HorodateReceptionEquipement Invalide
   - ✓ NumeroFichierFluxC2 Invalide
4. Générer
   - 2 acquittements auront : date invalide OU NumeroFichier ≠ 3 chiffres

## 🧪 Tests

Tous les tests existants fonctionnent avec les nouvelles erreurs.

```bash
mvn test  # Tous les tests
```

## 📊 Validations

### ACQ MTBORNE
| Champ | Validation |
|-------|-----------|
| name | Chaîne quelconque ou vide |
| file | Minimum 3 caractères |
| deployDateTime | Format ISO 8601 valide |

### ACQ PE
| Champ | Validation |
|-------|-----------|
| fetchDateTime | Format ISO 8601 valide |
| version | Exactement 4 chiffres |
| file | Chaîne quelconque ou vide |

### ACQ MTCAB
| Champ | Validation |
|-------|-----------|
| HorodateReceptionEquipement | Format datetime valide |
| NumeroFichierFluxC2 | Exactement 3 chiffres |
| IdEqpt | Chaîne quelconque ou vide |

## ✅ Workflow Complet

1. **Démarrer l'application**
   ```bash
   mvn spring-boot:run
   ```

2. **Ouvrir http://localhost:8080/**

3. **Sélectionner un type de fichier**
   - Les erreurs changent dynamiquement
   - Seules les erreurs pertinentes s'affichent

4. **Configurer les paramètres**
   - Nombre de lignes
   - Nombre d'erreurs
   - Nombre de doublons

5. **Sélectionner les types d'erreurs**
   - Cocher les erreurs souhaitées
   - Spécifiques au type de fichier

6. **Prévisualiser ou Télécharger**
   - Voir le résultat avant téléchargement
   - Ou télécharger directement

## 🎯 Avantages

✅ **Erreurs contextuelles** - Chaque type a ses propres erreurs  
✅ **Validations réalistes** - Respect des contraintes métier  
✅ **Interface adaptative** - Change selon le type sélectionné  
✅ **Génération cohérente** - Erreurs appliquées correctement  
✅ **Facilité d'utilisation** - Interface claire et intuitive  

## 📚 Fichiers Modifiés

- ✏️ `index.html` - Interface dynamique
- ✏️ `JsonFileGeneratorService.java` - Erreurs ACQ MTBORNE
- ✏️ `XmlFileGeneratorService.java` - Erreurs ACQ MTCAB
- ✏️ `MultiKeysJsonGeneratorService.java` - Erreurs ACQ PE

**Le système génère maintenant les erreurs correctes pour chaque type de fichier !** 🎉

