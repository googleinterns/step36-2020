/**
 * Extract the government officials from the civic API response.
 */
function extractOfficials(civicObj) {
  let officials = new Object();
  let levels = ["administrativeArea1", "administrativeArea2", "country", "international", "locality", "regional", "special", "subLocality1", "subLocality2"];
  levels.forEach((level) => {
    officials[level] = new Array();
  });
  civicObj.offices.forEach((officeObj) => {
    let level = officeObj.levels[0];
    officeObj.officialIndices.forEach((officialIndex) => {
      let official = civicObj.officials[officialIndex];
      official.title = officeObj.name;
      officials[level].push(official);
    });
  });
  return officials;
}
