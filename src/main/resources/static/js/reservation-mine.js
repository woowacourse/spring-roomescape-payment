const PAYMENT_AMOUNT = 21000;

document.addEventListener('DOMContentLoaded', () => {

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

        const theme = item.themeName;
        const date = item.date;
        const time = item.time;
        const status = item.status;
        const price = item.price;

        row.insertCell(0).textContent = theme;
        row.insertCell(1).textContent = date;
        row.insertCell(2).textContent = time;
        row.insertCell(3).textContent = status;
        row.insertCell(4).textContent = price;
        if (status.includes("번째 예약대기")) { // 예약 대기 상태일 때 예약 대기 취소 버튼 추가하는 코드, 상태 값은 변경 가능

            const cancelCell = row.insertCell(5);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '취소';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick = function () {
                requestDeleteWaiting(item.id).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);
        } else if (status === "예약") { // 예약 대기 상태일 때 예약 대기 취소 버튼 추가하는 코드, 상태 값은 변경 가능

            const cancelCell = row.insertCell(5);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '취소';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick = function () {
                requestDelete(item.id).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);
        } else if (status === "결제 대기중") { // 예약 대기 상태일 때 예약 대기 취소 버튼 추가하는 코드, 상태 값은 변경 가능

            const cancelCell = row.insertCell(5);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '결제';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick =
                function onReservationButtonClickWithPaymentWidget(event) {
                    popupModal(price, item.id);
                }
            cancelCell.appendChild(cancelButton);
        } else { // 예약 완료 상태일 때
            row.insertCell(5).textContent = '';
        }
    });
}

function popupModal(price, id) {
    let modal = document.getElementsByClassName("payment-modal-back")[0]; // 첫 번째 요소 선택
    if (modal) { // modal이 존재하는지 확인
        modal.style.display = 'block'; // display를 block으로 설정하여 모달 띄우기
    }

    // ------  결제위젯 초기화 ------
    // @docs https://docs.tosspayments.com/reference/widget-sdk#sdk-설치-및-초기화
    // @docs https://docs.tosspayments.com/reference/widget-sdk#renderpaymentmethods선택자-결제-금액-옵션
    const widgetClientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
    paymentWidget = PaymentWidget(widgetClientKey, PaymentWidget.ANONYMOUS);
    paymentWidget.renderPaymentMethods(
        "#payment-method",
        {value: price},
        {variantKey: "DEFAULT"}
    );

    document.getElementById('reserve-button').addEventListener('click', onReservationButtonClickWithPaymentWidget);

    function onReservationButtonClickWithPaymentWidget(event) {
        onReservationButtonClick(event, paymentWidget, id);
    }
}

function onReservationButtonClick(event, paymentWidget, id) {

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
        memberReservationId: id,
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

function requestDeleteWaiting(id) {
    const endpoint = `/reservations/${id}/waiting`;
    return fetch(endpoint, {
        method: 'DELETE'
    }).then(response => {
        if (response.status === 204) return;
        throw new Error('삭제에 실패했습니다.');
    });
}

function requestDelete(id) {
    const endpoint = `/reservations/${id}`;
    return fetch(endpoint, {
        method: 'DELETE'
    }).then(response => {
        if (response.status === 204) return;
        throw new Error('삭제에 실패했습니다.');
    });
}
