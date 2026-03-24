package com.EventManager.API.Domain.event;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Date;

public record EventRequestDTO(String title, String description, LocalDateTime date,
                              String city, String state, Boolean remote,
                              String eventUrl, MultipartFile image) {
}
