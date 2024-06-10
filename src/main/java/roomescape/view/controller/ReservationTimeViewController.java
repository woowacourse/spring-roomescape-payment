package roomescape.view.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "예약 시간 뷰 컨트롤러", description = "예약 시간 뷰 제공")
@Controller
public class ReservationTimeViewController {
    @GetMapping("/time")
    public String reservationTimePage() {
        return "admin/time";
    }
}
