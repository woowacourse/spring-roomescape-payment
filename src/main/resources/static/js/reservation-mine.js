document.addEventListener('DOMContentLoaded', () => {
    fetch('/reservations-mine') // 내 예약 목록 조회 API 호출
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
    const paymentAmount = 1999999;
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

        const reservationId = item.id;
        const theme = item.theme;
        const date = item.date;
        const time = item.time;
        const status = item.status;
        document.getElementById('reserve-button').addEventListener('click', () => onPayButtonClickWithPaymentWidget(paymentWidget, reservationId));
        row.insertCell(0).textContent = theme;
        row.insertCell(1).textContent = date;
        row.insertCell(2).textContent = time;
        row.insertCell(3).textContent = status;

        if (status !== '예약' && status !== '결제 대기') { // 예약 대기 상태일 때 예약 대기 취소 버튼 추가하는 코드, 상태 값은 변경 가능
            const cancelCell = row.insertCell(4);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '취소';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick = function () {
                requestDeleteWaiting(item.id).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);
        } else { // 예약 완료 상태일 때
            if (status === '예약') {
                row.insertCell(4).textContent = '';
                row.insertCell(5).textContent = item.paymentKey;
                row.insertCell(6).textContent = item.amount;
            } else {
                const paymentCell = row.insertCell(4);
                const paymentButton = document.createElement('button');
                const modal = document.querySelector('.modal');
                const modalClose = document.querySelector('.close_btn');
                paymentButton.className = 'btn btn-danger';
                paymentButton.textContent = '결제';
                paymentButton.addEventListener('click', function () {
                    modal.classList.add('on')
                });
                modalClose.addEventListener('click', function () {
                    modal.classList.remove('on');
                });
                paymentCell.appendChild(paymentButton);
            }
        }
    });
}

function requestDeleteWaiting(id) {
    const endpoint = `/waiting/${id}`;
    return fetch(endpoint, {
        method: 'DELETE'
    }).then(response => {
        if (response.status === 204) return;
        throw new Error('Delete failed');
    });
}

function onPayButtonClickWithPaymentWidget(paymentWidget, reservationId) {
    const selectedReservationId = reservationId;

    if (selectedReservationId) {

        const generateRandomString = () =>
            window.btoa(Math.random()).slice(0, 20);
        // TOSS 결제 위젯 Javascript SDK 연동 방식 중 'Promise로 처리하기'를 적용함
        // https://docs.tosspayments.com/reference/widget-sdk#promise%EB%A1%9C-%EC%B2%98%EB%A6%AC%ED%95%98%EA%B8%B0
        const orderIdPrefix = "MOVINPOKE";
        paymentWidget.requestPayment({
            orderId: orderIdPrefix + generateRandomString(),
            orderName: "어둠의 방탈출을 예약합니다.",
            amount: 1000,
        }).then(function (data) {
            console.debug(data);
            fetchReservationPayment(data, selectedReservationId);
        }).catch(function (error) {
            // TOSS 에러 처리: 에러 목록을 확인하세요
            // https://docs.tosspayments.com/reference/error-codes#failurl 로-전달되는-에러
            alert(error.code + " :" + error.message + "/ orderId : " + err.orderId);
        });
    } else {
        alert("Please select a date, theme, and time before making a reservation.");
    }
}

async function fetchReservationPayment(paymentData, selectedReservationId) {
    const reservationPaymentRequest = {
        reservationId: selectedReservationId,
        paymentKey: paymentData.paymentKey,
        orderId: paymentData.orderId,
        amount: paymentData.amount,
        paymentType: paymentData.paymentType,
    }

    const reservationURL = "/reservations-payment";
    fetch(reservationURL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(reservationPaymentRequest),
    }).then(response => {
        if (response.status !== 201) {
            return response.text().then(errorBody => {
                console.error("예약 결제 실패 : " + errorBody);
                window.alert("예약 결제에 실패했습니다." + errorBody);
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
