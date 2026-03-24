package com.EventManager.API.service;

import com.EventManager.API.Domain.coupon.Coupon;
import com.EventManager.API.Domain.coupon.CouponRequestDTO;
import com.EventManager.API.Domain.event.Event;
import com.EventManager.API.repositories.CouponRepository;
import com.EventManager.API.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class CouponService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CouponRepository couponRepository;

    public Coupon addCouponToEvent(UUID eventId, CouponRequestDTO couponData){
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new IllegalArgumentException("Event not found"));

        Coupon coupon = new Coupon();

        coupon.setCode(couponData.code());
        coupon.setDiscount(couponData.discount());
        coupon.setValid(couponData.date());
        coupon.setEvent(event);

                return couponRepository.save(coupon);
    }

    public List<Coupon> consultCoupons(UUID eventId, LocalDateTime currentDate){
        return couponRepository.findByEventIdAndValidAfter(eventId, currentDate);
    }


}
