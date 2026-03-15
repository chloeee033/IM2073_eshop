document.addEventListener("DOMContentLoaded", function () {
  var app = window.EbookshopApp;
  var data = app.getPageData();
  var homeLink = document.getElementById("query-home-link");
  var adminLink = document.getElementById("query-admin-link");
  var orderForm = document.getElementById("order-form");
  var messageBox = document.getElementById("query-message");
  var selectedAuthors = document.getElementById("selected-authors");
  var resultsBody = document.getElementById("query-results-body");
  var resultsPanel = document.getElementById("results-panel");
  var customerPanel = document.getElementById("customer-panel");
  var submitButton = document.getElementById("submit-order-button");
  var countNode = document.getElementById("query-count");

  if (homeLink) {
    homeLink.href = data.homeUrl || "#";
  }
  if (adminLink) {
    adminLink.href = data.adminLoginUrl || "#";
  }
  if (orderForm) {
    orderForm.action = data.orderAction || "";
  }

  (data.selectedAuthors || []).forEach(function (author) {
    var chip = document.createElement("span");
    chip.className = "selected-author-chip";
    chip.textContent = author;
    selectedAuthors.appendChild(chip);
  });

  if (!data.hasSelectedAuthors) {
    app.showAlert(messageBox, "warning", "No author selected. Please go back and choose at least one author.");
    resultsPanel.classList.add("d-none");
    customerPanel.classList.add("d-none");
    submitButton.classList.add("d-none");
    return;
  }

  if (data.errorMessage) {
    app.showAlert(messageBox, "danger", data.errorMessage);
    resultsPanel.classList.add("d-none");
    customerPanel.classList.add("d-none");
    submitButton.classList.add("d-none");
    return;
  }

  app.hide(messageBox);

  if (!(data.results || []).length) {
    resultsBody.appendChild(app.emptyStateRow(5, "No in-stock books matched your filters."));
    app.setText(countNode, "Found 0 matches for your selection.");
    customerPanel.classList.add("d-none");
    submitButton.classList.add("d-none");
    return;
  }

  (data.results || []).forEach(function (book) {
    var row = app.cloneTemplate("query-result-row-template");
    if (!row) {
      return;
    }
    var checkbox = row.querySelector("input[name='id']");
    if (checkbox) {
      checkbox.value = book.id;
    }
    var image = row.querySelector(".query-book-image");
    if (image) {
      image.src = book.imagePath || "";
      image.alt = book.title || "";
    }
    app.setText(row.querySelector(".query-book-author"), book.author);
    app.setText(row.querySelector(".query-book-title"), book.title);
    app.setText(row.querySelector(".query-book-price"), app.formatCurrency(book.price));
    resultsBody.appendChild(row);
  });

  app.setText(countNode, "Found " + (data.results || []).length + " match(es) for your selection.");
});
