package com.EventManager.API.service;

import com.EventManager.API.Domain.coupon.Coupon;
import com.EventManager.API.Domain.coupon.CouponRequestDTO;
import com.EventManager.API.Domain.event.Event;
import com.EventManager.API.repositories.CouponRepository;
import com.EventManager.API.repositories.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService service;


    @Test
    void addCouponToEvent() {
        UUID eventId = UUID.randomUUID();
        Event event = new Event();
        event.setId(eventId);

                Mockito.when(eventRepository
                        .findById(event.getId()))
                        .thenReturn(Optional.of(event));

        CouponRequestDTO couponData = new CouponRequestDTO("DESCONTO10", 10, LocalDateTime.now().plusDays(7));

        Mockito.when(couponRepository.save(any(Coupon.class))).thenAnswer(Invocation -> Invocation.getArgument(0));

        Coupon result = service.addCouponToEvent(eventId, couponData);

        assertNotNull(result);
        assertEquals("DESCONTO10", result.getCode());
        assertEquals(10, result.getDiscount());
        assertEquals(event, result.getEvent());
    }

    @DisplayName("shoul return IllegarArgumentException if event not exists")
    @Test
    void addCouponToEventcase2() {
        UUID eventId = UUID.randomUUID();

        Mockito.when(eventRepository
                        .findById(eventId))
                .thenReturn(Optional.empty());

        CouponRequestDTO couponData = new CouponRequestDTO("DESCONTO10", 10, LocalDateTime.now().plusDays(7));

        assertThrows(IllegalArgumentException.class, () -> {service.addCouponToEvent(eventId, couponData);});
    }

    @Test
    void consultCoupons() {
        UUID eventId = UUID.randomUUID();
        LocalDateTime currentDate = LocalDateTime.now().plusDays(10);

        List<Coupon> listCoupon = List.of(new Coupon(), new Coupon());

        Mockito.when(couponRepository.findByEventIdAndValidAfter(eventId,currentDate)).thenReturn(listCoupon);

        List<Coupon> result = service.consultCoupons(eventId,currentDate);

        assertNotNull(result);
        assertEquals(2,result.size());
        assertInstanceOf(Coupon.class, result.getFirst());

    }

    @DisplayName("should have a list empty")
    @Test
    void consultCouponscase2() {
        UUID eventId = UUID.randomUUID();
        LocalDateTime currentDate = LocalDateTime.now().plusDays(10);


        Mockito.when(couponRepository.findByEventIdAndValidAfter(eventId,currentDate)).thenReturn(List.of());

        List<Coupon> result = service.consultCoupons(eventId,currentDate);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}