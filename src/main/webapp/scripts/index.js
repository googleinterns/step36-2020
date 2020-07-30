const MODAL_PROMISE = loadTemplate('/templates/modalWindow.html');

$().ready(function() {
  if (getCookie('location') != "") {
    $('#location-checkbox').prop('checked', true);
    setLocationCookie();
  }
  else {
    eraseCookie('location');
  }

  $('#location-checkbox').change(function() {
    if(this.checked) {
      setLocationCookie();
    } else {
      eraseCookie('location');
    }
  });

  $('body').on('click', '#pop-up > .close', function() {
    $('#modal-window').addClass('hide');
  });

  $('#address').val(getCookie("address"));
});

$('submit-form').click(function () {
  let address = $('#address').val();
  setCookie('address', address, 1);  
});

async function loadModalWindow() {
  const modalHTML = await MODAL_PROMISE;
  $('#real-body').append(modalHTML);
}
