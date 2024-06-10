document.addEventListener('DOMContentLoaded', () => {
    // ------  결제위젯 초기화 ------
    // @docs https://docs.tosspayments.com/reference/widget-sdk#sdk-설치-및-초기화
    // @docs https://docs.tosspayments.com/reference/widget-sdk#renderpaymentmethods선택자-결제-금액-옵션
    const paymentAmount = 128000;
    const widgetClientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
    const paymentWidget = PaymentWidget(widgetClientKey, PaymentWidget.ANONYMOUS);
    paymentWidget.renderPaymentMethods(
        "#payment-method",
        {value: paymentAmount},
        {variantKey: "DEFAULT"}
    );

    fetch('/my/reservaitons') // 내 예약 목록 조회 API 호출
        .then(response => {
            if (response.status === 200) return response.json();
            throw new Error('Read failed');
        })
        .then(data => render(data, paymentWidget))
        .catch(error => console.error('Error fetching reservations:', error));
});

function render(data, paymentWidget) {
    const tableBody = document.getElementById('table-body');
    tableBody.innerHTML = '';

    data.forEach(item => {
        const row = tableBody.insertRow();

        const theme = item.themeName;
        const date = item.date;
        const time = item.startAt;
        const status = item.status;

        row.insertCell(0).textContent = theme;
        row.insertCell(1).textContent = date;
        row.insertCell(2).textContent = time;
        row.insertCell(3).textContent = status;

        if (status.endsWith("예약대기")) { // 예약 대기 상태일 때 예약 대기 취소 버튼 추가하는 코드, 상태 값은 변경 가능
            const cancelCell = row.insertCell(4);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '취소';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick = function () {
                requestDeleteWaiting(item.ownerId).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);
        } else if (status === "결제 대기") {
            const paymentCell = row.insertCell(4);
            const paymentButton = document.createElement('button');
            paymentButton.textContent = '결제';
            paymentButton.className = 'btn btn-danger';
            paymentButton.onclick = function () {
                // 모달 요소 가져오기
                var modal = document.getElementById("myModal");

                // 모달 열기
                modal.style.display = "block";

                // 닫기 버튼 요소 가져오기
                var span = document.getElementsByClassName("close")[0];

                // 닫기 버튼 클릭 시 모달 닫기
                span.onclick = function () {
                    modal.style.display = "none";
                }

                // 결제 버튼 가져오기
                var btn = document.getElementById("payment-button");

                btn.onclick = function () {
                    requestPayment(item.ownerId, paymentWidget).then(() => window.location.reload());
                };
            };
            paymentCell.appendChild(paymentButton);
        } else { // 예약 완료 상태일 때
            row.insertCell(4).textContent = '';
            row.insertCell(5).textContent = item.payment.paymentKey;
            row.insertCell(6).textContent = item.payment.amount;
        }
    });
}

function requestDeleteWaiting(id) {
    const endpoint = `/waitings/${id}`;
    return fetch(endpoint, {
        method: 'DELETE'
    }).then(response => {
        if (response.status === 204) return;
        throw new Error('Delete failed');
    });
}

function requestPayment(id, paymentWidget) {
    const reservationData = {
        reservationId: id
    };

    const generateRandomString = () =>
        window.btoa(Math.random()).slice(0, 20);
    // TOSS 결제 위젯 Javascript SDK 연동 방식 중 'Promise로 처리하기'를 적용함
    // https://docs.tosspayments.com/reference/widget-sdk#promise%EB%A1%9C-%EC%B2%98%EB%A6%AC%ED%95%98%EA%B8%B0
    const orderIdPrefix = "DokiDokiBangTalchul";
    paymentWidget.requestPayment({
        orderId: orderIdPrefix + generateRandomString(),
        orderName: "테스트 방탈출 예약 결제 1건",
        amount: 128000
    }).then(function (data) {
        console.debug(data);
        fetchReservationPayment(data, reservationData);
        var modal = document.getElementById("myModal");
        modal.style.display = "none";
    }).catch(function (error) {
        // TOSS 에러 처리: 에러 목록을 확인하세요
        // https://docs.tosspayments.com/reference/error-codes#failurl 로-전달되는-에러
        alert(error.message);
        var modal = document.getElementById("myModal");
        modal.style.display = "none";
    });
}

async function fetchReservationPayment(paymentData, reservationData) {
    console.log(paymentData);
    console.log(reservationData);

    const reservationPaymentRequest = {
        reservationId: reservationData.reservationId,
        paymentKey: paymentData.paymentKey,
        orderId: paymentData.orderId,
        amount: paymentData.amount,
        paymentType: paymentData.paymentType,
    }

    const paymentURL = "/reservations/payment";
    fetch(paymentURL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(reservationPaymentRequest),
    }).then(response => {
        if (!response.ok) {
            return response.json().then(errorBody => {
                console.error("예약 결제 실패 : " + JSON.stringify(errorBody.detail));
                window.alert(errorBody.detail);
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
