package roomescape.unit.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.auth.LoginInfo;
import roomescape.business.model.entity.Member;
import roomescape.business.model.entity.Reservation;
import roomescape.business.model.entity.Theme;
import roomescape.business.model.entity.TimeSlot;
import roomescape.business.model.vo.Id;
import roomescape.business.service.PaymentService;
import roomescape.business.service.ReservationService;
import roomescape.exception.business.DuplicatedException;
import roomescape.exception.business.NotFoundException;
import roomescape.infrastructure.MemberRepository;
import roomescape.infrastructure.ReservationRepository;
import roomescape.infrastructure.ReservationTimeRepository;
import roomescape.infrastructure.ThemeRepository;
import roomescape.infrastructure.WaitingRepository;
import roomescape.presentation.dto.request.AdminReservationRequest;
import roomescape.presentation.dto.request.ReservationCondition;
import roomescape.presentation.dto.request.ReservationRequest;
import roomescape.presentation.dto.response.MemberResponse;
import roomescape.presentation.dto.response.ReservationResponse;
import roomescape.presentation.dto.response.ThemeResponse;
import roomescape.presentation.dto.response.TimeSlotResponse;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    private final PaymentService paymentService = mock(PaymentService.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final ReservationRepository reservationRepository = mock(ReservationRepository.class);
    private final ReservationTimeRepository reservationTimeRepository = mock(ReservationTimeRepository.class);
    private final ThemeRepository themeRepository = mock(ThemeRepository.class);
    private final WaitingRepository waitingRepository = mock(WaitingRepository.class);
    private final ReservationService sut;

    public ReservationServiceTest() {
        this.sut = new ReservationService(
                paymentService,
                memberRepository,
                reservationRepository,
                reservationTimeRepository,
                themeRepository,
                waitingRepository
        );
    }

    @Test
    void 존재하지_않는_사용자_ID로_예약_시_예외가_발생한다() {
        // given
        LocalDate date = LocalDate.now().plusDays(1);
        String timeId = "time-id";
        String themeId = "theme-id";
        String userIdValue = "nonexistent-id";
        Id userId = Id.create(userIdValue);
        AdminReservationRequest request = new AdminReservationRequest(date, timeId, themeId, userIdValue);
        when(memberRepository.findById(userId)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(
                () -> sut.addAndGetWithoutPayment(request))
                .isInstanceOf(NotFoundException.class);

        verify(memberRepository).findById(userId);
        verifyNoInteractions(reservationTimeRepository);
        verifyNoInteractions(themeRepository);
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void 존재하지_않는_예약_시간_ID로_예약_시_예외가_발생한다() {
        // given
        LocalDate date = LocalDate.now().plusDays(1);
        String timeId = "nonexistent-time-id";
        String themeId = "theme-id";
        String userId = "user-id";
        AdminReservationRequest request = new AdminReservationRequest(date, timeId, themeId, userId);
        Member member = Member.restore(userId, "USER", "Test User", "test@example.com", "password");

        when(memberRepository.findById(Id.create(userId))).thenReturn(Optional.of(member));
        when(reservationTimeRepository.findById(Id.create(timeId))).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.addAndGetWithoutPayment(request))
                .isInstanceOf(NotFoundException.class);

        verify(memberRepository).findById(Id.create(userId));
        verify(reservationTimeRepository).findById(Id.create(timeId));
        verifyNoInteractions(themeRepository);
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void 존재하지_않는_테마_ID로_예약_시_예외가_발생한다() {
        // given
        LocalDate date = LocalDate.now().plusDays(1);
        String timeIdValue = "time-id";
        String themeIdValue = "nonexistent-theme-id";
        String userIdValue = "user-id";
        Id timeId = Id.create(timeIdValue);
        Id themeId = Id.create(themeIdValue);
        Id userId = Id.create(userIdValue);
        AdminReservationRequest request = new AdminReservationRequest(date, timeIdValue, themeIdValue, userIdValue);
        Member member = Member.restore(userIdValue, "USER", "Test User", "test@example.com", "password");
        TimeSlot timeSlot = TimeSlot.restore(timeIdValue, LocalTime.of(10, 0));

        when(memberRepository.findById(userId)).thenReturn(Optional.of(member));
        when(reservationTimeRepository.findById(timeId)).thenReturn(Optional.of(timeSlot));
        when(themeRepository.findById(themeId)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.addAndGetWithoutPayment(request))
                .isInstanceOf(NotFoundException.class);

        verify(memberRepository).findById(userId);
        verify(reservationTimeRepository).findById(timeId);
        verify(themeRepository).findById(themeId);
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void 이미_예약된_날짜_시간_테마로_예약_시_예외가_발생한다() {
        // given
        LocalDate date = LocalDate.now().plusDays(1);
        String timeIdValue = "time-id";
        String themeIdValue = "theme-id";
        String userIdValue = "user-id";
        Id timeId = Id.create(timeIdValue);
        Id themeId = Id.create(themeIdValue);
        Id userId = Id.create(userIdValue);
        AdminReservationRequest request = new AdminReservationRequest(date, timeIdValue, themeIdValue, userIdValue);
        Member member = Member.restore(userIdValue, "USER", "Test User", "test@example.com", "password");
        TimeSlot timeSlot = TimeSlot.restore(timeIdValue, LocalTime.of(10, 0));
        Theme theme = Theme.restore(themeIdValue, "Test Theme", "Description", "thumbnail.jpg");

        when(memberRepository.findById(userId)).thenReturn(Optional.of(member));
        when(reservationTimeRepository.findById(timeId)).thenReturn(Optional.of(timeSlot));
        when(themeRepository.findById(themeId)).thenReturn(Optional.of(theme));
        when(reservationRepository.existsByDate_ValueAndTimeSlot_StartAtAndThemeId(eq(date), any(LocalTime.class),
                eq(theme.getId())))
                .thenReturn(true);

        // when, then
        assertThatThrownBy(() -> sut.addAndGetWithoutPayment(request))
                .isInstanceOf(DuplicatedException.class);

        verify(memberRepository).findById(userId);
        verify(reservationTimeRepository).findById(timeId);
        verify(themeRepository).findById(themeId);
        verify(reservationRepository).existsByDate_ValueAndTimeSlot_StartAtAndThemeId(eq(date),
                any(LocalTime.class), eq(theme.getId()));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void 필터를_적용하여_모든_예약을_조회할_수_있다() {
        // given
        String themeIdValue = "theme-id";
        String userIdValue = "user-id";
        Id themeId = Id.create(themeIdValue);
        Id userId = Id.create(userIdValue);
        LocalDate dateFrom = LocalDate.now();
        LocalDate dateTo = LocalDate.now().plusDays(7);
        ReservationCondition condition = new ReservationCondition(themeIdValue, userIdValue, dateFrom,
                dateTo);

        Member member1 = Member.restore("user-id-1", "USER", "User One", "user1@example.com", "password1");
        Member member2 = Member.restore("user-id-2", "USER", "User Two", "user2@example.com", "password2");
        TimeSlot time1 = TimeSlot.restore("time-id-1", LocalTime.of(10, 0));
        TimeSlot time2 = TimeSlot.restore("time-id-2", LocalTime.of(14, 0));
        Theme theme1 = Theme.restore("theme-id-1", "Theme One", "Description One", "thumbnail1.jpg");
        Theme theme2 = Theme.restore("theme-id-2", "Theme Two", "Description Two", "thumbnail2.jpg");
        List<Reservation> reservationData = Arrays.asList(
                Reservation.restore("reservation-id-1", member1, dateFrom, time1, theme1),
                Reservation.restore("reservation-id-2", member2, dateFrom.plusDays(1), time2, theme2));
        List<ReservationResponse> expectedReservations = Arrays.asList(
                new ReservationResponse("reservation-id-1", MemberResponse.from(member1),
                        dateFrom, TimeSlotResponse.from(time1), ThemeResponse.from(theme1)),
                new ReservationResponse("reservation-id-2", MemberResponse.from(member2),
                        dateFrom.plusDays(1), TimeSlotResponse.from(time2),
                        ThemeResponse.from(theme2))
        );

        when(reservationRepository.findAllReservationWithFilter(themeId, userId, dateFrom, dateTo))
                .thenReturn(reservationData);

        // when
        List<ReservationResponse> result = sut.findAllReservations(condition);

        // then
        assertThat(result).isEqualTo(expectedReservations);
        verify(reservationRepository).findAllReservationWithFilter(themeId, userId, dateFrom, dateTo);
    }

    @Test
    void 예약을_생성하고_결제승인_요청을_보낸다() {
        // given
        LocalDate date = LocalDate.now().plusDays(1);
        String timeIdValue = "time-id";
        String themeIdValue = "theme-id";
        String userIdValue = "user-id";
        Id timeId = Id.create(timeIdValue);
        Id themeId = Id.create(themeIdValue);
        Id userId = Id.create(userIdValue);

        Member member = Member.restore(userIdValue, "USER", "Test User", "test@example.com", "password");
        TimeSlot timeSlot = TimeSlot.restore(timeIdValue, LocalTime.of(10, 0));
        Theme theme = Theme.restore(themeIdValue, "Test Theme", "Description", "thumbnail.jpg");
        LoginInfo loginInfo = new LoginInfo(userIdValue, member.getRole());
        ReservationRequest request = new ReservationRequest(date, timeIdValue, themeIdValue, "paymentKey",
                "orderId", 1000L, "paymentType");

        when(memberRepository.findById(userId)).thenReturn(Optional.of(member));
        when(reservationTimeRepository.findById(timeId)).thenReturn(Optional.of(timeSlot));
        when(themeRepository.findById(themeId)).thenReturn(Optional.of(theme));
        when(reservationRepository.existsByDate_ValueAndTimeSlot_StartAtAndThemeId(eq(date),
                eq(LocalTime.of(10, 0)), eq(theme.getId())))
                .thenReturn(false);
        when(reservationRepository.save(any(Reservation.class)))
                .thenReturn(Reservation.restore("id", member, date, timeSlot, theme));
        // when
        ReservationResponse result = sut.addAndGet(loginInfo, request);

        // then
        assertThat(result.id()).isEqualTo("id");
        assertThat(result.date()).isEqualTo(date);
        verify(memberRepository).findById(userId);
        verify(reservationTimeRepository).findById(timeId);
        verify(themeRepository).findById(themeId);
        verify(reservationRepository).existsByDate_ValueAndTimeSlot_StartAtAndThemeId(eq(date),
                any(LocalTime.class), eq(theme.getId()));
        verify(reservationRepository).save(any(Reservation.class));
    }
}
