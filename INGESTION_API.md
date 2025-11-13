# 📊 Ingestion API - Documentation

## Vue d'Ensemble

Cette fonctionnalité permet d'envoyer automatiquement les fichiers générés vers l'API de **Médiation Liste Noire** et d'analyser les résultats avec comparaison des attendus vs réels.

---

## 🎯 Caractéristiques Principales

### 1. Système d'Onglets

L'interface est divisée en 2 onglets:

```
┌──────────────────────────────────────────────┐
│ [Génération de Fichiers] [Ingestion API]    │
├──────────────────────────────────────────────┤
│                                              │
│  Contenu de l'onglet actif                   │
│                                              │
└──────────────────────────────────────────────┘
```

**Onglet 1 - Génération de Fichiers:**
- Actions: Prévisualiser, Télécharger
- Disponible pour tous les types

**Onglet 2 - Ingestion API:**
- Action: Envoyer pour Ingestion
- Disponible uniquement pour ACQ_MTBORNE et ACQ_PE
- Automatiquement masqué pour ACQ_MTCAB

---

## 🔗 URLs Automatiques

### Configuration par Type

| Type de Fichier | URL d'Ingestion |
|-----------------|-----------------|
| **ACQ_MTBORNE** | `http://127.0.0.1:2005/mediation-liste-noire-engine/liste-noires/acq` |
| **ACQ_PE** | `http://127.0.0.1:2005/mediation-liste-noire-engine/liste-noires/acq_pe` |
| **ACQ_MTCAB** | *(Onglet masqué - ingestion non supportée)* |

### Comportement Dynamique

```javascript
// L'URL change automatiquement selon le type sélectionné
Sélection: ACQ_MTBORNE → URL: .../acq
Sélection: ACQ_PE       → URL: .../acq_pe
Sélection: ACQ_MTCAB    → Onglet Ingestion masqué
```

---

## 🔐 Authentification Bearer Token

### Configuration

**Champ:** Token Bearer (optionnel)
- 🔒 Type: password (masqué par défaut)
- 👁️ Toggle: Afficher/Masquer
- Envoyé dans: Header `Authorization: Bearer {token}`

**Utilisation:**
```http
POST /mediation-liste-noire-engine/liste-noires/acq HTTP/1.1
Host: 127.0.0.1:2005
Content-Type: application/json
Authorization: Bearer eyJhbGc...    # Si fourni

{JSON du fichier}
```

---

## 📊 Réponse de l'API

### Structure Standard

```json
{
    "lignesTraitees": 5000,
    "lignesInsert": 5000,
    "lignesUpdate": 0,
    "lignesIgnorees": 0,
    "messageAvertissement": null,
    "statut": "SUCCESS"
}
```

### Champs de Réponse

| Champ | Description | Type |
|-------|-------------|------|
| `lignesTraitees` | Nombre total de lignes traitées | Integer |
| `lignesInsert` | Nombre de nouvelles lignes insérées | Integer |
| `lignesUpdate` | Nombre de lignes mises à jour | Integer |
| `lignesIgnorees` | Nombre de lignes ignorées (erreurs) | Integer |
| `messageAvertissement` | Message d'avertissement (si applicable) | String ou null |
| `statut` | Statut de l'ingestion | Enum: SUCCESS, PARTIAL, FAILED |

---

## 🎨 Affichage des Résultats

### 1. Statut Général

```
┌─────────────────────────────────────────┐
│ ✓ Ingestion Réussie (Status 200)       │
│ Statut: SUCCESS                         │
└─────────────────────────────────────────┘
```

**Codes Couleur:**
- 🟢 **Vert (SUCCESS):** Tout s'est bien passé
- 🟡 **Orange (PARTIAL):** Réussite partielle avec avertissements
- 🔴 **Rouge (FAILED):** Échec complet

### 2. Statistiques en Cartes

```
┌─────────────┬─────────────┬─────────────┬─────────────┐
│   Traitées  │   Insérées  │  Mises à    │  Ignorées   │
│             │             │    Jour     │             │
│    5000     │    5000     │      0      │      0      │
└─────────────┴─────────────┴─────────────┴─────────────┘
```

### 3. Tableau de Comparaison

```
┌──────────────────┬──────────┬──────────┬──────────┐
│                  │ Attendu  │  Réel    │  Statut  │
├──────────────────┼──────────┼──────────┼──────────┤
│ Total Lignes     │   10     │   10     │  ✓ OK    │
│ Lignes Valides   │    8     │    8     │  ✓ OK    │
│ Lignes en Erreur │    2     │    2     │  ✓ OK    │
└──────────────────┴──────────┴──────────┴──────────┘
```

**Badges de Statut:**
- ✅ `✓ OK` (vert) : Les valeurs correspondent
- ⚠️ `⚠ Différent` (orange) : Les valeurs diffèrent

