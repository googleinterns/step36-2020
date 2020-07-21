//$("checkbox").click(setLocationCookie);
$(document).ready(function() {
  $('#location-checkbox').change(function() {
    if(this.checked) {
      setLocationCookie();
    }
  });

  $('#blob-input').change(function() {
    const filePath = $(this).val();
    $('#file-path').text(filePath.split('\\').pop());
  });
});
