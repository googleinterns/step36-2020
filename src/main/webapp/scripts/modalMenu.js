const MODAL_TEMPLATE_PROMISE = loadTemplate('/templates/modalWindow.html');
const MENU_PROMISE = loadTemplate('/templates/menu.html');
const KEYWORDS_ALL_OBJ_PROMISE = loadObject('/all-keywords');

$().ready(async function() {
  const isLogUrlUndefined = typeof(LOG_URL_PROMISE) === 'undefined';
  if (isLogUrlUndefined || await LOG_URL_PROMISE != 2) {
    await loadModalWindow();
    loadMenu();
  }
});

async function loadModalWindow() {
  const modalObj = buildModalObj(await KEYWORDS_ALL_OBJ_PROMISE);
  const modalTemplate = await MODAL_TEMPLATE_PROMISE;
  const modalHTML = Mustache.render(modalTemplate, modalObj);
  $('#real-body').append(modalHTML);
  return true;
}

function buildModalObj(keywordsObj) {
  let modalObj = new Object();
  modalObj.keywords = new Array();
  for (key in keywordsObj) {
    let keywordObj = new Object();
    keywordObj.key = key;
    keywordObj.values = keywordsObj[key].join(' - ');
    modalObj.keywords.push(keywordObj);
  }
  return modalObj;
}

async function loadMenu() {
  const menuHTML = await MENU_PROMISE;
  $('#menu').append(menuHTML);
}

$('body').on('click', '#open-modal', function() {
  $('#modal-window').removeClass('hide');
});

$('body').on('click', '#pop-up > .close', function() {
  $('#modal-window').addClass('hide');
});

$('body').on('click', '.keys-list > p', function() {
  const key = $(this).parent().attr('data-key');
  const thisClass = $(this).attr('class');
  if (thisClass === 'ion-icon') {
    $(this).parent().remove();
    deleteKeywords(key);
  } else {
    $('this').off('click');
    window.location.href = `/results?k=${key}`;
  }
});

function deleteKeywords(key) {
  const params = new URLSearchParams();
  params.append('k', key);
  const request = new Request('/delete-keywords', {method: 'POST', body: params});
  fetch(request);
}
