package com.sonex.musiclibraryservice.model;

public record AudioUploadEvent(
        Long fileId,
        String storageUrl
) {}
