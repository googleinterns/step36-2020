const BLOB_URL_PROMISE = loadTemplate('/user-image');
const LOG_URL_PROMISE = loadObject('/login');

$().ready(async function() {
  const logURL = await LOG_URL_PROMISE;
  $('#log-btn').attr('href', logURL[0]);

  // TODO: Use true condition as default and if false inject the HTML
  if (logURL.length === 2) {  // User is not loggeed in.
    $('#form-title > p').text('You are not logged in.');
    $('#log-btn').text("Login");
    $('#keyword-form, #input-select, #location-input, #previous-keywords, #address-input').addClass('hide');
  } else {
    $('#form-title > p').text('Choose Type of Input');
    $('#log-btn').text('Logout');
    $('#input-select, #location-input, #previous-keywords, #address-input').removeClass('hide');
  }

  $('#log-btn').removeClass('hide');

  $('#previous-keywords').on('click', function() {
    let keywordsWindow = $('#keywords-window');
    if (keywordsWindow.hasClass('hide')) {
      keywordsWindow.removeClass('hide');
    } else {
      keywordsWindow.addClass('hide');
    }
  });

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

  $('#blob-file').change(function() {
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
