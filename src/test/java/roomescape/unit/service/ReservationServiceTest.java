package roomescape.unit.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import roomescape.dto.request.AddReservationRequest;
import roomescape.dto.request.CreateWaitReservationRequest;
import roomescape.dto.request.LoginMemberRequest;
import roomescape.dto.response.MyReservationResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.ReservationWaitResponse;
import roomescape.entity.Member;
import roomescape.entity.Reservation;
import roomescape.entity.ReservationTime;
import roomescape.entity.Theme;
import roomescape.exception.custom.InvalidReservationException;
import roomescape.global.ReservationStatus;
import roomescape.global.Role;
import roomescape.repository.MemberRepository;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.service.ReservationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ReservationServiceTest {

        @Autowired
        private ReservationService reservationService;

        @Autowired
        private MemberRepository memberRepository;

        @Autowired
        private ReservationRepository reservationRepository;

        @Autowired
        private ReservationTimeRepository reservationTimeRepository;

        @Autowired
        private ThemeRepository themeRepository;

        @Autowired
        private PaymentRepository paymentRepository;

        private Member member;
        private LoginMemberRequest loginMemberRequest;
        private ReservationTime time;
        private Theme theme;

        @BeforeEach
        void setup() {
                member = new Member("test", "test@email.com", "1234", Role.USER);
                memberRepository.save(member);

                loginMemberRequest = new LoginMemberRequest(member.getId(), member.getName(), member.getRole());
                time = new ReservationTime(LocalTime.of(10, 0));
                reservationTimeRepository.save(time);
                theme = new Theme("name", "description", "thumbnail");
                themeRepository.save(theme);
        }

        @Test
        void 예약_전체를_조회할_수_있다() {
                // given
                Member member2 = new Member("test2", "test2@email.com", "1234", Role.USER);
                memberRepository.save(member2);

                Reservation reservation = new Reservation(member, LocalDate.now(), time, theme,
                                ReservationStatus.RESERVED);
                reservationRepository.save(reservation);

                Reservation reservation2 = new Reservation(member2, LocalDate.now(), time, theme,
                                ReservationStatus.RESERVED);
                reservationRepository.save(reservation2);

                // when
                List<ReservationResponse> actual = reservationService.findAll();

                // then
                assertThat(actual).hasSize(2);
                assertThat(actual).extracting("name").containsExactlyInAnyOrder("test", "test2");
        }

        @Test
        void 예약을_추가한다() {
                // given
                AddReservationRequest request = new AddReservationRequest(LocalDate.now().plusDays(1),
                                time.getId(), theme.getId());

                // when
                ReservationResponse actual = reservationService.addReservationByMember(request, loginMemberRequest);

                // then
                assertAll(
                                () -> assertThat(actual.time()).isEqualTo(time.getStartAt()),
                                () -> assertThat(actual.themeName()).isEqualTo(theme.getName()),
                                () -> assertThat(actual.name()).isEqualTo(member.getName()));
        }

        @Test
        void 예약을_삭제할_수_있다() {
                // given
                Reservation reservation = new Reservation(member, LocalDate.of(3000, 1, 1), time, theme,
                                ReservationStatus.RESERVED);
                reservationRepository.save(reservation);

                // when
                reservationService.deleteReservation(reservation.getId());

                // then
                assertThat(member.getReservations()).doesNotContain(reservation);
                assertThat(reservation.getMember()).isNull();
        }

        @Test
        void 중복_예약은_불가능하다() {
                // given
                LocalDate targetDate = LocalDate.of(3000, 1, 1);
                member.reserve(targetDate, time, theme, ReservationStatus.RESERVED);
                memberRepository.save(member);

                // when & then
                assertThatThrownBy(() -> reservationService.addReservationByMember(
                                new AddReservationRequest(targetDate, time.getId(), theme.getId()), loginMemberRequest))
                                .isInstanceOf(InvalidReservationException.class);
        }

        @Test
        void 대상_유저의_예약_전체를_조회할_수_있다() {
                // given
                LocalDate date = LocalDate.of(3000, 1, 1);

                Reservation reservation = new Reservation(member, date, time, theme, ReservationStatus.RESERVED);
                reservationRepository.save(reservation);

                AddReservationRequest request = new AddReservationRequest(date, time.getId(), theme.getId());
                reservationService.addReservationByMember(request, loginMemberRequest);

                // when
                List<MyReservationResponse> actual = reservationService.findAllReservationOfMember(member.getId());

                // then
                assertThat(actual).hasSize(1);
        }

        @Test
        void 예약_대기를_추가한다() {
                // given
                CreateWaitReservationRequest request = new CreateWaitReservationRequest(LocalDate.now().plusDays(1),
                                time.getId(), theme.getId());

                // when
                ReservationWaitResponse actual = reservationService.addWaitReservation(request, loginMemberRequest);

                // then
                assertAll(
                                () -> assertThat(actual.startAt()).isEqualTo(time.getStartAt()),
                                () -> assertThat(actual.theme()).isEqualTo(theme.getName()),
                                () -> assertThat(actual.name()).isEqualTo(member.getName()));
        }

        @Test
        void 예약_대기를_삭제할_수_있다() {
                // given
                Reservation reservation = new Reservation(member, LocalDate.of(3000, 1, 1), time, theme,
                                ReservationStatus.WAIT);
                reservationRepository.save(reservation);

                // when
                reservationService.deleteReservation(reservation.getId());

                // then
                assertThat(member.getReservations()).doesNotContain(reservation);
                assertThat(reservation.getMember()).isNull();
        }

        @Test
        void 중복_예약_대기는_불가능하다() {
                // given
                LocalDate targetDate = LocalDate.of(3000, 1, 1);
                member.reserve(targetDate, time, theme, ReservationStatus.WAIT);
                memberRepository.save(member);

                // when & then
                assertThatThrownBy(() -> reservationService.addWaitReservation(
                                new CreateWaitReservationRequest(targetDate, time.getId(), theme.getId()),
                                loginMemberRequest))
                                .isInstanceOf(InvalidReservationException.class);
        }

        @Test
        void 기존_예약_삭제_시_첫번째_대기가_예약된다() {
                // given
                Member reserved = new Member("reserved", "reserved", "1234", Role.USER);
                memberRepository.save(reserved);

                LocalDate date = LocalDate.of(3000, 1, 1);

                Reservation reserve = reserved.reserve(date, time, theme, ReservationStatus.RESERVED);
                reservationRepository.save(reserve);

                Reservation wait = member.reserve(date, time, theme, ReservationStatus.WAIT);
                reservationRepository.save(wait);

                // when
                reservationService.deleteReservation(reserve.getId());

                // then
                assertAll(
                                () -> assertThat(member.getReservations()).contains(wait),
                                () -> assertThat(wait.getMember()).isEqualTo(member),
                                () -> assertThat(wait.getStatus()).isEqualTo(ReservationStatus.RESERVED));
        }
}
