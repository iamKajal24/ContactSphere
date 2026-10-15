document.addEventListener("DOMContentLoaded", () => {
  const modal = document.getElementById("deleteContactModal");
  const cancelButton = document.getElementById("cancelDeleteButton");
  const confirmButton = document.getElementById("confirmDeleteButton");

  if (!modal || !cancelButton || !confirmButton) {
    console.error("Delete confirmation modal elements were not found.");
    return;
  }

  let pendingForm = null;
  let previousActiveElement = null;

  const openModal = (form) => {
    pendingForm = form;
    previousActiveElement = document.activeElement;

    modal.classList.remove("hidden");
    modal.classList.add("flex");
    document.body.classList.add("overflow-hidden");

    cancelButton.focus();
  };

  const closeModal = () => {
    modal.classList.add("hidden");
    modal.classList.remove("flex");
    document.body.classList.remove("overflow-hidden");

    pendingForm = null;

    if (previousActiveElement instanceof HTMLElement) {
      previousActiveElement.focus();
    }
  };

  document.querySelectorAll("[data-delete-form]").forEach((form) => {
    form.addEventListener("submit", (event) => {
      event.preventDefault();
      openModal(form);
    });
  });

  cancelButton.addEventListener("click", closeModal);

  confirmButton.addEventListener("click", () => {
    if (!pendingForm) return;

    const formToSubmit = pendingForm;

    confirmButton.disabled = true;
    confirmButton.textContent = "Deleting...";

    // Submit the existing POST form to the same backend endpoint.
    HTMLFormElement.prototype.submit.call(formToSubmit);
  });

  modal.addEventListener("click", (event) => {
    if (event.target === modal) {
      closeModal();
    }
  });

  document.addEventListener("keydown", (event) => {
    if (event.key === "Escape" && !modal.classList.contains("hidden")) {
      closeModal();
    }
  });
});
