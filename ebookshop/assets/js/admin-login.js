document.addEventListener("DOMContentLoaded", function () {
  var app = window.EbookshopApp;
  var data = app.getPageData();
  var backLink = document.getElementById("login-back-link");
  var loginForm = document.getElementById("login-form");
  var errorBox = document.getElementById("login-error");

  if (backLink) {
    backLink.href = data.backToShopUrl || "#";
  }
  if (loginForm) {
    loginForm.action = data.formAction || "";
  }
  if (data.error) {
    app.showAlert(errorBox, "danger", data.error);
  } else {
    app.hide(errorBox);
  }
});
