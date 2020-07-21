$().ready(function() {
  $('#select-blob').on('click', function() {
    fetch('/user-image').then(response => response.json()).then((blobURL) => {
      $('#keyword-form, #blob-input').removeClass('hide');
      $('#keyword-form').attr('action', blobURL[0]).attr('enctype', 'multipart/form-data');
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
      const blobInput = $('#blob-input').prop('files');
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
    return true;
  });
});

