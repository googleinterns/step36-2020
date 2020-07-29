const MODAL_PROMISE = loadTemplate('/templates/modalWindow.html');

$().ready(function() {
  loadModalWindow();

  const location = getCookie('location');
  if (location != "") {
    $('#location-checkbox').prop('checked', true);
  }

  $('#location-checkbox').change(function() {
    if(this.checked) {
      setCookie('location', 'true', 1);
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
