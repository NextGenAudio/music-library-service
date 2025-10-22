package com.sonex.musiclibraryservice.model;

public record AudioUploadEvent(
        Long fileId,
        String storageUrl

) {
    @Override
    public Long fileId() {
        return fileId;
    }

    @Override
    public String storageUrl() {
        return storageUrl;
    }
}
