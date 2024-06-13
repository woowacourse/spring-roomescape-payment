package roomescape.helper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class DatabaseCleaner {
    @PersistenceContext
    private EntityManager entityManager;

    public void execute() {
        clearReservationPayment();
        clearReservationWaiting();
        clearReservation();
        clearMember();
        clearTime();
        clearTheme();
    }

    private void clearReservationPayment() {
        entityManager.createNativeQuery("DELETE FROM reservation_payment").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE reservation_payment ALTER COLUMN id RESTART").executeUpdate();
    }

    private void clearReservationWaiting() {
        entityManager.createNativeQuery("DELETE FROM reservation_waiting").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE reservation_waiting ALTER COLUMN id RESTART").executeUpdate();
    }

    private void clearReservation() {
        entityManager.createNativeQuery("DELETE FROM reservation").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE reservation ALTER COLUMN id RESTART").executeUpdate();
    }

    private void clearMember() {
        entityManager.createNativeQuery("DELETE FROM member").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE member ALTER COLUMN id RESTART").executeUpdate();
    }

    private void clearTime() {
        entityManager.createNativeQuery("DELETE FROM reservation_time").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE reservation_time ALTER COLUMN id RESTART").executeUpdate();
    }

    private void clearTheme() {
        entityManager.createNativeQuery("DELETE FROM theme").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE theme ALTER COLUMN id RESTART").executeUpdate();
    }
}
