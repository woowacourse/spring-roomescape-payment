package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createDefaultMember_1;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createMemberByName;
import static roomescape.TestFixture.createReservationOf;
import static roomescape.TestFixture.createTimeAt_10;
import static roomescape.TestFixture.createWaitingOf;

import java.time.LocalDate;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import roomescape.DBHelper;
import roomescape.auth.dto.LoginMember;
import roomescape.common.event.EventPublisher;
import roomescape.exception.NotFoundException;
import roomescape.exception.ReservationException;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.WaitingReservation;
import roomescape.reservation.dto.WaitingReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.WaitingReservationRepository;
import roomescape.reservation.service.dto.CreateRegistrationCommand;
import roomescape.reservation.service.dto.WaitingApprovedEvent;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

@SpringBootTest
@Transactional
class WaitingReservationServiceTest {

    @Autowired
    private WaitingReservationService service;

    @Autowired
    private WaitingReservationRepository waitingReservationRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @MockitoBean
    EventPublisher eventPublisher;

    @Autowired
    DBHelper dbHelper;

    @Nested
    @DisplayName("대기 등록")
    class RegisterWaitingReservation {
        @Test
        void 대기_예약이_정상적으로_저장된다() {
            // given
            Member member = dbHelper.insertMember(createDefaultMember_1());
            LocalDate date = DEFAULT_DATE;
            Theme theme = dbHelper.insertTheme(createDefaultTheme());
            ReservationTime time = dbHelper.insertTime(createTimeAt_10());

            // when
            WaitingReservationResponse response = service.registerWaitingReservation(
                    new CreateRegistrationCommand(member.getId(), date, time.getId(), theme.getId())
            );

            // then
            SoftAssertions.assertSoftly(soft -> {
                soft.assertThat(response.id()).isNotNull();
                soft.assertThat(response.date()).isEqualTo(date);
                soft.assertThat(response.member().name()).isEqualTo(member.getName());
                soft.assertThat(response.theme().name()).isEqualTo(theme.getName());
            });
        }

        @Test
        void 존재하지_않는_회원으로_대기_예약시_예외발생() {
            // given
            Theme theme = dbHelper.insertTheme(createDefaultTheme());
            ReservationTime time = dbHelper.insertTime(createTimeAt_10());

            Long nonExistsMemberId = 999L;

            // when & then
            assertThatThrownBy(() -> service.registerWaitingReservation(
                    new CreateRegistrationCommand(nonExistsMemberId, DEFAULT_DATE, time.getId(), theme.getId())
            )).isInstanceOf(NotFoundException.class)
                    .hasMessage("존재하지 않는 멤버입니다.");
        }

        @DisplayName("존재하지 않는 테마로 대기 예약시 예외 발생")
        @Test
        void registerWaitingReservation_withNonExistentTheme() {
            // given
            ReservationTime time = dbHelper.insertTime(createTimeAt_10());
            Member member = dbHelper.insertMember(createDefaultMember_1());

            Long nonExistsThemeId = 9999L;

            // when & then
            assertThatThrownBy(() -> service.registerWaitingReservation(
                    new CreateRegistrationCommand(member.getId(), DEFAULT_DATE, time.getId(), nonExistsThemeId)
            )).isInstanceOf(NotFoundException.class)
                    .hasMessage("존재하지 않는 테마입니다.");
        }

        @DisplayName("존재하지 않는 시간으로 대기 예약시 예외 발생")
        @Test
        void registerWaitingReservation_withNonExistentTime() {
            // given
            Member member = dbHelper.insertMember(createDefaultMember_1());
            Theme theme = dbHelper.insertTheme(createDefaultTheme());

            Long nonExistsTimeId = 9999L;

            // when & then
            assertThatThrownBy(() -> {
                service.registerWaitingReservation(
                        new CreateRegistrationCommand(member.getId(), DEFAULT_DATE, nonExistsTimeId, theme.getId())
                );
            }).isInstanceOf(NotFoundException.class)
                    .hasMessage("존재하지 않는 시간입니다.");
        }

