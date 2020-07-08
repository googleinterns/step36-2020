$().ready(() => {
  loadContentSection().then(() =>{ 
    $("#loader").addClass("hide");
    $("#real-body").removeClass("hide");
    $("#real-body").addClass("body");
  });
});

$("body").on('click', '.keyword', function() {
  $(this).addClass("active");
  $(".base").hide(200);
  $("#real-body").addClass("focus");
});

$("body").on('click', ".close", function(event) {
  event.stopPropagation();
  $(this).parent().removeClass("active");
  $(".base").show(200);
  $("#real-body").removeClass("focus");
});

$("body").on('click', ".active .item", function() {
  $(".item").off("click");
  const url = $(this).attr("data-url");
  window.location.href = url;
});