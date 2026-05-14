package com.yourcaryourway.chat.model;

/**
 * Type d'un message de chat.
 *
 * - CHAT    : message texte standard envoyé par un utilisateur
 * - JOIN    : notification d'arrivée d'un utilisateur dans la salle
 * - LEAVE   : notification de départ d'un utilisateur
 */
public enum MessageType {
    CHAT,
    JOIN,
    LEAVE
}
