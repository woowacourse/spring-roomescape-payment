package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "일반 사용자 페이지 API", description = "일반 사용자 페이지 API 입니다.")
@Controller
public class UserPageController {
    @Operation(summary = "일반 사용자 예약 페이지", description = "일반 사용자 예약 페이지를 조회합니다.")
    @GetMapping("/reservation")
    public String reservationPage() {
        return "reservation";
    }

    @Operation(summary = "인기 테마 페이지", description = "인기 테마 페이지를 조회합니다.")
    @GetMapping
    public String bestThemePage() {
        return "index";
    }

    @Operation(summary = "일반 사용자 예약 조회 페이지", description = "일반 사용자 예약을 조회합니다.")
    @GetMapping("/reservation-mine")
    public String myReservationPage() {
        return "reservation-mine";
    }

    @Operation(summary = "로그인 페이지", description = "로그인 페이지를 조회합니다.")
    @GetMapping("/login")
    public String loginView() {
        return "/login";
    }
}
