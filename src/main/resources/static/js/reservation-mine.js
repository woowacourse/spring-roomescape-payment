document.addEventListener('DOMContentLoaded', () => {
    const paymentAmount = 1000;
    const widgetClientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
    const paymentWidget = PaymentWidget(widgetClientKey, PaymentWidget.ANONYMOUS);

    paymentWidget.renderPaymentMethods(
        "#payment-method",
        { value: paymentAmount },
        { variantKey: "DEFAULT" }
    );

    fetch('/reservations/mine') // 내 예약 목록 조회 API 호출
        .then(response => {
            if (response.status === 200) return response.json();
            throw new Error('Read failed');
        })
        .then(data => render(data, paymentWidget)) // paymentWidget 넘김
        .catch(error => console.error('Error fetching reservations:', error));
});


function render(data, paymentWidget) {
    const tableBody = document.getElementById('table-body');
    tableBody.innerHTML = '';

    data.forEach(item => {
        const row = tableBody.insertRow();

        const theme = item.theme;
        const date = item.date;
        const time = item.time;
        const status = item.status;

        row.insertCell(0).textContent = theme;
        row.insertCell(1).textContent = date;
        row.insertCell(2).textContent = time;
        row.insertCell(3).textContent = status;

        if (status !== '예약') { // 예약 대기 상태일 때
            const cancelCell = row.insertCell(4);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '취소';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick = function () {
                requestDeleteWaiting(item.id).then(() => window.location.reload());
            };

            const payCell = row.insertCell(5);
            const payButton = document.createElement('button');
            payButton.textContent = '결제';
            payButton.className = 'btn btn-danger';
            payButton.onclick = () => {
                onReservationButtonClick(item, 1000, paymentWidget);
            };

            payCell.appendChild(payButton);
            cancelCell.appendChild(cancelButton);
        } else { // 예약 완료 상태일 때
            row.insertCell(4).textContent = '';
            row.insertCell(5).textContent = '';
            row.insertCell(6).textContent = item.paymentKey;
            row.insertCell(7).textContent = item.amount;
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

function onReservationButtonClick(item, amount, paymentWidget) {
    const waitingId = item.id;

    if (!waitingId || !amount) {
        alert("유효하지 않은 대기 정보입니다.");
        return;
    }

    const generateRandomString = () =>
        window.btoa(Math.random()).slice(0, 20);

    const orderIdPrefix = "WTEST";
    paymentWidget.requestPayment({
        orderId: orderIdPrefix + generateRandomString(),
        orderName: "예약 대기 결제",
        amount: amount,
    }).then(function (paymentData) {
        console.debug(paymentData);
        fetchReservationPayment(waitingId, item, paymentData);
    }).catch(function (error) {
        alert(error.code + " :" + error.message);
    });
}

async function fetchReservationPayment(waitingId, item, paymentData) {
    const reservationURL = `/waitings/approve/${waitingId}`;

    const body = {
        date: item.date,
        timeId: item.timeId,
        themeId: item.themeId,
        paymentKey: paymentData.paymentKey,
        orderId: paymentData.orderId,
        amount: paymentData.amount,
    };

    fetch(reservationURL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(body),
    }).then(response => {
        if (!response.ok) {
            return response.json().then(errorBody => {
                console.error("결제 승인 실패 : " + JSON.stringify(errorBody));
                window.alert(errorBody.message);
            });
        } else {
            response.json().then(successBody => {
                console.log("결제 승인 성공 : " + JSON.stringify(successBody));
                window.alert("결제가 완료되었습니다.");
                window.location.reload();
            });
        }
    }).catch(error => {
        console.error("결제 승인 실패 : " + error.message);
        window.alert(error.message);
    });
}

