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

/** 
 * Checks whether the form inputs are valid before submitting said form.
 */
function validateForm(event) {
  if ($('#blob-input').hasClass('hide')) {
    // Check the text input
    if ($('#text-input').val() == null || $('#text-input').val() === "") {
      alert('Your input cannot be empty!'); 
      event.preventDefault();
      return false;
    } else {}
  } else {  // Check blob input
    const blobInput = document.getElementById('blob-input');
    if (blobInput.files.length == 0) {
      alert("You must upload an image file!");
      event.preventDefault();
      return false;
    }
    const blobFile = blobInput.files[0];
    const fileType = blobFile['type'];
    const validImageTypes = ['image/jpeg', 'image/png', 'image/jpg'];
    if (blobFile == null || !validImageTypes.includes(fileType)) {
      alert('You must upload an image file!');
      event.preventDefault();
      return false;
    }    
  }
  return true;
}

