package com.EventManager.API.service;

import com.EventManager.API.Domain.address.Address;
import com.EventManager.API.Domain.coupon.Coupon;
import com.EventManager.API.Domain.event.Event;
import com.EventManager.API.Domain.event.EventDetailsDTO;
import com.EventManager.API.Domain.event.EventRequestDTO;
import com.EventManager.API.Domain.event.EventResponseDTO;
import com.EventManager.API.repositories.EventRepository;
import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {



    @Mock
    private AddressService addressService;

    @Mock
    private CouponService couponService;

    @Mock
    private EventRepository repository;

    @InjectMocks
    private EventService service;



    @Test
    void createEvent() {

        LocalDateTime now = LocalDateTime.now().plusDays(10);
        EventRequestDTO data = new EventRequestDTO("Event test",
                "Event service teste",now,"São Paulo",
                "SP", false, null,null );
        Mockito.when(repository.save(any(Event.class))).thenAnswer(Invocation -> Invocation.getArgument(0));
        Event result = service.createEvent(data);

        assertNotNull(result);
        assertEquals(result.getTitle(), data.title());
        assertEquals(result.getDescription(), data.description());
        assertEquals(result.getDate() , data.date());
        assertEquals(result.getCity(), data.city());
        assertEquals(result.getState(), data.state());

        Mockito.verify(addressService).createAddress(Mockito.eq(data), any(Event.class));
        Mockito.verify(repository).save(any(Event.class));

    }

    @Test
    void createEvent_case_WhenTitleisNull() {

        LocalDateTime now = LocalDateTime.now().plusDays(10);
        EventRequestDTO data = new EventRequestDTO(null,
                "Event service teste",now,"São Paulo",
                "SP", false, null,null );

        assertThrows(IllegalArgumentException.class, () -> {service.createEvent(data);});

        Mockito.verify(repository, Mockito.never()).save(any());

    }

    @Test
    void createEvent_case_WhenDescriptionisNull() {

        LocalDateTime now = LocalDateTime.now().plusDays(10);
        EventRequestDTO data = new EventRequestDTO("Teste",
                null,now,"São Paulo",
                "SP", false, null,null );

        assertThrows(IllegalArgumentException.class, () -> {service.createEvent(data);});

        Mockito.verify(repository, Mockito.never()).save(any());

    }

    @Test
    void createEvent_case_WhenDataisNull() {

        LocalDateTime now = LocalDateTime.now().plusDays(10);
        EventRequestDTO data = new EventRequestDTO("Teste",
                "Teste",null,"São Paulo",
                "SP", false, null,null );

        assertThrows(IllegalArgumentException.class, () -> {service.createEvent(data);});

        Mockito.verify(repository, Mockito.never()).save(any());

    }



    @Test
    void getUpcomingEvents() {
        int page =0;
        int size = 5;
        Event event1 = new Event();
        event1.setTitle("Evento 1");
        event1.setDescription("Desc 1");
        event1.setDate(LocalDateTime.now().plusDays(1));
        event1.setRemote(false);

        Event event2 = new Event();
        event2.setTitle("Evento 2");
        event2.setDescription("Desc 2");
        event2.setDate(LocalDateTime.now().plusDays(2));
        event2.setRemote(true);

        List<Event> listEvent = List.of(event1, event2);
        List<EventResponseDTO> listResponse;

        Pageable pageable = PageRequest.of(page, size);
        Page <Event> events = new PageImpl<>(listEvent ,pageable , listEvent.size());

        Mockito.when(repository.findUpcomingEvents(any(LocalDateTime.class),Mockito.eq(pageable)))
                .thenReturn(events);

        listResponse = service.getUpcomingEvents(page, size);

        assertNotNull(listResponse);
        assertEquals(2 , listResponse.size());

        assertEquals("Evento 1", listResponse.get(0).title());
        assertEquals("Desc 1", listResponse.get(0).description());

        assertEquals("Evento 2", listResponse.get(1).title());
        assertEquals("Desc 2", listResponse.get(1).description());

        Mockito.verify(repository).findUpcomingEvents(Mockito.any(LocalDateTime.class), Mockito.eq(pageable));

    }

    @Test
    void getUpcomingEvents_Case_WhentEventNotExists() {
        int page =0;
        int size = 5;

        List<EventResponseDTO> listResponse;

        Mockito.when(repository.findUpcomingEvents(any(LocalDateTime.class),any(Pageable.class)))
                .thenReturn(Page.empty());

        listResponse = service.getUpcomingEvents(page, size);

        assertNotNull(listResponse);
        assertTrue(listResponse.isEmpty());

        Mockito.verify(repository).findUpcomingEvents(Mockito.any(LocalDateTime.class),any(Pageable.class));

    }

    @Test
    void getEventDetails() {
        UUID eventId = UUID.randomUUID();
        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Evento 1");
        event.setDescription("Desc 1");
        event.setDate(LocalDateTime.now().plusDays(1));
        event.setRemote(false);
        event.setImgURL("img.png");
        event.setEventURL("url.com");

        Address address = new Address();
        address.setCity("São Paulo");
        address.setUf("SP");

        event.setAddresses(address);

        Coupon coupon = new Coupon();
        coupon.setCode("DESC10");
        coupon.setDiscount(10);
        coupon.setValid(LocalDateTime.now().plusDays(5));

        List<Coupon> coupons = List.of(coupon);

        Mockito.when(repository.findById(eventId)).thenReturn(Optional.of(event));
        Mockito.when(couponService.consultCoupons(Mockito.eq(eventId),Mockito.any(LocalDateTime.class)))
                .thenReturn(coupons);

        EventDetailsDTO result = service.getEventDetails(eventId);

        assertNotNull(result);
        assertEquals(event.getTitle() , result.title());
        assertEquals(event.getDescription(), result.description());

        assertEquals(1, result.coupons().size());
        assertEquals("DESC10", result.coupons().getFirst().code());
        assertEquals(10, result.coupons().getFirst().discount());

        Mockito.verify(repository).findById(eventId);
        Mockito.verify(couponService).consultCoupons(Mockito.eq(eventId), Mockito.any(LocalDateTime.class));

    }

    @Test
    void getFilteredEvents() {
        int page = 0;
        int size = 2;
        String title= "Evento test";
        String city = "";
        String uf = "";
        LocalDateTime startDate = LocalDateTime.of(2025,1,1,0,0);

        Event event1 = new Event();
        event1.setTitle("Evento test");
        event1.setDescription("Desc 1");
        event1.setDate(startDate);
        event1.setRemote(false);

        Event event2 = new Event();
        event2.setTitle("Evento test");
        event2.setDescription("Desc 2");
        event2.setDate(LocalDateTime.now().plusDays(2));
        event2.setRemote(true);

        List<Event> listEvent = List.of(event1, event2);
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventPage = new PageImpl<>(listEvent, pageable, listEvent.size());

        Mockito.when(repository.findFilterEvents(Mockito.eq(title),
                Mockito.eq(city),
                Mockito.eq(uf),
                Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class),
                Mockito.eq(pageable)))
                .thenReturn(eventPage);


        List<EventResponseDTO> listResponse = service.getFilteredEvents(page, size, title, city,uf, startDate,
                startDate.plusDays(10));

        assertNotNull(listResponse);
        assertEquals(2 , listResponse.size());

        assertEquals("Evento test", listResponse.getFirst().title());
        assertEquals("Desc 1", listResponse.getFirst().description());

        Mockito.verify(repository).findFilterEvents(Mockito.eq(title),
                Mockito.eq(city),
                Mockito.eq(uf),
                Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class),
                Mockito.eq(pageable));
    }
}