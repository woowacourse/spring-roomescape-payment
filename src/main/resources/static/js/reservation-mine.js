document.addEventListener('DOMContentLoaded', () => {
    fetch('/users/reservations') // 내 예약 목록 조회 API 호출
        .then(response => {
            if (response.status === 200) return response.json();
            throw new Error('Read failed');
        })
        .then(render)
        .catch(error => console.error('Error fetching reservations:', error));
});

function render(data) {
    const tableBody = document.getElementById('table-body');
    tableBody.innerHTML = '';

    data.forEach(item => {
        const row = tableBody.insertRow();

        /*
        내 예약 목록 조회 기능
              response 명세에 맞춰 값 설정
         */
        const theme = item.theme.name;
        const date = item.date;
        const time = item.time.startAt;
        const status = item.status;

        row.insertCell(0).textContent = theme;
        row.insertCell(1).textContent = date;
        row.insertCell(2).textContent = time;
        row.insertCell(3).textContent = status;

        /*
        예약 대기 기능 - 예약 대기 취소 기능 구현 후 활성화
         */
        if (status === '예약') {
            row.insertCell(4).textContent = '';
            row.insertCell(5).textContent = item.payment.paymentKey;
            row.insertCell(6).textContent = item.payment.amount;

        } else if (status === '결제 대기') {
            const paymentCell = row.insertCell(4);
            const paymentButton = document.createElement('button');
            paymentButton.textContent = '결제';
            paymentButton.className = 'btn btn-primary'
            paymentButton.onclick = () => initWidget(item.id);
            paymentCell.appendChild(paymentButton);
            row.insertCell(5).textContent = '';
            row.insertCell(6).textContent = '';
        } else {
            const cancelCell = row.insertCell(4);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '취소';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick = function () {
                requestDeleteWaiting(item.id).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);
            row.insertCell(5).textContent = '';
            row.insertCell(6).textContent = '';
        }
    });
}

function initWidget(reservationId) {
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

    const reserveButton = document.getElementById('reserve-button');
    reserveButton.style.display = 'block';

    reserveButton.addEventListener('click', onReservationButtonClickWithPaymentWidget);

    function onReservationButtonClickWithPaymentWidget(event) {
        onReservationButtonClick(event, paymentWidget, reservationId);
    }
}

function onReservationButtonClick(event, paymentWidget, reservationId) {
    const generateRandomString = () =>
        window.btoa(Math.random()).slice(0, 20);
    /*
    [1단계] - orderIdPrefix 를 자신만의 prefix로 변경
    */
    // TOSS 결제 위젯 Javascript SDK 연동 방식 중 'Promise로 처리하기'를 적용함
    // https://docs.tosspayments.com/reference/widget-sdk#promise%EB%A1%9C-%EC%B2%98%EB%A6%AC%ED%95%98%EA%B8%B0
    const orderName = "테스트 방탈출 예약 결제 1건";
    const orderIdPrefix = "ROOM_ESCAPE_";

    paymentWidget.requestPayment({
        orderId: orderIdPrefix + generateRandomString(),
        orderName: orderName,
        amount: 1000,
    }).then(function (data) {
        console.debug(data);
        fetchReservationPayment(data, reservationId, orderName);
    }).catch(function (error) {
        // TOSS 에러 처리: 에러 목록을 확인하세요
        // https://docs.tosspayments.com/reference/error-codes#failurl 로-전달되는-에러
        alert(error.code + " :" + error.message + "/ orderId : " + err.orderId);
    });
}


async function fetchReservationPayment(paymentData, reservationId, orderName) {
    /*
    [1단계]
        - 자신의 예약 API request에 맞게 reservationPaymentRequest 필드명 수정
        - 내 서버 URL에 맞게 reservationURL 변경
        - 예약 결제 실패 시, 사용자가 실패 사유를 알 수 있도록 alert 에서 에러 메시지 수정
    */
    const reservationPaymentRequest = {
        paymentKey: paymentData.paymentKey,
        orderId: paymentData.orderId,
        amount: paymentData.amount,
        paymentType: paymentData.paymentType,
        orderName: orderName,
    }

    const reservationURL = `/pending-payments/${reservationId}/payment`;
    fetch(reservationURL, {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(reservationPaymentRequest),
    }).then(response => {
        if (!response.ok) {
            return response.json().then(errorBody => {
                console.error("예약 결제 실패 : " + JSON.stringify(errorBody));
                window.alert(errorBody.message);
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


function requestDeleteWaiting(id) {
    /*
    예약 대기 기능 - 예약 대기 취소 API 호출
     */
    const WAITING_PATH = '/waitings';

    const endpoint = `${WAITING_PATH}/${id}`;
    return fetch(endpoint, {
        method: 'DELETE'
    }).then(response => {
        if (response.status === 204) return;
        throw new Error('Delete failed');
    });
}
