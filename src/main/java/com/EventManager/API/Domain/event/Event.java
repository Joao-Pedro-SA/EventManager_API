package com.EventManager.API.Domain.event;

import com.EventManager.API.Domain.address.Address;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Table(name = "event")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue
    private UUID id;

    private String title;

    private String description;

    private String imgURL;

    private String eventURL;

    private Boolean remote;

    private LocalDateTime date;

    private String city;

    private String state;

    @OneToOne(mappedBy = "event", cascade = CascadeType.ALL)
    private Address addresses;

}
