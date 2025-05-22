package com.example.idea_reminder.controllers;

import com.example.idea_reminder.entities.Idea;
import com.example.idea_reminder.entities.User;
import com.example.idea_reminder.services.MainService;
//import com.sun.tools.javac.Main;
import jakarta.mail.Multipart;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.messaging.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/operations")
public class MainController {
    @Autowired
    private MainService mainService;
    // USER CREATION + AUTHENTICATION
    @GetMapping("/wake")
    public ResponseEntity<?> wakeUp() {
        return new ResponseEntity<>("SYSTEM IS AWAKE", HttpStatus.OK);
    }
    @PostMapping("/newUser")
    public ResponseEntity<?> createNewUser(@RequestBody User newUser) {
        try {
            return new ResponseEntity<>(mainService.addUser(newUser), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/send-otp-mail/{UserMail}")
    public ResponseEntity<?> getOtp(@PathVariable("UserMail") String userMail) {
        try {
            return new ResponseEntity<>(mainService.sendOtpMail(userMail), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/validate-otp/{userMail}/{OTP}")
    public ResponseEntity<?> validateOtp(@PathVariable("userMail") String userEmail,@PathVariable("OTP") int userEnteredOtp, @RequestBody User newUser) {
        try {
//            return new ResponseEntity<>(createNewUser(newUser), HttpStatus.OK);
            if(mainService.validateOtp(userEmail, userEnteredOtp)){
                return createNewUser(newUser);
            } else {
                return new ResponseEntity<>("Otp entered was either invalid or incorrect", HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/login/{UserMail}/{UserPass}")
    public ResponseEntity<?> userLogin(@PathVariable("UserMail") String userMail, @PathVariable("UserPass") String userPass) {
        try {
            return new ResponseEntity<>(mainService.validateUser(userMail, userPass), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    //USER TASKS ADDITION + UPDATE + DELETIONS
    @PostMapping(value = "/add-idea", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addIdea(
            @RequestParam String UserMail,
            @RequestPart("idea") Idea newIdea,
            @RequestPart("images") List<MultipartFile> images) {
        try {
            mainService.addUserIdea(UserMail, newIdea, images);
            return new ResponseEntity<>(newIdea, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping(value = "/update-idea/{UserMail}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateIdea(
            @PathVariable("UserMail") String UserMail,
            @RequestPart("idea") Idea updatedIdeas,
            @RequestPart("images") List<MultipartFile> images) {
        try {
            return new ResponseEntity<>(mainService.updateUserIdeas(UserMail, updatedIdeas, images), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/delete-idea/{UserMail}/{IdeaId}")
    public ResponseEntity<?> deleteIdea(@PathVariable("UserMail") String UserMail, @PathVariable("IdeaId") String IdeaId) {
        try {
            return new ResponseEntity<>(mainService.deleteIdea(UserMail,IdeaId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/fetch-user-ideas/{userMail}")
    public ResponseEntity<?> fetchUserIdeas(@PathVariable("userMail") String userMail) {
        try {
           return new ResponseEntity<>(mainService.getUserIdeas(userMail), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/upload-image-test")
    public ResponseEntity<?> testUploadImage(@RequestParam MultipartFile file) {
        try {
            String imageUrl = mainService.uploadImage(file);
            return new ResponseEntity<>(imageUrl, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
