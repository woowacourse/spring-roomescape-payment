package roomescape.view.ui;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicController {

    @GetMapping
    @Operation(summary = "홈페이지")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    @Operation(summary = "로그인 페이지")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    @Operation(summary = "회원가입 페이지")
    public String signUp() {
        return "signup";
    }

    @GetMapping("/reservation")
    @Operation(summary = "예약 페이지")
    public String reservation() {
        return "reservation";
    }

    @GetMapping("/reservation-mine")
    @Operation(summary = "내 예약 페이지")
    public String reservationMine() {
        return "reservation-mine";
    }

    @GetMapping("/waiting-mine")
    @Operation(summary = "내 예약 대기 페이지")
    public String waitingMine() {
        return "waiting-mine";
    }
}
