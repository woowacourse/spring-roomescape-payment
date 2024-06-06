document.addEventListener('DOMContentLoaded', () => {
    /*
     [2단계] 내 예약 목록 조회 기능
          endpoint 설정
     */
    fetch('/reservations-mine') // 내 예약 목록 조회 API 호출
        .then(response => {
            if (response.status === 200) return response.json();
            throw new Error('Read failed');
        })
        .then(render)
        .catch(error => console.error('Error fetching reservations:', error));

    const paymentAmount = 1000;
    const widgetClientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
    const paymentWidget = PaymentWidget(widgetClientKey, PaymentWidget.ANONYMOUS);
    paymentWidget.renderPaymentMethods(
        "#payment-method",
        {value: paymentAmount},
        {variantKey: "DEFAULT"}
    );
});

function render(data) {
    const tableBody = document.getElementById('table-body');
    tableBody.innerHTML = '';

    data.forEach(item => {
        const row = tableBody.insertRow();

        let status;
        if (item.status === 'RESERVED') {
            status = '예약';
        } else if (item.status === 'PAYMENT_PENDING') {
            status = '결제 대기';
        } else if (item.status === 'WAITING') {
            status = `${item.rank}번째 예약대기`;
        } else if (item.status === 'CANCELED') {
            status = '취소';
        } else {
            status = '관리자에게 문의 바랍니다.';
        }

        row.insertCell(0).textContent = item.reservationId;
        row.insertCell(1).textContent = item.theme;
        row.insertCell(2).textContent = item.date;
        row.insertCell(3).textContent = item.time;
        row.insertCell(4).textContent = status;

        /*
        [3단계] 예약 대기 기능 - 예약 대기 취소 기능 구현 후 활성화
         */

        if (item.status === 'PAYMENT_PENDING') { // 결제 대기 상태일 때 결제 버튼 추가하는 코드
            const actionCell = row.insertCell(5);
            const payButton = document.createElement('button');
            payButton.textContent = '결제';
            payButton.className = 'btn btn-primary';
            payButton.onclick = function () {
                showPaymentModal(item); // 결제 모달 창 띄우는 함수 호출
            };
            actionCell.appendChild(payButton);
        } else if (item.status !== 'CANCELED') { // 예약 대기 상태일 때 예약 대기 취소 버튼 추가하는 코드
            const cancelCell = row.insertCell(5);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '취소';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick = function () {
                requestDeleteWaiting(item.reservationId).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);
        } else { // 예약 완료 상태일 때
            row.insertCell(5).textContent = '';
        }
    });
}

function showPaymentModal(item) {

    // 결제 위젯 초기화
    const paymentAmount = 1000; // 실제 결제 금액을 설정
    const widgetClientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
    const paymentWidget = PaymentWidget(widgetClientKey, PaymentWidget.ANONYMOUS);

    $('#payment-modal').modal('show'); // 결제 모달을 띄우는 코드

    paymentWidget.renderPaymentMethods(
        "#payment-method",
        {value: paymentAmount},
        {variantKey: "DEFAULT"}
    );

    document.getElementById('reserve-button').onclick = function () {
        const generateRandomString = () => window.btoa(Math.random()).slice(0, 20);
        const orderIdPrefix = "WTEST";
        paymentWidget.requestPayment({
            orderId: orderIdPrefix + generateRandomString(),
            orderName: "테스트 방탈출 예약 결제 1건",
            amount: paymentAmount,
        }).then(function (data) {
            console.debug(data);
            fetchReservationPayment(data, {
                reservationId: item.reservationId
            });
        }).catch(function (error) {
            alert(error.code + " :" + error.message + "/ orderId : " + error.orderId);
        });
    };
}


function requestDeleteWaiting(id) {
    /*
    TODO: [3단계] 예약 대기 기능 - 예약 대기 취소 API 호출
     */
    const endpoint = '/waitings/' + id;
    return fetch(endpoint, {
        method: 'DELETE'
    }).then(response => {
        if (response.status === 204) return;
        throw new Error('Delete failed');
    });
}

function onReservationButtonClick(event, paymentWidget) {
    const generateRandomString = () =>
        window.btoa(Math.random()).slice(0, 20);
    /*
    TODO: [1단계]
          - orderIdPrefix 를 자신만의 prefix로 변경
    */
    // TOSS 결제 위젯 Javascript SDK 연동 방식 중 'Promise로 처리하기'를 적용함
    // https://docs.tosspayments.com/reference/widget-sdk#promise%EB%A1%9C-%EC%B2%98%EB%A6%AC%ED%95%98%EA%B8%B0
    const orderIdPrefix = "WTEST";
    paymentWidget.requestPayment({
        orderId: orderIdPrefix + generateRandomString(),
        orderName: "테스트 방탈출 예약 결제 1건",
        amount: 1000,
    }).then(function (data) {
        console.debug(data);
        fetchReservationPayment(data, reservationData)
            .then(() => window.location.reload());
    }).catch(function (error) {
        // TOSS 에러 처리: 에러 목록을 확인하세요
        // https://docs.tosspayments.com/reference/error-codes#failurl 로-전달되는-에러
        alert(error.code + " :" + error.message + "/ orderId : " + err.orderId);
    });
}

async function fetchReservationPayment(paymentData, reservationData) {
    console.log(reservationData);
    /*
    TODO: [1단계]
        - 자신의 예약 API request에 맞게 reservationPaymentRequest 필드명 수정
        - 내 서버 URL에 맞게 reservationURL 변경
        - 예약 결제 실패 시, 사용자가 실패 사유를 알 수 있도록 alert 에서 에러 메시지 수정
    */
    const reservationPaymentRequest = {
        reservationId: reservationData.reservationId,
        paymentKey: paymentData.paymentKey,
        orderId: paymentData.orderId,
        amount: paymentData.amount,
        paymentType: paymentData.paymentType,
    }

    console.log(reservationPaymentRequest);

    const reservationURL = "/reservations/payment";
    fetch(reservationURL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(reservationPaymentRequest),
    }).then(response => {
        if (!response.ok) {
            return response.json().then(errorBody => {
                console.error("예약 결제 실패 : " + JSON.stringify(errorBody));
                window.alert("예약 결제 실패 메시지");
            });
        } else {
            response.json().then(successBody => {
                console.log("예약 결제 성공 : " + JSON.stringify(successBody));
                window.location.reload();
            });
        }
    }).catch(error => {
        console.error(error.message);
    });
}
