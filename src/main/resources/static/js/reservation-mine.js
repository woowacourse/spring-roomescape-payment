document.addEventListener('DOMContentLoaded', () => {
    /*
    TODO: [2단계] 내 예약 목록 조회 기능
          endpoint 설정
     */
    fetch('/reservations-mine') // 내 예약 목록 조회 API 호출
        .then(response => {
            if (response.status === 200) return response.json();
            throw new Error('Read failed');
        })
        .then(render)
        .catch(error => console.error('Error fetching reservations:', error));
});


const paymentAmount = 1000;
const widgetClientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
const paymentWidget1 = PaymentWidget(widgetClientKey, PaymentWidget.ANONYMOUS);
paymentWidget1.renderPaymentMethods(
    "#payment-method",
    {value: paymentAmount},
    {variantKey: "DEFAULT"}
);

function onPaymentButtonClick(paymentWidget)  {
        const generateRandomString = () =>
            window.btoa(Math.random()).slice(0, 20);

        const orderIdPrefix = "WTEST";
        paymentWidget.requestPayment({
            orderId: orderIdPrefix + generateRandomString(),
            orderName: "테스트 방탈출 예약 결제 1건",
            amount: 1000,
        }).then(function (data) {
            console.debug(data);
        }).catch(function (error) {
            alert(error.code + " :" + error.message + "/ orderId : " + err.orderId);
        });
}

function render(data) {
    const tableBody = document.getElementById('table-body');
    tableBody.innerHTML = '';

    data.userReservationViewResponses.forEach(item => {
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
        if (status !== '예약') {
            // 예약 대기 상태일 때 예약 대기 취소 버튼 추가하는 코드, 상태 값은 변경 가능
            row.insertCell(4).textContent = item.paymentKey;
            row.insertCell(5).textContent = item.amount;
            const cancelCell = row.insertCell(6);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '취소';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick = function () {
                requestDeleteWaiting(item.id).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);
        } else if (item.paymentKey == null) {
            row.insertCell(4).textContent = item.paymentKey;
            row.insertCell(5).textContent = item.amount;
            const paymentCell = row.insertCell(6);
            const paymentButton = document.createElement('button');
            paymentButton.textContent = '결제';
            paymentButton.className = 'btn btn-danger';
            paymentButton.onclick = function () {
                onPaymentButtonClick(paymentWidget1)
            };
            paymentCell.appendChild(paymentButton);
        } else { // 예약 완료 상태일 때
            /*
            TODO: [미션4 - 2단계] 내 예약 목록 조회 시,
            예약 완료 상태일 때 결제 정보를 함께 보여주기
            결제 정보 필드명은 자신의 response 에 맞게 변경하기
             */
            row.insertCell(4).textContent = item.paymentKey;
            row.insertCell(5).textContent = item.amount;
            row.insertCell(6).textContent = '';
        }
    });
}

function requestDeleteWaiting(id) {
    /*
    TODO: [3단계] 예약 대기 기능 - 예약 대기 취소 API 호출
     */
    const endpoint = '/waitings?id=' + id;
    return fetch(endpoint, {
        method: 'DELETE'
    }).then(response => {
        if (response.status === 204) return;
        throw new Error('Delete failed');
    });
}
