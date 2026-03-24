package com.EventManager.API.Domain.coupon;

import java.time.LocalDateTime;

public record CouponRequestDTO(String code, Integer discount, LocalDateTime date) {
}
