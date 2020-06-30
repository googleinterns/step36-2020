$().ready(()=>{
  loadContentSection().then(()=>{
    $(".keyword").click(function() {
      $(this).addClass("active");
      $(".base").hide(200);
      $("body").addClass("focus");
      $(".section").addClass('active-url');
      $(".active-url").click(function(event) {
        event.stopPropagation();
        let url = $(this).attr("data-url");
        window.open(url);
      });
    });

    $(".close").click(function(event) {
      event.stopPropagation();
      $(this).parent().removeClass("active");
      $(".base").show(200);
      $("body").removeClass("focus");
      $(".active-url").off("click");
      $(".section").removeClass('active-url');
    });
  });
});
