document.addEventListener("DOMContentLoaded", function () {
  var app = window.EbookshopApp;
  var data = app.getPageData();
  var adminLink = document.getElementById("home-admin-link");
  var filterForm = document.getElementById("home-filter-form");
  var authorOptions = document.getElementById("author-options");
  var bookGrid = document.getElementById("book-grid");
  var alertBox = document.getElementById("home-alert");
  var countNode = document.getElementById("catalog-count");

  if (adminLink) {
    adminLink.href = data.adminLoginUrl || "#";
  }
  if (filterForm) {
    filterForm.action = data.filterAction || "";
  }
  if (data.errorMessage) {
    app.showAlert(alertBox, "danger", data.errorMessage);
  } else {
    app.hide(alertBox);
  }

  (data.authors || []).forEach(function (author, index) {
    var node = app.cloneTemplate("home-author-option-template");
    if (!node) {
      return;
    }
    var input = node.querySelector(".author-option__input");
    var text = node.querySelector(".author-option__text");
    if (input) {
      input.id = "author-" + index;
      input.value = author;
    }
    app.setText(text, author);
    authorOptions.appendChild(node);
  });

  (data.books || []).forEach(function (book, index) {
    var node = app.cloneTemplate("home-book-card-template");
    if (!node) {
      return;
    }
    node.style.setProperty("--card-delay", (index * 0.08) + "s");
    var image = node.querySelector(".book-cover");
    if (image) {
      image.src = book.imagePath || "";
      image.alt = book.title || "";
    }
    app.setText(node.querySelector(".book-author"), book.author);
    app.setText(node.querySelector(".book-title"), book.title);
    app.setText(node.querySelector(".book-price"), app.formatCurrency(book.price));
    app.setText(node.querySelector(".book-stock"), "Stock: " + book.qty);
    bookGrid.appendChild(node);
  });

  app.setText(countNode, (data.books || []).length + " titles ready to browse");
});
