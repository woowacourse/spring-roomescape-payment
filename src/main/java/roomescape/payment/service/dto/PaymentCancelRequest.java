package roomescape.payment.service.dto;

public record PaymentCancelRequest (String cancelReason){
    public PaymentCancelRequest(){
       this("단순 변심");
    }
}
