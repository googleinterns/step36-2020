const KEYWORD_TEMPLATE_PROMISE = loadTemplate('/templates/keyword.html');
const LOCATION_TEMPLATE_PROMISE = loadTemplate('/templates/location.html');
const NO_KEYWORDS_HTML = loadTemplate('/templates/noKeywords.html')

const KEYWORDS_OBJ_URL = '/keyword';
const CIVIC_OBJ_URL = '/actions/civic'

const BOOKS_OBJ_URL =  '/books';
const PROJECTS_OBJ_URL = '/actions/projects';
const OBJECTS_URLS = [BOOKS_OBJ_URL, PROJECTS_OBJ_URL];

/**
 * Loads the content section. 
 * Returns a promise that resolves when everything loads.
 */
async function loadContentSection() {
  const keywords = await loadKeywords(KEYWORDS_OBJ_URL);
  const elementsToLoad = Math.max(keywords.length, 1);
  loadingCounter.add(elementsToLoad);
  if (keywords.length === 0) {
    loadNoKeywords();
  } else {
    keywords.forEach(loadKeywordSection);
  }
  loadCivicSection();
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
  const keywordObj = buildKeywordObj(objs[0], objs[1], keyword);
  const template = await KEYWORD_TEMPLATE_PROMISE;
  renderKeyword(template, keywordObj);
  loadingCounter.decrement();
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
  hideLoading();
}

/**
 * Loads the civic section to the DOM, or alerts the user if there aren't any results for their location.
 */
async function loadCivicSection() {
  const lat = getCookie("latitude");
  const lng = getCookie("longitude");
  if (lat != "" && lng != "") {
    try {
      const civicObj = await loadObject(`${CIVIC_OBJ_URL}?lat=${lat}&lng=${lng}`);
      const locationObj = buildLocationObj(civicObj);
      const locationTemplate = await LOCATION_TEMPLATE_PROMISE;
      renderLocation(locationTemplate, locationObj);
    } catch (err) {
      alert("We couldn't find any civic information for your location.");
    }
  }
  loadingCounter.decrement();
}

/**
 * Builds a keyword object given a civic object returned by the civic API.
 */
function buildLocationObj(civicObj) {
  let locationObj = new Object();
  locationObj.address = civicObj.normalizedInput;
  locationObj.levels = officialsByLevel(civicObj);
  return locationObj;
}

function renderLocation(template, locationObj) {
  const locationHTML = Mustache.render(template, locationObj);
  $('#keywords').append(locationHTML);
  hideLoading();
}
