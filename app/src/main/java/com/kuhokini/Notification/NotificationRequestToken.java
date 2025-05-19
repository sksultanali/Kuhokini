package com.kuhokini.Notification;

public class NotificationRequestToken {
    private Message message;

    public NotificationRequestToken(Message message) {
        this.message = message;
    }

    public static class Message {
        private String token;
        private Notification notification;

        public Message(String token, Notification notification) {
            this.token = token;
            this.notification = notification;
        }
    }

    public static class Notification {
        private String title;
        private String body;

        public Notification(String title, String body) {
            this.title = title;
            this.body = body;
        }
    }
}
