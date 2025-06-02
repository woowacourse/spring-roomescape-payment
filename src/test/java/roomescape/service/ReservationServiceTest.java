package roomescape.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRole;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationitem.ReservationItem;
import roomescape.domain.reservationitem.ReservationTheme;
import roomescape.domain.reservationitem.ReservationTime;
import roomescape.dto.request.CreateReservationRequest;
import roomescape.dto.response.MyPageReservationResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.service.reservation.ReservationService;
import roomescape.test_util.ServiceTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class ReservationServiceTest extends ServiceTest {

    private static final LocalDate DATE1 = LocalDate.now().plusDays(1);
    private static final LocalDate DATE2 = LocalDate.now().plusDays(2);
    private static final LocalDate DATE3 = LocalDate.now().plusDays(3);
    private static final LocalTime TIME1 = LocalTime.of(12, 0);

    @Autowired
    private ReservationService reservationService;

    @Test
    @DisplayName("사용자의 id를 이용해 예약을 생성한다")
    void createReservationTest() {
        // given
        Member member = insertMember("new@example.com", "new-password", "new-name", MemberRole.USER);
        ReservationTime time = insertReservationTime(LocalTime.now());
        ReservationTheme theme = insertReservationTheme("new Theme", "new Description", "new Thumbnail");
        LocalDate date = DATE1;

        final CreateReservationRequest createReservationRequest = new CreateReservationRequest(member.getId(), date, theme.getId(), time.getId());

        // when
        ReservationResponse reservationResponse = reservationService.save(createReservationRequest);

        // then
        assertAll(
                () -> assertThat(reservationResponse.date()).isEqualTo(date),
                () -> assertThat(reservationResponse.name()).isEqualTo(member.getName())
        );
    }

    @Test
    @DisplayName("사용자가 없는 theme id 또는 time id를 이용해 예약을 생성한다")
    void createReservationTest2() {
        // given
        Member member = insertMember("member1@example.com", "password1", "Member 1", MemberRole.USER);
        ReservationTheme theme = insertReservationTheme("Theme 1", "Description 1", "Thumbnail 1");
        ReservationTime time = insertReservationTime(TIME1);

        final CreateReservationRequest request1 = new CreateReservationRequest(
                member.getId(),
                DATE1,
                999L,
                time.getId()
        );

        final CreateReservationRequest request2 = new CreateReservationRequest(
                member.getId(),
                DATE1,
                theme.getId(),
                999L
        );

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> reservationService.save(request1))
                        .isInstanceOf(NoSuchElementException.class),
                () -> assertThatThrownBy(() -> reservationService.save(request2))
                        .isInstanceOf(NoSuchElementException.class)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    @DisplayName("미래가 아닌 날짜로 예약 시도 시 예외 발생")
    void createReservationTest3(int minusDay) {
        // given
        Member member = insertMember("member1@example.com", "password1", "Member 1", MemberRole.USER);
        ReservationTheme theme = insertReservationTheme("Theme 1", "Description 1", "Thumbnail 1");
        ReservationTime time = insertReservationTime(TIME1);

        final CreateReservationRequest request = new CreateReservationRequest(
                member.getId(),
                LocalDate.now().minusDays(minusDay),
                theme.getId(),
                time.getId()
        );

        // when, then
        assertThatThrownBy(() -> reservationService.save(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Nested
    class 필터링_조회_테스트 {

        @Test
        @DisplayName("회원 ID로만 필터링하면 해당 회원의 모든 예약이 조회된다")
        void filterByMemberIdOnly() {
            // given
            Member member1 = insertMember("이메일1", "비밀번호", "이름1", MemberRole.USER);
            Member member2 = insertMember("이메일2", "비밀번호", "이름2", MemberRole.USER);
            ReservationTheme theme = insertReservationTheme("테마", "설명", "썸네일");
            ReservationTime time = insertReservationTime(TIME1);
            ReservationItem item = insertReservationItem(DATE1, time, theme);
            Reservation reservation1 = insertReservation(member1, item, ReservationStatus.ACCEPTED);
            Reservation reservation2 = insertReservation(member1, item, ReservationStatus.PENDING);
            insertReservation(member2, item, ReservationStatus.PENDING);

            // when
            List<ReservationResponse> result = reservationService.getAllFiltered(member1.getId(), null, null, null);

            // then
            assertThat(result).hasSize(2).extracting(ReservationResponse::id)
                    .containsExactlyInAnyOrder(reservation1.getId(), reservation2.getId());
        }

        @Test
        @DisplayName("존재하지 않는 회원 ID로 필터링하면 빈 결과가 반환된다")
        void filterByNonExistingMemberId() {
            // when
            List<ReservationResponse> result = reservationService.getAllFiltered(999L, null, null, null);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("테마 ID로만 필터링하면 해당 테마의 모든 예약이 조회된다")
        void filterByThemeIdOnly() {
            // given
            Member member = insertMember("이메일", "비밀번호", "이름1", MemberRole.USER);
            ReservationTheme theme1 = insertReservationTheme("테마1", "설명", "썸네일");
            ReservationTheme theme2 = insertReservationTheme("테마2", "설명", "썸네일");
            ReservationTime time = insertReservationTime(TIME1);
            ReservationItem item1 = insertReservationItem(DATE1, time, theme1);
            ReservationItem item2 = insertReservationItem(DATE1, time, theme2);
            Reservation reservation1 = insertReservation(member, item1, ReservationStatus.ACCEPTED);
            Reservation reservation2 = insertReservation(member, item1, ReservationStatus.PENDING);
            insertReservation(member, item2, ReservationStatus.PENDING);

            // when
            List<ReservationResponse> result = reservationService.getAllFiltered(null, theme1.getId(), null, null);

            // then
            assertThat(result).hasSize(2).extracting(ReservationResponse::id)
                    .containsExactlyInAnyOrder(reservation1.getId(), reservation2.getId());
        }

        @Test
        @DisplayName("존재하지 않는 테마 ID로 필터링하면 빈 결과가 반환된다")
        void filterByNonExistingThemeId() {
            // when
            List<ReservationResponse> result = reservationService.getAllFiltered(null, 999L, null, null);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("시작 날짜만 지정하면 해당 날짜 이후의 예약이 조회된다")
        void filterByDateFromOnly() {
            // given
            Member member = insertMember("이메일", "비밀번호", "이름1", MemberRole.USER);
            ReservationTheme theme = insertReservationTheme("테마1", "설명", "썸네일");
            ReservationTime time = insertReservationTime(TIME1);
            ReservationItem item1 = insertReservationItem(DATE1, time, theme);
            ReservationItem item2 = insertReservationItem(DATE2, time, theme);
            insertReservation(member, item1, ReservationStatus.ACCEPTED);
            insertReservation(member, item1, ReservationStatus.PENDING);
            Reservation reservation = insertReservation(member, item2, ReservationStatus.PENDING);

            // when
            List<ReservationResponse> result = reservationService.getAllFiltered(null, null, DATE2, null);

            // then
            assertThat(result).hasSize(1).extracting(ReservationResponse::id)
                    .containsExactlyInAnyOrder(reservation.getId());
        }

        @Test
        @DisplayName("종료 날짜만 지정하면 해당 날짜 이전의 예약이 조회된다")
        void filterByDateToOnly() {
            // given
            Member member = insertMember("이메일", "비밀번호", "이름1", MemberRole.USER);
            ReservationTheme theme = insertReservationTheme("테마1", "설명", "썸네일");
            ReservationTime time = insertReservationTime(TIME1);
            ReservationItem item1 = insertReservationItem(DATE1, time, theme);
            ReservationItem item2 = insertReservationItem(DATE2, time, theme);
            Reservation reservation = insertReservation(member, item1, ReservationStatus.ACCEPTED);
            insertReservation(member, item2, ReservationStatus.PENDING);
            insertReservation(member, item2, ReservationStatus.PENDING);

            // when
            List<ReservationResponse> result = reservationService.getAllFiltered(null, null, null, DATE1);

            // then
            assertThat(result).hasSize(1).extracting(ReservationResponse::id)
                    .containsExactlyInAnyOrder(reservation.getId());
        }

        @Test
        @DisplayName("시작 날짜와 종료 날짜를 모두 지정하면 해당 기간 내의 예약이 조회된다")
        void filterByDateRange() {
            // given
            Member member = insertMember("이메일", "비밀번호", "이름1", MemberRole.USER);
            ReservationTheme theme = insertReservationTheme("테마1", "설명", "썸네일");
            ReservationTime time = insertReservationTime(TIME1);
            ReservationItem item1 = insertReservationItem(DATE1, time, theme);
            ReservationItem item2 = insertReservationItem(DATE2, time, theme);
            ReservationItem item3 = insertReservationItem(DATE3, time, theme);
            Reservation reservation1 = insertReservation(member, item1, ReservationStatus.ACCEPTED);
            Reservation reservation2 = insertReservation(member, item2, ReservationStatus.PENDING);
            insertReservation(member, item3, ReservationStatus.PENDING);

            // when
            List<ReservationResponse> result = reservationService.getAllFiltered(null, null, DATE1, DATE2);

            // then
            assertThat(result).hasSize(2).extracting(ReservationResponse::id)
                    .containsExactlyInAnyOrder(reservation1.getId(), reservation2.getId());
        }

        @Test
        @DisplayName("날짜 범위에 예약이 없으면 빈 결과가 반환된다")
        void filterByEmptyDateRange() {
            // given
            Member member = insertMember("이메일", "비밀번호", "이름1", MemberRole.USER);
            ReservationTheme theme = insertReservationTheme("테마1", "설명", "썸네일");
            ReservationTime time = insertReservationTime(TIME1);
            ReservationItem item1 = insertReservationItem(DATE1, time, theme);
            ReservationItem item2 = insertReservationItem(DATE2, time, theme);
            ReservationItem item3 = insertReservationItem(DATE3, time, theme);
            insertReservation(member, item1, ReservationStatus.ACCEPTED);
            insertReservation(member, item2, ReservationStatus.PENDING);
            insertReservation(member, item3, ReservationStatus.PENDING);

            // when
            List<ReservationResponse> result = reservationService.getAllFiltered(null, null, LocalDate.now().plusDays(10), LocalDate.now().plusDays(15));

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("회원 ID와 테마 ID로 필터링하면 해당 회원의 해당 테마 예약만 조회된다")
        void filterByMemberIdAndThemeId() {
            // given
            Member member1 = insertMember("이메일1", "비밀번호", "이름1", MemberRole.USER);
            Member member2 = insertMember("이메일2", "비밀번호", "이름2", MemberRole.USER);
            ReservationTheme theme1 = insertReservationTheme("테마1", "설명", "썸네일");
            ReservationTheme theme2 = insertReservationTheme("테마2", "설명", "썸네일");
            ReservationTime time = insertReservationTime(TIME1);
            ReservationItem item1 = insertReservationItem(DATE1, time, theme1);
            ReservationItem item2 = insertReservationItem(DATE2, time, theme2);
            Reservation reservation = insertReservation(member1, item1, ReservationStatus.PENDING);
            insertReservation(member1, item2, ReservationStatus.PENDING);
            insertReservation(member2, item1, ReservationStatus.PENDING);
            insertReservation(member2, item2, ReservationStatus.PENDING);

            // when
            List<ReservationResponse> result = reservationService.getAllFiltered(member1.getId(), theme1.getId(), null, null);

            // then
            assertThat(result).hasSize(1).extracting(ReservationResponse::id)
                    .containsExactlyInAnyOrder(reservation.getId());
        }

        @Test
        @DisplayName("모든 필터 조건을 적용하면 모든 조건을 만족하는 예약만 조회된다")
        void filterByAllConditions() {
            // given
            Member member1 = insertMember("이메일1", "비밀번호", "이름1", MemberRole.USER);
            Member member2 = insertMember("이메일2", "비밀번호", "이름2", MemberRole.USER);
            ReservationTheme theme1 = insertReservationTheme("테마1", "설명", "썸네일");
            ReservationTheme theme2 = insertReservationTheme("테마2", "설명", "썸네일");
            ReservationTime time = insertReservationTime(TIME1);
            ReservationItem item1 = insertReservationItem(DATE1, time, theme1);
            ReservationItem item2 = insertReservationItem(DATE2, time, theme1);
            ReservationItem item3 = insertReservationItem(DATE3, time, theme2);
            insertReservation(member1, item1, ReservationStatus.PENDING);
            Reservation reservation = insertReservation(member1, item2, ReservationStatus.PENDING);
            insertReservation(member1, item3, ReservationStatus.PENDING);
            insertReservation(member2, item1, ReservationStatus.PENDING);
            insertReservation(member2, item2, ReservationStatus.PENDING);
            insertReservation(member2, item3, ReservationStatus.PENDING);

            // when
            List<ReservationResponse> result = reservationService.getAllFiltered(
                    member1.getId(), theme1.getId(), DATE2, DATE3);

            // then
            assertThat(result).hasSize(1).extracting(ReservationResponse::id)
                    .containsExactlyInAnyOrder(reservation.getId());
        }

        @Test
        @DisplayName("모든 파라미터가 null이면, 모든 예약이 조회된다")
        void filterWithAllNullParameters() {
            // given
            Member member1 = insertMember("이메일1", "비밀번호", "이름1", MemberRole.USER);
            Member member2 = insertMember("이메일2", "비밀번호", "이름2", MemberRole.USER);
            ReservationTheme theme1 = insertReservationTheme("테마1", "설명", "썸네일");
            ReservationTheme theme2 = insertReservationTheme("테마2", "설명", "썸네일");
            ReservationTime time = insertReservationTime(TIME1);
            ReservationItem item1 = insertReservationItem(DATE1, time, theme1);
            ReservationItem item2 = insertReservationItem(DATE2, time, theme1);
            ReservationItem item3 = insertReservationItem(DATE3, time, theme2);
            Reservation r1 = insertReservation(member1, item1, ReservationStatus.PENDING);
            Reservation r2 = insertReservation(member1, item2, ReservationStatus.PENDING);
            Reservation r3 = insertReservation(member1, item3, ReservationStatus.PENDING);
            Reservation r4 = insertReservation(member2, item1, ReservationStatus.PENDING);
            Reservation r5 = insertReservation(member2, item2, ReservationStatus.PENDING);
            Reservation r6 = insertReservation(member2, item3, ReservationStatus.PENDING);

            // when
            List<ReservationResponse> result = reservationService.getAllFiltered(null, null, null, null);

            // then
            assertThat(result).hasSize(6).extracting(ReservationResponse::id)
                    .containsExactlyInAnyOrder(r1.getId(), r2.getId(), r3.getId(), r4.getId(), r5.getId(), r6.getId());
        }

        @Test
        @DisplayName("조건에 맞는 예약이 없는 경우 빈 목록이 반환된다")
        void returnsEmptyListWhenNoReservationsMatch() {
            // when
            List<ReservationResponse> result = reservationService.getAllFiltered(1L, 1L, DATE2, DATE3);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Test
    @DisplayName("존재하지 않은 예약 항목이라면 새로운 에약을 확정 상태로 생성한다.")
    void saveReservationAccepted() {
        // given
        Member member = insertMember("member1@example.com", "password1", "Member 1", MemberRole.USER);
        ReservationTheme theme = insertReservationTheme("Theme 1", "Description 1", "Thumbnail 1");
        ReservationTime time = insertReservationTime(TIME1);
        final CreateReservationRequest createReservationRequest = new CreateReservationRequest(member.getId(), DATE1, theme.getId(), time.getId());

        // when
        final ReservationResponse reservation = reservationService.save(createReservationRequest);

        // then
        assertThat(reservation.status()).isEqualTo(ReservationStatus.ACCEPTED.description);
    }

    @Test
    @DisplayName("존재하는 예약 항목이라면 새로운 에약을 대기 상태로 생성한다.")
    void saveReservationPending() {
        // given
        Member member1 = insertMember("member1@example.com", "password1", "Member 1", MemberRole.USER);
        Member member2 = insertMember("member2@example.com", "password1", "Member 1", MemberRole.USER);
        ReservationTheme theme = insertReservationTheme("Theme 1", "Description 1", "Thumbnail 1");
        ReservationTime time = insertReservationTime(TIME1);
        reservationService.save(new CreateReservationRequest(member1.getId(), DATE1, theme.getId(), time.getId()));

        final CreateReservationRequest request = new CreateReservationRequest(member2.getId(), DATE1, theme.getId(), time.getId());

        // when
        final ReservationResponse reservation = reservationService.pending(request);

        // then
        assertThat(reservation.status()).isEqualTo(ReservationStatus.PENDING.description);
    }

    @Test
    @DisplayName("대기 예약 삭제 시 예약만 삭제된다.")
    void deletePendingReservationTest() {
        // given
        Member member1 = insertMember("member1@example.com", "password1", "Member 1", MemberRole.USER);
        Member member2 = insertMember("member2@example.com", "password2", "Member 2", MemberRole.USER);
        ReservationTheme theme = insertReservationTheme("Theme 1", "Description 1", "Thumbnail 1");
        ReservationTime time = insertReservationTime(TIME1);

        ReservationResponse acceptedReservation = reservationService.save(new CreateReservationRequest(member1.getId(), DATE1, theme.getId(), time.getId()));
        ReservationResponse pendingReservation = reservationService.pending(new CreateReservationRequest(member2.getId(), DATE1, theme.getId(), time.getId()));

        int beforeCount = countReservation();

        // when
        reservationService.remove(pendingReservation.id());

        // then
        List<Reservation> remainingReservations = getAllReservations();
        assertAll(
                () -> assertThat(remainingReservations).hasSize(beforeCount - 1),
                () -> assertThat(remainingReservations).noneMatch(reservation -> reservation.getId() == pendingReservation.id()),
                () -> assertThat(remainingReservations).anyMatch(reservation -> reservation.getId() == acceptedReservation.id())
        );

    }

    @Test
    @DisplayName("확정 예약 삭제 시 다음 대기가 있으면 예약 항목은 삭제되지 않고 다음 예약이 결제 대기 상태가 된다.")
    void deleteAcceptedReservationAndHasPendingReservationTest() {
        // given
        Member member1 = insertMember("member1@example.com", "password1", "Member 1", MemberRole.USER);
        Member member2 = insertMember("member2@example.com", "password2", "Member 2", MemberRole.USER);
        ReservationTheme theme = insertReservationTheme("Theme 1", "Description 1", "Thumbnail 1");
        ReservationTime time = insertReservationTime(TIME1);

        ReservationResponse acceptedReservation = reservationService.save(new CreateReservationRequest(member1.getId(), DATE1, theme.getId(), time.getId()));
        ReservationResponse pendingReservation = reservationService.pending(new CreateReservationRequest(member2.getId(), DATE1, theme.getId(), time.getId()));

        int beforeCount = countReservation();

        // when
        reservationService.remove(acceptedReservation.id());

        // then
        List<Reservation> remainingReservations = getAllReservations();
        assertAll(
                () -> assertThat(remainingReservations).hasSize(beforeCount - 1),
                () -> assertThat(remainingReservations).noneMatch(reservation -> reservation.getId() == acceptedReservation.id()),
                () -> assertThat(remainingReservations).anyMatch(reservation ->
                        reservation.getId() == pendingReservation.id() &&
                        reservation.getReservationStatus() == ReservationStatus.NOT_PAID
                )
        );
    }

    @Test
    @DisplayName("확정 예약의 priority는 0이다.")
    void acceptedReservationHasPriorityZero() {
        // given
        Member member1 = insertMember("이메일1", "비밀번호", "이름1", MemberRole.USER);
        Member member2 = insertMember("이메일2", "비밀번호", "이름2", MemberRole.USER);
        ReservationTheme theme = insertReservationTheme("테마1", "설명", "썸네일");
        ReservationTime time = insertReservationTime(TIME1);
        ReservationItem item = insertReservationItem(DATE1, time, theme);
        Reservation acceptedReservation = insertReservation(member1, item, ReservationStatus.ACCEPTED);
        insertReservation(member2, item, ReservationStatus.PENDING);
        insertPayment("paymentKey", 10000, acceptedReservation);

        // when
        List<MyPageReservationResponse> myReservations = reservationService.getAllBy(member1.getId());

        // then
        assertThat(myReservations).anyMatch(reservation ->
                reservation.reservationId().equals(acceptedReservation.getId()) &&
                reservation.priority() == 0
        );
    }

    @Test
    @DisplayName("대기 예약의 priority는 앞선 예약 수에 따라 결정된다.")
    void pendingReservationPriorityTest() {
        // given
        Member member1 = insertMember("이메일1", "비밀번호", "이름1", MemberRole.USER);
        Member member2 = insertMember("이메일2", "비밀번호", "이름2", MemberRole.USER);
        Member member3 = insertMember("이메일3", "비밀번호", "이름3", MemberRole.USER);
        ReservationTheme theme = insertReservationTheme("테마1", "설명", "썸네일");
        ReservationTime time = insertReservationTime(TIME1);
        ReservationItem item = insertReservationItem(DATE1, time, theme);
        insertReservation(member1, item, ReservationStatus.ACCEPTED);
        insertReservation(member2, item, ReservationStatus.PENDING);
        Reservation pendingReservation = insertReservation(member3, item, ReservationStatus.PENDING);

        // when
        List<MyPageReservationResponse> myReservations = reservationService.getAllBy(member3.getId());

        // then
        assertThat(myReservations).anyMatch(reservation ->
                reservation.reservationId().equals(pendingReservation.getId()) &&
                reservation.priority() == 2
        );
    }

    @Test
    @DisplayName("확정 예약 삭제 후 다음 대기 예약이 확정되면 priority가 0으로 변경된다.")
    void priorityChangesAfterReservationDeletion() {
        // given
        Member member1 = insertMember("이메일1", "비밀번호", "이름1", MemberRole.USER);
        Member member2 = insertMember("이메일2", "비밀번호", "이름2", MemberRole.USER);
        ReservationTheme theme = insertReservationTheme("테마1", "설명", "썸네일");
        ReservationTime time = insertReservationTime(TIME1);
        ReservationItem item = insertReservationItem(DATE1, time, theme);
        Reservation acceptedReservation = insertReservation(member1, item, ReservationStatus.ACCEPTED);
        Reservation pendingReservation = insertReservation(member2, item, ReservationStatus.PENDING);

        // before
        List<MyPageReservationResponse> beforeMyReservation = reservationService.getAllBy(member2.getId());
        assertThat(beforeMyReservation).anyMatch(reservation ->
                reservation.reservationId().equals(pendingReservation.getId()) &&
                reservation.priority() == 1
        );

        // when
        reservationService.remove(acceptedReservation.getId());

        // after
        List<MyPageReservationResponse> afterMyReservation = reservationService.getAllBy(member2.getId());
        assertThat(afterMyReservation).anyMatch(reservation ->
                reservation.reservationId().equals(pendingReservation.getId()) &&
                reservation.priority() == 0
        );
    }

    @Test
    @DisplayName("같은 사용자가 같은 예약 항목에 예약을 두번 걸 수 없다")
    void duplicateSameMemberSameReservationItemTest() {
        // given
        Member member = insertMember("이메일", "비밀번호", "이름1", MemberRole.USER);
        ReservationTheme theme = insertReservationTheme("테마1", "설명", "썸네일");
        ReservationTime time = insertReservationTime(TIME1);
        ReservationItem item = insertReservationItem(DATE1, time, theme);
        insertReservation(member, item, ReservationStatus.ACCEPTED);

        // when, then
        assertThatThrownBy(() -> reservationService.save(new CreateReservationRequest(member.getId(), DATE1, theme.getId(), time.getId())))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
