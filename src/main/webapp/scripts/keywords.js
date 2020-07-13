$().ready(function() {
  $("#select-blob").on("click", function() {
    fetch('/user-image').then(response => response.text()).then((blobURL) => {
      $("#keyword-form").removeClass("hide").attr("action", blobURL);
      $("#blob-input").removeClass("hide");
      $("#text-input").addClass("hide");
      $("#form-title *").text("Upload an Image Here");
    });
  });

  $("#select-text").on("click", function() {
    $("#keyword-form, #text-input").removeClass("hide");
    $("#keyword-form").attr("action", "/keyword");
    $("#blob-input").addClass("hide");
    $("#form-title *").text("Copy Your Tweet Here");
  });
});