        @DisplayName("같은 사람이 같은 슬롯에 중복 대기 시 예외 발생")
        @Test
        void registerWaitingReservation_duplicateWaiting() {
            // given
            LocalDate date = DEFAULT_DATE;
            Theme theme = dbHelper.insertTheme(createDefaultTheme());
            ReservationTime time = dbHelper.insertTime(createTimeAt_10());
            Member member = dbHelper.insertMember(createDefaultMember_1());

            // 첫 번째 대기 등록
            dbHelper.insertWaiting(createWaitingOf(member, date, time, theme));

            // when & then
            assertThatThrownBy(() -> service.registerWaitingReservation(
                    new CreateRegistrationCommand(member.getId(), date, time.getId(), theme.getId())
            )).isInstanceOf(ReservationException.class)
                    .hasMessageContaining("사용자는 이미 해당 날짜에 예약 또는 대기했습니다.");
        }

        @DisplayName("과거 시간으로 대기 시 예외 발생")
        @Test
        void registerWaitingReservation_pastDate() {
            // given
            Theme theme = dbHelper.insertTheme(createDefaultTheme());
            ReservationTime time = dbHelper.insertTime(createTimeAt_10());
            Member member = dbHelper.insertMember(createDefaultMember_1());

            LocalDate pastDate = LocalDate.now().minusDays(1);

            // when & then
            assertThatThrownBy(() -> service.registerWaitingReservation(
                    new CreateRegistrationCommand(member.getId(), pastDate, time.getId(), theme.getId())
            )).isInstanceOf(ReservationException.class)
                    .hasMessageContaining("지난 날짜에 대한 대기입니다.");
        }
    }

    @Nested
    @DisplayName("대기 승인")
    class ApproveWaitingReservation {
        @DisplayName("승인 가능한 대기를 승인하면 Reservation으로 변환되고, 대기는 삭제되며 결제 취소 이벤트가 발행된다.")
        @Test
        void approveWaitingReservation_success() {
            // given
            LocalDate date = DEFAULT_DATE;
            Theme theme = dbHelper.insertTheme(createDefaultTheme());
            ReservationTime time = dbHelper.insertTime(createTimeAt_10());
            Member member = dbHelper.insertMember(createDefaultMember_1());

            WaitingReservation waitingReservation = dbHelper.insertWaiting(createWaitingOf(member, date, time, theme));
            Long waitingId = waitingReservation.getId();

            // when
            service.approveWaitingReservation(waitingId);

            // then
            assertThat(waitingReservationRepository.findById(waitingId)).isEmpty();

            Reservation approvedReservation = reservationRepository.findAll().getFirst();
            SoftAssertions.assertSoftly(soft -> {
                soft.assertThat(approvedReservation.getMember().getId()).isEqualTo(member.getId());
                soft.assertThat(approvedReservation.getDate()).isEqualTo(date);
                soft.assertThat(approvedReservation.getTime().getId()).isEqualTo(time.getId());
                soft.assertThat(approvedReservation.getTheme().getId()).isEqualTo(theme.getId());
            });

            verify(eventPublisher).raise(any(WaitingApprovedEvent.class));
        }

