package roomescape.view;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Tag(name = "ClientView API", description = "사용자 화면 관련 API")
public class ClientViewController {

    @Operation(summary = "사용자 예약 페이지 연결 API")
    @GetMapping("/reservation")
    public String reservationPage() {
        return "reservation";
    }

    @Operation(summary = "사용자 로그인 페이지 연결 API")
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @Operation(summary = "사용자 회원가입 페이지 연결 API")
    @GetMapping("/signup")
    public String signUpPage() {
        return "signup";
    }

    @Operation(summary = "로그인한 사용자의 예약 관리 페이지 연결 API")
    @GetMapping("/reservation-mine")
    public String myReservationPage() {
        return "reservation-mine";
    }
}
