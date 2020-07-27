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

$('#address').keypress(function(event){
  let keycode = (event.keyCode ? event.keyCode : event.which);
  // If enter key is pressed for address input, store the address as a cookie.
  if(keycode == '13'){
    let address = $('#address').val();
    setCookie('address', address, 1);  
  }
});
