package roomescape.service.member;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.fixture.MemberFixture.createGuest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
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
import roomescape.service.member.dto.MemberResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql("/truncate.sql")
class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationDetailRepository reservationDetailRepository;

    @DisplayName("존재하는 모든 사용자를 조회한다.")
    @Test
    void findAll() {
        //given
        memberRepository.save(createGuest());

        //when
        List<MemberResponse> memberResponses = memberService.findAll();

        //then
        assertThat(memberResponses).hasSize(1);
    }

    @DisplayName("id로 사용자를 조회한다.")
    @Test
    void findById() {
        //given
        Member member = memberRepository.save(createGuest());

        //when
        Member result = memberService.findById(member.getId());

        //then
        assertThat(result.getEmail()).isEqualTo(member.getEmail());
    }

    @DisplayName("사용자 본인의 모든 예약 및 대기 정보를 조회한다.")
    @Test
    void findReservations() {
        //given
        Member member = memberRepository.save(createGuest("lini", "lini@email.com", "lini123"));
        Member member2 = memberRepository.save(createGuest("pedro", "pedro@email.com", "pedro123"));
        ReservationDetail reservationDetail = createReservationDetail();
        Reservation reservation = new Reservation(member, reservationDetail, ReservationStatus.RESERVED, Payment.createEmpty());
        Reservation reservation2 = new Reservation(member, reservationDetail, ReservationStatus.WAITING, Payment.createEmpty());
        Reservation reservation3 = new Reservation(member2, reservationDetail, ReservationStatus.WAITING, Payment.createEmpty());
        reservationRepository.save(reservation);
        reservationRepository.save(reservation2);
        reservationRepository.save(reservation3);
    }

    private ReservationDetail createReservationDetail() {
        ReservationDate reservationDate = ReservationDate.of(LocalDate.now().plusDays(1));
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(10, 0)));
        Theme theme = themeRepository.save(new Theme("레벨2 탈출", "우테코 레벨2를 탈출하는 내용입니다.",
            "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"));
        return reservationDetailRepository.save(new ReservationDetail(new Schedule(reservationDate, reservationTime), theme));
    }
}
