const MEMBER_RESERVATION_API_ENDPOINT = '/members/reservations';
const WAITING_API_ENDPOINT = '/waitings';
const PAYMENT_API_ENDPOINT = '/reservations/{id}/payment';
document.addEventListener('DOMContentLoaded', () => {
    /*
    TODO: [2단계] 내 예약 목록 조회 기능
          endpoint 설정
     */
    fetch(MEMBER_RESERVATION_API_ENDPOINT) // 내 예약 목록 조회 API 호출
        .then(response => {
            if (response.status === 200) return response.json();
            throw new Error('Read failed');
        })
        .then(render)
        .catch(error => console.error('Error fetching reservations:', error));
});

function render(data) {
    // ------  결제위젯 초기화 ------
    // @docs https://docs.tosspayments.com/reference/widget-sdk#sdk-설치-및-초기화
    // @docs https://docs.tosspayments.com/reference/widget-sdk#renderpaymentmethods선택자-결제-금액-옵션
    const paymentAmount = 1000;
    const widgetClientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
    const paymentWidget = PaymentWidget(widgetClientKey, PaymentWidget.ANONYMOUS);
    paymentWidget.renderPaymentMethods(
        "#payment-method",
        {value: paymentAmount},
        {variantKey: "DEFAULT"}
    );
    const tableBody = document.getElementById('table-body');
    tableBody.innerHTML = '';

    data.forEach(item => {
        const row = tableBody.insertRow();

        /*
        TODO: [2단계] 내 예약 목록 조회 기능
              response 명세에 맞춰 값 설정
         */
        const theme = item.theme;
        const date = item.date;
        const time = item.time;
        const status = item.reservationStatus.status === '예약대기'
            ? (item.reservationStatus.rank + '번째 ' + item.reservationStatus.status): item.reservationStatus.status;

        row.insertCell(0).textContent = theme;
        row.insertCell(1).textContent = date;
        row.insertCell(2).textContent = time;
        row.insertCell(3).textContent = status;

        /*
        TODO: [3단계] 예약 대기 기능 - 예약 대기 취소 기능 구현 후 활성화
         */
        if(status === '결제대기'){
            const paymentCell = row.insertCell(4);
            const paymentButton = document.createElement('button');
            paymentButton.textContent = "결제";
            paymentButton.className = 'btn btn-primary';
            paymentButton.style.marginRight = '10px';
            paymentButton.onclick = event => onPaymentButtonClick(event, item.reservationId, paymentWidget);
            paymentCell.appendChild(paymentButton);
        }
        if (status !== '예약') { // 예약 대기 상태일 때 예약 대기 취소 버튼 추가하는 코드, 상태 값은 변경 가능
            const cancelCell = row.insertCell(4);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '취소';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick = function () {
                requestDeleteWaiting(item.reservationId).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);
        } else { // 예약 완료 상태일 때
            /*
          TODO: [미션4 - 2단계] 내 예약 목록 조회 시,
           예약 완료 상태일 때 결제 정보를 함께 보여주기
           결제 정보 필드명은 자신의 response 에 맞게 변경하기
           */
            row.insertCell(4).textContent = '';
            row.insertCell(5).textContent = item.payment.paymentKey;
            row.insertCell(6).textContent = item.payment.amount;
        }
    });
}

function requestDeleteWaiting(id) {
    /*
    TODO: [3단계] 예약 대기 기능 - 예약 대기 취소 API 호출
     */
    const endpoint = `${WAITING_API_ENDPOINT}/${id}`;
    return fetch(endpoint, {
        method: 'DELETE'
    }).then(response => {
        if (response.status !== 204) return response.json().then(data => {
            throw new Error(data.message || 'Delete failed');
        });
    });
}

function onPaymentButtonClick(event, reservationId, paymentWidget) {
    const generateRandomString = () =>
        window.btoa(Math.random()).slice(0, 20);
    // TOSS 결제 위젯 Javascript SDK 연동 방식 중 'Promise로 처리하기'를 적용함
    // https://docs.tosspayments.com/reference/widget-sdk#promise%EB%A1%9C-%EC%B2%98%EB%A6%AC%ED%95%98%EA%B8%B0
    const orderIdPrefix = new Date().getTime();
    paymentWidget.requestPayment({
        orderId: orderIdPrefix + generateRandomString(),
        orderName: "테스트 방탈출 예약 결제 1건",
        amount: 1000,
    }).then(function (data) {
        console.debug(data);
        fetchReservationPayment(data, reservationId);
    }).catch(function (error) {
        // TOSS 에러 처리: 에러 목록을 확인하세요
        // https://docs.tosspayments.com/reference/error-codes#failurl 로-전달되는-에러
        alert(error.code + " :" + error.message + "/ orderId : " + err.orderId);
    });
}

async function fetchReservationPayment(paymentData, reservationId) {
    const reservationPaymentRequest = {
        paymentKey: paymentData.paymentKey,
        orderId: paymentData.orderId,
        amount: paymentData.amount,
    }

    const reservationURL = PAYMENT_API_ENDPOINT.replace("{id}", reservationId);
    fetch(reservationURL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(reservationPaymentRequest),
    }).then(response => {
        if (!response.ok) { return response.json().then(data => {
                throw new Error(data.message || 'Reservation Payment failed');
            });
        } else {
            console.log("예약 결제 성공");
            window.location.reload();
        }
    }).catch(error => {
        alert(error.message);
        console.error(error.message);
    });
}
