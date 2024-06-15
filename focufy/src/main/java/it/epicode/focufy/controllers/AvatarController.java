package it.epicode.focufy.controllers;

import it.epicode.focufy.services.AvatarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AvatarController {

    @Autowired
    private AvatarService avatarService;

    @PostMapping("/assign")
    public void assignAvatarToUser(@RequestParam int userId){
        avatarService.assignAvatarToUser(userId);
    }
}
