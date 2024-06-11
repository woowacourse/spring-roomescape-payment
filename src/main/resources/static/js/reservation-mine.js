document.addEventListener('DOMContentLoaded', () => {
    fetch('/reservations-mine') // 내 예약 목록 조회 API 호출
        .then(response => {
            if (response.status === 200) return response.json();
            throw new Error('Read failed');
        })
        .then(render)
        .catch(error => console.error('Error fetching reservations:', error));

    // TODO
    const paymentAmount = 1000;
    const widgetClientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
    const paymentWidget = PaymentWidget(widgetClientKey, PaymentWidget.ANONYMOUS);
    paymentWidget.renderPaymentMethods(
        "#payment-method",
        {value: paymentAmount},
        {variantKey: "DEFAULT"}
    );

    document.getElementById('payment-button')
        .addEventListener('click', onReservationButtonClickWithPaymentWidget);

    function onReservationButtonClickWithPaymentWidget(event) {
        onReservationButtonClick(event, paymentWidget);
    }

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
});

let bookedMemberId = {};

function render(data) {
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

        /*
        TODO: [3단계] 예약 대기 기능 - 예약 대기 취소 기능 구현 후 활성화
         */
        if (status === '결제 대기') { // TODO: 예약 완료 상태일 때
            const paymentCell = row.insertCell(4);
            const paymentButton = document.createElement('button');
            paymentButton.textContent = '결제';
            paymentButton.className = 'btn btn-danger';
            paymentButton.onclick = function() {
                bookedMemberId = item.id;
                const modal = document.getElementById('myModal');
                modal.style.display = "block";
            };
            paymentCell.appendChild(paymentButton);

        } else if (status !== '예약') { // 예약 대기 상l태일 때 예약 대기 취소 버튼 추가하는 코드, 상태 값은 변경 가능
            row.insertCell(4).textContent = '';
            const cancelCell = row.insertCell(5);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '취소';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick = function () {
                requestDeleteWaiting(item.id).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);

        } else { // 예약 완료 상태일 때
            /*
           TODO: [미션4 - 2단계] 내 예약 목록 조회 시,
           예약 완료 상태일 때 결제 정보를 함께 보여주기
           결제 정보 필드명은 자신의 response 에 맞게 변경하기
           */
            row.insertCell(4).textContent = '';
            row.insertCell(5).textContent = '';
            row.insertCell(6).textContent = item.paymentKey;
            row.insertCell(7).textContent = item.amount;
        }
    });
}

function requestDeleteWaiting(id) {
    /*
    TODO: [3단계] 예약 대기 기능 - 예약 대기 취소 API 호출
     */
    const endpoint = '/reservations/waiting/' + id;
    return fetch(endpoint, {
        method: 'DELETE'
    }).then(response => {
        if (response.status === 204) return;
        throw new Error('Delete failed');
    });
}
