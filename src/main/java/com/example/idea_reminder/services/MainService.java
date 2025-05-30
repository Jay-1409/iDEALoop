package com.example.idea_reminder.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.idea_reminder.entities.GeminiReq;
import com.example.idea_reminder.entities.Idea;
import com.example.idea_reminder.entities.User;
import com.example.idea_reminder.repository.MainRepository;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MainService {
    @Autowired
    MainRepository mainRepository;
    @Autowired
    JavaMailSender mailSender;
    @Autowired
    private Cloudinary cloudinary;

    public boolean checkIfUserExists(String email){
        Optional<User> reqUser = mainRepository.findById(email);
        return reqUser.isPresent();
    }
    public String addUser(@NotNull User newUser) {
        Optional<User> reqUser = mainRepository.findById(newUser.getUserEmail());
        if(reqUser.isPresent()){
            return "USER WITH THIS EMAIL ALREADY EXISTS";
        }
        mainRepository.save(newUser);
        return "USER CREATED";
    }
    private final Map<String, Integer> otpStore = new ConcurrentHashMap<>();
    public int generateOtp(String email) {
        int otp = (int)(Math.random() * 9000) + 1000;
        otpStore.put(email, otp);
        return otp;
    }
    public boolean sendOtpMail(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sjay140905@gmail.com");
        message.setTo(to);
        message.setSubject("Otp for idea-board");
        int otp = generateOtp(to);
        System.out.println("Otp for email: " + to + " :" + otp);
        message.setText("Enter this code to login: " + otp);
        try {
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            System.out.println("Could not send email to: " + to + ": "+ e.getMessage());
        }
        return false;
    }
    public boolean validateOtp(String email, int otp) {
        Integer storedOtp = otpStore.get(email);
        if (storedOtp != null && storedOtp == otp) {
            otpStore.remove(email);
            return true;
        }
        return false;
    }
    public boolean validateUser(String email, String inPass) {
        Optional<User> getUsr = mainRepository.findById(email);
        if(getUsr.isPresent()) {
            User activeUser = getUsr.get();
            String reqPass = activeUser.getUserPass();
//            System.out.println("Correct pass: " + reqPass + " Pass provided " + inPass);
            return reqPass.equals(inPass);
        }
        System.out.println("User not found: " + email);
        return false;
    }
    public boolean addUserIdea(String email, Idea newIdea, List<MultipartFile> images) {
        Optional<User> reqUser = mainRepository.findById(email);
        if(reqUser.isPresent()) {
            User activeUser = reqUser.get();
            ObjectId id = new ObjectId();
            newIdea.setIdeaId(id.toString());
            if(!images.isEmpty()){
                newIdea.setImageUrls(HandleImageUploadReq(images));
            }
            activeUser.getIdeas().add(newIdea);
            mainRepository.save(activeUser);
            return true;
        }
        return false;
    }
    public boolean updateUserIdeas(String email, @NotNull Idea updatedIdea, List<MultipartFile> images) {
        String ideaId = updatedIdea.getIdeaId();
        Optional<User> reqUser = mainRepository.findById(email);
        if (reqUser.isPresent()) {
            User activeUser = reqUser.get();
            List<Idea> ideas = activeUser.getIdeas();
            boolean updated = false;
            for (Idea idea : ideas) {
                if (idea.getIdeaId().equals(ideaId)) {
                    idea.setIdeaTitle(updatedIdea.getIdeaTitle());
                    idea.setIdeaText(updatedIdea.getIdeaText());
                    idea.setRemindMe(updatedIdea.isRemindMe());
                    idea.setNextRemindDate(updatedIdea.getNextRemindDate());
                    idea.setGap(updatedIdea.getGap());
                    idea.setImageUrls(updatedIdea.getImageUrls());
                    if(!images.isEmpty()){
                        List<String> newUrls = (HandleImageUploadReq(images));
                        for(String urls: newUrls) {
                            idea.getImageUrls().add(urls);
                        }
                    }
                    updated = true;
                    break;
                }
            }
            if (updated) {
                mainRepository.save(activeUser);
                return true;
            }
        }
        return false;
    }
    public boolean deleteIdea(String userMail, String ideaId) {
        Optional<User> reqUser = mainRepository.findById(userMail);
        if(reqUser.isPresent()) {
            User activeUser = reqUser.get();
            Boolean deleted = false;
            for(Idea x : activeUser.getIdeas()) {
                if(x.getIdeaId().equals(ideaId)){
                    activeUser.getIdeas().remove(x);
                    deleted = true;
                    break;
                }
            }
            mainRepository.save(activeUser);
            return deleted;
        }
        return false;
    }
    @Scheduled(cron = "0 0 6 * * *", zone = "Asia/Kolkata")
    public void remindEveryone() {
        List<User> allUsers = mainRepository.findAll();
        for(User x : allUsers) {
            List<Idea> allIdeas = x.getIdeas();
            List<Idea> ideasToSend =  new ArrayList<>();
            for(Idea y : allIdeas){
                if (y.getNextRemindDate().equals(LocalDate.now()) && y.isRemindMe()) {
                    y.setNextRemindDate(LocalDate.now().plusDays(y.getGap()));
                    ideasToSend.add(y);
                }
            }
            if(ideasToSend.isEmpty())continue;
            sendReminderMail(x.getUserEmail(), ideasToSend);
        }
    }
    public boolean sendReminderMail(String userMail, List<Idea> allPendingIdeas) {
        if (allPendingIdeas == null || allPendingIdeas.isEmpty()) {
            return false;
        }
        StringBuilder content = new StringBuilder();
        content.append("Hi,\n\nHere's a reminder of your pending ideas:\n\n");
        int count = 1;
        for (Idea idea : allPendingIdeas) {
            content.append(count++)
                    .append(". ")
                    .append(idea.getIdeaTitle())
                    .append(" - ")
                    .append(idea.getIdeaText())
                    .append("\n");
        }
        content.append("\nStay creative!\n\n- Your Idea Reminder App");
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(userMail);
            message.setSubject("Your Pending Ideas Reminder");
            message.setText(content.toString());
            message.setFrom("sjay140905@gmail.com");
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            System.out.println("Error sending reminder email: " + e.getMessage());
            return false;
        }
    }

    // Getting user data
    public List<Idea> getUserIdeas(String Email) {
        Optional<User> reqUser = mainRepository.findById(Email);
        if (reqUser.isPresent()) {
            User activeUser = reqUser.get();
            return activeUser.getIdeas();
        }
        return null;
    }
    // IMAGE HANDLING
    public String uploadImage(MultipartFile image) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    image.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "auto",
                            "folder", "iDEALoop"
                    )
            );
            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    public List<String> HandleImageUploadReq(@NotNull List<MultipartFile> images){
        // THIS FUNCTION WILL GENERATE THE CLOUDINARY LINK FOR THE UPLOADED IMAGE AND THEN STORE IT IN THE LIST<> inside the idea entity
        List<String> imageUrls = new ArrayList<>();
        for(MultipartFile image: images) {
            String url = uploadImage(image);
            imageUrls.add(url);
        }
        return imageUrls;
    }

    // GEMINI INTEGRATION
    @Value("${gemini.api.key}")
    private String apiKey;
    private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";
    public String sendPromptToGemini(String ideaTitle, String ideaDescription) {
        GeminiReq.Content.Part part = new GeminiReq.Content.Part();
        part.setText("You are going to be given the idea Title and idea Description, your task would be to give 3 suggestion's on the ideas, each suggestion must not exceed 20 words." + "Idea title: " + ideaTitle + " Idea description: " + ideaDescription);
        GeminiReq.Content content = new GeminiReq.Content();
        content.setRole("user");
        content.setParts(List.of(part));
        GeminiReq request = new GeminiReq();
        request.setContents(List.of(content));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GeminiReq> entity = new HttpEntity<>(request, headers);
        RestTemplate restTemplate = new RestTemplate();
        System.out.println(GEMINI_URL + apiKey);
        ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_URL + apiKey, entity, String.class);
        return response.getBody();
    }


}
