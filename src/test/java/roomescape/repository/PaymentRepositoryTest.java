package roomescape.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import roomescape.model.Payment;
import roomescape.service.fixture.MemberFixture;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(scripts = {"/initialize_table.sql", "/test_data.sql"})
class PaymentRepositoryTest {

    @Autowired
    PaymentRepository paymentRepository;

    @Test
    @DisplayName("")
    void should_get_payments() {
        final List<Payment> payments = paymentRepository.findByMember(MemberFixture.GENERAL.getMember());
        assertThat(payments).hasSize(2);
    }
}