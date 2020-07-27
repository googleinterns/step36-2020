$().ready(function() {
  $('#location-checkbox').change(function() {
    if(this.checked) {
      setCookie('location', 'true', 1);
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
