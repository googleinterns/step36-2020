/**
 * Extract the government officials from the civic API response.
 * Returns an array of objects. Each object has the following structure:
 *   {levelType: level string as it appears in the API.
 *    levelName: Readable level string,
 *    officials: Officials object returned by the Google Civic API plus the title of the office they hold}
 */
function officialsByLevel(civicObj) {
  let levelsArray = new Array();
  const levels = {"special" : "Special",
                  "subLocality1" : "Sublocality 1",
                  "subLocality2" : "Sublocality 2",
                  "locality" : "Locality",
                  "administrativeArea2" : "County",
                  "administrativeArea1" : "State",
                  "regional" : "Regional",
                   "country" : "Country",
                  "international" : "International"};
  for (const level in levels) {
    let levelsObj = new Object();
    levelsObj.levelType = level;
    levelsObj.levelName = levels[level]
    levelsObj.officials = new Array();
    levelsArray.push(levelsObj);
  }
  civicObj.offices.forEach((officeObj) => {
    const level = officeObj.levels[0];
    officeObj.officialIndices.forEach((officialIndex) => {
      let official = civicObj.officials[officialIndex];
      official.title = officeObj.name;
      levelsArray.find(obj => obj.levelType === level).officials.push(official);
    });
  });
  return levelsArray.filter(obj => obj.officials.length > 0);
}
