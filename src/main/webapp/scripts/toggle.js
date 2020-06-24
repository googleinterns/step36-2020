const keywordsTemplateUrl = '/templates/keywords.html';
const newsTemplateUrl =  '/templates/news.html';
const actionsTemplateUrl = '/templates/actions.html';
const templatesUrls = [keywordsTemplateUrl, newsTemplateUrl, actionsTemplateUrl];

const keywordsObjUrl = '/json/keywords.json';
const newsObjUrl =  '/json/news.json';
const actionsObjUrl = '/json/actions.json';
const objectsUrls = [keywordsObjUrl, newsObjUrl, actionsObjUrl];

const sectionsNames = ['keywords', 'news', 'actions'];

const htmlSectionsPromises = loadHtmlSections(templatesUrls, objectsUrls, sectionsNames);

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
  for (let i = 0; i < sectionsNames.length; i++){
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
 * Loads a section from the htmlSectionsPromises array.
 */
async function loadSection(sectionName) {
  resultSection.innerHTML = "";
  const htmlSections = await htmlSectionsPromises;
  resultSection.innerHTML = htmlSections[sectionName];
  return;
}
