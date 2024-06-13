document.addEventListener('DOMContentLoaded', () => {
    /*
    TODO: [2단계] 내 예약 목록 조회 기능
          endpoint 설정
     */
    fetch('/reservations/my') // 내 예약 목록 조회 API 호출
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
        TODO: [2단계] 내 예약 목록 조회 기능
              response 명세에 맞춰 값 설정
         */
        const theme = item.theme;
        const date = item.date;
        const time = item.time;
        const status = item.status;

        row.insertCell(0).textContent = theme;
        row.insertCell(1).textContent = date;
        row.insertCell(2).textContent = time;
        row.insertCell(3).textContent = status;

        /*
        TODO: [3단계] 예약 대기 기능 - 예약 대기 취소 기능 구현 후 활성화
         */
        const cancelCell = row.insertCell(4);
        const cancelButton = document.createElement('button');
        cancelButton.className = 'btn btn-danger';

        const payCell = row.insertCell(5);
        const payButton = document.createElement('button');
        payButton.className = 'btn btn-primary';

        if (status !== '예약') {
            cancelButton.textContent = '대기 취소';
            cancelButton.onclick = function () {
                requestDelete(item.reservationId).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);

            if (status === '결제대기') {
                payButton.textContent = '결제';
                payButton.onclick = function () {
                    showTossPaymentModal(item);
                };
                payCell.appendChild(payButton);
            }
        }
        else{
            cancelButton.textContent = '예약 취소';
            cancelButton.onclick = function () {
                requestDelete(item.reservationId).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);

            row.insertCell(6).textContent = item.paymentKey;
            row.insertCell(7).textContent = item.amount;
        }
    });
}

function showTossPaymentModal(item) {

    $('#payment-modal').modal('show');

    const paymentAmount = 20000;
    const widgetClientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
    const paymentWidget = PaymentWidget(widgetClientKey, PaymentWidget.ANONYMOUS);

    paymentWidget.renderPaymentMethods(
        "#payment-method",
        {value: paymentAmount},
        {variantKey: "DEFAULT"}
    );

    document.getElementById('reserve-button').onclick = function () {
        const generateRandomString = () => window.btoa(Math.random()).slice(0, 20);
        const orderIdPrefix = "ROOMESCAPE_";
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

async function fetchReservationPayment(paymentData, reservationData) {
    const reservationPaymentRequest = {
        reservationId: reservationData.reservationId,
        paymentKey: paymentData.paymentKey,
        orderId: paymentData.orderId,
        amount: paymentData.amount,
        paymentType: paymentData.paymentType,
    }

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
                window.alert("예약 결제 실패 메시지 : " + errorBody.message);
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

function requestDelete(id) {
    /*
    TODO: [3단계] 예약 대기 기능 - 예약 대기 취소 API 호출
     */
    const endpoint = `/reservations/${id}`;
    return fetch(endpoint, {
        method: 'DELETE'
    }).then(response => {
        if (response.status === 204) return;
        throw new Error('Delete failed');
    });
}
