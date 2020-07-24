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
  var keycode = (event.keyCode ? event.keyCode : event.which);
  if(keycode == '13'){
    var address = $('#address').val();
    setCookie('address', address, 1);  
  }
});
