const THEME_API_ENDPOINT = '/themes';
const RESERVATION_API_ENDPOINT = '/reservations';

document.addEventListener('DOMContentLoaded', () => {
  requestRead(THEME_API_ENDPOINT)
      .then(renderTheme)
      .catch(error => console.error('Error fetching times:', error));

  flatpickr("#datepicker", {
    inline: true,
    onChange: function (selectedDates, dateStr, instance) {
      if (dateStr === '') return;
      checkDate();
    },
    disable: [
      function (date) {
        // 과거 날짜 선택 비활성화
        return date < new Date().setHours(0, 0, 0, 0);
      }
    ]
  });
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

  document.getElementById('reserve-button').addEventListener('click', onReservationButtonClickWithPaymentWidget);
  document.getElementById('wait-button').addEventListener('click', onWaitButtonClick);

  function onReservationButtonClickWithPaymentWidget(event) {
    onReservationButtonClick(event, paymentWidget);
  }
});

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
    // if (booked) {
    //   div.classList.add('disabled');
    // }
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

  fetch(`${RESERVATION_API_ENDPOINT}/${themeId}?date=${date}`, { // 예약 가능 시간 조회 API endpoint
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  }).then(response => {
    if (response.status === 200) return response.json();
    response.text().then(text => {
      alert('ERROR! ' + text);
      throw new Error('Read failed');
    });
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

function checkDateAndThemeAndTime() {
  const selectedDate = document.getElementById("datepicker").value;
  const selectedThemeElement = document.querySelector('.theme-slot.active');
  const selectedTimeElement = document.querySelector('.time-slot.active');
  const reserveButton = document.getElementById("reserve-button");
  const waitButton = document.getElementById("wait-button")

  if (selectedDate && selectedThemeElement && selectedTimeElement) {
    if (selectedTimeElement.getAttribute('data-time-booked') === 'true') {
      // 선택된 시간이 이미 예약된 경우
      reserveButton.classList.add("disabled");
      waitButton.classList.remove("disabled");  // 예약 대기 버튼 활성화
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

async function onReservationButtonClick(event, paymentWidget) {
  const selectedDate = document.getElementById("datepicker").value;
  const selectedThemeId = document.querySelector('.theme-slot.active')?.getAttribute('data-theme-id');
  const selectedTimeId = document.querySelector('.time-slot.active')?.getAttribute('data-time-id');

  if (selectedDate && selectedThemeId && selectedTimeId) {

    const reservationData = {
      date: selectedDate,
      themeId: selectedThemeId,
      timeId: selectedTimeId
    };

    const generateRandomString = () =>
        window.btoa(Math.random()).slice(0, 20);

    const selectedThemePrice = await requestRead(THEME_API_ENDPOINT + "/" + selectedThemeId)
        .then(data => {
          return data.price;
        })
        .catch(error => {
          console.error("Error fetching theme data:", error);
          return null;
        });

    /*
    TODO: [1단계]
          - orderIdPrefix 를 자신만의 prefix로 변경
    */
    // TOSS 결제 위젯 Javascript SDK 연동 방식 중 'Promise로 처리하기'를 적용함
    // https://docs.tosspayments.com/reference/widget-sdk#promise%EB%A1%9C-%EC%B2%98%EB%A6%AC%ED%95%98%EA%B8%B0
    paymentWidget.renderPaymentMethods().updateAmount(selectedThemePrice);
    const orderIdPrefix = "SANCHO";
    paymentWidget.requestPayment({
      orderId: orderIdPrefix + generateRandomString(),
      orderName: "테스트 방탈출 예약 결제 1건",
      amount: selectedThemePrice,
    }).then(function (data) {
      fetchReservationPayment(data, reservationData);
    }).catch(function (error) {
      // TOSS 에러 처리: 에러 목록을 확인하세요
      // https://docs.tosspayments.com/reference/error-codes#failurl 로-전달되는-에러
      console.debug(error);
      alert(error.code + " :" + error.message + "/ orderId : " + error.orderId);
    });

  } else {
    alert("예약을 신청하기 전 날짜, 테마, 시간을 모두 선택해 주세요.");
  }
}

async function fetchReservationPayment(paymentData, reservationData) {
  /*
  TODO: [1단계]
      - 자신의 예약 API request에 맞게 reservationPaymentRequest 필드명 수정
      - 내 서버 URL에 맞게 reservationURL 변경
      - 예약 결제 실패 시, 사용자가 실패 사유를 알 수 있도록 alert 에서 에러 메시지 수정
  */
  const reservationPaymentRequest = {
    date: reservationData.date,
    themeId: reservationData.themeId,
    timeId: reservationData.timeId,
    paymentKey: paymentData.paymentKey,
    orderId: paymentData.orderId,
    amount: paymentData.amount,
    paymentType: paymentData.paymentType,
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

    /*
    TODO: [3단계] 예약 대기 생성 요청 API 호출
     */
    fetch('/waitings', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(reservationData)
    })
        .then(response => {
          if (!response.ok) {
            response.text().then(text => {
              alert('ERROR! ' + text);
              throw new Error('예약 대기 실패');
            });
          }
          return response.json();
        })
        .then(data => {
          alert('예약 대기가 성공하였습니다!');
          window.location.href = "/";
        })
        .catch(error => {
          alert('예약 대기 생성 중 에러가 발생하였습니다.');
          console.error(error);
        });
  } else {
    alert('예약 대기를 신청하기 전 날짜, 테마, 시간을 모두 선택해 주세요.');
  }
}

function requestRead(endpoint) {
  return fetch(endpoint)
      .then(response => {
        if (response.status === 200) return response.json();
        response.text().then(text => {
          alert('ERROR! ' + text);
          throw new Error('Read failed');
        });
      });
}
