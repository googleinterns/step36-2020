$().ready(function() {
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

  $('#address').val(getCookie("address"));
});

$('submit-form').click(function() {
  let address = $('#address').val();
  setCookie('address', address, 1);  
});
