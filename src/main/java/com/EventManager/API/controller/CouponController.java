package com.EventManager.API.controller;

import com.EventManager.API.Domain.coupon.Coupon;
import com.EventManager.API.Domain.coupon.CouponRequestDTO;
import com.EventManager.API.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping
    public ResponseEntity<Coupon> addCouponToEvent (@PathVariable UUID eventId, @RequestBody CouponRequestDTO couponRequestDTO){
        Coupon coupon = couponService.addCouponToEvent(eventId,couponRequestDTO);

        return ResponseEntity.ok(coupon);


    }




}
