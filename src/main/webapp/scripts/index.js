$().ready(function() {
  $('#location-checkbox').change(function() {
    if(this.checked) {
      setCookie('location', 'true', 1);
    } else {
      eraseCookie('location');
    }
  });

  $('#blob-input').change(function() {
    const filePath = $(this).val();
    $('#file-path').text(filePath.split('\\').pop());
  });
});
