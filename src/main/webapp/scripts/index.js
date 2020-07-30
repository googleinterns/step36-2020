$().ready(function() {
  console.log(getCookie('location'));
  if (getCookie('location') != "") {
    console.log('in right if statement');
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
  $('#address').val(getCookie("address"));
});

$('submit-form').click(function () {
  let address = $('#address').val();
  setCookie('address', address, 1);  
});
