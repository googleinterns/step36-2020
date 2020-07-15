const KEYWORD_TEMPLATE_PROMISE = loadTemplate('/templates/keyword.html');
const LOCATION_TEMPLATE_PROMISE = loadTemplate('/templates/location.html');
const NO_KEYWORDS_HTML = loadTemplate('/templates/noKeywords.html')

const KEYWORDS_OBJ_URL = '/keyword';
const CIVIC_OBJ_URL = '/actions/civic';
const LOCATION_OBJ_URL = '/location';

const BOOKS_OBJ_URL =  '/books';
const PROJECTS_OBJ_URL = '/actions/projects';
const OBJECTS_URLS = [BOOKS_OBJ_URL, PROJECTS_OBJ_URL];

/**
 * Loads the content section.
 */
async function loadContentSection() {
  const keywords = await loadKeywords(KEYWORDS_OBJ_URL);
  if (keywords.length === 0) {
    loadNoKeywords();
  } else {
    keywords.forEach(loadKeywordSection);
  }
  const location = getCookie('location');
  if (location != "") {
    loadLocationObj(loadCivicSection);
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
}

/**
 * Loads a keyword section to the DOM.
 */
async function loadKeywordSection(keyword) {
  const queryString = `?key=${keyword}`;
  const objsUrls = OBJECTS_URLS.map(url => `${url}${queryString}`);
  const objs = await loadUrls(objsUrls, loadObject);
  const keywordObj = buildKeywordObj(objs[0], objs[1], keyword);
  const template = await KEYWORD_TEMPLATE_PROMISE;
  renderKeyword(template, keywordObj);
}

/**
 * Builds a keyword object given a keyword, books, and projects.
 */
function buildKeywordObj(booksObj, projectsObj, keyword) {
  let keywordObj = new Object();
  keywordObj.term = keyword;
  keywordObj.books = booksObj.books[keyword];
  keywordObj.projects = projectsObj.results[keyword];
  return keywordObj;
}

function renderKeyword(template, keywordObj) {
  const keywordHTML = Mustache.render(template, keywordObj);
  $('#keywords').prepend(keywordHTML);
}

/**
 * Loads the current location of the user, and passes the locationObj to the callback function.
 * Returns a locationObj.
 */
function loadLocationObj(callback) {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(async (position) => {
      const lat = position.coords.latitude;
      const lng = position.coords.longitude;
      locationObj = await loadObject(`${LOCATION_OBJ_URL}?lat=${lat}&lng=${lng}`);
      callback(locationObj);
    }, (err) => {
      console.warn(`ERROR(${err.code}): ${err.message}`);
      alert("We cannot access your location. Try checking your browswer settings");
    });
  }
}

/**
 * Loads the civic section to the DOM, or alerts the user if there aren't any results for their location.
 */
async function loadCivicSection(locationObj) {
  if (locationObj.Country === "United States") {
    const address = locationObj2Address(locationObj);
    const civicObj = await loadObject(`${CIVIC_OBJ_URL}?address=${address}`);
    const civicLocationObj = buildCivicLocationObj(civicObj);
    const locationTemplate = await LOCATION_TEMPLATE_PROMISE;
    renderLocation(locationTemplate, civicLocationObj);
  } else {
    alert('Sorry, your current location is not supported');
  }
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
}
