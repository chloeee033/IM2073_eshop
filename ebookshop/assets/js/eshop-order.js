document.addEventListener("DOMContentLoaded", function () {
  var app = window.EbookshopApp;
  var data = app.getPageData();
  var statusBadge = document.getElementById("order-status-badge");
  var errorBox = document.getElementById("order-error");
  var successBlock = document.getElementById("receipt-success");
  var printButton = document.getElementById("print-receipt-button");
  var backLink = document.getElementById("order-back-link");
  var itemsBody = document.getElementById("order-items-body");

  if (backLink) {
    backLink.href = data.backToShopUrl || "#";
  }

  if (!data.success) {
    app.setText(statusBadge, "Order Failed");
    statusBadge.style.background = "rgba(182, 76, 47, 0.12)";
    statusBadge.style.color = "#b64c2f";
    app.showAlert(errorBox, "danger", data.errorMessage || "Unable to place the order.");
    printButton.classList.add("d-none");
    return;
  }

  app.hide(errorBox);
  app.show(successBlock);
  app.setText(statusBadge, "Order Placed");
  app.setText(document.getElementById("receipt-thanks"), "Thank you for your purchase, " + (data.customerName || "") + ".");
  app.setText(document.getElementById("receipt-order-id"), "#" + data.orderId);
  app.setText(document.getElementById("receipt-order-date"), data.orderDate);
  app.setText(document.getElementById("receipt-customer-name"), data.customerName);
  app.setText(document.getElementById("receipt-customer-email"), data.customerEmail);
  app.setText(document.getElementById("receipt-customer-phone"), data.customerPhone);
  app.setText(document.getElementById("order-total"), app.formatCurrency(data.total));

  (data.items || []).forEach(function (item) {
    var row = app.cloneTemplate("order-item-row-template");
    if (!row) {
      return;
    }
    app.setText(row.querySelector(".order-item-title"), item.title);
    app.setText(row.querySelector(".order-item-qty"), item.qty);
    app.setText(row.querySelector(".order-item-unit-price"), app.formatCurrency(item.unitPrice));
    app.setText(row.querySelector(".order-item-line-total"), app.formatCurrency(item.lineTotal));
    itemsBody.appendChild(row);
  });

  if (!(data.items || []).length) {
    itemsBody.appendChild(app.emptyStateRow(4, "No order items available."));
  }

  printButton.addEventListener("click", function () {
    window.print();
  });
});
