package roomescape.domain.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {

    @Test
    @DisplayName("계좌 객체를 생성한다.")
    void createAccount() {
        Account account = new Account("1111-5555-7890", "미르", "우리은행");

        assertThat(account.getAccountNumber()).isEqualTo("1111-5555-7890");
        assertThat(account.getAccountHolder()).isEqualTo("미르");
        assertThat(account.getBankName()).isEqualTo("우리은행");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("계좌번호가 비어있으면 예외가 발생한다.")
    void validateEmptyAccountNumber(String invalidAccountNumber) {
        assertThatThrownBy(() -> new Account(invalidAccountNumber, "미르", "우리은행"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("계좌번호는 필수입니다.");
    }

    @Test
    @DisplayName("계좌번호가 20자를 넘으면 예외가 발생한다.")
    void validateAccountNumberLength() {
        String invalidAccountNumber = "1".repeat(21);

        assertThatThrownBy(() -> new Account(invalidAccountNumber, "미르", "우리은행"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("계좌번호는 최대 20자입니다.");
    }

    @Test
    @DisplayName("계좌번호가 형식에 맞지 않으면 예외가 발생한다.")
    void validateAccountNumberPattern() {
        String invalidAccountNumber = "1111$5555";

        assertThatThrownBy(() -> new Account(invalidAccountNumber, "미르", "우리은행"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("계좌번호 형식이 올바르지 않습니다.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("예금주가 비어있으면 예외가 발생한다.")
    void validateEmptyAccountHolder(String invalidAccountHolder) {
        assertThatThrownBy(() -> new Account("1111-5555-7890", invalidAccountHolder, "우리은행"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("예금주는 필수입니다.");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 11})
    @DisplayName("예금주가 2자 미만이거나 10자를 넘으면 예외가 발생한다.")
    void validateAccountHolderLength(int length) {
        String invalidAccountHolder = "프".repeat(length);

        assertThatThrownBy(() -> new Account("1111-5555-7890", invalidAccountHolder, "우리은행"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("예금주는 2자 이상 10자 이하여야 합니다.");
    }

    @Test
    @DisplayName("예금주가 한글명이 아니면 예외가 발생한다.")
    void validateAccountHolderPattern() {
        String invalidAccountHolder = "prin";

        assertThatThrownBy(() -> new Account("1111-5555-7890", invalidAccountHolder, "우리은행"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("예금주는 한글명이어야 합니다.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("은행명이 비어있으면 예외가 발생한다.")
    void validateEmptyBankName(String invalidBankName) {
        assertThatThrownBy(() -> new Account("1111-5555-7890", "미르", invalidBankName))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("은행명은 필수입니다.");
    }

    @Test
    @DisplayName("은행명이 10자를 넘으면 예외가 발생한다.")
    void validateBankNameLength() {
        String invalidBankName = "행".repeat(11);

        assertThatThrownBy(() -> new Account("1111-5555-7890", "미르", invalidBankName))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("은행명은 최대 10자입니다.");
    }
}
