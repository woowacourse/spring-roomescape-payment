package roomescape.view;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "일반 페이지 API", description = "일반 페이지 관련 API")
@Controller
public class ViewController {

    @Operation(summary = "메인 페이지 API")
    @GetMapping("/")
    public String getMainPage() {
        return "index";
    }

    @Operation(summary = "로그인 페이지 API")
    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @Operation(summary = "예약 페이지 API")
    @GetMapping("/reservation")
    public String getReservationPage() {
        return "reservation";
    }

    @Operation(summary = "사용자 예약 목록 페이지 API")
    @GetMapping("/reservation-mine")
    public String getReservationMinePage() {
        return "reservation-mine";
    }
}
