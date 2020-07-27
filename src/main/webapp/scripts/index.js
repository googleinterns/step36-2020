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
});
