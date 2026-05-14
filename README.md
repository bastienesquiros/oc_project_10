# PoC Chat — Your Car Your Way

> **Preuve de concept** — Salle de chat en temps réel via WebSocket STOMP  
> Stack : **Spring Boot 3** (Java 21) + HTML/JS vanilla

---

## Qu'est-ce que cette PoC ?

Ce projet est une **preuve de concept ciblée** du projet Your Car Your Way.
Il ne représente pas l'application finale, mais démontre la faisabilité de la
communication en temps réel avec la stack technique retenue (Spring Boot).

**Ce que la PoC démontre :**
- La connexion WebSocket entre un navigateur et Spring Boot
- Le protocole STOMP pour la messagerie publish/subscribe
- L'architecture en couches de Spring Boot (Config / Controller / Model)
- La gestion des événements de déconnexion côté serveur

**Ce qui est hors périmètre :**
- Authentification et comptes utilisateurs (JWT, Spring Security)
- Persistance des messages (pas de base de données)
- L'interface de l'application finale (Next.js)

---

## Prérequis

Avant de commencer, installe les outils suivants sur ta machine :

| Outil | Version minimale | Vérification |
|---|---|---|
| **Java JDK** | 21 | `java -version` |
| **Maven** | 3.9+ | `mvn -version` |
| **Git** | Toute version récente | `git --version` |

> 💡 **Pas sûr d'avoir Java 21 ?** Télécharge-le sur [adoptium.net](https://adoptium.net/) — c'est gratuit.

---

## Installation et démarrage

### 1. Cloner le dépôt

```bash
git clone https://github.com/your-car-your-way/poc-chat.git
cd poc-chat
```

### 2. Lancer le serveur

```bash
mvn spring-boot:run
```

Tu devrais voir dans le terminal :

```
Started ChatApplication in 2.3 seconds
Tomcat started on port 8080
```

### 3. Ouvrir le chat

Ouvre **plusieurs onglets** dans ton navigateur :

```
http://localhost:8080
```

Entre un pseudo différent dans chaque onglet et commence à envoyer des messages.
Tu verras les messages apparaître en temps réel dans tous les onglets ouverts. 🎉

---

## Structure du projet

```
poc-chat/
├── src/
│   └── main/
│       ├── java/com/yourcaryourway/chat/
│       │   ├── ChatApplication.java          ← Point d'entrée Spring Boot
│       │   ├── config/
│       │   │   └── WebSocketConfig.java      ← Configuration du broker WebSocket
│       │   ├── controller/
│       │   │   ├── ChatController.java       ← Réception et diffusion des messages
│       │   │   └── WebSocketEventListener.java ← Gestion des déconnexions
│       │   └── model/
│       │       ├── ChatMessage.java          ← Structure d'un message
│       │       └── MessageType.java          ← Types : CHAT, JOIN, LEAVE
│       └── resources/
│           ├── application.properties        ← Configuration (port, etc.)
│           └── static/
│               └── index.html               ← Interface web du chat
└── pom.xml                                  ← Dépendances Maven
```

---

## Comment ça fonctionne ?

### Vue d'ensemble

```
Navigateur A          Serveur Spring Boot          Navigateur B
    │                        │                          │
    │──── connexion WS ─────►│                          │
    │                        │◄──── connexion WS ───────│
    │                        │                          │
    │── /app/chat.send ─────►│                          │
    │   { "Bonjour !" }      │──── /topic/messages ────►│
    │                        │◄─── /topic/messages ─────│
    │◄─── /topic/messages ───│   (reçoit aussi son msg) │
```

### Le protocole STOMP en 3 étapes

**1. Le client se connecte** à l'endpoint WebSocket `/ws`

**2. Le client s'abonne** au topic `/topic/messages` pour recevoir les messages

**3. Le client envoie** ses messages vers `/app/chat.send`  
   → Spring les route vers `ChatController.sendMessage()`  
   → Spring les rediffuse à **tous les abonnés** de `/topic/messages`

### Les fichiers clés expliqués

#### `WebSocketConfig.java` — La configuration

C'est ici que l'on dit à Spring :
- « Le endpoint WebSocket est `/ws` »
- « Les messages clients arrivent avec le préfixe `/app` »
- « Les diffusions partent sur les topics `/topic` »

#### `ChatController.java` — Le cerveau

Deux méthodes annotées :
- `@MessageMapping("/chat.send")` → reçoit les messages de chat
- `@MessageMapping("/chat.join")` → gère les connexions

L'annotation `@SendTo("/topic/messages")` diffuse automatiquement la réponse à tous.

#### `WebSocketEventListener.java` — Les déconnexions

Quand un utilisateur ferme son onglet, Spring émet un `SessionDisconnectEvent`.
Ce listener le capte et envoie un message "X a quitté la salle" à tout le monde.

---

## Lancer les tests

```bash
mvn test
```

Le test `ChatApplicationTest` vérifie que le contexte Spring Boot démarre
correctement (tous les beans sont bien configurés).

---

## Commandes utiles

| Commande | Description |
|---|---|
| `mvn spring-boot:run` | Démarre le serveur en mode développement |
| `mvn test` | Lance les tests unitaires |
| `mvn package` | Compile et crée le `.jar` exécutable |
| `java -jar target/poc-chat-0.0.1-SNAPSHOT.jar` | Lance le `.jar` compilé |

---

## Dépannage

**Le serveur ne démarre pas**
- Vérifie que Java 21 est installé : `java -version`
- Vérifie que le port 8080 est libre : `lsof -i :8080` (Mac/Linux)

**La connexion WebSocket échoue dans le navigateur**
- Assure-toi que le serveur est bien démarré (message `Started ChatApplication` dans le terminal)
- Essaie de recharger la page (`F5`)

**Les messages n'apparaissent pas dans l'autre onglet**
- Les deux onglets doivent être connectés (avoir entré un pseudo et cliqué "Rejoindre")
- Vérifie la console du navigateur (`F12` → Console) pour des erreurs

---

## Lien avec l'architecture globale

Cette PoC illustre les principes qui seront appliqués à l'ensemble du projet :

| Ce que la PoC démontre | Ce que ça sera dans le projet final |
|---|---|
| `WebSocketConfig` | Configuration Spring Security + JWT |
| `ChatController` | Tous les controllers REST (réservations, profil…) |
| `ChatMessage` (modèle) | Entités JPA (User, Reservation, Offer…) |
| Broker en mémoire | Redis en production (scalable, persistant) |
| HTML vanilla | Interface Next.js (React) |

---

## Ressources pour aller plus loin

- [Documentation Spring WebSocket](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket)
- [Guide officiel Spring Boot WebSocket](https://spring.io/guides/gs/messaging-stomp-websocket/)
- [Protocole STOMP](https://stomp.github.io/)
- [Proposition d'architecture YCYW](../proposition-architecture.md)
