$().ready(() => {
  loadContentSection().then(() =>{ 
    $("#loader").addClass("hide");
    $("#real-body").removeClass("hide");
    $("#real-body").addClass("body");

    $(".keyword").click(function() {
      $(this).addClass("active");
      $(".base").hide(200);
      $("#real-body").addClass("focus");
    });

    $(".close").click(function(event) {
      event.stopPropagation();
      $(this).parent().removeClass("active");
      $(".base").show(200);
      $("#real-body").removeClass("focus");
    });
  });
});
