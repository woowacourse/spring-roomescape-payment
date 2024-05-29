package roomescape.reservation.service.dto;

import roomescape.reservation.controller.dto.ThemeRequest;

import java.math.BigDecimal;

public record ThemeCreate(String name, String description, String thumbnail, BigDecimal price) {
    public static ThemeCreate from(ThemeRequest themeRequest) {
        return new ThemeCreate(themeRequest.name(), themeRequest.description(), themeRequest.thumbnail(),
                themeRequest.price());
    }
}

