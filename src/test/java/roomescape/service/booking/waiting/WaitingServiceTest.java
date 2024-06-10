package roomescape.service.booking.waiting;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.waiting.Waiting;
import roomescape.repository.WaitingRepository;

@Sql("/waiting-test-data.sql")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class WaitingServiceTest {

    @Autowired
    WaitingService waitingService;

    @Autowired
    WaitingRepository waitingRepository;

    @Test
    void 예약_대기_취소후_자동으로_다음_대기번호_변경() {
        //when
        waitingService.cancelWaiting(1L);

        //then
        Waiting waiting = waitingRepository.findById(2L).orElseThrow();
        assertThat(waiting.getWaitingOrderValue()).isEqualTo(1L);
    }
}
