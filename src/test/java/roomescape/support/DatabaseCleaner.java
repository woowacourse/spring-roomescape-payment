package roomescape.support;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DatabaseCleaner {

    private final EntityManager entityManager;

    @Transactional
    public void truncateAllTables() {
        entityManager.createNativeQuery("DELETE FROM toss_payment").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE toss_payment ALTER COLUMN id RESTART WITH 1").executeUpdate();

        entityManager.createNativeQuery("DELETE FROM waiting").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE waiting ALTER COLUMN id RESTART WITH 1").executeUpdate();

        entityManager.createNativeQuery("DELETE FROM reservation_ticket").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE reservation_ticket ALTER COLUMN id RESTART WITH 1")
                .executeUpdate();

        entityManager.createNativeQuery("DELETE FROM reservation_time").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE reservation_time ALTER COLUMN id RESTART WITH 1").executeUpdate();

        entityManager.createNativeQuery("DELETE FROM theme").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE theme ALTER COLUMN id RESTART WITH 1").executeUpdate();

        entityManager.createNativeQuery("DELETE FROM member").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE member ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }
}
