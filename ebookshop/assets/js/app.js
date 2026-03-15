(function () {
  function getPageData() {
    var node = document.getElementById("page-data");
    if (!node || !node.textContent) {
      return {};
    }
    try {
      return JSON.parse(node.textContent);
    } catch (error) {
      console.error("Failed to parse page data", error);
      return {};
    }
  }

  function cloneTemplate(templateId) {
    var template = document.getElementById(templateId);
    if (!template) {
      return null;
    }

    var wrapper = document.createElement("div");
    wrapper.appendChild(template.content.cloneNode(true));
    return wrapper.firstElementChild;
  }

  function showAlert(node, type, message) {
    if (!node) {
      return;
    }
    node.className = "alert alert-" + type;
    node.textContent = message;
  }

  function hide(node) {
    if (node) {
      node.classList.add("d-none");
    }
  }

  function show(node, displayClass) {
    if (!node) {
      return;
    }
    node.classList.remove("d-none");
    if (displayClass) {
      node.classList.add(displayClass);
    }
  }

  function formatCurrency(value) {
    var amount = Number(value || 0);
    return "$" + amount.toFixed(2);
  }

  function setText(node, text) {
    if (node) {
      node.textContent = text == null ? "" : String(text);
    }
  }

  function emptyStateRow(colspan, message) {
    var row = document.createElement("tr");
    row.className = "empty-row";
    var cell = document.createElement("td");
    cell.colSpan = colspan;
    cell.textContent = message;
    row.appendChild(cell);
    return row;
  }

  window.EbookshopApp = {
    cloneTemplate: cloneTemplate,
    emptyStateRow: emptyStateRow,
    formatCurrency: formatCurrency,
    getPageData: getPageData,
    hide: hide,
    setText: setText,
    show: show,
    showAlert: showAlert
  };
}());
