/**
 * Loads an array of urls using the loadFunction given by the user.
 * Returns a promise.all of all the urls.
 */
function loadUrls(urls, loadFunction) {
  let promises = [];
  for (url of urls) {
    let promise = loadFunction(url);
    promises.push(promise);
  }
  return Promise.all(promises);
}

/**
 * Loads the template from the url.
 * Returns a promise of the template.
 */
function loadTemplate(url) {
  const templatePromise = fetch(url).then(promiseResponse => promiseResponse.text());
  return templatePromise;
}

/**
 * Loads the object from the url.
 * Returns a promise of the object.
 */
function loadObject(url) {
  const objPromise = fetch(url).then(promiseResponse => promiseResponse.json());
  return objPromise;
}

