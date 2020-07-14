$().ready(function() {
  $('#select-blob').on('click', function() {
    fetch('/user-image').then(response => response.text()).then((blobURL) => {
      $('#keyword-form, #blob-input').removeClass('hide');
      $('#keyword-form').attr('action', blobURL).attr('enctype', 'multipart/form-data');
      $('#text-input').addClass('hide').val(null);
      $('#form-title *').text('Upload an Image Here');
    });
  });

  $('#select-text').on('click', function() {
    $('#keyword-form, #text-input').removeClass('hide');
    $('#keyword-form').attr('action', '/keyword').attr('enctype', 'application/x-www-form-urlencoded');
    $('#blob-input').addClass('hide').val(null);
    $('#form-title *').text('Copy Your Tweet Here');
  });
});
