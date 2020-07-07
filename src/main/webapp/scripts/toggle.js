const KEYWORDS_TEMPLATE_URL = '/templates/keywords.html';

const KEYWORDS_OBJ_URL = '/json/keywords.json';
const NEWS_OBJ_URL =  '/books';
const ACTIONS_OBJ_URL = '/actions';
const OBJECTS_URLS = [NEWS_OBJ_URL, ACTIONS_OBJ_URL];

const KEYWORDS_PROMISE = loadKeywords(KEYWORDS_OBJ_URL);

const HTML_SECTIONS_PROMISE = loadHtmlSections(KEYWORDS_TEMPLATE_URL, OBJECTS_URLS, KEYWORDS_PROMISE);

/**
 * Loads the keywords array from a servlet or from cookies.
 * Returns a promise of the keywords array.
 */
async function loadKeywords(keywordsUrl) {
  const cookieName = 'keywords';
  let keywords;
  const cookie = getCookie(cookieName);
  if (cookie === "") {
    keywords = await loadObject(keywordsUrl);
    const keywordsJson = JSON.stringify(keywords);
    setCookie(cookieName, keywordsJson, 7);
  } else {
    keywords = JSON.parse(cookie);
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
  const values =  await Promise.all([htmlTemplatePromise, objsPromises]);
  const template = values[0];
  const objs = values[1];
  return renderTemplateObj(template, objs, keywords);
}

function renderTemplateObj(template, objs, keywords) {
  let result = new Object();
  result.keywords = new Array(keywords.length);
  for (let i = 0; i < keywords.length; i++) {
    let term = keywords[i];
    let keywordsObj = new Object();
    keywordsObj.term = term;
    keywordsObj.news = objs[0].books[term];
    keywordsObj.actions = objs[1].results[term];
    result.keywords[i] = keywordsObj;

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
