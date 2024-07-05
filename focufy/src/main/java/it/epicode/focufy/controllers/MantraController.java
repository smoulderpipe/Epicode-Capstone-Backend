package it.epicode.focufy.controllers;

import it.epicode.focufy.exceptions.BadRequestException;
import it.epicode.focufy.services.MantraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RestController
public class MantraController {

    @Autowired
    private MantraService mantraService;

    @PostMapping("/api/mantras/upload")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String uploadMantras(@RequestParam("file") MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            mantraService.loadMantrasFromFile(reader);
            return "Mantras loaded successfully";
        } catch (IOException e) {
            throw new BadRequestException("Failed to read file: " + e.getMessage());
        }
    }
}
