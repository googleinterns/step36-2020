const KEYWORDS_TEMPLATE_URL = '/templates/keywords.html';
const NEWS_TEMPLATE_URL =  '/templates/news.html';
const ACTIONS_TEMPLATE_URL = '/templates/actions.html';
const TEMPLATES_URLS = [KEYWORDS_TEMPLATE_URL, NEWS_TEMPLATE_URL, ACTIONS_TEMPLATE_URL];
const KEYWORDS_OBJ_URL = '/keyword';
const NEWS_OBJ_URL =  '/news';
const ACTIONS_OBJ_URL = '/json/actions.json';
const OBJECTS_URLS = [KEYWORDS_OBJ_URL, NEWS_OBJ_URL, ACTIONS_OBJ_URL];

const SECTIONS_NAMES = ['keywords', 'news', 'actions'];

const HTML_SECTIONS_PROMISES = loadHtmlSections(TEMPLATES_URLS, OBJECTS_URLS, SECTIONS_NAMES);

/**
 * Loads an array of html templates, an array of json objects, and renders them using mustache.
 * Returns a promise.all of all the render html sections.
 */
async function loadHtmlSections(templatesUrls, objsUrls, sectionsNames) {
  const htmlTemplatesPromises = loadUrls(templatesUrls, loadTemplate);
  const objsPromises = loadUrls(objsUrls, loadObject);
  const values =  await Promise.all([htmlTemplatesPromises, objsPromises]);
  const templates = values[0];
  const objs = values[1];
  let htmlSections = new Object();
  for (let i = 0; i < sectionsNames.length; i++) {
    let sectionHtml = Mustache.render(templates[i], objs[i]);
    htmlSections[sectionsNames[i]] = sectionHtml;
  }
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
 * Loads a section from the HTML_SECTIONS_PROMISES array.
 */
async function loadSection(sectionName) {
  resultSection.innerHTML = "";
  const htmlSections = await HTML_SECTIONS_PROMISES;
  resultSection.innerHTML = htmlSections[sectionName];
  return;
}
