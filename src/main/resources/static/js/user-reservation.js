const THEME_API_ENDPOINT = '/themes';

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

  // ------  결제위젯 초기화 ------
  // @docs https://docs.tosspayments.com/reference/widget-sdk#sdk-설치-및-초기화
  // @docs https://docs.tosspayments.com/reference/widget-sdk#renderpaymentmethods선택자-결제-금액-옵션
  const paymentAmount = 0;
  const widgetClientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
  const paymentWidget = PaymentWidget(widgetClientKey, PaymentWidget.ANONYMOUS);
  const renderedPaymentWidget = paymentWidget.renderPaymentMethods(
      "#payment-method",
      {value: paymentAmount},
      {variantKey: "DEFAULT"}
  );

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

  document.getElementById('reserve-confirm').addEventListener('click', onReservationButtonClickWithPaymentWidget);
  document.getElementById('wait-button').addEventListener('click', onWaitButtonClick);
  function onReservationButtonClickWithPaymentWidget(event) {
    onReservationButtonClick(event, paymentWidget, renderedPaymentWidget);
  }
});

function renderTheme(themes) {
  const themeSlots = document.getElementById('theme-slots');
  themeSlots.innerHTML = '';
  themes.forEach(theme => {
    const name = theme.name;
    const themeId = theme.id;
    const price = theme.price;
    themeSlots.appendChild(createSlot('theme', name, themeId, null, price));
  });
}

function createSlot(type, text, id, booked, price) {
  const div = document.createElement('div');
  div.className = type + '-slot cursor-pointer bg-light border rounded p-3 mb-2';
  div.textContent = text;
  div.setAttribute('data-' + type + '-id', id);
  if (type === 'theme') {
    div.setAttribute('data-' + type + '-price', price);
  }
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
  fetch('/times/available?date='+date+"&themeId="+themeId, { // 예약 가능 시간 조회 API endpoint
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
  times.forEach(response => {
    const time = response.time;
    const startAt = time.startAt;
    const timeId = time.id;
    const alreadyBooked = response.isBooked;

    const div = createSlot('time', startAt, timeId, alreadyBooked, null); // createSlot('time', 시작 시간, time id, 예약 여부)
    timeSlots.appendChild(div);
  });
}

function checkDateAndThemeAndTime() {
  const selectedDate = document.getElementById("datepicker").value;
  const selectedThemeElement = document.querySelector('.theme-slot.active');
  const selectedTimeElement = document.querySelector('.time-slot.active');
  const reserveButton = document.getElementById("reserve-button");
  const waitButton = document.getElementById("wait-button");

  if (selectedDate && selectedThemeElement && selectedTimeElement) {
    if (selectedTimeElement.getAttribute('data-time-booked') === 'true') {
      // 선택된 시간이 이미 예약된 경우
      reserveButton.classList.add("disabled");
      waitButton.classList.remove("disabled"); // 예약 대기 버튼 활성화
    } else {
      // 선택된 시간이 예약 가능한 경우
      reserveButton.classList.remove("disabled");
      waitButton.classList.add("disabled"); // 예약 대기 버튼 비활성화
    }
  } else {
    // 날짜, 테마, 시간 중 하나라도 선택되지 않은 경우
    reserveButton.classList.add("disabled");
    waitButton.classList.add("disabled");
  }
}

function onReservationButtonClick(event, paymentWidget, renderedPaymentWidget) {
  const selectedDate = document.getElementById("datepicker").value;
  const themeName = document.querySelector('.theme-slot.active')?.textContent;
  const selectedThemeId = document.querySelector('.theme-slot.active')?.getAttribute('data-theme-id');
  const price = document.querySelector('.theme-slot.active')?.getAttribute('data-theme-price');
  const selectedTimeId = document.querySelector('.time-slot.active')?.getAttribute('data-time-id');
  // const name = document.getElementById('user-name').value;
  renderedPaymentWidget.updateAmount(price);
  if (selectedDate && selectedThemeId && selectedTimeId) {

    const reservationData = {
      date: selectedDate,
      themeId: selectedThemeId,
      timeId: selectedTimeId,
      price: price
    };
    const generateRandomString = () =>
        window.btoa(Math.random()).slice(0, 20);
    // TOSS 결제 위젯 Javascript SDK 연동 방식 중 'Promise로 처리하기'를 적용함
    // https://docs.tosspayments.com/reference/widget-sdk#promise%EB%A1%9C-%EC%B2%98%EB%A6%AC%ED%95%98%EA%B8%B0
    const orderIdPrefix = "WTEST";
    paymentWidget.requestPayment({
      orderId: orderIdPrefix + generateRandomString(),
      orderName: themeName,
      amount: price
    }).then(function (data) {
      console.debug(data);
      fetchReservationPayment(data, reservationData);
    }).catch(function (error) {
      // TOSS 에러 처리: 에러 목록을 확인하세요
      // https://docs.tosspayments.com/reference/error-codes#failurl 로-전달되는-에러
      alert(error.message);
    });
  } else {
    alert("Please select a date, theme, and time before making a reservation.");
  }
}

async function fetchReservationPayment(paymentData, reservationData) {
  const reservationPaymentRequest = {
    date: reservationData.date,
    themeId: reservationData.themeId,
    timeId: reservationData.timeId,
    paymentKey: paymentData.paymentKey,
    orderId: paymentData.orderId,
  }

  const reservationURL = "/reservations";
  fetch(reservationURL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(reservationPaymentRequest),
  }).then(response => {
    if (!response.ok) {
      return response.json().then(errorBody => {
        console.error("예약 생성 실패 : " + JSON.stringify(errorBody));
        window.alert("예약을 생성하는 데 실패했습니다.");
      });
    } else {
      response.json().then(successBody => {
        console.log("예약 생성 성공 : " + JSON.stringify(successBody));
        confirmPurchaseReservation(paymentData);
      });
    }
  }).catch(error => {
    console.error(error.message);
  });
}

async function confirmPurchaseReservation(paymentData) {
  const price = document.querySelector('.theme-slot.active')?.getAttribute('data-theme-price');
  const request = {
    amount: price,
    paymentKey: paymentData.paymentKey,
    orderId: paymentData.orderId
  }
  fetch('/payment',
    {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(paymentData)
  }).then(response => {
    if (!response.ok) {
      return response.json().then(errorBody => {
        console.error("결제 실패 : " + JSON.stringify(errorBody));
        window.alert("결제 실패 메시지");
      });
    } else {
      response.json().then(successBody => {
        console.log("결제 성공 : " + JSON.stringify(successBody));
        alert("결제가 완료되었습니다.");
        window.location.reload();
      });
    }
  })
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

    fetch('/reservations/queue', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(reservationData)
    })
        .then(response => {
          if (!response.ok) {
            response.json().then(data => alert(data.detail));
            throw new Error('Reservation waiting failed');
          }
          return response.json();
        })
        .then(data => {
          alert('Reservation waiting successful!');
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
        else {
          response.json().then(data => alert(data.detail));

          throw new Error('Read failed');
        }
      });
}
