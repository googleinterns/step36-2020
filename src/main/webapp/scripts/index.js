$().ready(function() {
  const location = getCookie('location');
  if (location != "") {
    $('#location-checkbox').prop('checked', true);
    setCoordinateCookie();
  }
  else {
    eraseCookie('coordinates');
  }

  $('#location-checkbox').change(function() {
    if(this.checked) {
      setCookie('location', 'true', 1);
      setCoordinateCookie();
    } else {
      eraseCookie('location');
      eraseCookie('coordinates');
    }
  });
  $('#address').val(getCookie("address"));
});

$('submit-form').click(function () {
  let address = $('#address').val();
  setCookie('address', address, 1);  
});
