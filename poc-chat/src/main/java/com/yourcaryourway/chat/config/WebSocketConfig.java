package com.yourcaryourway.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration du broker WebSocket STOMP.
 *
 * STOMP (Simple Text Oriented Messaging Protocol) est un protocole de messagerie
 * qui s'appuie sur WebSocket. Il définit un format de message standardisé et un
 * mécanisme de publication/abonnement (publish/subscribe).
 *
 * Fonctionnement :
 *  - Le client se connecte à l'endpoint WebSocket (/ws)
 *  - Il s'abonne à un topic (/topic/messages) pour recevoir les messages
 *  - Il envoie ses messages vers /app/chat.send
 *  - Le broker les redistribue à tous les abonnés du topic
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configure le broker de messages.
     *
     * - /topic  : préfixe des destinations côté serveur → clients (broadcast)
     * - /app    : préfixe des messages envoyés par les clients → serveur
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Broker en mémoire pour les topics de diffusion
        registry.enableSimpleBroker("/topic");
        // Préfixe pour les messages destinés aux @MessageMapping du contrôleur
        registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Enregistre l'endpoint WebSocket.
     *
     * SockJS est un fallback JavaScript qui émule WebSocket
     * sur les navigateurs ou proxies qui ne le supportent pas.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // À restreindre en production
                .withSockJS();
    }
}