        @DisplayName("존재하지 않는 대기 ID로 승인 요청 시 예외 발생")
        @Test
        void approveWaitingReservation_notFound() {
            // given
            Long nonExistentWaitingId = 9999L;

            // when & then
            assertThatThrownBy(() -> service.approveWaitingReservation(nonExistentWaitingId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Waiting을 찾지 못했습니다");
        }

        @DisplayName("대기 승인을 원하는 슬롯에 이미 예약이 있을 시 예외 발생")
        @Test
        void approveWaitingReservation_alreadyReserved() {
            // given
            Theme theme = dbHelper.insertTheme(createDefaultTheme());
            ReservationTime time = dbHelper.insertTime(createTimeAt_10());
            Member member1 = dbHelper.insertMember(createMemberByName("회원1"));
            Member member2 = dbHelper.insertMember(createMemberByName("회원2"));

            // 같은 슬롯의 예약 + 대기 생성
            Reservation reservation = dbHelper.insertReservation(
                    createReservationOf(member1, DEFAULT_DATE, time, theme));
            WaitingReservation waitingReservation = dbHelper.insertWaiting(
                    createWaitingOf(member2, DEFAULT_DATE, time, theme));

            // when & then
            assertThatThrownBy(() -> service.approveWaitingReservation(waitingReservation.getId()))
                    .isInstanceOf(ReservationException.class)
                    .hasMessageContaining("이미 해당 날짜에 예약이 존재합니다.");
        }
    }

    @Nested
    @DisplayName("대기 취소")
    class CancelWaitingReservation {
        @DisplayName("회원이 자신의 대기를 취소하면 대기가 삭제된다.")
        @Test
        void cancelWaitingByIdForMember_success() {
            // given
            LocalDate date = DEFAULT_DATE;
            Theme theme = dbHelper.insertTheme(createDefaultTheme());
            ReservationTime time = dbHelper.insertTime(createTimeAt_10());
            Member member = dbHelper.insertMember(createDefaultMember_1());
            LoginMember loginMember = LoginMember.from(member);

            WaitingReservation waitingReservation = dbHelper.insertWaiting(createWaitingOf(member, date, time, theme));
            Long waitingId = waitingReservation.getId();

            // when
            service.cancelWaitingByIdForMember(waitingId, loginMember);

            // then
            assertThat(waitingReservationRepository.findById(waitingId)).isEmpty();
        }

        @DisplayName("관리자가 대기를 거절하면 대기가 삭제된다.")
        @Test
        void denyWaitingByIdForAdmin_success() {
            // given
            LocalDate date = DEFAULT_DATE;
            Theme theme = dbHelper.insertTheme(createDefaultTheme());
            ReservationTime time = dbHelper.insertTime(createTimeAt_10());
            Member member = dbHelper.insertMember(createDefaultMember_1());

            WaitingReservation waitingReservation = dbHelper.insertWaiting(createWaitingOf(member, date, time, theme));
            Long waitingId = waitingReservation.getId();

            // when
            service.denyWaitingByIdForAdmin(waitingId);

            // then
            assertThat(waitingReservationRepository.findById(waitingId)).isEmpty();
        }

        @DisplayName("회원이 타인의 대기를 취소할 시 예외가 발생한다.")
        @Test
        void cancelWaitingByIdForMember_otherMember() {
            // given
            LocalDate date = DEFAULT_DATE;
            Theme theme = dbHelper.insertTheme(createDefaultTheme());
            ReservationTime time = dbHelper.insertTime(createTimeAt_10());
            Member member1 = dbHelper.insertMember(createMemberByName("회원1"));
            Member member2 = dbHelper.insertMember(createMemberByName("회원2"));
            LoginMember loginMember2 = LoginMember.from(member2);

            WaitingReservation waitingReservation = dbHelper.insertWaiting(createWaitingOf(member1, date, time, theme));
            Long waitingId = waitingReservation.getId();

            // when & then
            assertThatThrownBy(() -> service.cancelWaitingByIdForMember(waitingId, loginMember2))
                    .isInstanceOf(ReservationException.class)
                    .hasMessageContaining("자신의 예약만 삭제할 수 있습니다");
        }

        @DisplayName("존재하지 않는 대기 ID로 취소 요청 시 예외 발생")
        @Test
        void cancelWaitingById_notFound() {
            // given
            Long nonExistentWaitingId = 9999L;
            Member member = dbHelper.insertMember(createDefaultMember_1());
            LoginMember loginMember = LoginMember.from(member);

            // when & then
            assertThatThrownBy(() -> service.cancelWaitingByIdForMember(nonExistentWaitingId, loginMember))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Waiting을 찾지 못했습니다");
        }
    }
}
