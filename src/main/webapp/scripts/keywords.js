const BLOB_URL_PROMISE = loadTemplate('/user-image');

$().ready(function() {
  $('#select-blob').on('click', async function() {
    const blobURL = await BLOB_URL_PROMISE;
    $('#keyword-form, #blob-input, #submit-form').removeClass('hide');
    $('#keyword-form').attr('action', blobURL).attr('enctype', 'multipart/form-data');
    $('#text-input').addClass('hide').val(null);
    $('#form-title > p').text('Upload an Image Here');
  });

  $('#select-text').on('click', function() {
    $('#keyword-form, #text-input, #submit-form').removeClass('hide');
    $('#keyword-form').attr('action', '/keyword').attr('enctype', 'application/x-www-form-urlencoded');
    $('#blob-input').addClass('hide').val(null);
    $('#form-title > p').text('Copy Your Tweet Here');
  });
});
