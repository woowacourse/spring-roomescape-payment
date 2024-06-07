const paymentAmount = 1000;
const widgetClientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
const paymentWidget = PaymentWidget(widgetClientKey, PaymentWidget.ANONYMOUS);
paymentWidget.renderPaymentMethods(
    "#payment-method",
    {value: paymentAmount},
    {variantKey: "DEFAULT"}
);

let reservationId = {};

// 모달 닫기 버튼 기능
document.querySelector('.close').addEventListener('click', function() {
    const modal = document.getElementById('myModal');
    modal.style.display = "none";
});

// 모달 외부 클릭시 닫기 기능
window.addEventListener('click', function(event1) {
    const modal = document.getElementById('myModal');
    if (event1.target === modal) {
        modal.style.display = "none";
    }
});

document.getElementById('payment-button')
    .addEventListener('click', onPaymentButtonClickWithPaymentWidget);

function onPaymentButtonClickWithPaymentWidget(event) {
    onPaymentButtonClick(event, paymentWidget);
}

document.addEventListener('DOMContentLoaded', () => {
    fetch('/reservations/accounts') // 내 예약 목록 조회 API 호출
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
        const time = item.startAt;
        const status = item.status;
        const paymentStatus = item.paymentStatus;

        row.insertCell(0).textContent = theme;
        row.insertCell(1).textContent = date;
        row.insertCell(2).textContent = time;
        row.insertCell(3).textContent = status;

        if (status !== '예약') { // 예약 대기 상태일 때 예약 대기 취소 버튼 추가하는 코드, 상태 값은 변경 가능
            const cancelCell = row.insertCell(4);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '취소';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick = function () {
                requestDeleteWaiting(item.waitingId).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);
        } else { // 예약 완료 상태일 때
            /*
            TODO: [미션4 - 2단계] 내 예약 목록 조회 시,
             예약 완료 상태일 때 결제 정보를 함께 보여주기
             결제 정보 필드명은 자신의 response 에 맞게 변경하기
             */
            row.insertCell(4).textContent = '';
            row.insertCell(5).textContent = item.paymentKey;
            row.insertCell(6).textContent = item.amount;
        }

        if (paymentStatus !== 'COMPLETED' && status === '예약') {
            row.insertCell(7).textContent = '결제 대기';

            const paymentCell = row.insertCell(8);
            const paymentButton = document.createElement('button');
            paymentButton.textContent = '결제하기';
            paymentButton.className = 'btn btn-danger';
            //TODO
            paymentButton.onclick = function () {
                reservationId = item.id;
                const modal = document.getElementById('myModal');
                modal.style.display = "block";
            };
            paymentCell.appendChild(paymentButton);
        } else {
            row.insertCell(7).textContent = '결제 완료';
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

function onPaymentButtonClick(event, paymentWidget) {
    const generateRandomString = () =>
        window.btoa(Math.random()).slice(0, 20);

    const orderIdPrefix = "WTEST";
    paymentWidget.requestPayment({
        orderId: orderIdPrefix + generateRandomString(),
        orderName: "테스트 방탈출 예약 결제 1건",
        amount: 1000,
    }).then(function (data) {
        console.debug(data);
        fetchPayment(data, reservationId);
    }).catch(function (error) {
    });
}

async function fetchPayment(paymentData, id) {
    const paymentRequest = {
        paymentKey: paymentData.paymentKey,
        orderId: paymentData.orderId,
        amount: paymentData.amount,
        paymentType: paymentData.paymentType,
    }

    const reservationURL = `/reservations/${id}/payment`;
    fetch(reservationURL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(paymentRequest),
    }).then(response => {
        if (!response.ok) {
            return response.json().then(errorBody => {
                console.error("예약 결제 실패 : " + JSON.stringify(errorBody));
                window.alert(errorBody.errorMessage);
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
