package com.example.idea_reminder.controllers;

import com.example.idea_reminder.entities.Idea;
import com.example.idea_reminder.entities.User;
import com.example.idea_reminder.services.MainService;
//import com.sun.tools.javac.Main;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.messaging.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operations")
public class MainController {
    MainService mainService;
    // USER CREATION + AUTHENTICATION
    @GetMapping()

    @PostMapping("/newUser")
    public ResponseEntity<?> createNewUser(@RequestBody User newUser) {
        try {
            return new ResponseEntity<>(mainService.addUser(newUser), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/send-otp-mail")
    public ResponseEntity<?> getOtp(@PathVariable String userMail) {
        try {
            return new ResponseEntity<>(mainService.sendOtpMail(userMail), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/validate-otp")
    public ResponseEntity<?> validateOtp(@PathVariable String userEmail, int userEnteredOtp) {
        try {
            return new ResponseEntity<>(mainService.validateOtp(userEmail, userEnteredOtp), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    //USER TASKS ADDITION + UPDATE + DELETIONS
    @PutMapping("/add-idea")
    public ResponseEntity<?> addIdea(@RequestParam String UserMail, @RequestParam Idea newIdea) {
        try {
            return new ResponseEntity<>(mainService.addUserIdea(UserMail, newIdea), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/update-idea")
    public ResponseEntity<?> updateIdea(@RequestParam String UserMail, @RequestParam Idea updatedIdeas) {
        try {
            return new ResponseEntity<>(mainService.updateUserIdeas(UserMail,updatedIdeas), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/delete-idea")
    public ResponseEntity<?> deleteIdea(@RequestParam String UserMail, @RequestParam ObjectId IdeaId) {
        try {
            return new ResponseEntity<>(mainService.deleteIdea(UserMail,IdeaId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
