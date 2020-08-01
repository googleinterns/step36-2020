const LOG_URL_PROMISE = loadObject('/login');

$().ready(async function() {
  let logURL = await LOG_URL_PROMISE;
  $('#log-btn').attr('href', logURL[0]);
  if (logURL.length == 2) {  // User is not logged in.
    $('#form-title > p').text('You are not logged in.');
    $('#log-btn').text("Log In");
    $('#keyword-form, #input-select, #location').addClass('hide');
  } else {
    $('#form-title > p').text('Choose Type of Input');
    $('#log-btn').text('Log Out');
    $('#input-select, #location').removeClass('hide');
  }
  $('#log-btn').removeClass('hide');
});
