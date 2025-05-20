package com.example.idea_reminder;

import com.example.idea_reminder.services.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class IdeaReminderApplication {
	//Got an idea?, past it, forget it, get remembered!!!.
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(IdeaReminderApplication.class, args);
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
//		MainService mainService = context.getBean(MainService.class);
//		mainService.remindEveryone();
	}
}
