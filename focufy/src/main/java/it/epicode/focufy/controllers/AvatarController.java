package it.epicode.focufy.controllers;
import it.epicode.focufy.exceptions.BadRequestException;
import it.epicode.focufy.services.AvatarService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@RestController
public class AvatarController {

    @Autowired
    private AvatarService avatarService;

    @PostMapping("/api/avatars/upload")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> uploadAvatarsFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        try {
            File tempFile = File.createTempFile("avatars", ".tmp");
            FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);

            try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
                avatarService.loadAvatarsFromFile(reader);
                return ResponseEntity.ok("Avatars uploaded successfully");
            } finally {
                FileUtils.deleteQuietly(tempFile);
            }
        } catch (IOException e) {
            throw new BadRequestException("Failed to process file: " + e.getMessage());
        }
    }

    @PutMapping("/api/users/{userId}/remove-avatar")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<String> removeAvatarAssignment(@PathVariable int userId) {

        avatarService.removeAvatarAssignment(userId);
        return ResponseEntity.ok("Avatar assignment removed successfully");
    }

}
