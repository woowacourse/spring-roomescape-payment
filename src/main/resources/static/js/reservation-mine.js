document.addEventListener('DOMContentLoaded', () => {

    fetch('/reservations-mine') // 내 예약 목록 조회 API 호출
        .then(response => {
            if (response.status === 200) return response.json();
            throw new Error('Read failed');
        })
        .then(render)
        .catch(error => console.error('Error fetching reservations:', error));
});

const RESERVATION_STATUS = {
    RESERVED: '예약',
    PAYMENT_PENDING: '예약 가능',
    WAITING: (rank) => `${rank}번째 예약 대기`
};

function render(data) {
    const tableBody = document.getElementById('table-body');
    tableBody.innerHTML = '';

    function makeStatusMessage(status, waitingRank) {
        if (status === 'CONFIRMED') {
            return RESERVATION_STATUS.RESERVED;
        } else if (status === 'PAYMENT_PENDING') {
            return RESERVATION_STATUS.PAYMENT_PENDING;
        }
        return RESERVATION_STATUS.WAITING(waitingRank);
    }

    data.forEach(item => {
        const row = tableBody.insertRow();

        const theme = item.theme;
        const date = item.date;
        const time = item.time;
        const status = item.status;
        const waitingRank = item.waitingRank;
        const statusMessage = makeStatusMessage(status, waitingRank);

        row.insertCell(0).textContent = theme;
        row.insertCell(1).textContent = date;
        row.insertCell(2).textContent = time;
        row.insertCell(3).textContent = statusMessage;

        if (statusMessage === RESERVATION_STATUS.RESERVED) { // 예약 완료 상태일 때
            /*
            TODO: [미션4 - 2단계] 내 예약 목록 조회 시,
           예약 완료 상태일 때 결제 정보를 함께 보여주기
           결제 정보 필드명은 자신의 response 에 맞게 변경하기
           */
            row.insertCell(4).textContent = item.paymentKey;
            row.insertCell(5).textContent = item.amount;
            row.insertCell(6).textContent = '';
        } else if (statusMessage === RESERVATION_STATUS.PAYMENT_PENDING) { // 예약 가능 상태일 때
            row.insertCell(4);
            row.insertCell(5);
            const cancelCell = row.insertCell(6);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '취소';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick = function () {
                requestDeleteWaiting(item.reservationSlotId).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);
            const paymentCell = row.insertCell(7);
            const paymentButton = document.createElement('button');
            paymentButton.textContent = '결제하기';
            paymentButton.className = 'btn btn-danger';
            paymentButton.onclick = function () {
                requestWaitingPayment(item.reservationSlotId).then(() => window.location.reload());
            }
            paymentCell.appendChild(paymentButton);

        } else { // 예약 대기 상태일 때 예약 대기 취소 버튼 추가하는 코드, 상태 값은 변경 가능
            row.insertCell(4);
            row.insertCell(5);
            const cancelCell = row.insertCell(6);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '취소';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick = function () {
                requestDeleteWaiting(item.reservationSlotId).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);
        }
    });
}

function requestWaitingPayment(reservationSlotId) {
    if (reservationSlotId) {
        const reservationData = {
            reservationSlotId: reservationSlotId
        };

        const generateRandomString = () => window.btoa(Math.random()).slice(0, 20);
        const orderIdPrefix = "RESERVATION_";
        paymentWidget.requestPayment({
            orderId: orderIdPrefix + generateRandomString(),
            orderName: "테스트 방탈출 예약 결제 1건",
            amount: 1_000,
        }).then(function (data) {
            console.debug(data);
            fetchReservationPayment(data, reservationData);
        }).catch(function (error) {
            // TOSS 에러 처리: 에러 목록을 확인하세요
            // https://docs.tosspayments.com/reference/error-codes#failurl 로-전달되는-에러
            alert(error.code + " :" + error.message + "/ orderId : " + err.orderId);
        });
    } else {
        alert("오류가 발생했습니다.");
    }
}

async function fetchReservationPayment(paymentData, reservationData) {
    // 결제 요청 완료 후 suceessUrl
    const reservationPaymentRequest = {
        reservationSlotId: reservationData.reservationSlotId,
        paymentKey: paymentData.paymentKey,
        orderId: paymentData.orderId,
        amount: paymentData.amount,
        paymentType: paymentData.paymentType,
    }

    const reservationURL = "/waiting-reservations/confirm";
    fetch(reservationURL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(reservationPaymentRequest),
    }).then(response => {
        if (!response.ok) {
            return response.json().then(errorBody => {
                let errorMessage = errorBody.message;
                console.error("예약 결제 실패 : " + errorMessage);
                window.alert(errorMessage);
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

function requestDeleteWaiting(reservationSlotId) {
    const endpoint = `/waiting-reservations/${reservationSlotId}`;
    return fetch(endpoint, {
        method: 'DELETE'
    }).then(response => {
        if (response.status === 204) return;
        throw new Error('Delete failed');
    });
}

// ------  결제위젯 초기화 ------
// @docs https://docs.tosspayments.com/reference/widget-sdk#sdk-설치-및-초기화
// @docs https://docs.tosspayments.com/reference/widget-sdk#renderpaymentmethods선택자-결제-금액-옵션
const paymentAmount = 1_000;
const widgetClientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
const paymentWidget = PaymentWidget(widgetClientKey, PaymentWidget.ANONYMOUS);
paymentWidget.renderPaymentMethods(
    "#payment-method",
    {value: paymentAmount},
    {variantKey: "DEFAULT"}
);
