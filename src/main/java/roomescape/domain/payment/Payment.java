package roomescape.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import roomescape.domain.reservation.Reservation;

import java.math.BigDecimal;

@Entity
public class Payment {

    private static final int MAX_PAYMENT_KEY_LENGTH = 200;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = MAX_PAYMENT_KEY_LENGTH)
    private String paymentKey;

    @Embedded
    private Account account;

    @Embedded
    private Amount amount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayType payType;

    public static Payment tossPay(String paymentKey, BigDecimal amount, Reservation reservation) {
        validatePaymentKey(paymentKey);
        return new Payment(paymentKey, null, amount, reservation, PayType.TOSS_PAY);
    }

    private static void validatePaymentKey(String paymentKey) {
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new IllegalArgumentException("Payment key는 필수입니다.");
        }

        if (paymentKey.length() > MAX_PAYMENT_KEY_LENGTH) {
            throw new IllegalArgumentException(String.format("Payment key는 최대 %d자입니다.", MAX_PAYMENT_KEY_LENGTH));
        }
    }

    public static Payment accountTransfer(String accountNumber, String accountHolder, String bankName, BigDecimal amount, Reservation reservation) {
        return new Payment(null, new Account(accountNumber, accountHolder, bankName), amount, reservation, PayType.ACCOUNT_TRANSFER);
    }

    protected Payment() {
    }

    private Payment(String paymentKey, Account account, BigDecimal amount, Reservation reservation, PayType payType) {
        this(paymentKey, account, new Amount(amount), reservation, payType);
    }

    private Payment(String paymentKey, Account account, Amount amount, Reservation reservation, PayType payType) {
        validateReservation(reservation);

        this.paymentKey = paymentKey;
        this.account = account;
        this.amount = amount;
        this.reservation = reservation;
        this.payType = payType;
    }

    private void validateReservation(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation은 필수입니다.");
        }
    }

    public Payment copy() {
        return new Payment(paymentKey, account, amount, reservation, payType);
    }

    public boolean isNotAccountTransfer() {
        return payType != PayType.ACCOUNT_TRANSFER;
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public BigDecimal getAmount() {
        return amount.getValue();
    }

    public Long getReservationId() {
        return reservation.getId();
    }

    public PayType getPayType() {
        return payType;
    }

    public String getAccountNumber() {
        if (account == null) {
            return null;
        }
        return account.getAccountNumber();
    }

    public String getAccountHolder() {
        if (account == null) {
            return null;
        }
        return account.getAccountHolder();
    }

    public String getBankName() {
        if (account == null) {
            return null;
        }
        return account.getBankName();
    }
}
