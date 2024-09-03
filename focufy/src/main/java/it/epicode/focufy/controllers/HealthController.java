package it.epicode.focufy.controllers;

import it.epicode.focufy.dtos.*;
import it.epicode.focufy.entities.PersonalAnswer;
import it.epicode.focufy.entities.SharedAnswer;
import it.epicode.focufy.entities.enums.CDAnswerType;
import it.epicode.focufy.exceptions.BadRequestException;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.repositories.SharedAnswerRepo;
import it.epicode.focufy.services.AnswerService;
import it.epicode.focufy.services.AvatarService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class HealthController {

   @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
       Map<String, String> response = new HashMap<>();
       response.put("status", "Backend is up");
       return ResponseEntity.ok(response);
    }

}