document.addEventListener('DOMContentLoaded', () => {
    fetch('/reservations/mine') // 내 예약 목록 조회 API 호출
        .then(response => {
            if (response.status === 200) return response.json();
            throw new Error('Read failed');
        })
        .then(render)
        .catch(error => console.error('Error fetching reservations:', error));
});

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

// document.getElementById('reserve-button').addEventListener('click', onPayButtonClickWithPaymentWidget);
//
// function onPayButtonClickWithPaymentWidget(event) {
//     onPayButtonClick(event, paymentWidget);
// }

function render(data) {
    const tableBody = document.getElementById('table-body');
    tableBody.innerHTML = '';

    data.forEach(item => {
        const row = tableBody.insertRow();

        const theme = item.theme;
        const date = item.date;
        const time = item.time;
        const paymentKey = item.paymentKey;
        const amount = item.amount;
        const status = item.status;
        const waitingOrder = item.waitingOrder;

        row.insertCell(0).textContent = theme;
        row.insertCell(1).textContent = date;
        row.insertCell(2).textContent = time;

        if (status === '예약 완료') {
            row.insertCell(3).textContent = paymentKey;
            row.insertCell(4).textContent = amount;
            row.insertCell(5).textContent = status;

        } else if (status === '예약 대기') {
            row.insertCell(3).textContent = '';
            row.insertCell(4).textContent = '';
            row.insertCell(5).textContent = waitingOrder + "번째" + status;

            const cancelCell = row.insertCell(6);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '취소';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick = function () {
                requestDeleteReservation(item.id).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);

        } else { // 결제 대기 상태일 때
            row.insertCell(3).textContent = '';
            row.insertCell(4).textContent = '';
            row.insertCell(5).textContent = status;

            const cancelCell = row.insertCell(6);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '취소';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick = function () {
                requestDeleteReservation(item.id).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);

            const payCell = row.insertCell(7);
            const payButton = document.createElement('button');
            payButton.textContent = '결제';
            payButton.className = 'btn btn-pay';
            payButton.onclick = function (event) {
                onPayButtonClick(event, paymentWidget, item.id);
            };
            payCell.appendChild(payButton);
        }
    });
}

function requestDeleteReservation(id) {
    /*
    TODO: [3단계] 예약 대기 기능 - 예약 대기 취소 API 호출
     */
    const endpoint = '/reservations/' + id;
    return fetch(endpoint, {
        method: 'DELETE'
    }).then(response => {
        if (response.status === 204) return;
        throw new Error('Delete failed');
    });
}

function onPayButtonClick(event, paymentWidget, id) {
    const generateRandomString = () =>
        window.btoa(Math.random()).slice(0, 20);

    // TOSS 결제 위젯 Javascript SDK 연동 방식 중 'Promise로 처리하기'를 적용함
    // https://docs.tosspayments.com/reference/widget-sdk#promise%EB%A1%9C-%EC%B2%98%EB%A6%AC%ED%95%98%EA%B8%B0
    const orderIdPrefix = "WTEST";
    paymentWidget.requestPayment({
        orderId: orderIdPrefix + generateRandomString(),
        orderName: "테스트 방탈출 예약 결제 1건",
        amount: 1000,
    }).then(function (data) {
        console.debug(data);
        fetchReservationPayment(data, id);
    }).catch(function (error) {
        // TOSS 에러 처리: 에러 목록을 확인하세요
        // https://docs.tosspayments.com/reference/error-codes#failurl 로-전달되는-에러
        alert(error.code + " :" + error.message + "/ orderId : " + err.orderId);
    });
}

async function fetchReservationPayment(paymentData, id) {

    const reservationPaymentRequest = {
        paymentKey: paymentData.paymentKey,
        orderId: paymentData.orderId,
        amount: paymentData.amount,
        paymentType: paymentData.paymentType,
    }

    const reservationURL = "/reservations/pay/" + id;
    fetch(reservationURL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(reservationPaymentRequest),
    }).then(response => {
        if (response.status !== 201) {
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
