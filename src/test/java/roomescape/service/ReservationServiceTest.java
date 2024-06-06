package roomescape.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.domain.*;
import roomescape.dto.LoginMember;
import roomescape.dto.request.MemberReservationRequest;import roomescape.dto.response.ReservationResponse;
import roomescape.repository.ReservationRepository;
import roomescape.repository.TimeSlotRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static roomescape.fixture.MemberFixture.ADMIN_MEMBER;
import static roomescape.fixture.ThemeFixture.THEME_ONE;
import static roomescape.fixture.TimeSlotFixture.TIME_ONE;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private MemberService memberService;

    @Mock
    private TimeService timeService;

    @Mock
    private ThemeService themeService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private ReservationService reservationService;

    @DisplayName("Client는 결제 후 예약을 추가할 수 있다.")
    @Test
    void createdByClient(){
        Member mockMember = ADMIN_MEMBER;
        TimeSlot mockTimeSlot = TIME_ONE;
        Theme mockTheme = THEME_ONE;

        MemberReservationRequest reservationRequest =
                new MemberReservationRequest(LocalDate.now().plusDays(1), 1L, 1L, "paymentKey", "orderId", 1L);
        LoginMember loginMember = new LoginMember(1L);
        Reservation reservation = new Reservation(1L, mockMember, LocalDate.now().plusDays(1), mockTimeSlot, mockTheme, ReservationStatus.BOOKING);


        when(memberService.findMemberById(anyLong())).thenReturn(mockMember);
        when(timeService.findTimeSlotById(anyLong())).thenReturn(mockTimeSlot);
        when(themeService.findThemeById(anyLong())).thenReturn(mockTheme);
        when(reservationRepository.existsByDateAndTimeAndThemeAndMember(any(), any(), any(), any())).thenReturn(false);
        doNothing().when(paymentService).payment(any(MemberReservationRequest.class));
        when(reservationRepository.save(any())).thenReturn(reservation);

        ReservationResponse response = reservationService.createByClient(reservationRequest, loginMember);

        assertNotNull(response);
        verify(paymentService).payment(any(MemberReservationRequest.class));
    }
}

