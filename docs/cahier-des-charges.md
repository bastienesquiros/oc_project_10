# Cahier des Charges — Your Car Your Way
## Application web centralisée de location de voitures

| Champ | Valeur                                                  |
|---|---------------------------------------------------------|
| Version | 1.1                                     |
| Auteur | Bastien Esquiros - Lead Développeur — Your Car Your Way |
| Date | Mai 2026                                                |

---

## Sommaire

1. [Objet du document](#1-objet-du-document)
2. [Contexte et enjeux](#2-contexte-et-enjeux)
3. [Périmètre](#3-périmètre)
4. [Profils utilisateurs](#4-profils-utilisateurs)
5. [Considérations transverses](#5-considérations-transverses)
6. [Exigences fonctionnelles consolidées](#6-exigences-fonctionnelles-consolidées)
7. [User Stories et critères d'acceptation](#7-user-stories-et-critères-dacceptation)
8. [Règles métier](#8-règles-métier)
9. [Exigences non fonctionnelles](#9-exigences-non-fonctionnelles)
10. [Glossaire](#10-glossaire)

---

## 1. Objet du document

Ce document constitue le cahier des charges fonctionnel consolidé pour la nouvelle application web centralisée de **Your Car Your Way**. Il complète et structure la version initiale rédigée par les équipes internes, en y intégrant :

- une analyse exhaustive des besoins des différents profils utilisateurs ;
- la prise en compte des utilisateurs en situation de handicap (PSH) ;
- les exigences transverses : accessibilité (RGAA), sécurité, internationalisation, impact écologique ;
- les user stories priorisées accompagnées de leurs critères d'acceptation.

---

## 2. Contexte et enjeux

Your Car Your Way est une entreprise de location de voitures présente depuis plus de vingt ans sur le marché européen et récemment implantée en Amérique du Nord. Sa croissance a engendré une multiplication d'applications web par pays, causant :

- une **complexité technique** croissante et des coûts de maintenance élevés ;
- des **incohérences fonctionnelles** entre pays ;
- une **expérience utilisateur fragmentée**.

La nouvelle application centralisée vise à :

- **Unifier** l'expérience client à l'international ;
- **Moderniser** la stack technique pour assurer évolutivité et maintenabilité ;
- **Garantir** la conformité réglementaire (RGPD, RGAA, normes de paiement PCI DSS) ;
- **Réduire** l'empreinte écologique numérique.

---

## 3. Périmètre

### Dans le périmètre ✅

- Application web responsive accessible aux **clients finaux** (B2C).
- Déploiement **international** (Europe + Amérique du Nord).
- **API REST** exposée pour les applications internes des agences (CRUD complet par domaine).
- Intégration d'un **prestataire de paiement en ligne** (ex. Stripe).
- Gestion des catégories véhicules selon la **norme ACRISS**.

### Hors périmètre ❌

- Application interne de gestion d'agence (back-office employés).
- Gestion de la flotte véhicules.
- Application mobile native (iOS/Android) — v2 potentielle.

---

## 4. Profils utilisateurs

### 4.1 Visiteur anonyme

Utilisateur non authentifié accédant à l'application pour la première fois ou sans compte.

**Besoins :** découvrir le service, consulter les agences, effectuer une recherche d'offres, créer un compte ou se connecter.

### 4.2 Client authentifié

Utilisateur disposant d'un compte actif, connecté à l'application.

**Besoins :** rechercher et réserver des véhicules, gérer ses réservations, consulter et modifier son profil.

### 4.3 Client en situation de handicap (PSH)

Sous-profil transverse applicable aux deux profils précédents. Regroupe les utilisateurs avec :

- **handicap visuel** (cécité, malvoyance, daltonisme) ;
- **handicap moteur** (navigation clavier, absence de souris) ;
- **handicap cognitif** (troubles de l'attention, dyslexie) ;
- **handicap auditif** (impact sur les contenus multimédia).

**Besoins :** accès équivalent à toutes les fonctionnalités via technologies d'assistance (lecteurs d'écran, navigation clavier, plages braille).

### 4.4 Application d'agence (consommateur API)

Système logiciel interne utilisé par les employés en agence, consommant l'API REST exposée par l'application.

**Besoins :** lire et modifier les données (utilisateurs, réservations, offres, véhicules) via des endpoints CRUD standardisés et sécurisés.

---

## 5. Considérations transverses

### 5.1 Accessibilité (RGAA 4.1 / WCAG 2.1 AA)

L'application doit être **conforme au niveau AA du RGAA 4.1**, applicable aux services numériques à destination du public. Cela implique notamment :

- Structure sémantique HTML correcte (titres, landmarks, listes) ;
- Contraste des couleurs ≥ 4,5:1 pour le texte normal, ≥ 3:1 pour le grand texte ;
- Tous les éléments interactifs accessibles au clavier (Tab, Entrée, Échap) ;
- Attributs ARIA utilisés uniquement en complément du HTML sémantique ;
- Textes alternatifs pour toutes les images porteuses de sens ;
- Formulaires avec labels explicites et messages d'erreur descriptifs ;
- Pas de contenu clignotant à plus de 3 Hz.

### 5.2 Sécurité

- **Authentification sécurisée** : mots de passe hachés (bcrypt/argon2), tokens JWT ou sessions serveur sécurisées.
- **RGPD** : consentement explicite, droit à la suppression (droit à l'oubli), minimisation des données.
- **HTTPS** obligatoire sur tous les endpoints.
- **Validation** des entrées côté serveur (protection XSS, injection SQL).
- **Paiement PCI DSS** : aucune donnée de carte bancaire stockée côté application (délégation totale à Stripe).
- **Rate limiting** sur les endpoints sensibles (connexion, réinitialisation de mot de passe).

### 5.3 Internationalisation (i18n)

- Support multilingue dès la conception (français, anglais, espagnol au minimum).
- Formats de dates, heures et devises adaptés à la locale de l'utilisateur.
- Gestion des fuseaux horaires pour les dates de réservation.
- Contenu textuel externalisé (fichiers de traduction), non codé en dur.

### 5.4 Impact écologique numérique (Green IT)

- Optimisation du poids des pages (compression des assets, lazy loading des images).
- Limiter les requêtes réseau superflues (mise en cache côté client et serveur).
- Hébergement privilégiant les datacenters à faible émission carbone.
- Pas de fonctionnalité superflue (principe de sobriété fonctionnelle).

---

## 6. Exigences fonctionnelles consolidées

### 6.1 Authentification et gestion de compte

| ID | Fonctionnalité | Priorité |
|---|---|---|
| AUTH-01 | Créer un compte (email + mot de passe) | 🔴 Haute |
| AUTH-02 | Se connecter | 🔴 Haute |
| AUTH-03 | Se déconnecter | 🔴 Haute |
| AUTH-04 | Réinitialiser son mot de passe (lien par email) | 🔴 Haute |
| AUTH-05 | Vérifier son adresse email à l'inscription | 🟡 Moyenne |
| AUTH-06 | Maintenir la session active (remember me) | 🟢 Basse |

### 6.2 Gestion du profil

| ID | Fonctionnalité | Priorité |
|---|---|---|
| PROFIL-01 | Consulter son profil | 🔴 Haute |
| PROFIL-02 | Modifier ses informations personnelles (nom, prénom, date de naissance, adresse) | 🔴 Haute |
| PROFIL-03 | Modifier son mot de passe | 🔴 Haute |
| PROFIL-04 | Modifier son adresse email | 🟡 Moyenne |
| PROFIL-05 | Supprimer son compte (confirmation par mot de passe) | 🔴 Haute |
| PROFIL-06 | Télécharger ses données personnelles (conformité RGPD) | 🟡 Moyenne |

### 6.3 Gestion des agences

| ID | Fonctionnalité | Priorité |
|---|---|---|
| AGENCE-01 | Consulter la liste des agences de location | 🔴 Haute |
| AGENCE-02 | Afficher le détail d'une agence (adresse, horaires, contact) | 🟡 Moyenne |
| AGENCE-03 | Localiser les agences sur une carte interactive | 🟢 Basse |

### 6.4 Recherche et consultation des offres

| ID | Fonctionnalité | Priorité |
|---|---|---|
| OFFRE-01 | Rechercher des offres via formulaire (ville départ, ville retour, dates, catégorie ACRISS) | 🔴 Haute |
| OFFRE-02 | Afficher la liste des offres correspondant aux critères | 🔴 Haute |
| OFFRE-03 | Consulter le détail d'une offre (véhicule, tarif, conditions) | 🔴 Haute |
| OFFRE-04 | Filtrer et trier les résultats (prix, catégorie, disponibilité) | 🟡 Moyenne |
| OFFRE-05 | Afficher les catégories véhicules selon la norme ACRISS | 🔴 Haute |

### 6.5 Gestion des réservations

| ID | Fonctionnalité | Priorité |
|---|---|---|
| RESA-01 | Réserver une offre de location | 🔴 Haute |
| RESA-02 | Pré-remplir les informations personnelles depuis le profil | 🟡 Moyenne |
| RESA-03 | Effectuer le paiement via Stripe | 🔴 Haute |
| RESA-04 | Recevoir une confirmation de réservation par email | 🔴 Haute |
| RESA-05 | Consulter l'historique des réservations (passées et en cours) | 🔴 Haute |
| RESA-06 | Consulter le détail d'une réservation | 🔴 Haute |
| RESA-07 | Modifier une réservation (jusqu'à 48h avant le départ) | 🔴 Haute |
| RESA-08 | Annuler une réservation (avec application des règles de remboursement) | 🔴 Haute |
| RESA-09 | Recevoir un rappel avant le début de la réservation | 🟢 Basse |

### 6.6 API Agences (consommateurs internes)

| ID | Fonctionnalité | Priorité |
|---|---|---|
| API-01 | CRUD Utilisateurs | 🔴 Haute |
| API-02 | CRUD Réservations | 🔴 Haute |
| API-03 | CRUD Offres | 🔴 Haute |
| API-04 | CRUD Agences | 🔴 Haute |
| API-05 | Authentification sécurisée (clé API ou OAuth2) | 🔴 Haute |

---

## 7. User Stories et critères d'acceptation

---

### AUTH-01 — Créer un compte

> **En tant que** visiteur anonyme,  
> **je veux** créer un compte avec mon adresse email et un mot de passe,  
> **afin de** pouvoir accéder aux fonctionnalités réservées aux clients authentifiés.

**Critères d'acceptation :**
- [ ] Le formulaire d'inscription contient les champs : prénom, nom, email, mot de passe, confirmation du mot de passe.
- [ ] Le mot de passe doit respecter une politique de sécurité (≥ 8 caractères, 1 majuscule, 1 chiffre, 1 caractère spécial).
- [ ] Un message d'erreur clair est affiché si l'email est déjà utilisé.
- [ ] Un email de vérification est envoyé après inscription.
- [ ] Les champs du formulaire ont des labels explicites et sont accessibles au clavier.
- [ ] Les messages d'erreur sont associés aux champs concernés via `aria-describedby`.

---

### AUTH-02 — Se connecter

> **En tant que** client possédant un compte,  
> **je veux** me connecter avec mon email et mon mot de passe,  
> **afin d'** accéder à mon espace personnel.

**Critères d'acceptation :**
- [ ] Le formulaire de connexion contient les champs email et mot de passe.
- [ ] En cas d'échec, un message d'erreur générique est affiché (sans préciser si c'est l'email ou le mot de passe qui est incorrect).
- [ ] Après 5 tentatives échouées, un mécanisme anti-brute-force est activé (délai, captcha).
- [ ] L'utilisateur est redirigé vers la page qu'il souhaitait accéder (ou l'accueil par défaut).
- [ ] La session est maintenue de façon sécurisée (cookie HttpOnly, Secure, SameSite).

---

### AUTH-04 — Réinitialiser son mot de passe

> **En tant que** client ayant oublié son mot de passe,  
> **je veux** recevoir un lien de réinitialisation par email,  
> **afin de** retrouver l'accès à mon compte.

**Critères d'acceptation :**
- [ ] Un lien "Mot de passe oublié" est visible sur la page de connexion.
- [ ] L'utilisateur saisit son email ; un message de confirmation s'affiche qu'il existe un compte ou non (anti-énumération).
- [ ] Le lien de réinitialisation est valide 1 heure et à usage unique.
- [ ] Le nouveau mot de passe respecte la politique de sécurité.

---

### PROFIL-02 — Modifier ses informations personnelles

> **En tant que** client authentifié,  
> **je veux** modifier mes informations personnelles (nom, prénom, date de naissance, adresse),  
> **afin de** maintenir mon profil à jour.

**Critères d'acceptation :**
- [ ] Les informations actuelles sont pré-remplies dans le formulaire.
- [ ] Les modifications sont sauvegardées et confirmées visuellement.
- [ ] La date de naissance est validée (format cohérent, âge ≥ 18 ans pour louer).
- [ ] L'adresse est structurée en champs distincts (rue, ville, code postal, pays).
- [ ] Un retour d'erreur explicite est affiché en cas de données invalides.

---

### PROFIL-05 — Supprimer son compte

> **En tant que** client authentifié,  
> **je veux** supprimer définitivement mon compte,  
> **afin d'** exercer mon droit à l'oubli (RGPD).

**Critères d'acceptation :**
- [ ] L'utilisateur doit saisir son mot de passe actuel pour confirmer la suppression.
- [ ] Une modale de confirmation avertit des conséquences irréversibles.
- [ ] Les données personnelles sont supprimées ou anonymisées dans un délai de 30 jours.
- [ ] Les réservations en cours ou futures bloquent la suppression immédiate (avertissement préalable).
- [ ] L'utilisateur est déconnecté et redirigé après suppression.

---

### OFFRE-01 — Rechercher des offres

> **En tant que** visiteur ou client authentifié,  
> **je veux** rechercher des offres de location en renseignant mes critères,  
> **afin de** trouver un véhicule adapté à mon trajet.

**Critères d'acceptation :**
- [ ] Le formulaire contient : ville de départ, ville de retour, date/heure de début, date/heure de retour, catégorie de véhicule (ACRISS).
- [ ] La date de retour ne peut pas être antérieure à la date de départ.
- [ ] Les villes sont suggérées via autocomplétion.
- [ ] Les catégories ACRISS sont présentées avec une description lisible (pas uniquement le code).
- [ ] Le formulaire est entièrement utilisable au clavier et compatible lecteur d'écran.
- [ ] Si aucune offre n'est disponible, un message explicite est affiché avec des suggestions alternatives.

---

### OFFRE-03 — Consulter le détail d'une offre

> **En tant que** visiteur ou client authentifié,  
> **je veux** consulter le détail complet d'une offre de location,  
> **afin d'** avoir toutes les informations avant de réserver.

**Critères d'acceptation :**
- [ ] La page de détail affiche : catégorie ACRISS, description du véhicule type, tarif total, dates, agences de départ et retour, conditions d'annulation.
- [ ] Les images du véhicule ont un texte alternatif descriptif.
- [ ] Un bouton "Réserver" mène directement au tunnel de réservation.
- [ ] Le tarif inclut le détail des éventuels frais supplémentaires.

---

### RESA-01 — Réserver une offre

> **En tant que** client authentifié,  
> **je veux** réserver une offre de location,  
> **afin d'** avoir un véhicule disponible pour mon trajet.

**Critères d'acceptation :**
- [ ] Le tunnel de réservation se déroule en étapes claires (récapitulatif → informations personnelles → paiement → confirmation).
- [ ] Les informations personnelles sont pré-remplies depuis le profil si disponibles.
- [ ] Le paiement est traité via Stripe (aucune donnée bancaire ne transite par nos serveurs).
- [ ] Un email de confirmation est envoyé immédiatement après paiement.
- [ ] En cas d'échec du paiement, l'utilisateur est informé et peut réessayer sans perdre ses données.
- [ ] La réservation est visible dans l'historique immédiatement après confirmation.

---

### RESA-07 — Modifier une réservation

> **En tant que** client authentifié,  
> **je veux** modifier ma réservation,  
> **afin d'** adapter les détails à mes nouveaux besoins.

**Critères d'acceptation :**
- [ ] La modification est possible uniquement si la date de départ est à plus de 48 heures.
- [ ] Les champs modifiables sont : dates, ville de départ/retour, catégorie véhicule.
- [ ] Si le nouveau tarif est supérieur, le différentiel est facturé via Stripe.
- [ ] Si le nouveau tarif est inférieur, le remboursement du différentiel est effectué selon les règles en vigueur.
- [ ] Un email de confirmation de modification est envoyé.
- [ ] Une tentative de modification à moins de 48h affiche un message bloquant explicite.

---

### RESA-08 — Annuler une réservation

> **En tant que** client authentifié,  
> **je veux** annuler ma réservation,  
> **afin de** me libérer d'un engagement que je ne peux pas honorer.

**Critères d'acceptation :**
- [ ] L'utilisateur voit clairement le montant qui lui sera remboursé avant de confirmer.
- [ ] Si l'annulation est à plus de 7 jours du départ : remboursement intégral.
- [ ] Si l'annulation est à moins de 7 jours du départ : remboursement de 25% du montant total.
- [ ] Une confirmation explicite est demandée avant l'annulation définitive.
- [ ] Un email de confirmation d'annulation est envoyé avec le détail du remboursement.
- [ ] Le remboursement est traité via Stripe dans un délai de 5 à 10 jours ouvrés.

---

### RESA-05 — Consulter l'historique des réservations

> **En tant que** client authentifié,  
> **je veux** consulter l'historique de toutes mes réservations,  
> **afin de** suivre mes locations passées et en cours.

**Critères d'acceptation :**
- [ ] Les réservations sont listées par ordre chronologique décroissant.
- [ ] Chaque réservation affiche : statut (à venir / en cours / terminée / annulée), dates, véhicule, montant.
- [ ] Un filtre par statut est disponible.
- [ ] Un lien vers le détail complet de chaque réservation est accessible.
- [ ] La liste est paginée ou utilise un chargement progressif pour les longs historiques.

---

### PSH — Accessibilité transverse

> **En tant que** client en situation de handicap,  
> **je veux** accéder à toutes les fonctionnalités de l'application via des technologies d'assistance,  
> **afin de** bénéficier d'une expérience équivalente à celle des autres utilisateurs.

**Critères d'acceptation :**
- [ ] L'ensemble de l'application est navigable au clavier sans piège de focus.
- [ ] L'ordre de tabulation suit la logique visuelle de la page.
- [ ] Toutes les actions déclenchables à la souris le sont aussi au clavier.
- [ ] Le contenu est structuré avec des niveaux de titres cohérents (h1 → h2 → h3).
- [ ] Les messages d'état (succès, erreur, chargement) sont annoncés par les lecteurs d'écran (`aria-live`).
- [ ] Les modales gèrent correctement le focus (piège de focus actif, fermeture avec Échap).
- [ ] Les contrastes respectent le niveau AA du RGAA (4,5:1 minimum).
- [ ] Les formulaires ont des labels explicites, des champs groupés (`fieldset/legend`) si nécessaire.

---

## 8. Règles métier

| ID | Règle | Domaine |
|---|---|---|
| RG-01 | La modification d'une réservation est possible jusqu'à 48h avant le début | Réservation |
| RG-02 | L'annulation à moins de 7 jours du départ entraîne un remboursement de 25% | Réservation |
| RG-03 | L'annulation à plus de 7 jours du départ entraîne un remboursement intégral | Réservation |
| RG-04 | Aucune donnée bancaire ne transite par les serveurs de l'application | Paiement |
| RG-05 | Les catégories de véhicules suivent la norme ACRISS | Offre |
| RG-06 | La suppression du compte requiert la saisie du mot de passe | Profil |
| RG-07 | Un client doit avoir 18 ans minimum pour louer un véhicule | Profil |
| RG-08 | Les données personnelles supprimées doivent être anonymisées sous 30 jours | RGPD |
| RG-09 | L'API agences est accessible uniquement via authentification sécurisée | API |
| RG-10 | Les fuseaux horaires des agences s'appliquent aux dates de réservation | Réservation |

---

## 9. Exigences non fonctionnelles

### Performance
- Temps de réponse des pages < 3 secondes en conditions normales.
- Chargement initial (LCP) < 2,5 secondes (seuil Core Web Vitals).

### Disponibilité
- Disponibilité cible : 99,9% (hors maintenance planifiée).

### Scalabilité
- L'architecture doit supporter une montée en charge horizontale.

### Compatibilité
- Support des 2 dernières versions des navigateurs majeurs (Chrome, Firefox, Safari, Edge).
- Affichage responsive (mobile, tablette, desktop).

### Sécurité
- Conformité RGPD.
- Conformité PCI DSS (via délégation Stripe).
- Audit de sécurité (OWASP Top 10) avant mise en production.

---

## 10. Glossaire

| Terme | Définition |
|---|---|
| ACRISS | Association of Car Rental Industry Systems Standards — norme internationale de classification des véhicules de location |
| RGAA | Référentiel Général d'Amélioration de l'Accessibilité — cadre réglementaire français pour l'accessibilité numérique |
| RGPD | Règlement Général sur la Protection des Données — réglementation européenne sur la vie privée |
| PSH | Personne en Situation de Handicap |
| Offre | Combinaison d'un véhicule (catégorie ACRISS), d'un itinéraire et d'un tarif proposée à la location |
| Réservation | Engagement contractuel d'un client sur une offre, suite au paiement |
| API Agences | Interface REST exposée par l'application pour les systèmes internes des agences |
| PCI DSS | Payment Card Industry Data Security Standard — norme de sécurité pour le traitement des paiements par carte |
