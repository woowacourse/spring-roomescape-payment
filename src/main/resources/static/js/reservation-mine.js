document.addEventListener('DOMContentLoaded', () => {

    fetch('/reservations/mine') // 내 예약 목록 조회 API 호출
        .then(response => {
            if (response.status === 200) return response.json();
            throw new Error('Read failed');
        })
        .then(render)
        .catch(error => console.error('Error fetching reservations:', error));

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

    document.getElementById('payment-button').addEventListener('click', onReservationButtonClickWithPaymentWidget);

    function onReservationButtonClickWithPaymentWidget(event) {
        onReservationButtonClick(event, paymentWidget);
    }
});

function onReservationButtonClick(event, paymentWidget) {
    const reservationId = document.getElementById('payment-button')
        .getAttribute('data-reservation-id');

    const reservationData = {
        reservationId: reservationId,
    };
    const generateRandomString = () =>
        window.btoa(Math.random()).slice(0, 20);
    // TOSS 결제 위젯 Javascript SDK 연동 방식 중 'Promise로 처리하기'를 적용함
    // https://docs.tosspayments.com/reference/widget-sdk#promise%EB%A1%9C-%EC%B2%98%EB%A6%AC%ED%95%98%EA%B8%B0
    const orderIdPrefix = "FLINT_ZORO";
    paymentWidget.requestPayment({
        orderId: orderIdPrefix + generateRandomString(),
        orderName: "테스트 방탈출 예약 결제 1건",
        amount: 1000,
    }).then(function (data) {
        console.log(data);
        fetchReservationPayment(data, reservationData);
    }).catch(function (error) {
        // TOSS 에러 처리: 에러 목록을 확인하세요
        // https://docs.tosspayments.com/reference/error-codes#failurl 로-전달되는-에러
        alert(error.code + " :" + error.message + "/ orderId : " + err.orderId);
    });

}

function render(data) {
    const tableBody = document.getElementById('table-body');
    tableBody.innerHTML = '';

    data.forEach(item => {
        const row = tableBody.insertRow();

        const id = item.id;
        const theme = item.theme;
        const date = item.date;
        const time = item.time;
        const status = item.status;

        row.insertCell(0).textContent = id;
        row.insertCell(1).textContent = theme;
        row.insertCell(2).textContent = date;
        row.insertCell(3).textContent = time
        row.insertCell(4).textContent = status;

        if (status.includes('예약대기')) { // 예약 대기 상태일 때 예약 대기 취소 버튼 추가하는 코드, 상태 값은 변경 가능
            row.insertCell(5).textContent = '';
            const cancelCell = row.insertCell(6);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '취소';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick = function () {
                requestDeleteWaiting(item.id).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);
            row.insertCell(7).textContent = '';
            row.insertCell(8).textContent = '';
        } else if (status === '결제 대기') {
            const pendingButton = document.createElement('button');
            const pendingCell = row.insertCell(5);
            pendingButton.textContent = '예약 확정';
            pendingButton.className = 'btn btn-primary';

            // 현재 선택된 예약 ID를 결제 버튼에 저장
            document.getElementById('payment-button')
                .setAttribute('data-reservation-id', id);

            pendingButton.onclick = function () {
                // 결제창 표시
                const paymentContainer = document.getElementById('payment-container');
                paymentContainer.classList.add('show');
            };
            pendingCell.appendChild(pendingButton);
            row.insertCell(6).textContent = '';
            row.insertCell(7).textContent = '';
            row.insertCell(8).textContent = '';
        } else { // 예약 완료 상태일 때
            row.insertCell(5).textContent = '';
            row.insertCell(6).textContent = '';
            row.insertCell(7).textContent = item.paymentKey;
            row.insertCell(8).textContent = item.amount;
        }
    });
}

async function fetchReservationPayment(paymentData, reservationData) {
    const reservationPaymentRequest = {
        reservationId: reservationData.reservationId,
        paymentKey: paymentData.paymentKey,
        orderId: paymentData.orderId,
        amount: paymentData.amount,
        paymentType: paymentData.paymentType,
    };
    console.log(reservationPaymentRequest);
    const endpoint = `/reservations/waiting/${reservationData.reservationId}/confirm`;
    fetch(endpoint, {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(reservationPaymentRequest),
    }).then(response => {
        if (response.status !== 200) {
            return response.json().then(errorBody => {
                console.error("예약 결제 실패 : " + JSON.stringify(errorBody));
                window.alert("예약 결제 실패 : " + JSON.stringify(errorBody));
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
    const endpoint = `/reservations/waiting/${id}`;
    return fetch(endpoint, {
        method: 'DELETE'
    }).then(response => {
        if (response.status === 204) return;
        throw new Error('Delete failed');
    });
}
