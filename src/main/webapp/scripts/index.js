$().ready(function() {
  $('#address').change(function() {
    $(this).attr('size', ($(this).val().length - 10));
  });

  if (getCookie('location') != "") {
    $('#location-checkbox').prop('checked', true);
    $('#address').prop('readonly', true).val('Loading...').change();
    setLocationCookie();
  } else {
    eraseCookie('location');
  }

  $('#location-checkbox').change(function() {
    if (this.checked) {
      $('#address').prop('readonly', true).val('Loading...').change();
      setLocationCookie();
    } else {
      $('#address').prop('readonly', false);
      eraseCookie('location');
    }
  });

  $('#address').val(getCookie("address")).change();
});

$('submit-form').click(function() {
  let address = $('#address').val();
  setCookie('address', address, 1);  
});
