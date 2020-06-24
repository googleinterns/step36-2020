const menuInputs = document.querySelectorAll("#menu input[name='option']");
const resultSection = document.querySelector("#selected-option");

menuInputs.forEach((input) => {
  input.addEventListener('change', activateSection);
});
