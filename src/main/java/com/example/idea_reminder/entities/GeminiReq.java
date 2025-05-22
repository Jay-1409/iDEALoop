package com.example.idea_reminder.entities;

import java.util.List;

public class GeminiReq {
    private List<Content>  contents;
    public static class Content {
        private String role;
        private List<Part> parts;
        public static class Part {
            private String text;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }
        }
        public String getRole() {
            return role;
        }
        public void setRole(String role) {
            this.role = role;
        }
        public List<Part> getParts() {
            return parts;
        }
        public void setParts(List<Part> parts) {
            this.parts = parts;
        }
    }
    public List<Content> getContents() {
        return contents;
    }
    public void setContents(List<Content> contents) {
        this.contents = contents;
    }
}
