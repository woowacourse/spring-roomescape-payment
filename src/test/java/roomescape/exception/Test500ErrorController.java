package roomescape.exception;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test-error")
@Profile("test")
public class Test500ErrorController {

    @GetMapping("/500")
    public void triggerServerError() {
        throw new RuntimeException("테스트용 의도된 서버 에러입니다.");
    }
}
