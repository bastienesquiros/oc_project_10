package com.yourcaryourway.chat.controller;

import com.yourcaryourway.chat.model.ChatMessage;
import com.yourcaryourway.chat.model.MessageType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

/**
 * Contrôleur WebSocket — gère les messages entrants du chat.
 *
 * @MessageMapping  : route les messages STOMP entrants selon leur destination
 * @SendTo          : diffuse la réponse à tous les abonnés du topic indiqué
 *
 * Flux complet :
 *   Client → /app/chat.send → sendMessage() → /topic/messages → tous les clients abonnés
 *   Client → /app/chat.join → joinRoom()    → /topic/messages → tous les clients abonnés
 */
@Controller
public class ChatController {

    /**
     * Reçoit un message texte et le diffuse à tous les abonnés.
     *
     * @param message le message envoyé par le client (désérialisé depuis JSON)
     * @return le même message, rediffusé sur /topic/messages
     */
    @MessageMapping("/chat.send")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(@Payload ChatMessage message) {
        return message;
    }

    /**
     * Gère l'arrivée d'un nouvel utilisateur dans la salle.
     *
     * Le nom d'utilisateur est stocké dans les attributs de session WebSocket
     * pour pouvoir l'utiliser lors d'une déconnexion (voir EventListener).
     *
     * @param message        le message de type JOIN envoyé par le client
     * @param headerAccessor accès aux headers STOMP et à la session WebSocket
     * @return un message de notification de connexion diffusé à tous
     */
    @MessageMapping("/chat.join")
    @SendTo("/topic/messages")
    public ChatMessage joinRoom(
            @Payload ChatMessage message,
            SimpMessageHeaderAccessor headerAccessor) {

        // Stocke le pseudo dans la session WebSocket (utile pour le LEAVE)
        headerAccessor.getSessionAttributes().put("username", message.getSender());

        return message;
    }
}
