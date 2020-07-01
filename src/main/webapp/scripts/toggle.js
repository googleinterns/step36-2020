const KEYWORDS_TEMPLATE_URL = '/templates/keywords.html';
const TEMPLATES_URL = [KEYWORDS_TEMPLATE_URL];

const KEYWORDS_OBJ_URL = '/keyword';
const NEWS_OBJ_URL =  '/json/news.json';
const ACTIONS_OBJ_URL = '/actions';
const OBJECTS_URLS = [KEYWORDS_OBJ_URL, NEWS_OBJ_URL, ACTIONS_OBJ_URL];

const HTML_SECTIONS_PROMISE = loadHtmlSections(TEMPLATES_URL, OBJECTS_URLS);

/**
 * Loads an array of html templates, an array of json objects, and renders them using mustache.
 * Returns a promise.all of all the render html sections.
 */
async function loadHtmlSections(templatesUrls, objsUrls) {
  const htmlTemplatesPromises = loadUrls(templatesUrls, loadTemplate);
  const objsPromises = loadUrls(objsUrls, loadObject);
  const values =  await Promise.all([htmlTemplatesPromises, objsPromises]);
  const templates = values[0];
  const objs = values[1];
  return renderTemplateObj(templates, objs);
}

function renderTemplateObj(templates, objs) {
  let result = objs[0];
  for (let i = 0; i < result.keywords.length; i++) {
    let term = result.keywords[i].term;
    result.keywords[i].news = objs[1].articles[term];
    result.keywords[i].actions = objs[2].results[term];
  }
  let htmlSections = Mustache.render(templates[0], result);
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
