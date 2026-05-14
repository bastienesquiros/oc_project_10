# Audit Technique de l'Existant — Your Car Your Way

| Champ | Valeur                                                  |
|---|---------------------------------------------------------|
| Version | 1.0                                                     |
| Auteur | Bastien Esquiros - Lead Développeur — Your Car Your Way |
| Date | Mai 2026                                                |

---

## Sommaire

1. [Introduction et critères d'évaluation](#1-introduction-et-critères-dévaluation)
2. [Cartographie de l'existant](#2-cartographie-de-lexistant)
3. [Analyse par critère](#3-analyse-par-critère)
4. [Synthèse Forces / Faiblesses / Contraintes](#4-synthèse-forces--faiblesses--contraintes)
5. [Conclusion](#5-conclusion)

---

## 1. Introduction et critères d'évaluation

### 1.1 Objectif de l'audit

Cet audit a pour but d'évaluer objectivement l'état des applications web existantes de Your Car Your Way, afin d'identifier les actifs réutilisables, les dettes techniques à traiter et les contraintes à intégrer dans la conception de la nouvelle solution centralisée.

> ⚠️ L'audit décrit l'existant tel qu'il est — il ne propose pas encore de solution. Les recommandations figureront dans la proposition d'architecture.

### 1.2 Critères d'évaluation retenus

Cinq critères structurent cet audit. Ils sont définis ci-dessous et serviront de grille de lecture pour chaque application.

| Critère | Définition |
|---|---|
| **Maintenabilité** | Capacité à corriger, faire évoluer et comprendre le code sans effort disproportionné. Inclut la lisibilité, la modularité, la couverture de tests et la gestion des dépendances. |
| **Performance** | Capacité à répondre aux requêtes dans des délais acceptables, y compris en charge élevée (pics saisonniers). Mesurée par le débit (req/s), le taux d'erreur et les temps de réponse. |
| **Évolutivité (Scalabilité)** | Capacité à faire croître le système horizontalement ou verticalement pour absorber une augmentation de la charge ou l'ajout de nouvelles fonctionnalités sans refonte majeure. |
| **Sécurité** | Niveau de protection des données utilisateurs (authentification, chiffrement, gestion des secrets, surface d'attaque via les dépendances). |
| **Fiabilité** | Capacité à rester disponible, à récupérer rapidement après un incident, et à garantir l'intégrité des données (MTTR, taux de disponibilité, backups). |
| **Interopérabilité** | Capacité des applications à échanger des données entre elles, à exposer des APIs cohérentes et à s'intégrer avec des composants tiers. |

---

## 2. Cartographie de l'existant

### 2.1 Vue d'ensemble des applications

```
┌─────────────────────────────────────────────────────────────────────┐
│                     YOUR CAR YOUR WAY — EXISTANT                    │
│                                                                     │
│  ┌─────────────────────────────────┐   ┌──────────────────────────┐ │
│  │      CLUSTER OVH (Europe)       │   │    CLUSTER AWS/AZURE     │ │
│  │                                 │   │                          │ │
│  │  ┌────────┐  ┌────────────────┐ │   │  ┌──────┐  ┌─────────┐  │ │
│  │  │   FR   │  │  DE / ES / IT  │ │   │  │  UK  │  │   CA    │  │ │
│  │  │Java EE │  │    Java EE     │ │   │  │ PHP  │  │Node.js  │  │ │
│  │  │JSP/JSF │  │   JSP/JSF      │ │   │  │Laravel│  │React   │  │ │
│  │  │ BDD FR │  │  BDD DE/ES/IT  │ │   │  │BDD UK│  │BDD CA  │  │ │
│  │  └────────┘  └────────────────┘ │   │  └──────┘  └─────────┘  │ │
│  │                                 │   │                          │ │
│  │         Déploiements manuels    │   │  ┌─────────────────────┐ │ │
│  └─────────────────────────────────┘   │  │          US         │ │ │
│                                        │  │  Spring Boot        │ │ │
│                                        │  │  Angular / Azure    │ │ │
│                                        │  │  BDD US (non redond)│ │ │
│                                        │  └─────────────────────┘ │ │
│                                        └──────────────────────────┘ │
│                                                                     │
│  ← Aucune API commune ─ Aucun schéma partagé ─ Aucun SSO →         │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.2 Tableau des technologies par pays

| Pays | Frontend | Backend | Hébergement | BDD | Déploiement |
|---|---|---|---|---|---|
| France | JSP/JSF | Java EE | OVH | Propre | Manuel |
| Allemagne | JSP/JSF | Java EE | OVH | Propre | Manuel |
| Espagne | JSP/JSF | Java EE | OVH | Propre | Manuel |
| Italie | JSP/JSF | Java EE | OVH | Propre | Manuel |
| Royaume-Uni | PHP/Laravel | PHP/Laravel | AWS EC2 | Propre | Partiellement auto |
| Canada | React | Node.js | AWS | Propre | Partiellement auto |
| États-Unis | Angular | Spring Boot | Azure (containers) | Propre | CI/CD partiel |

**Observation :** 6 stacks techniques distinctes, 6 bases de données indépendantes, 0 API commune.

---

## 3. Analyse par critère

### 3.1 Maintenabilité

#### Problématiques identifiées

**Code dupliqué et divergent (FR → DE/ES/IT)**
Les applications allemande, espagnole et italienne sont des forks du code source français. Cette stratégie de copie a engendré une **divergence progressive** : les corrections de bugs et évolutions doivent être appliquées manuellement sur chaque branche, sans garantie de cohérence.

```
FR (source)
 ├── DE (fork v2018) → bugfixes FR non propagés
 ├── ES (fork v2019) → règles métier locales divergentes
 └── IT (fork v2019) → fonctionnalités absentes vs FR
```

**Ratio de vulnérabilités dans les dépendances**

| Application | % dépendances vulnérables |
|---|---|
| France | **41 %** 🔴 |
| Allemagne | **~38 %** 🔴 |
| Espagne | **~37 %** 🔴 |
| Italie | **~35 %** 🔴 |
| Canada | 22 % 🟡 |
| Royaume-Uni | 18 % 🟡 |
| États-Unis | **11 %** 🟢 |

Les applications Java EE héritées ont accumulé une **dette technique massive** en termes de dépendances non maintenues. JSP/JSF sont des technologies dont le support communautaire est en déclin, rendant les recrutements plus difficiles.

**Délai de stabilisation post-release**

| Cluster | Délai moyen de stabilisation |
|---|---|
| OVH (FR/DE/ES/IT) | **3,4 jours** 🔴 |
| Cloud (UK/CA/US) | 1,7 jour 🟡 |

Un délai de 3,4 jours après chaque release signifie une **période de risque prolongée** à chaque mise à jour — incompatible avec un rythme de livraison moderne.

**Verdict maintenabilité :** ❌ Non satisfaisant sur le cluster OVH. Correct mais non optimal sur le cluster cloud.

---

### 3.2 Performance

#### Débit maximal sans dégradation

```
Requêtes/seconde
400 |                                          ████
350 |                                     ████ ████
300 |                               ████  ████ ████
250 |                          ████ ████  ████ ████
200 |                          ████ ████  ████ ████
150 |  ████ ████ ████ ████     ████ ████  ████ ████
100 |  ████ ████ ████ ████     ████ ████  ████ ████
 50 |  ████ ████ ████ ████     ████ ████  ████ ████
  0 +--FR---DE---ES---IT---------UK---CA----US------
        OVH (monolithes)         Cloud (modernes)
```

Les applications OVH plafonnent à **~150 req/s**, soit **2,3× moins performantes** que l'application US (350 req/s). Ces limites sont intrinsèques à l'architecture monolithique non répliquée.

#### Taux d'erreur en pic saisonnier

| Application | Taux d'erreur en pic |
|---|---|
| FR/DE/ES/IT | Jusqu'à **4 %** 🔴 |
| UK/CA | 1,5 % 🟡 |
| États-Unis | **0,8 %** 🟢 |

Un taux de 4% d'erreurs pendant les vacances représente **1 transaction sur 25 en échec** — impact direct sur le chiffre d'affaires et la satisfaction client.

**Verdict performance :** ❌ Insuffisant sur OVH. Acceptable sur cloud mais sans marge de progression claire.

---

### 3.3 Évolutivité (Scalabilité)

#### Architecture de déploiement

| Cluster | Scalabilité horizontale | Conteneurisation | CI/CD |
|---|---|---|---|
| OVH (FR/DE/ES/IT) | ❌ Aucune | ❌ Non | ❌ Manuel |
| AWS (UK/CA) | ⚠️ Partielle | ❌ Non | ⚠️ Partielle |
| Azure (US) | ✅ Oui | ✅ Oui | ⚠️ Partielle |

**Taux de succès des déploiements**

| Cluster | Taux de succès |
|---|---|
| OVH (manuels) | **82 %** 🔴 |
| Cloud (semi-auto) | 91 % 🟡 |

Un taux de réussite de 82% sur OVH signifie qu'en moyenne **1 déploiement sur 5 échoue ou nécessite une intervention manuelle**, bloquant les livraisons et augmentant le risque de régression.

L'architecture monolithique rend toute mise à l'échelle partielle impossible : une surcharge sur le module de réservation implique de dupliquer l'intégralité de l'application, avec un coût disproportionné.

**Verdict évolutivité :** ❌ Critique sur OVH. 🟢 Acceptable uniquement pour US.

---

### 3.4 Sécurité

#### Hashage des mots de passe

| Application | Algorithme | Niveau de sécurité |
|---|---|---|
| FR/DE/ES/IT | **SHA-1** | 🔴 **OBSOLÈTE** — attaques par rainbow tables triviales |
| UK | bcrypt (cost 10) | 🟢 Acceptable |
| CA | **argon2id** | 🟢 **Recommandé** par OWASP |
| US | bcrypt (strength 12) | 🟢 Bon |

> SHA-1 est officiellement déprécié depuis 2011. Son utilisation pour les mots de passe expose des millions de clients européens à un risque de compromission immédiate en cas de fuite de base de données.

#### Chiffrement du trafic

| Application | TLS version | Risque |
|---|---|---|
| FR, IT | TLS 1.0 encore actif | 🔴 Vulnérable (POODLE, BEAST) |
| Autres | TLS 1.2+ | 🟢 OK |

TLS 1.0 est désactivé par défaut dans tous les navigateurs modernes depuis 2020 et son usage est **non conforme PCI DSS v4**.

#### Gestion des secrets

| Application | Méthode | Risque |
|---|---|---|
| FR/DE/ES/IT | Fichiers de config sur serveur | 🔴 Secrets en clair, accessibles si compromission du serveur |
| UK/CA | Variables d'environnement AWS | 🟡 Mieux, mais sans rotation automatique |
| US | Azure KeyVault (partiel) | 🟢 Bonne pratique, mais incomplet |

#### Surface d'attaque globale

La combinaison SHA-1 + TLS 1.0 + secrets en clair + 41% de dépendances vulnérables sur les applications FR/DE/ES/IT représente une **surface d'attaque critique** concernant la majorité des clients européens (marchés les plus anciens).

**Verdict sécurité :** 🔴 **Critique sur FR/DE/ES/IT.** Satisfaisant uniquement sur US.

---

### 3.5 Fiabilité

#### Disponibilité et temps de récupération

| Application | Disponibilité | MTTR | Indisponibilité mensuelle |
|---|---|---|---|
| FR/DE/ES/IT | 97,2 % | ~2h45 | 21–28 min 🔴 |
| UK | 98,6 % | ~1h10 | 9–16 min 🟡 |
| CA | 98,1 % | ~1h10 | 9–16 min 🟡 |
| US | **98,9 %** | ~1h10 | 7 min 🟢 |

La cible standard pour une application e-commerce internationale est **99,9% de disponibilité** (SLA), soit moins de 44 minutes d'indisponibilité par mois. Aucune application n'atteint ce seuil.

#### Redondance et backups

| Application | Redondance | Backup | Test de restauration |
|---|---|---|---|
| FR/DE/ES/IT | ❌ Aucune | Manuel 1×/jour | ❌ Jamais testé |
| UK/CA | ⚠️ Partielle | Snapshots AWS quotidiens | ❌ Pas régulier |
| US | ✅ App containerisée | Azure automatisé | ✅ Tous les 90 jours |

> Un backup non testé est un backup dont on ne peut pas garantir la récupération. Sur FR/DE/ES/IT, un incident majeur pourrait entraîner une **perte de données irrémédiable**.

**Verdict fiabilité :** 🔴 Insuffisant sur OVH. 🟢 Correct sur US uniquement.

---

### 3.6 Interopérabilité

#### APIs et échange de données

- **Aucune API commune** entre les applications.
- **Schémas de données divergents** : chaque pays a sa propre modélisation (clients, réservations, offres).
- **Partage d'information** : inexistant ou via échanges manuels (exports CSV, imports).
- **Intégrations tierces** : hétérogènes, non documentées, potentiellement redondantes (plusieurs contrats Stripe/paiement).

Ce silotage empêche toute vue unifiée du client, rend impossible la gestion de réservations multi-pays, et multiplie les coûts d'intégration.

**Verdict interopérabilité :** 🔴 Inexistante. Blocage fondamental à l'internationalisation.

---

## 4. Synthèse Forces / Faiblesses / Contraintes

### 4.1 Forces (actifs à capitaliser)

| Force | Localisation | Intérêt pour la nouvelle solution |
|---|---|---|
| **Argon2id** pour le hashage | CA | Algorithme de référence OWASP à adopter globalement |
| **Bcrypt (strength 12)** | UK, US | Standard acceptable à conserver si argon2id non retenu |
| **Conteneurisation** | US (Azure) | Modèle de déploiement à généraliser |
| **Azure KeyVault** | US | Gestion des secrets à étendre à l'ensemble |
| **CI/CD partiel** | US, UK, CA | Base à automatiser complètement |
| **React (CA)** | CA | Compétences frontend modernes disponibles en interne |
| **Spring Boot (US)** | US | Stack Java moderne, éprouvée, nombreux profils disponibles |
| **Base fonctionnelle riche** | FR (historique) | Référentiel métier le plus complet, source de vérité fonctionnelle |

### 4.2 Faiblesses (dettes techniques à corriger)

| Faiblesse | Criticité | Impact |
|---|---|---|
| SHA-1 pour les mots de passe (FR/DE/ES/IT) | 🔴 Critique | Compromission des comptes en cas de fuite BDD |
| TLS 1.0 actif (FR, IT) | 🔴 Critique | Non-conformité PCI DSS, attaques MITM possibles |
| Secrets en clair dans fichiers de config (OVH) | 🔴 Critique | Exposition en cas de compromission serveur |
| 35–41% de dépendances vulnérables (FR/DE/ES/IT) | 🔴 Critique | Surface d'attaque massive |
| Déploiements manuels (OVH) — 18% d'échec | 🔴 Haute | Risque de régression à chaque release |
| Aucune redondance applicative (OVH) | 🔴 Haute | SPOF — panne = indisponibilité totale |
| Backups non testés (FR/DE/ES/IT) | 🔴 Haute | Risque de perte de données irrémédiable |
| Code dupliqué/forké (DE/ES/IT depuis FR) | 🟡 Haute | Corrections à appliquer N fois, incohérences fonctionnelles |
| 4% d'erreurs en pic (OVH) | 🟡 Haute | Perte de chiffre d'affaires lors des périodes clés |
| Aucune API commune | 🟡 Haute | Impossible d'unifier la vision client |
| Schémas de données divergents par pays | 🟡 Haute | Migration complexe vers une BDD commune |
| Technologies vieillissantes JSP/JSF (OVH) | 🟡 Moyenne | Recrutement difficile, support communautaire déclinant |

### 4.3 Contraintes (à intégrer dans la conception)

| Contrainte | Nature | Implication pour la nouvelle solution |
|---|---|---|
| **Migration des données** | Technique | Unification de 6 schémas divergents — nécessite une phase de migration rigoureuse |
| **Continuité de service** | Opérationnelle | La migration ne peut pas interrompre le service existant — déploiement progressif requis |
| **Compétences internes** | Humaine | Expertise Java EE dominante → plan de montée en compétences ou recrutements ciblés |
| **Conformité RGPD multi-pays** | Réglementaire | Données de résidents européens soumis au RGPD même si hébergées hors EU |
| **PCI DSS** | Réglementaire | Toute donnée bancaire doit être externalisée (Stripe) — zero stockage carte côté app |
| **Fuseaux horaires multiples** | Fonctionnelle | Réservations à traiter avec timezone de l'agence concernée |
| **Diversité des devises** | Fonctionnelle | EUR, GBP, CAD, USD à gérer nativement |

---

## 5. Conclusion

### Évaluation globale par critère

| Critère | FR/DE/ES/IT | UK | CA | US |
|---|---|---|---|---|
| Maintenabilité | 🔴 | 🟡 | 🟡 | 🟢 |
| Performance | 🔴 | 🟡 | 🟡 | 🟢 |
| Évolutivité | 🔴 | 🟡 | 🟡 | 🟢 |
| Sécurité | 🔴 | 🟢 | 🟢 | 🟢 |
| Fiabilité | 🔴 | 🟡 | 🟡 | 🟢 |
| Interopérabilité | 🔴 | 🔴 | 🔴 | 🔴 |

### Lecture des résultats

L'audit révèle une **fracture nette** entre les applications historiques OVH (FR/DE/ES/IT) et les applications plus récentes (UK/CA/US).

**Les applications OVH** échouent sur l'ensemble des critères. Elles constituent un risque opérationnel, sécuritaire et réglementaire immédiat. Elles ne peuvent pas servir de base à la nouvelle solution.

**Les applications UK et CA** sont dans un état intermédiaire : sécurité satisfaisante, performances correctes, mais architecture toujours monolithique et non interopérable.

**L'application US** est la seule à valider la majorité des critères. Elle sert de référence pour les choix d'infrastructure (conteneurisation, gestion des secrets, CI/CD) sans pour autant être réplicable telle quelle (monolithe, non generalisable, base non redondante).

**L'interopérabilité est universellement absente** : ce constat valide à lui seul la décision de concevoir une nouvelle application centralisée plutôt que de tenter une fédération de l'existant.

### Recommandations préliminaires issues de l'audit

> *Ces points seront développés dans la proposition d'architecture.*

1. **Repartir d'une base neuve** — la dette technique des apps OVH est trop lourde pour une refonte incrémentale.
2. **Adopter les bonnes pratiques de l'app US** — conteneurisation, KeyVault, CI/CD — comme standard.
3. **Planifier une migration progressive des données** — coexistence temporaire avec l'existant pendant la transition.
4. **Traiter en priorité les failles de sécurité critiques** — SHA-1, TLS 1.0, secrets en clair — dès la phase de transition, indépendamment de la nouvelle app.
5. **Concevoir une API REST unifiée** dès le départ, comme colonne vertébrale de l'architecture.
