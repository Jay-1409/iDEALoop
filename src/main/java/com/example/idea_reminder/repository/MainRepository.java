package com.example.idea_reminder.repository;

import com.example.idea_reminder.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MainRepository extends MongoRepository<User, String> {
}
