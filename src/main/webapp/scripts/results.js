$().ready(() => {
  loadContentSection().then(() =>{ 
    $('body').removeClass('loading');
  });
});

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
  const isThisExtended = $(this).parent().hasClass("extended");
  $(".extended").removeClass("extended");
  if (!isThisExtended) {
    $(this).parent().addClass("extended");
  }
});
