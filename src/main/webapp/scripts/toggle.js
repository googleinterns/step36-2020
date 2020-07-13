const KEYWORDS_TEMPLATE_URL = '/templates/keywords.html';

const KEYWORDS_OBJ_URL = '/keyword';

const CIVIC_OBJ_URL = '/actions/civic'

const BOOKS_OBJ_URL =  '/books';
const PROJECTS_OBJ_URL = '/actions/projects';
const OBJECTS_URLS = [BOOKS_OBJ_URL, PROJECTS_OBJ_URL];

const LOADING_TEXT = $("#loading-text");

const KEYWORDS_PROMISE = loadKeywords(KEYWORDS_OBJ_URL);

const HTML_SECTIONS_PROMISE = loadHtmlSections(KEYWORDS_TEMPLATE_URL, OBJECTS_URLS, KEYWORDS_PROMISE);

/**
 * Loads the keywords array from a servlet or from cookies.
 * Returns a promise of the keywords array.
 */
async function loadKeywords(keywordsUrl) {
  LOADING_TEXT.text("Loading your keywords...");
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

/**
 * Loads an array of html templates, an array of json objects, and renders them using mustache.
 * Returns a promise.all of all the render html sections.
 */
async function loadHtmlSections(templateUrl, objsUrls, keywordsPromise) {
  const htmlTemplatePromise = loadTemplate(templateUrl);
  const keywords = await keywordsPromise;
  const queryString = `?key=${keywords.join('&key=')}`;
  objsUrls = objsUrls.map(url => `${url}${queryString}`);
  const objsPromises = loadUrls(objsUrls, loadObject);
  LOADING_TEXT.text("Looking up books and projects...")
  const values =  await Promise.all([htmlTemplatePromise, objsPromises]);
  const template = values[0];
  const objs = values[1];
  return await renderTemplateObj(template, objs, keywords);
}

async function renderTemplateObj(template, objs, keywords) {
  let result = new Object();
  result.keywords = new Array(keywords.length);
  for (let i = 0; i < keywords.length; i++) {
    let term = keywords[i];
    let keywordsObj = new Object();
    keywordsObj.term = term;
    keywordsObj.books = objs[0].books[term];
    keywordsObj.projects = objs[1].results[term];
    result.keywords[i] = keywordsObj;
  }

  const lat = getCookie("latitude");
  const lng = getCookie("longitude");
  if (lat != "" && lng != "") {
    LOADING_TEXT.text("Getting your government officials...");
    try{
      let civicObj = await loadObject(`${CIVIC_OBJ_URL}?lat=${lat}&lng=${lng}`);
      let locationObj = new Object();
      locationObj.address = civicObj.normalizedInput;
      locationObj.levels = extractOfficials(civicObj);
      result.location = locationObj;
    } catch(err) {
      alert("We couldn't find any civic information for your location.");
    }
  }
  let htmlSections = Mustache.render(template, result);
  return htmlSections;
}

/**
 * Activates the section that triggered the event.
 */
function activateSection(event) {
  const selectedOption = event.currentTarget.value;
  loadSection(selectedOption);
  return;
}

/**
 * Loads the content section.
 */
async function loadContentSection() {
  const htmlSection = await HTML_SECTIONS_PROMISE;
  $("#content").html(htmlSection);
  return;
}
