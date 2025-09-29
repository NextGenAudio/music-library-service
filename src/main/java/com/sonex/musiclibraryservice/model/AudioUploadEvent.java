package com.sonex.musiclibraryservice.model;

public record AudioUploadEvent(
        String fileId,
        String storageUrl
) {}
