package roomescape.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberPageController {

    @GetMapping("/reservation")
    public String findReservationPage() {
        return "reservation";
    }

    @GetMapping("/member/reservation")
    public String findMyReservationPage() {
        return "reservation-mine";
    }

    @GetMapping("/login")
    public String findLoginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String findSignUpPage() {
        return "signup";
    }
}