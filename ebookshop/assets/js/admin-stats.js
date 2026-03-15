document.addEventListener("DOMContentLoaded", function () {
  var app = window.EbookshopApp;
  var data = app.getPageData();
  var homeLink = document.getElementById("stats-home-link");
  var alertBox = document.getElementById("stats-alert");
  var customerBody = document.getElementById("customer-stats-body");
  var bookBody = document.getElementById("book-stats-body");

  if (homeLink) {
    homeLink.href = data.homeUrl || "#";
  }

  if (data.errorMessage) {
    app.showAlert(alertBox, "danger", data.errorMessage);
  } else {
    app.hide(alertBox);
  }

  if (!(data.topCustomers || []).length) {
    customerBody.appendChild(app.emptyStateRow(3, "No customer orders yet."));
  } else {
    (data.topCustomers || []).forEach(function (customer) {
      var row = app.cloneTemplate("stats-customer-row-template");
      if (!row) {
        return;
      }
      app.setText(row.querySelector(".stats-customer-name"), customer.name);
      app.setText(row.querySelector(".stats-customer-email"), customer.email);
      app.setText(row.querySelector(".stats-customer-total"), app.formatCurrency(customer.totalSpent));
      customerBody.appendChild(row);
    });
  }

  if (!(data.topBooks || []).length) {
    bookBody.appendChild(app.emptyStateRow(3, "No book sales yet."));
  } else {
    (data.topBooks || []).forEach(function (book) {
      var row = app.cloneTemplate("stats-book-row-template");
      if (!row) {
        return;
      }
      app.setText(row.querySelector(".stats-book-title"), book.title);
      app.setText(row.querySelector(".stats-book-sold"), book.totalSold);
      app.setText(row.querySelector(".stats-book-revenue"), app.formatCurrency(book.revenue));
      bookBody.appendChild(row);
    });
  }
});
