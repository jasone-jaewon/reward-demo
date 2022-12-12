package com.example.rewarddemo.events.repository;

import com.example.rewarddemo.events.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, String> {
}
