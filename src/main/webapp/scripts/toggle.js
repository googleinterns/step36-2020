const KEYWORD_TEMPLATE_PROMISE = loadTemplate('/templates/keyword.html');
const LOCATION_TEMPLATE_PROMISE = loadTemplate('/templates/location.html');
const NO_KEYWORDS_HTML = loadTemplate('/templates/noKeywords.html')

const KEYWORDS_OBJ_URL = '/keyword';
const CIVIC_OBJ_URL = '/actions/civic';
const LOCATION_OBJ_URL = '/location';

const NEWS_OBJ_URL = '/news';
const BOOKS_OBJ_URL =  '/books';
const PROJECTS_OBJ_URL = '/actions/projects';
const OBJECTS_URLS = [NEWS_OBJ_URL, BOOKS_OBJ_URL, PROJECTS_OBJ_URL];

let loadingCounter;
let lat = getCookie("lat");
let lng = getCookie("lng");

/**
 * Loads the content section. 
 * Returns a promise that resolves when everything loads.
 */
async function loadContentSection() {
  const keywords = await loadKeywords(KEYWORDS_OBJ_URL);
  const elementsToLoad = Math.max(keywords.length, 1);
  counter.add(elementsToLoad);
  loadingCounter = traceCounterMethods(counter, elementsToLoad);
  if (keywords.length === 0) {
    loadNoKeywords();
  } else {
    keywords.forEach(loadKeywordSection);
  }
  const address = encodeURI(getCookie('address'));
  const location = getCookie('location');
  if (address != "") {
    loadCivicSectionFromAddress(address);
  } else {
    if (location != "") {
      console.log(`${LOCATION_OBJ_URL}?lat=${lat}&lng=${lng}`);
      let locationObj = await loadObject(`${LOCATION_OBJ_URL}?lat=${lat}&lng=${lng}`);
      console.log(locationObj);
      loadCivicSectionFromLocation(locationObj);
    } else {
      loadingCounter.decrement();
    }
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
    const keywordsJson = JSON.stringify(keywords);
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

/**
 * Loads a keyword section to the DOM.
 */
async function loadKeywordSection(keyword) {
  const queryString = `?key=${keyword}`;
  const objsUrls = OBJECTS_URLS.map(url => `${url}${queryString}`);
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

/**
 * Loads the current location of the user, and passes the locationObj to the callback function.
 * Returns a locationObj.
 */
 /*
function loadLocationObj(callback) {
  locationObj = await loadObject(`${LOCATION_OBJ_URL}?lat=${coordinates[0]}&lng=${coordinates[1]}`);
  callback(locationObj);
}
*/

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
  console.log(locationObj.Country);
  if (locationObj.Country === "United States") {
    console.log("In the US");
    const address = locationObj2Address(locationObj);
    loadCivicSectionFromAddress(address);
  } else {
    console.log("NOT in the US");
    alert('Sorry, your current location is not supported');
  }
  loadingCounter.decrement();
}

function locationObj2Address(locationObj) {
  const addressTemplate = '{{Street Number}} {{Street Name}}, {{City}} {{State}} {{Zip Code}}';
  return Mustache.render(addressTemplate, locationObj);
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
