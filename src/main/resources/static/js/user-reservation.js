const THEME_API_ENDPOINT = '/api/v1/themes';
paymentWidget = null;
document.addEventListener('DOMContentLoaded', () => {
    requestRead(THEME_API_ENDPOINT)
        .then(renderTheme)
        .catch(error => console.error('Error fetching times:', error));

    flatpickr("#datepicker", {
        inline: true,
        onChange: function (selectedDates, dateStr, instance) {
            if (dateStr === '') return;
            checkDate();
        }
    });

    document.getElementById('theme-slots').addEventListener('click', event => {
        if (event.target.classList.contains('theme-slot')) {
            document.querySelectorAll('.theme-slot').forEach(slot => slot.classList.remove('active'));
            event.target.classList.add('active');
            checkDateAndTheme();
        }
    });

    document.getElementById('time-slots').addEventListener('click', event => {
        if (event.target.classList.contains('time-slot') && !event.target.classList.contains('disabled')) {
            document.querySelectorAll('.time-slot').forEach(slot => slot.classList.remove('active'));
            event.target.classList.add('active');
            checkDateAndThemeAndTime();
        }
    });

    document.getElementById("close-modal").addEventListener("click", function () {
        let modal = document.getElementsByClassName("payment-modal-back")[0];
        if (modal) {
            modal.style.display = 'none';
        }
    });

    document.getElementById('reservation-button').addEventListener('click', popupModal);
    document.getElementById('wait-button').addEventListener('click', onWaitButtonClick);

});

function popupModal() {
    const modal = document.getElementsByClassName("payment-modal-back")[0];
    if (modal) {
        modal.style.display = 'block';
    }

    const price = document.getElementById('price-amount').getAttribute('priceAmount');
    const widgetClientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
    paymentWidget = PaymentWidget(widgetClientKey, PaymentWidget.ANONYMOUS);
    paymentWidget.renderPaymentMethods("#payment-method", { value: price }, { variantKey: "DEFAULT" });

    const reserveButton = document.getElementById('reserve-button');
    reserveButton.removeEventListener('click', onReservationButtonClickWithPaymentWidget); // 기존 이벤트 제거
    reserveButton.addEventListener('click', onReservationButtonClickWithPaymentWidget); // 새 이벤트 추가
}

function onReservationButtonClickWithPaymentWidget(event) {
    const selectedDate = document.getElementById("datepicker").value;
    const selectedThemeId = document.querySelector('.theme-slot.active')?.getAttribute('data-theme-id');
    const theme = document.querySelector('.theme-slot.active')?.textContent;
    const selectedTimeId = document.querySelector('.time-slot.active')?.getAttribute('data-time-id');
    const price = document.getElementById('price-amount').getAttribute('priceAmount');

    if (selectedDate && selectedThemeId && selectedTimeId) {
        const reservationData = { date: selectedDate, themeId: selectedThemeId, timeId: selectedTimeId };
        const generateRandomString = () => window.btoa(Math.random()).slice(0, 20);
        const orderIdPrefix = "WTEST";

        const loadingSpinner = document.getElementById('loading-spinner');
        const loadingOverlay = document.getElementById('loading-overlay');
        loadingSpinner.style.display = 'block';
        loadingOverlay.style.display = 'block';

        paymentWidget.requestPayment({
            orderId: orderIdPrefix + generateRandomString(),
            orderName: theme + " 예약 결제",
            amount: price,
        }).then(function (data) {
            loadingSpinner.style.display = 'none';
            loadingOverlay.style.display = 'none';
            fetchReservationPayment(data, reservationData);
            alert("결제가 완료되었습니다.");
        }).catch(function (error) {
            loadingSpinner.style.display = 'none';
            loadingOverlay.style.display = 'none';
            alert(error.code + " :" + error.message + "/ orderId : " + error.orderId);
        });
    } else {
        alert("예약날짜, 테마, 예약시간을 모두 선택하세요.");
    }
}

function renderTheme(themes) {
    const themeSlots = document.getElementById('theme-slots');
    themeSlots.innerHTML = '';
    themes.forEach(theme => {
        const name = theme.name;
        const themeId = theme.id;

        themeSlots.appendChild(createSlot('theme', name, themeId));
    });
}

