$().ready(loadContentSection);

$("body").on('click', '.keyword', function() {
  $(this).addClass("active");
  $(".base").hide(200);
  $("#real-body").addClass("focus");
});

$("body").on('click', ".close", function(event) {
  event.stopPropagation();
  $(".extended").removeClass("extended");
  $(this).parent().removeClass("active");
  $(".base").show(200);
  $("#real-body").removeClass("focus");
});

$("body").on('click', ".active .item", function() {
  $(".item").off("click");
  const url = $(this).attr("data-url");
  window.location.href = url;
});

$("body").on('click', '.level-name', function() {
  const parent = $(this).parent();
  const isThisExtended = parent.hasClass("extended");
  $(".extended").removeClass("extended");
  if (!isThisExtended) {
    parent.addClass("extended");
    parent.parent().addClass('reduce');
  } else {
    parent.parent().removeClass('reduce');
  }
});

$("body").on('click', '#go-back', function() {
  $("#body").off("click");
  window.location.href = '/';
});
