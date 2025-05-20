package com.example.idea_reminder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class IdeaReminderApplication {
	//Got an idea?, past it, forget it, get remembered!!!.
	public static void main(String[] args) {
		SpringApplication.run(IdeaReminderApplication.class, args);
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
		SpringApplication.run(IdeaReminderApplication.class, args);
	}

}
