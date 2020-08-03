const BLOB_URL_PROMISE = loadTemplate('/user-image');

function loadKeywordsFormListeners() {
  $('#select-blob').on('click', async function() {
    $('#keyword-form, #blob-input').removeClass('hide');
    $('#keyword-form')
      .attr('action', await BLOB_URL_PROMISE)
      .attr('enctype', 'multipart/form-data');
      
    $('#text-input').addClass('hide').val(null);
    $('#form-title > p').text('Upload an Image Here');
  });

  $('#select-text').on('click', function() {
    $('#keyword-form, #text-input').removeClass('hide');
    $('#keyword-form')
      .attr('action', '/keyword')
      .attr('enctype', 'application/x-www-form-urlencoded');

    $('#blob-input').addClass('hide').val(null);
    $('#form-title > p').text('Copy Your Tweet Here');
  });

  $('#blob-file').change(function() {
    const filePath = $(this).val();
    $('#file-path').text(filePath.split('\\').pop());
  });

  $('#keyword-form').on('submit', function(event) {
    // Check whether the inputs are valid before submitting.
    if ($('#blob-input').hasClass('hide')) {
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
    $('body').addClass('loading').children(':not(.load)').hide();
    $('.load').removeClass('hide');
    return true;
  });
}
