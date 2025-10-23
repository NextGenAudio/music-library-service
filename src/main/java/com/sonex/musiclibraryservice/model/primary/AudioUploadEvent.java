package com.sonex.musiclibraryservice.model.primary;

public record AudioUploadEvent(
        Long fileId,
        String storageUrl
) {}
