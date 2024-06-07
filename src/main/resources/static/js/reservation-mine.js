document.addEventListener('DOMContentLoaded', () => {
    /*
    TODO: [2단계] 내 예약 목록 조회 기능
          endpoint 설정
     */
    const payButton = document.getElementById('payment-button');
    payButton.classList.add("disabled");
    fetch('/reservations/mine') // 내 예약 목록 조회 API 호출
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
        if (status === 'RESERVATION') { // 예약 대기 상태일 때 예약 대기 취소 버튼 추가하는 코드, 상태 값은 변경 가능
            /*
            TODO: [미션4 - 2단계] 내 예약 목록 조회 시,
                예약 완료 상태일 때 결제 정보를 함께 보여주기
                결제 정보 필드명은 자신의 response 에 맞게 변경하기
            */
            row.insertCell(4).textContent = '';
            row.insertCell(5).textContent = item.paymentKey;
            row.insertCell(6).textContent = item.totalAmount;
        } else if(status === 'PAYMENT_WAITING') {
            const payCell = row.insertCell(4);
            const paymentButton = document.createElement('input');
            paymentButton.type = 'radio';
            paymentButton.name = 'payment';
            paymentButton.value = item.id;
            paymentButton.className = 'btn-payment';
            paymentButton.onclick = function () {
                requestPaymentWaiting(item.id).then(() => window.location.reload());
            };
            const payLabel = document.createElement('label');
            payLabel.textContent = '결제'
            payCell.appendChild(paymentButton);
            payCell.appendChild(payLabel);
        } else { // 예약 완료 상태일 때
            const cancelCell = row.insertCell(4);
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

function requestPaymentWaiting(id) {
    const payButton = document.getElementById('payment-button');
    payButton.classList.remove("disabled");
    const widgetClientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
    const customerKey = 'evmbwvjYyP4gZ6Lv5KyOz' // 내 상점에서 고객을 구분하기 위해 발급한 고객의 고유 ID
    const paymentWidget = PaymentWidget(widgetClientKey, customerKey);
    getReservationInformation(id).then(reservationInfo => {
        requestPriceRead(reservationInfo.themeId).then(payAmount => {
            paymentWidget.renderPaymentMethods(
                "#payment-method",
                {value: payAmount.price},
                {variantKey: "DEFAULT"}
            );
            document.getElementById('payment-button').addEventListener('click', onPaymentButtonClickWithPaymentWidget);
        });
        function onPaymentButtonClickWithPaymentWidget(event) {
            onPaymentButtonClick(event, paymentWidget, reservationInfo);
        }
    });
}

function requestDeleteWaiting(id) {
    /*
    TODO: [3단계] 예약 대기 기능 - 예약 대기 취소 API 호출
     */
    const endpoint = `/waitings/`;
    return fetch(endpoint + id, {
        method: 'DELETE'
    }).then(response => {
        if (response.status === 204) return;
        throw new Error('Delete failed');
    });
}

function onPaymentButtonClick(event, paymentWidget, reservationInfo) {
    const reservationData = {
        id: reservationInfo.id,
        date: reservationInfo.date,
        themeId: reservationInfo.themeId,
        timeId: reservationInfo.timeId
    };
    const generateRandomString = () =>
        window.btoa(Math.random()).slice(0, 20);
    /*
       TODO: [1단계]
             - orderIdPrefix 를 자신만의 prefix로 변경
       */
    // TOSS 결제 위젯 Javascript SDK 연동 방식 중 'Promise로 처리하기'를 적용함
    // https://docs.tosspayments.com/reference/widget-sdk#promise%EB%A1%9C-%EC%B2%98%EB%A6%AC%ED%95%98%EA%B8%B0
    const orderIdPrefix = "WTEST";
    paymentWidget.requestPayment({
        orderId: orderIdPrefix + generateRandomString(),
        orderName: "테스트 방탈출 예약 결제 1건",
        amount: 1000,
    }).then(function (data) {
        console.debug(data);
        fetchPaymentWaitingPayment(data, reservationData);
    }).catch(function (error) {
        // TOSS 에러 처리: 에러 목록을 확인하세요
        // https://docs.tosspayments.com/reference/error-codes#failurl 로-전달되는-에러
        alert(error.code + " :" + error.message + "/ orderId : " + error.orderId);
    });
}

function getReservationInformation(id) {
    /*
    TODO: [3단계] 예약 대기 기능 - 예약 대기 취소 API 호출
     */
    const endpoint = `/reservation/information/`;
    return new Promise((resolve, reject) => {
        fetch(endpoint + id, {
            method: 'GET'
        }).then(response => {
            if (response.status === 200) {
                return response.json();
            } else {
                reject(new Error('Read failed'));
            }
        }).then(data => {
            resolve(data);
        }).catch(error => {
            reject(error);
        });
    });
}

function fetchPaymentWaitingPayment(paymentData, reservationData) {
    const reservationPaymentRequest = {
        id: reservationData.id,
        date: reservationData.date,
        themeId: reservationData.themeId,
        timeId: reservationData.timeId,
        paymentKey: paymentData.paymentKey,
        orderId: paymentData.orderId,
        amount: paymentData.amount,
        //paymentType: paymentData.paymentType,
    }
    console.log(reservationPaymentRequest)

    const reservationURL = "/reservation/approve";
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
                window.alert("예약 결제 실패 메시지: " + errorBody.message);
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

function requestPriceRead(id) {
    const endpoint = `/themes/${id}/price`;
    return new Promise((resolve, reject) => {
        fetch(endpoint, {
            method: 'GET'
        }).then(response => {
            if (response.status === 200) {
                return response.json();
            } else {
                reject(new Error('Read failed'));
            }
        }).then(data => {
            resolve(data);
        }).catch(error => {
            reject(error);
        });
    });
}
