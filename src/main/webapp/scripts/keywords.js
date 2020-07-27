const BLOB_URL_PROMISE = loadTemplate('/user-image');

$().ready(function() {
  $('#select-blob').on('click', async function() {
    const blobURL = await BLOB_URL_PROMISE;
    $('#keyword-form, #blob-input, #submit-form').removeClass('hide');
    $('#keyword-form').attr('action', blobURL[0]).attr('enctype', 'multipart/form-data');
    $('#text-input').addClass('hide').val(null);
    $('#form-title > p').text('Upload an Image Here');
  });

  $('#select-text').on('click', function() {
    $('#keyword-form, #text-input, #submit-form').removeClass('hide');
    $('#keyword-form').attr('action', '/keyword').attr('enctype', 'application/x-www-form-urlencoded');
    $('#blob-input').addClass('hide').val(null);
    $('#form-title > p').text('Copy Your Tweet Here');
  });

  $('#blob-input').change(function() {
    const filePath = $(this).val();
    $('#file-path').text(filePath.split('\\').pop());
  });

  $('#keyword-form').on('submit', function(event) {
    // Check whether the inputs are valid before submitting.
    if ($('#blob-input').hasClass('hide')) {
      // Check whether the text input is null, or has an empty string as a value.
      if ($('#text-input').val() == null || $('#text-input').val() === "") {
        alert('Your input cannot be empty!'); 
        event.preventDefault();
        return false;
      }
    } else {
      // Check whether a file got uploaded to the input tag.
      const blobInput = $('#blob-file').prop('files');
      if (blobInput.length == 0) {
        alert("You must upload an image file!");
        event.preventDefault();
        return false;
      }
      // Check whether the file is of an image type.
      const blobFile = blobInput[0];
      const fileType = blobFile['type'];
      if (blobFile == null || !fileType.includes('image')) {
        alert('You must upload an image file!');
        event.preventDefault();
        return false;
      }    
    }
    $('#keyword-form').off('submit');
    $('body').addClass('loading');
    $('body').children(':not(.load)').hide();
    $('.load').removeClass('hide');
    return true;
  });
});