### 4. Message d'Avertissement (si présent)

```
┌─────────────────────────────────────────────────────┐
│ ⚠ Avertissement:                                    │
│ Ingestion BMT partiellement réussie : 16 ligne(s)  │
│ traitée(s) avec succès, mais 6 ligne(s) ignorée(s) │
│ en raison d'erreurs de validation                  │
└─────────────────────────────────────────────────────┘
```

### 5. JSON Brut (Collapsible)

```
▼ Voir la réponse JSON brute
  ┌─────────────────────────────────────┐
  │ {                                   │
  │   "lignesTraitees": 5000,           │
  │   "lignesInsert": 5000,             │
  │   ...                               │
  │ }                                   │
  └─────────────────────────────────────┘
```

---

## 📈 Cas d'Usage

### Cas 1: Ingestion Réussie (SUCCESS)

**Configuration:**
```
Type: ACQ_MTBORNE
Total: 10 lignes
Erreurs: 0
Doublons: 0
```

**Réponse Attendue:**
```json
{
    "lignesTraitees": 10,
    "lignesInsert": 10,
    "lignesUpdate": 0,
    "lignesIgnorees": 0,
    "messageAvertissement": null,
    "statut": "SUCCESS"
}
```

**Comparaison:**
```
Total Lignes:     10 = 10  ✓ OK
Lignes Valides:   10 = 10  ✓ OK
Lignes en Erreur:  0 = 0   ✓ OK
```

---

### Cas 2: Ingestion Partielle (PARTIAL)

**Configuration:**
```
Type: ACQ_PE
Total: 20 lignes
Erreurs: 5
Doublons: 2
```

**Réponse Attendue:**
```json
{
    "lignesTraitees": 22,
    "lignesInsert": 12,
    "lignesUpdate": 5,
    "lignesIgnorees": 5,
    "messageAvertissement": "Ingestion PE partiellement réussie : 17 ligne(s) traitée(s) avec succès, mais 5 ligne(s) ignorée(s) en raison d'erreurs de validation",
    "statut": "PARTIAL"
}
```

**Comparaison:**
```
Total Lignes:     22 = 22  ✓ OK
Lignes Valides:   17 = 17  ✓ OK
Lignes en Erreur:  5 = 5   ✓ OK
```

**Affichage:**
- Statut: 🟡 Orange (PARTIAL)
- Avertissement affiché
- Status HTTP: 206

---

### Cas 3: Ingestion Échouée (FAILED)

**Configuration:**
```
Type: ACQ_MTBORNE
Total: 10 lignes
Erreurs: 10 (toutes en erreur!)
```

**Réponse Attendue:**
```json
{
    "lignesTraitees": 10,
    "lignesInsert": 0,
    "lignesUpdate": 0,
    "lignesIgnorees": 10,
    "messageAvertissement": "Ingestion BMT échouée : 0 ligne(s) traitée(s) avec succès, 10 ligne(s) ignorée(s) en raison d'erreurs de validation",
    "statut": "FAILED"
}
```

**Comparaison:**
```
Total Lignes:     10 = 10  ✓ OK
Lignes Valides:    0 = 0   ✓ OK
Lignes en Erreur: 10 = 10  ✓ OK
```

**Affichage:**
- Statut: 🔴 Rouge (FAILED)
- Avertissement affiché
- Status HTTP: 400

---

## 🔄 Workflow Complet

```
┌──────────────────────────────────────────┐
│ 1. Configuration du Fichier              │
│    • Type: ACQ_MTBORNE ou ACQ_PE         │
│    • Lignes: Total / Erreurs / Doublons  │
│    • Types d'erreurs sélectionnés        │
└─────────────────┬────────────────────────┘
                  │
                  ▼
┌──────────────────────────────────────────┐
│ 2. Basculer vers Onglet "Ingestion API" │
│    • URL automatiquement définie         │
│    • Token optionnel                     │
└─────────────────┬────────────────────────┘
                  │
                  ▼
┌──────────────────────────────────────────┐
│ 3. Cliquer "Envoyer pour Ingestion"     │
└─────────────────┬────────────────────────┘
                  │
                  ▼
┌──────────────────────────────────────────┐
│ 4. Génération du Fichier (côté client)  │
│    • Appel /api/generate                 │
│    • Réception du JSON                   │
└─────────────────┬────────────────────────┘
                  │
                  ▼
┌──────────────────────────────────────────┐
│ 5. Envoi vers API d'Ingestion           │
│    • POST vers URL configurée            │
│    • Header Bearer si token fourni       │
│    • Body: JSON du fichier               │
└─────────────────┬────────────────────────┘
                  │
                  ▼
┌──────────────────────────────────────────┐
│ 6. Réception de la Réponse              │
│    • Status: 200/206/400                 │
│    • Body: JSON avec statistiques        │
└─────────────────┬────────────────────────┘
                  │
                  ▼
┌──────────────────────────────────────────┐
│ 7. Affichage des Résultats               │
│    • Statut coloré                       │
│    • Statistiques en cartes              │
│    • Comparaison Attendu vs Réel         │
│    • Message d'avertissement             │
│    • JSON brut                           │
└──────────────────────────────────────────┘
```

