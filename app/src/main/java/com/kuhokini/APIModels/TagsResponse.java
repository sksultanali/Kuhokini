package com.kuhokini.APIModels;

import java.util.List;

public class TagsResponse {
    private String status;
    private String message;
    private List<String> tags;

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
