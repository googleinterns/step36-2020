const FORM_HTML_PROMISE = loadTemplate('/templates/inputForm.html');
const LOG_URL_PROMISE = loadObject('/login');

$().ready(async function() {
  let logURL = await LOG_URL_PROMISE;
  console.log("Log URL: " + logURL);
  $('#log-btn').attr('href', logURL[0]);
  if (logURL.length == 2) {  // User is not logged in.
    $('#form-title > p').text('You are not logged in');
    $('#log-btn').text("Log In");
  } else {
    $('#form-title > p').text('Choose Type of Input');
    $('#log-btn').text('Log Out');
    $('#content').append(await FORM_HTML_PROMISE);
    if ($('index').length) {  // Index page.
      loadIndexListeners();
      loadKeywordsFormListeners();
    }
  }
  $('#log-btn').removeClass('hide');
  $('body').removeClass('loading');
});
