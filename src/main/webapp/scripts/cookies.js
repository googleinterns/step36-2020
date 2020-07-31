const HOURS_PER_DAY = 24;
const MINUTES_PER_HOUR = 60;
const SECONDS_PER_MINUTE = 60;
const SECONDS_PER_DAY = HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE;
const MILLISECONDS_PER_SECOND = 1000;
const LOCATION_OBJ_URL = '/location';

/**
 * Get the value of a cookie.
 */
function getCookie(name) {
  let decodedCookie = decodeURIComponent(document.cookie);
  let cookies = decodedCookie.split(';');
  for (let i = 0; i < cookies.length; i++) {
    cookieParts = cookies[i].trim().split("=");
    if (cookieParts[0] === name) {
      return cookieParts[1];
    }
  }
  return "";
}

/**
 * Create a new cookie based on name, value and days before expiring.
 */
function setCookie(name, value, expirationDays) {
  let date = new Date();
  date.setTime(date.getTime + expirationDays * SECONDS_PER_DAY * MILLISECONDS_PER_SECOND);
  document.cookie = `${name}=${value};expires=${expirationDays}`;
}

/**
 * Set the cookie value of a change event.
 */
function setChangeCookie(event) {
  let name = event.currentTarget.id;
  let value = event.currentTarget.value;
  setCookie(name, value, 1);
}

/**
 * Set the cookie value of a checkbox click event.
 */
function setCheckboxCookie(event) {
  let name = event.currentTarget.id;
  let value = event.currentTarget.checked;
  setCookie(name, value, 1);
}

/**
 * Erase a cookie based on name.
 */
function eraseCookie(name) {
  document.cookie = `${name}=;expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;
}

/**
 * Load the cookie value of a select element to the DOM.
 */
function loadSelectValueCookie(cookieName) {
  cookieValue = getCookie(cookieName);
  if (cookieValue !== "") {
      document.getElementById(`${cookieValue}-option`).selected = "selected";
  }
}

/**
 * Load the cookie value of a numeric input element to the DOM.
 */
function loadNumericValueCookie(cookieName) {
  cookieValue = getCookie(cookieName);
  if (cookieValue !== "") {
      document.getElementById(cookieName).value = cookieValue;
  }
}

/**
 * Load the cookie state of a checkbox input element to the DOM.
 */
function loadCheckboxStateCookie(cookieName) {
  cookieValue = getCookie(cookieName);
  if (cookieValue !== "") {
      document.getElementById(cookieName).checked = (cookieValue === "true");
  }
}

/** 
 * Set cookie for user's coordinates with Navigator API.
 */
async function setLocationCookie() {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(async (position) => {
      const lat = position.coords.latitude;
      const lng = position.coords.longitude;
      const locationObj = await loadObject(`${LOCATION_OBJ_URL}?lat=${lat}&lng=${lng}`);
      const address = locationObj2Address(locationObj);
      $('#address').val(address).change();
      setCookie('location', JSON.stringify(locationObj), 1);
    }, (err) => {
      console.warn(`ERROR(${err.code}): ${err.message}`);
      alert("We cannot access your location. Try checking your browswer settings");
    });
  }
}

function locationObj2Address(locationObj) {
  const addressTemplate = '{{Street Number}} {{Street Name}}, {{City}} {{State}} {{Zip Code}}';
  return Mustache.render(addressTemplate, locationObj);
}
