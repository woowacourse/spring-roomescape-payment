package roomescape.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.controller.request.AdminReservationRequest;
import roomescape.controller.request.ReservationRequest;
import roomescape.exception.DuplicatedException;
import roomescape.exception.NotFoundException;
import roomescape.model.Member;
import roomescape.model.Reservation;
import roomescape.model.ReservationTime;
import roomescape.model.Theme;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              ReservationTimeRepository reservationTimeRepository,
                              ThemeRepository themeRepository,
                              MemberRepository memberRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
    }

    public List<Reservation> findAllReservations() {
        return reservationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Reservation> filterReservation(Long themeId, Long memberId, LocalDate dateFrom, LocalDate dateTo) {
        Theme theme = themeRepository.findById(themeId)
                .orElse(null);
        Member member = memberRepository.findById(memberId)
                .orElse(null);
        return reservationRepository.findByConditions(theme, member, dateFrom, dateTo);
    }

    @Transactional
    public Reservation addReservation(ReservationRequest request, Member member) {
        ReservationTime reservationTime = findReservationTime(request.date(), request.timeId(),
                request.themeId());
        Theme theme = findTheme(request.themeId());

        Reservation reservation = new Reservation(request.date(), reservationTime, theme, member);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation addReservation(AdminReservationRequest request) {
        ReservationTime reservationTime = findReservationTime(request.date(), request.timeId(), request.themeId());

        Theme theme = findTheme(request.themeId());
        Member member = findMember(request.memberId());

        Reservation reservation = new Reservation(request.date(), reservationTime, theme, member);
        return reservationRepository.save(reservation);
    }

    private ReservationTime findReservationTime(LocalDate date, long timeId, long themeId) {
        ReservationTime reservationTime = findReservationTime(timeId);
        validateDuplicatedReservation(date, themeId, timeId);
        return reservationTime;
    }

    private void validateDuplicatedReservation(LocalDate date, Long themeId, Long timeId) {
        ReservationTime reservationTime = findReservationTime(timeId);
        Theme theme = findTheme(themeId);

        boolean exists = reservationRepository.existsByDateAndTimeAndTheme(date, reservationTime, theme);
        if (exists) {
            throw new DuplicatedException("이미 해당 시간에 예약이 존재합니다.");
        }
    }

    @Transactional
    public void deleteReservation(long id) {
        validateExistReservation(id);
        reservationRepository.deleteById(id);
    }

    private void validateExistReservation(long id) {
        findById(reservationRepository, id);
    }

    @Transactional(readOnly = true)
    public List<Reservation> findMemberReservations(Long memberId) {
        Member member = findById(memberRepository, memberId);
        return reservationRepository.findAllByMember(member);
    }

    @Transactional(readOnly = true)
    public Reservation findById(Long id) {
        return findById(reservationRepository, id);
    }

    private <T> T findById(CrudRepository<T, Long> repository, Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 id:[%s] 값으로 예약된 내역이 존재하지 않습니다.".formatted(id)));
    }

    private Theme findTheme(Long id) {
        return themeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("아이디가 %s인 테마가 존재하지 않습니다.".formatted(id)));
    }

    private Member findMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("아이디가 %s인 사용자가 존재하지 않습니다.".formatted(id)));
    }

    private ReservationTime findReservationTime(Long id) {
        return reservationTimeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("아이디가 %s인 예약 시간이 존재하지 않습니다.".formatted(id)));
    }
}
