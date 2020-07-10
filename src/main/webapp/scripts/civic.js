/**
 * Extract the government officials from the civic API response.
 */
function extractOfficials(civicObj) {
  let levelsArray = new Array();
  const levels = {"international" : "International",
                  "country" : "Country",
                  "regional" : "Regional",
                  "administrativeArea2" : "Administrative Area 2",
                  "administrativeArea1" : "Administrative Area 1",
                  "locality" : "Locality",
                  "subLocality2" : "Sublocality 2",
                  "subLocality1" : "Sublocality 1",
                  "special" : "Special",};
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
