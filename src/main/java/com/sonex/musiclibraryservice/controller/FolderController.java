package com.sonex.musiclibraryservice.controller;

import com.sonex.musiclibraryservice.model.primary.Folder;
import com.sonex.musiclibraryservice.service.FolderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/folders")
public class FolderController {

    @Autowired
    private FolderService folderService;

    // Create
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Folder> createFolder(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "artwork", required = false) MultipartFile artwork,
            HttpServletRequest request
    ) {
        Folder folder = folderService.createFolder(name, description, artwork, request);
        return ResponseEntity.ok(folder);
    }

    // Get all
    @GetMapping
    public ResponseEntity<List<Folder>> getAllFolders(HttpServletRequest request) {
        return ResponseEntity.ok(folderService.getAllFolders(request));
    }

    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<Folder> getFolderById(@PathVariable Long id) {
        Folder folder = folderService.getFolderById(id);
        return (folder != null) ? ResponseEntity.ok(folder) : ResponseEntity.notFound().build();
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<Folder> updateFolder(@PathVariable Long id, @RequestBody Folder folder) {
        Folder updated = folderService.updateFolder(id, folder);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFolder(@PathVariable Long id, HttpServletRequest request) {
        try{
            folderService.deleteFolder(id, request);
            return ResponseEntity.ok("Folder deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
