package roomescape.service.booking.waiting.module;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.waiting.Waiting;

@Sql("/waiting-test-data.sql")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class WaitingSearchServiceTest {

    @Autowired
    WaitingSearchService waitingSearchService;

    @Test
    void 모든_예약_대기_조회() {
        //when
        List<Waiting> allWaitingReservations = waitingSearchService.findAllWaitingReservations();

        //then
        assertThat(allWaitingReservations).hasSize(2);
    }

    @Test
    void 예약_id에_해당하는_대기_조회() {
        // when
        List<Waiting> waitings = waitingSearchService.findWaitingByReservationIds(List.of(2L, 3L));

        // then
        assertThat(waitings).hasSize(2);
    }
}
