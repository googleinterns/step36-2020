$().ready(function() {
  if (getCookie('location') != "") {
    $('#location-checkbox').prop('checked', true);
    setLocationCookie();
  } else {
    eraseCookie('location');
  }

  $('#location-checkbox').change(function() {
    if (this.checked) {
      $('#address').prop('disabled', true);
      setLocationCookie();
    } else {
      $('#address').prop('disabled', false);
      eraseCookie('location');
    }
  });

  $('#address').val(getCookie("address"));
});

$('submit-form').click(function() {
  let address = $('#address').val();
  setCookie('address', address, 1);  
});
