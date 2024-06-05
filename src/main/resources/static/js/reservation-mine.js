let selectedReservationId;
let selectedThemePrice;

document.addEventListener('DOMContentLoaded', () => {
    /*
    TODO: [2단계] 내 예약 목록 조회 기능
          endpoint 설정
     */
    Promise.all([
        fetch('/reservations/mine').then(response => {
            if (!response.ok) throw new Error('Failed to fetch reservations');
            return response.json();
        }),
        fetch('/reservation-waitings/mine').then(response => {
            if (!response.ok) throw new Error('Failed to fetch waitings');
            return response.json();
        })
    ]).then(([reservations, waitings]) => {
        reservations.forEach(reservation => {
                reservation.status = "예약";
        });
        waitings.forEach(waiting => {
            if (waiting.deniedAt) {
                waiting.status = "예약대기 거절";
            } else {
                waiting.status = (waiting.order) + "번째 예약대기";
            }
        });
        const combinedData = [...reservations, ...waitings];
        render(combinedData);
    }).catch(error => console.error('Error fetching data:', error));


    const paymentAmount = -1;
    const widgetClientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
    const paymentWidget = PaymentWidget(widgetClientKey, PaymentWidget.ANONYMOUS);
    paymentWidget.renderPaymentMethods(
        "#payment-method",
        {value: paymentAmount},
        {variantKey: "DEFAULT"}
    );

    const modal = document.querySelector('.modal');
    const modalClose = document.querySelector('.close_btn');
    modalClose.addEventListener('click',function(){
        modal.style.display = 'none';
        selectedThemePrice = '';
        selectedReservationId = '';
    });

    document.getElementById('payment-button').addEventListener('click', onPaymentButtonClickWithPaymentWidget);
    function onPaymentButtonClickWithPaymentWidget(event) {
        if (selectedReservationId && selectedThemePrice) {
            requestPayment(paymentWidget);
        } else {
            alert("결제 창을 띄울 수 없습니다.");
        }
    }
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
        const time = item.time.startAt;
        const status = item.status;

        row.insertCell(0).textContent = theme.name;
        row.insertCell(1).textContent = date;
        row.insertCell(2).textContent = time;
        row.insertCell(3).textContent = status;

        const noDataPlaceholder = '없음';
        /*
        TODO: [3단계] 예약 대기 기능 - 예약 대기 취소 기능 구현 후 활성화
         */
        if (status === '예약') {
            const payment = item.payment;
            if (payment.id) {
                row.insertCell(4).textContent = payment.paymentKey;
                row.insertCell(5).textContent = payment.totalAmount;
                row.insertCell(6).textContent = '';
            } else {
                row.insertCell(4).textContent = '결제 대기';
                row.insertCell(5).textContent = '결제 대기';
                const payCell = row.insertCell(6);
                const payButton = document.createElement('button');
                payButton.textContent = '결제';
                payButton.className = 'btn btn-info';
                const modal = document.querySelector('.modal');
                payButton.onclick = function () {
                    modal.style.display="flex";
                    console.log(theme);
                    console.log(item);
                    selectedThemePrice = theme.price;
                    selectedReservationId = item.reservationId;
                };
                payCell.appendChild(payButton);
            }
        } else {
            row.insertCell(4).textContent = '없음';
            row.insertCell(5).textContent = '없음';
            const cancelCell = row.insertCell(6);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '취소';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick = function () {
                requestDeleteWaiting(item.id).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);
        }
    });
}

function requestDeleteWaiting(id) {
    /*
    TODO: [3단계] 예약 대기 기능 - 예약 대기 취소 API 호출
     */
    const endpoint = `/reservation-waitings/${id}`;
    return fetch(endpoint, {
        method: 'DELETE'
    }).then(response => {
        if (response.status === 204) return;
        throw new Error('Delete failed');
    });
}

function requestPayment(paymentWidget) {
    const generateRandomString = () =>
        window.btoa(Math.random()).slice(0, 20);
    const orderIdPrefix = "WTEST1111";

    paymentWidget.renderPaymentMethods().updateAmount(selectedThemePrice);
    paymentWidget.requestPayment({
        orderId: orderIdPrefix + generateRandomString(),
        orderName: "테스트 방탈출 예약 결제 1건",
        amount: selectedThemePrice,
    }).then(function (data) {
        console.debug(data);
        fetchReservationPayment(data);
    }).catch(function (error) {
        alert(error.code + " :" + error.message + "/ orderId : " + error.orderId);
    });
}


async function fetchReservationPayment(paymentData) {
    const reservationPaymentRequest = {
        reservationId: selectedReservationId,
        paymentKey: paymentData.paymentKey,
        orderId: paymentData.orderId,
        amount: paymentData.amount
    }

    const reservationURL = "/payments";
    fetch(reservationURL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(reservationPaymentRequest),
    }).then(response => {
        if (!response.ok) {
            return response.json().then(errorBody => {
                console.error("예약 결제 실패 : " + errorBody);
                window.alert("예약 결제 실패 : " + errorBody.message);
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
