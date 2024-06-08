package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "사용자 페이지 API", description = "사용자 페이지 관련 API 입니다.")
@Controller
public class ClientPageController {
    @Operation(summary = "사용자 메인 페이지")
    @GetMapping("/reservation")
    public String reservationPage() {
        return "reservation";
    }

    @Operation(summary = "사용자 로그인 페이지")
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @Operation(summary = "사용자 회원 가입 페이지")
    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @Operation(summary = "사용자 내 예약 조회 페이지")
    @GetMapping("/reservation-mine")
    public String reservationMinePage() {
        return "reservation-mine";
    }
}
