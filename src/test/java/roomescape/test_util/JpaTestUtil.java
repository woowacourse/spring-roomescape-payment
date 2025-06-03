package roomescape.test_util;

import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import roomescape.business.model.entity.Member;
import roomescape.business.model.entity.Reservation;
import roomescape.business.model.entity.Theme;
import roomescape.business.model.entity.TimeSlot;
import roomescape.business.model.vo.Id;
import roomescape.infrastructure.MemberRepository;
import roomescape.infrastructure.ReservationRepository;
import roomescape.infrastructure.ReservationTimeRepository;
import roomescape.infrastructure.ThemeRepository;

@Component
public class JpaTestUtil {

    private final ReservationRepository reservationDao;
    private final ReservationTimeRepository timeDao;
    private final MemberRepository userDao;
    private final ThemeRepository themeDao;

    @Autowired
    public JpaTestUtil(ReservationRepository reservationDao, ReservationTimeRepository timeDao,
                       MemberRepository userDao, ThemeRepository themeDao) {
        this.reservationDao = reservationDao;
        this.timeDao = timeDao;
        this.userDao = userDao;
        this.themeDao = themeDao;
    }

    public void insertUser(final String id, final String name) {
        userDao.save(Member.restore(id, "USER", name, name + "@email.com", "password123"));
    }

    public void insertReservation(final String id, final LocalDate date, final String timeId, final String themeId,
                                  final String userId) {
        Member member = userDao.findById(Id.create(userId)).get();
        TimeSlot time = timeDao.findById(Id.create(timeId)).get();
        Theme theme = themeDao.findById(Id.create(themeId)).get();
        reservationDao.save(Reservation.restore(id, member, date, time, theme));
    }

    public void insertReservationTime(final String id, final LocalTime time) {
        timeDao.save(TimeSlot.restore(id, time));
    }

    public void insertTheme(final String id, final String name) {
        themeDao.save(Theme.restore(id, name, "", ""));
    }

    public int countReservation() {
        return (int) reservationDao.count();
    }

    public int countReservationTime() {
        return (int) timeDao.count();
    }

    public int countTheme() {
        return (int) themeDao.count();
    }

    public void deleteAll() {
        reservationDao.deleteAll();
        timeDao.deleteAll();
        themeDao.deleteAll();
        userDao.deleteAll();
    }
}
