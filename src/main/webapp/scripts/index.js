//$("checkbox").click(setLocationCookie);
$(document).ready(function() {
  $('#location-checkbox').change(function() {
    if(this.checked) {
      setLocationCookie();
    }
  });
});