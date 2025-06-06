package roomescape.ui;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class MemberController {

    @GetMapping("/reservation")
    public String getReservationPage() {
        log.info("[MEMBER] 예약 페이지 진입");
        return "reservation";
    }

    @GetMapping("/reservation-mine")
    public String getMyReservationPage() {
        log.info("[MEMBER] 내 예약 페이지 진입");
        return "reservation-mine";
    }

    @GetMapping("/sign-in")
    public String getSignInPage() {
        log.info("[MEMBER] 로그인 페이지 진입");
        return "signin";
    }

    @GetMapping("/sign-up")
    public String getSignUpPage() {
        log.info("[MEMBER] 회원가입 페이지 진입");
        return "signup";
    }
}
