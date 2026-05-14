package com.yourcaryourway.chat.model;

/**
 * Représente un message échangé dans la salle de chat.
 *
 * Cette classe est sérialisée/désérialisée automatiquement en JSON
 * par Jackson lors des échanges WebSocket STOMP.
 */
public class ChatMessage {

    private MessageType type;
    private String content;
    private String sender;

    public ChatMessage() {}

    public ChatMessage(MessageType type, String content, String sender) {
        this.type = type;
        this.content = content;
        this.sender = sender;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