---

## 🎯 Tests Recommandés

### Test 1: Ingestion Complète Réussie
```bash
Configuration:
- Type: ACQ_MTBORNE
- Total: 100, Erreurs: 0, Doublons: 0
- Token: (vide ou valide)

Résultat Attendu:
✅ Status 200
✅ Statut: SUCCESS
✅ lignesInsert: 100
✅ lignesIgnorees: 0
```

### Test 2: Ingestion Partielle
```bash
Configuration:
- Type: ACQ_PE
- Total: 50, Erreurs: 10, Doublons: 5
- Token: (vide ou valide)

Résultat Attendu:
⚠️ Status 206
⚠️ Statut: PARTIAL
✅ lignesTraitees: 55
✅ lignesInsert + lignesUpdate: 45
✅ lignesIgnorees: 10
```

### Test 3: Ingestion Échouée
```bash
Configuration:
- Type: ACQ_MTBORNE
- Total: 20, Erreurs: 20 (tout en erreur!)
- Token: (vide ou valide)

Résultat Attendu:
❌ Status 400
❌ Statut: FAILED
✅ lignesInsert: 0
✅ lignesIgnorees: 20
```

### Test 4: Changement de Type
```bash
Action:
1. Sélectionner ACQ_MTBORNE
2. Observer l'URL: .../acq
3. Sélectionner ACQ_PE
4. Observer l'URL: .../acq_pe
5. Sélectionner ACQ_MTCAB
6. Observer: Onglet Ingestion masqué

Résultat Attendu:
✅ URL change automatiquement
✅ Onglet masqué pour MTCAB
```

---

## 🐛 Dépannage

### Problème: Onglet Ingestion pas visible
**Cause:** Type ACQ_MTCAB sélectionné
**Solution:** Sélectionner ACQ_MTBORNE ou ACQ_PE

### Problème: "Failed to fetch"
**Cause:** API d'ingestion non accessible
**Solution:** 
- Vérifier que l'API tourne sur `127.0.0.1:2005`
- Vérifier les logs de l'API

### Problème: Status 401 Unauthorized
**Cause:** Token invalide ou expiré
**Solution:** 
- Vérifier le token Bearer
- Régénérer un nouveau token

### Problème: Comparaison montre des différences
**Cause:** Comportement normal si l'API a des règles de validation
**Solution:**
- Analyser le message d'avertissement
- Vérifier les logs de l'API
- Adapter la configuration du générateur

---

## 📋 Checklist Avant Ingestion

- [ ] API de médiation démarrée (`127.0.0.1:2005`)
- [ ] Type de fichier: ACQ_MTBORNE ou ACQ_PE
- [ ] Configuration valide (Total >= Erreurs)
- [ ] Token Bearer (si nécessaire)
- [ ] Onglet "Ingestion API" visible

---

## 🎓 Bonnes Pratiques

### 1. Tests Progressifs
```
Étape 1: Tester avec 0 erreur → SUCCESS attendu
Étape 2: Tester avec quelques erreurs → PARTIAL attendu
Étape 3: Tester avec tout en erreur → FAILED attendu
```

### 2. Analyse des Résultats
```
✅ Toujours vérifier la comparaison Attendu vs Réel
✅ Lire le message d'avertissement si présent
✅ Consulter le JSON brut en cas de doute
```

### 3. Sécurité
```
🔒 Utiliser un token Bearer en environnement sécurisé
🔒 Ne pas partager les tokens
🔒 Régénérer les tokens régulièrement
```

---

## 📊 Métriques de Performance

| Métrique | Valeur Cible |
|----------|--------------|
| Temps de génération | < 1s pour 100 lignes |
| Temps d'envoi | < 2s |
| Temps d'affichage | < 100ms |
| **Total** | **< 3s** |

---

## 🔮 Améliorations Futures

- [ ] Support ACQ_MTCAB pour ingestion
- [ ] Historique des ingestions
- [ ] Export des résultats (CSV/PDF)
- [ ] Graphiques de performance
- [ ] Retry automatique en cas d'échec
- [ ] Configuration des URLs personnalisées
- [ ] Support de plusieurs environnements (dev/staging/prod)

---

**Version:** 1.2
**Date:** 2025-11-13
**Statut:** ✅ Disponible

