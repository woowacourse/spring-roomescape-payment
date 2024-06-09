package roomescape.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.regex.Pattern;

@Embeddable
public class Account {

    private static final int MAX_ACCOUNT_NUMBER_LENGTH = 20;
    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("^\\d+(-\\d+)+$");
    private static final int MIN_ACCOUNT_HOLDER_LENGTH = 2;
    private static final int MAX_ACCOUNT_HOLDER_LENGTH = 10;
    private static final Pattern ACCOUNT_HOLDER_PATTERN = Pattern.compile("^[가-힣]*$");
    private static final int MAX_BANK_NAME_LENGTH = 10;

    @Column(nullable = false, length = MAX_ACCOUNT_NUMBER_LENGTH)
    private String accountNumber;

    @Column(nullable = false, length = MAX_ACCOUNT_HOLDER_LENGTH)
    private String accountHolder;

    @Column(nullable = false, length = MAX_BANK_NAME_LENGTH)
    private String bankName;

    protected Account() {
    }

    public Account(String accountNumber, String accountHolder, String bankName) {
        validateAccountNumber(accountNumber);
        validateAccountHolder(accountHolder);
        validateBankName(bankName);

        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.bankName = bankName;
    }

    private void validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("계좌번호는 필수입니다.");
        }

        if (accountNumber.length() > MAX_ACCOUNT_NUMBER_LENGTH) {
            throw new IllegalArgumentException(String.format("계좌번호는 최대 %d자입니다.", MAX_ACCOUNT_NUMBER_LENGTH));
        }

        if (!ACCOUNT_NUMBER_PATTERN.matcher(accountNumber).matches()) {
            throw new IllegalArgumentException("계좌번호 형식이 올바르지 않습니다.");
        }
    }

    private void validateAccountHolder(String accountHolder) {
        if (accountHolder == null || accountHolder.isBlank()) {
            throw new IllegalArgumentException("예금주는 필수입니다.");
        }

        if (accountHolder.length() < MIN_ACCOUNT_HOLDER_LENGTH || accountHolder.length() > MAX_ACCOUNT_HOLDER_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("예금주는 %d자 이상 %d자 이하여야 합니다.", MIN_ACCOUNT_HOLDER_LENGTH, MAX_ACCOUNT_HOLDER_LENGTH)
            );
        }

        if (!ACCOUNT_HOLDER_PATTERN.matcher(accountHolder).matches()) {
            throw new IllegalArgumentException("예금주는 한글명이어야 합니다.");
        }
    }

    private void validateBankName(String bankName) {
        if (bankName == null || bankName.isBlank()) {
            throw new IllegalArgumentException("은행명은 필수입니다.");
        }

        if (bankName.length() > MAX_BANK_NAME_LENGTH) {
            throw new IllegalArgumentException(String.format("은행명은 최대 %d자입니다.", MAX_BANK_NAME_LENGTH));
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public String getBankName() {
        return bankName;
    }
}
