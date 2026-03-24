package com.EventManager.API.controller;

import com.EventManager.API.Domain.coupon.CouponRequestDTO;
import com.EventManager.API.Domain.event.Event;
import com.EventManager.API.Domain.event.EventDetailsDTO;
import com.EventManager.API.Domain.event.EventRequestDTO;
import com.EventManager.API.Domain.event.EventResponseDTO;
import com.EventManager.API.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/event")
public class eventController {

    @Autowired
    private EventService service;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Event> create(@RequestParam ("title")String title,
                                        @RequestParam (value = "description", required = false)String description,
                                        @RequestParam ("date") LocalDateTime date,
                                        @RequestParam ("city") String city,
                                        @RequestParam ("state") String state,
                                        @RequestParam("remote") Boolean remote,
                                        @RequestParam("eventUrl") String eventUrl,
                                        @RequestParam(value = "image", required = false) MultipartFile image){
        EventRequestDTO eventRequestDTO = new EventRequestDTO(title,
                description, date, city,state, remote,eventUrl, image);
        if (image == null || image.isEmpty()) {
            System.out.println("❌ imagem NÃO chegou");
        } else {
            System.out.println("✅ imagem chegou: " + image.getOriginalFilename());
        }

        Event newEvent= this.service.createEvent(eventRequestDTO);
        return ResponseEntity.ok(newEvent);
    }

    @GetMapping
    public ResponseEntity<List<EventResponseDTO>> getEvents(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size){
        List<EventResponseDTO> allEvents = this.service.getUpcomingEvents(page, size);
        return ResponseEntity.ok(allEvents);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDetailsDTO> getEventsById(@PathVariable UUID eventId){
        EventDetailsDTO eventDetailsDTO = service.getEventDetails(eventId);
        return ResponseEntity.ok(eventDetailsDTO);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<EventResponseDTO>> filterEvents(@RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "5")int size,
                                                               @RequestParam(required = false)String title, @RequestParam(required = false)String city,
                                                               @RequestParam(required = false)String uf, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime startDate,
                                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime endDate){

        List<EventResponseDTO> events = service.getFilteredEvents(page,size,title,city,uf,endDate,startDate);
        return ResponseEntity.ok(events);
    }



    }