function createSlot(type, text, id, booked) {
    const div = document.createElement('div');
    div.className = type + '-slot cursor-pointer bg-light border rounded p-3 mb-2';
    div.textContent = text;
    div.setAttribute('data-' + type + '-id', id);
    if (type === 'time') {
        div.setAttribute('data-time-booked', booked);
    }
    return div;
}

function checkDate() {
    const selectedDate = document.getElementById("datepicker").value;
    if (selectedDate) {
        const themeSection = document.getElementById("theme-section");
        if (themeSection.classList.contains("disabled")) {
            themeSection.classList.remove("disabled");
        }
        const timeSlots = document.getElementById('time-slots');
        timeSlots.innerHTML = '';

        requestRead(THEME_API_ENDPOINT)
            .then(renderTheme)
            .catch(error => console.error('Error fetching times:', error));
    }
}

function checkDateAndTheme() {
    const selectedDate = document.getElementById("datepicker").value;
    const selectedThemeElement = document.querySelector('.theme-slot.active');
    if (selectedDate && selectedThemeElement) {
        const selectedThemeId = selectedThemeElement.getAttribute('data-theme-id');
        fetchAvailableTimes(selectedDate, selectedThemeId);
    }
}

function fetchAvailableTimes(date, themeId) {
    fetch(`/api/v1/times/available?date=${date}&themeId=${themeId}`, { // 예약 가능 시간 조회 API endpoint
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    }).then(response => {
        if (response.status === 200) return response.json();
        throw new Error('Read failed');
    }).then(renderAvailableTimes)
        .catch(error => console.error("Error fetching available times:", error));
}

function renderAvailableTimes(times) {
    const timeSection = document.getElementById("time-section");
    if (timeSection.classList.contains("disabled")) {
        timeSection.classList.remove("disabled");
    }

    const timeSlots = document.getElementById('time-slots');
    timeSlots.innerHTML = '';
    if (times.length === 0) {
        timeSlots.innerHTML = '<div class="no-times">선택할 수 있는 시간이 없습니다.</div>';
        return;
    }
    times.forEach(time => {
        const startAt = time.startAt;
        const timeId = time.timeId;
        const alreadyBooked = time.alreadyBooked;
        const div = createSlot('time', startAt, timeId, alreadyBooked); // createSlot('time', 시작 시간, time id, 예약 여부)
        timeSlots.appendChild(div);
    });
}

function fetchPrice(date, themeId, timeId) {
    fetch(`/api/v1/reservations/price?date=${date}&themeId=${themeId}&timeId=${timeId}`, { // 예약 가격 조회 API endpoint
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    }).then(response => {
        if (response.status === 200) return response.json();
        throw new Error('Read failed');
    }).then(renderPrice)
        .catch(error => console.error("Error fetching available times:", error));
}

function renderPrice(price) {
    const summarySection = document.getElementById("price");

    summarySection.innerHTML = '';
    const priceSpan = document.createElement('span');
    priceSpan.className = 'price' + '-slot cursor-pointer';
    priceSpan.id = 'price-amount';
    priceSpan.setAttribute('priceAmount', price);
    priceSpan.textContent = price;
    summarySection.appendChild(priceSpan);
}

function checkDateAndThemeAndTime() {
    const selectedDate = document.getElementById("datepicker").value;
    const selectedThemeElement = document.querySelector('.theme-slot.active');
    const selectedTimeElement = document.querySelector('.time-slot.active');
    const reserveButton = document.getElementById("reserve-button");
    const reservationButton = document.getElementById("reservation-button");
    const waitButton = document.getElementById("wait-button");

    let price;
    if (selectedDate && selectedThemeElement && selectedTimeElement) {
        if (selectedTimeElement.getAttribute('data-time-booked') === 'true') {
            // 선택된 시간이 이미 예약된 경우
            reserveButton.classList.add("disabled");
            reservationButton.classList.add("disabled");
            waitButton.classList.remove("disabled"); // 예약 대기 버튼 활성화

            // 여기서 하면 됨
            const selectedThemeId = selectedThemeElement.getAttribute('data-theme-id');
            const selectedTimeId = selectedTimeElement.getAttribute('data-time-id');
            fetchPrice(selectedDate, selectedThemeId, selectedTimeId);

        } else {
            // 선택된 시간이 예약 가능한 경우
            reserveButton.classList.remove("disabled");
            reservationButton.classList.remove("disabled");
            waitButton.classList.add("disabled"); // 예약 대기 버튼 비활성화
            const selectedThemeId = selectedThemeElement.getAttribute('data-theme-id');
            const selectedTimeId = selectedTimeElement.getAttribute('data-time-id');
            fetchPrice(selectedDate, selectedThemeId, selectedTimeId);
        }
    } else {
        // 날짜, 테마, 시간 중 하나라도 선택되지 않은 경우
        reserveButton.classList.add("disabled");
        waitButton.classList.add("disabled");
    }
}


