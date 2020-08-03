const KEYWORD_TEMPLATE_PROMISE = loadTemplate('/templates/keyword.html');
const LOCATION_TEMPLATE_PROMISE = loadTemplate('/templates/location.html');
const NO_KEYWORDS_HTML = loadTemplate('/templates/noKeywords.html')

let locationObj = null;

const KEYWORDS_OBJ_URL = '/keyword';
const CIVIC_OBJ_URL = '/actions/civic';

const NEWS_OBJ_URL = '/news';
const BOOKS_OBJ_URL =  '/books';
const PROJECTS_OBJ_URL = '/actions/projects';
const OBJECTS_URLS = [NEWS_OBJ_URL, BOOKS_OBJ_URL, PROJECTS_OBJ_URL];

let loadingCounter;

/**
 * Loads the content section. 
 * Returns a promise that resolves when everything loads.
 */
async function loadContentSection() {
  let locationCookie = getCookie('location');
  if (locationCookie != "") {
    locationObj = JSON.parse(getCookie('location'))
  }
  let keywordsJson = await loadKeywords(KEYWORDS_OBJ_URL);
  const keywords = keywordsJson["keywords"];
  const language = keywordsJson["language"];
  if (keywords == null) {
    $('#login-error').removeClass('hide');
    hideLoading();
    return;
  }
  const elementsToLoad = Math.max(keywords.length, 1);
  counter.add(elementsToLoad);
  loadingCounter = traceCounterMethods(counter, elementsToLoad);
  if (keywords.length === 0) {
    loadNoKeywords();
  } else {
    keywords.forEach(loadKeywordSection);
  }
  const address = encodeURI(getCookie('address'));
  if (address != "") {
    loadCivicSectionFromAddress(address);
  } else if (locationObj) {
    loadCivicSectionFromLocation(locationObj);
  } else {
    loadingCounter.decrement();
  }
}

/**
 * Loads the keywords array from a servlet or from cookies.
 * Returns a promise of the keywords array.
 */
async function loadKeywords(keywordsUrl) {
  const key = $("body").attr("data-key");
  let keywords;
  const keywordsCookie = getCookie(key);
  if (keywordsCookie === "") {
    keywords = await loadObject(`${keywordsUrl}?k=${key}`);
    let keywordsJson = JSON.stringify(keywords);
    setCookie(key, keywordsJson, 1);
  } else {
    keywords = JSON.parse(keywordsCookie);
  }
  return keywords;
}

async function loadNoKeywords() {
  const noKeywordsHTML = await NO_KEYWORDS_HTML;
  $('#keywords').append(noKeywordsHTML);
  hideLoading();
  loadingCounter.decrement();
}

function hideLoading() {
  $("body").children(":not(#real-body)").addClass("hide");
  $("#real-body").removeClass("hide").addClass("body");
}

function makeUrl(url, keyword) {
  let queryString = `?key=${keyword}`;
  let fullUrl = `${url}${queryString}`;
  if (url === '/news' && locationObj != null) {
    fullUrl = `${fullUrl}&country=${locationObj["Short Country"]}`;
  }
  return fullUrl;
}

/**
 * Loads a keyword section to the DOM.
 */
async function loadKeywordSection(keyword) {
  const objsUrls = OBJECTS_URLS.map(url => makeUrl(url, keyword));
  const objs = await loadUrls(objsUrls, loadObject);
  const keywordObj = buildKeywordObj(objs[0], objs[1], objs[2], keyword);
  const template = await KEYWORD_TEMPLATE_PROMISE;
  renderKeyword(template, keywordObj);
  loadingCounter.decrement();
}

/**
 * Builds a keyword object given a keyword, news, books, and projects.
 */
function buildKeywordObj(newsObj, booksObj, projectsObj, keyword) {
  let keywordObj = new Object();
  keywordObj.term = keyword;
  keywordObj.news = newsObj.news[keyword];
  keywordObj.books = booksObj.books[keyword];
  keywordObj.projects = projectsObj.results[keyword];
  return keywordObj;
}

function renderKeyword(template, keywordObj) {
  const keywordHTML = Mustache.render(template, keywordObj);
  $('#keywords').prepend(keywordHTML);
  hideLoading();
}

async function loadCivicSectionFromAddress(address) {
  const civicObj = await loadObject(`${CIVIC_OBJ_URL}?address=${address}`);
  if ('error' in civicObj) {
    alert('Sorry, your current location is not supported');
  } else {
    const civicLocationObj = buildCivicLocationObj(civicObj);
    const locationTemplate = await LOCATION_TEMPLATE_PROMISE;
    renderLocation(locationTemplate, civicLocationObj);
  }
  loadingCounter.decrement();
}

/**
 * Loads the civic section to the DOM, or alerts the user if there aren't any results for their location.
 */
async function loadCivicSectionFromLocation(locationObj) {
  if (locationObj.Country === "United States") {
    const address = locationObj2Address(locationObj);
    loadCivicSectionFromAddress(address);
  } else {
    alert('Sorry, your current location is not supported');
  }
  loadingCounter.decrement();
}

/**
 * Builds a civic location object given a civic object returned by the civic API.
 */
function buildCivicLocationObj(civicObj) {
  let civicLocationObj = new Object();
  civicLocationObj.address = civicObj.normalizedInput;
  civicLocationObj.levels = officialsByLevel(civicObj);
  return civicLocationObj;
}

function renderLocation(template, civicLocationObj) {
  const locationHTML = Mustache.render(template, civicLocationObj);
  $('#keywords').append(locationHTML);
  hideLoading();
}

function languageToCountry(language) {
  let country = "";
  switch(language) {
    case "en":
      country = "us";
      break;
    case "zh":
      country = "cn";
      break;
    case "ja":
      country = "jp";
      break;
    case "ko":
      country = "kp";
      break;
    case "pt":
      country = "br";
      break;
    default:
      country = "language"
  }
  return country;
}
