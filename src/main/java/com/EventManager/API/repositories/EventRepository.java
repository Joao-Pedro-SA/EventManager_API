package com.EventManager.API.repositories;

import com.EventManager.API.Domain.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {




}
