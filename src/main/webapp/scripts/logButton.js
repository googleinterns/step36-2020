const LOG_URL_PROMISE = loadObject('/login');

$().ready(async function() {
  const logURL = await LOG_URL_PROMISE;
  $('#log-btn').attr('href', logURL[0]);
  if (logURL.length == 2) {  // User is not loggeed in.
    $('#form-title > p').text('You are not logged in.');
    $('#log-btn').text("Login");
    $('#keyword-form, #input-select, #location-input, #previous-keywords, #address-input').addClass('hide');
  } else {
    $('#form-title > p').text('Choose Type of Input');
    $('#log-btn').text('Logout');
    $('#input-select, #location-input, #previous-keywords, #address-input').removeClass('hide');
  }
  $('#log-btn').removeClass('hide');
});