function onReservationButtonClick(event, paymentWidget) {
    const selectedDate = document.getElementById("datepicker").value;
    const selectedThemeId = document.querySelector('.theme-slot.active')?.getAttribute('data-theme-id');
    const theme = document.querySelector('.theme-slot.active')?.textContent;
    const selectedTimeId = document.querySelector('.time-slot.active')?.getAttribute('data-time-id');
    const price = document.getElementById('price-amount').getAttribute('priceAmount');
    if (selectedDate && selectedThemeId && selectedTimeId) {

        const reservationData = {
            date: selectedDate,
            themeId: selectedThemeId,
            timeId: selectedTimeId
        };

        const generateRandomString = () =>
            window.btoa(Math.random()).slice(0, 20);

        // TOSS 결제 위젯 Javascript SDK 연동 방식 중 'Promise로 처리하기'를 적용함
        // https://docs.tosspayments.com/reference/widget-sdk#promise%EB%A1%9C-%EC%B2%98%EB%A6%AC%ED%95%98%EA%B8%B0
        const orderIdPrefix = "WTEST";
        paymentWidget.requestPayment({
            orderId: orderIdPrefix + generateRandomString(),
            orderName: theme + " 예약 결제",
            amount: price,
        }).then(function (data) {
            console.debug(data);
            fetchReservationPayment(data, reservationData);
            alert("결제가 완료되었습니다.");
        }).catch(function (error) {
            // TOSS 에러 처리: 에러 목록을 확인하세요
            // https://docs.tosspayments.com/reference/error-codes#failurl 로-전달되는-에러
            alert(error.code + " :" + error.message + "/ orderId : " + err.orderId);
        });
    } else {
        alert("예약날짜, 테마, 예약시간을 모두 선택해주세요.");
    }
}

async function fetchReservationPayment(paymentData, reservationData) {

    const reservationPaymentRequest = {
        date: reservationData.date,
        themeId: reservationData.themeId,
        timeId: reservationData.timeId,
        paymentKey: paymentData.paymentKey,
        orderId: paymentData.orderId,
        amount: paymentData.amount,
        paymentType: paymentData.paymentType,
    }

    const reservationURL = "/api/v1/reservations";
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

function onWaitButtonClick() {
    const selectedDate = document.getElementById("datepicker").value;
    const selectedThemeId = document.querySelector('.theme-slot.active')?.getAttribute('data-theme-id');
    const selectedTimeId = document.querySelector('.time-slot.active')?.getAttribute('data-time-id');

    if (selectedDate && selectedThemeId && selectedTimeId) {
        const reservationData = {
            date: selectedDate,
            themeId: selectedThemeId,
            timeId: selectedTimeId
        };

        fetch('/api/v1/reservations/waiting', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(reservationData)
        })
            .then(response => {
                if (!response.ok) throw new Error('Reservation waiting failed');
                return response.json();
            })
            .then(data => {
                alert('대기 등록이 완료 되었습니다.');
                window.location.href = "/";
            })
            .catch(error => {
                alert("An error occurred while making the reservation waiting.");
                console.error(error);
            });
    } else {
        alert("Please select a date, theme, and time before making a reservation waiting.");
    }
}

function requestRead(endpoint) {
    return fetch(endpoint)
        .then(response => {
            if (response.status === 200) return response.json();
            throw new Error('Read failed');
        });
}
