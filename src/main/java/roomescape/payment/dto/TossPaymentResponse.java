package roomescape.payment.dto;

public record TossPaymentResponse(
        String status,
        String paymentKey,
        String orderId,
        String method, // 결제 수단
        Card card,
        EasyPay easyPay,
        Receipt receipt

) {
    private record Receipt(String url) {
    }

    private record Card(String number, String approvedNo){
    }

    private record EasyPay(String provider, Long amount, Long discountAmount) {
    }

    public String receiptUrl() {
        if(receipt == null) {
            return null;
        }
        return receipt.url;
    }

    public String cardNumber() {
        if(card == null) {
            return null;
        }
        return card.number;
    }

    public String cardApprovedNo() {
        if(card == null) {
            return null;
        }
        return card.approvedNo;
    }

    public String easyPayProvider() {
        if(easyPay == null) {
            return null;
        }
        return easyPay.provider;
    }
}
