package roomescape.service.waiting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.member.Role;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.reservationdetail.ReservationDetailRepository;
import roomescape.domain.schedule.ReservationDate;
import roomescape.domain.schedule.ReservationTime;
import roomescape.domain.schedule.ReservationTimeRepository;
import roomescape.domain.schedule.Schedule;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.exception.InvalidReservationException;
import roomescape.service.reservation.dto.ReservationResponse;
import roomescape.service.waiting.dto.WaitingRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql("/truncate.sql")
class WaitingCreateServiceTest {

    @Autowired
    private WaitingCreateService waitingCreateService;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReservationDetailRepository reservationDetailRepository;
    private ReservationDetail reservationDetail;
    private Theme theme;
    private Member member;
    private Member anotherMember;

    @BeforeEach
    void setUp() {
        ReservationDate reservationDate = ReservationDate.of(LocalDate.now().plusDays(1));
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(10, 0)));
        theme = themeRepository.save(new Theme("레벨2 탈출", "우테코 레벨2를 탈출하는 내용입니다.",
            "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"));
        member = memberRepository.save(new Member("lini", "lini@email.com", "lini123", Role.GUEST));
        anotherMember = memberRepository.save(new Member("pedro", "pedro@email.com", "pedro123", Role.GUEST));
        reservationDetail = reservationDetailRepository.save(new ReservationDetail(new Schedule(reservationDate, reservationTime), theme));
    }

    @DisplayName("사용자가 새로운 예약 대기를 저장한다.")
    @Test
    void createMemberWaiting() {
        //given
        WaitingRequest waitingRequest = new WaitingRequest(reservationDetail.getDate(),
            reservationDetail.getReservationTime().getId(), theme.getId());
        reservationRepository.save(new Reservation(anotherMember, reservationDetail, ReservationStatus.RESERVED, Payment.createEmpty()));

        //when
        ReservationResponse result = waitingCreateService.createWaiting(waitingRequest, member.getId());

        //then
        assertAll(
            () -> assertThat(result.id()).isNotZero(),
            () -> assertThat(result.time().id()).isEqualTo(reservationDetail.getReservationTime().getId()),
            () -> assertThat(result.theme().id()).isEqualTo(theme.getId()),
            () -> assertThat(result.status()).isEqualTo(ReservationStatus.WAITING.getDescription())
        );
    }

    @DisplayName("사용자가 이미 예약인 상태에서 예약 대기 요청을 한다면 예외가 발생한다.")
    @Test
    void cannotCreateByExistingMemberWaiting() {
        //given
        WaitingRequest waitingRequest = new WaitingRequest(reservationDetail.getDate(),
            reservationDetail.getReservationTime().getId(), theme.getId());
        reservationRepository.save(new Reservation(member, reservationDetail, ReservationStatus.RESERVED, Payment.createEmpty()));

        //when & then
        assertThatThrownBy(() -> waitingCreateService.createWaiting(waitingRequest, member.getId()))
            .isInstanceOf(InvalidReservationException.class)
            .hasMessage("이미 예약(대기) 상태입니다.");
    }

}
