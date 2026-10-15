document.addEventListener("DOMContentLoaded", () => {
  const modal = document.getElementById("contactViewModal");
  const loading = document.getElementById("contactLoading");
  const errorBox = document.getElementById("contactError");
  const details = document.getElementById("contactDetails");

  const closeButton = document.getElementById("closeContactModal");
  const closeFooterButton = document.getElementById("closeContactModalFooter");

  let previousFocusedElement = null;

  function setText(id, value, fallback = "Not provided") {
    const element = document.getElementById(id);
    element.textContent =
      value !== null && value !== undefined && String(value).trim() !== ""
        ? String(value)
        : fallback;
  }

  function formatDate(value) {
    if (!value) return "Not provided";

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
      return String(value);
    }

    return date.toLocaleString();
  }

  function showModal() {
    modal.classList.remove("hidden");
    modal.classList.add("flex");
    document.body.style.overflow = "hidden";
  }

  function closeModal() {
    modal.classList.add("hidden");
    modal.classList.remove("flex");
    document.body.style.overflow = "";

    if (previousFocusedElement) {
      previousFocusedElement.focus();
    }
  }

  function resetModal() {
    loading.classList.add("hidden");
    errorBox.classList.add("hidden");
    details.classList.add("hidden");

    errorBox.textContent = "";
  }

  function setSafeLink(elementId, emptyId, value) {
    const link = document.getElementById(elementId);
    const emptyMessage = document.getElementById(emptyId);

    link.classList.add("hidden");
    emptyMessage.classList.remove("hidden");
    link.removeAttribute("href");

    if (!value || !String(value).trim()) {
      return;
    }

    try {
      const url = new URL(String(value));

      // Only allow normal web links.
      if (url.protocol !== "https:" && url.protocol !== "http:") {
        return;
      }

      link.href = url.href;
      link.classList.remove("hidden");
      emptyMessage.classList.add("hidden");
    } catch {
      // Invalid URLs remain displayed as "Not provided".
    }
  }

  function renderContact(contact) {
    setText("viewContactName", contact.name);
    setText("viewContactEmail", contact.email);
    setText("viewContactPhone", contact.phoneNumber);
    setText("viewContactAddress", contact.address);
    setText("viewContactDescription", contact.description);
    setText("viewContactCreatedAt", formatDate(contact.createdAt));
    setText("viewContactUpdatedAt", formatDate(contact.updatedAt));

    const favorite = document.getElementById("viewContactFavorite");

    if (contact.favorite) {
      favorite.textContent = "♥ Favorite";
      favorite.className =
        "mt-3 inline-flex rounded-full bg-rose-100 px-3 py-1 text-xs font-semibold text-rose-700 dark:bg-rose-900/40 dark:text-rose-300";
    } else {
      favorite.textContent = "Regular Contact";
      favorite.className =
        "mt-3 inline-flex rounded-full bg-gray-100 px-3 py-1 text-xs font-semibold text-gray-700 dark:bg-gray-700 dark:text-gray-200";
    }

    const picture = document.getElementById("viewContactPicture");
    const initial = document.getElementById("viewContactInitial");

    picture.classList.add("hidden");
    initial.classList.remove("hidden");
    picture.removeAttribute("src");
    picture.onerror = null;

    const pictureUrl = contact.picture;

    if (pictureUrl) {
      try {
        const url = new URL(pictureUrl, window.location.origin);

        if (url.protocol === "https:" || url.protocol === "http:") {
          picture.onerror = () => {
            picture.classList.add("hidden");
            initial.classList.remove("hidden");
          };

          picture.src = url.href;
          picture.classList.remove("hidden");
          initial.classList.add("hidden");
        }
      } catch {
        // Keep the initial avatar if the image URL is invalid.
      }
    }

    setText(
      "viewContactInitial",
      contact.name ? contact.name.trim().charAt(0).toUpperCase() : "C",
      "C",
    );

    setSafeLink(
      "viewContactWebsite",
      "viewContactWebsiteEmpty",
      contact.websiteLink,
    );

    setSafeLink(
      "viewContactLinkedIn",
      "viewContactLinkedInEmpty",
      contact.linkedInLink,
    );
  }

  async function loadContact(contactId) {
    resetModal();
    showModal();
    loading.classList.remove("hidden");

    try {
      const response = await fetch(
        `/api/contacts/${encodeURIComponent(contactId)}`,
        {
          method: "GET",
          headers: {
            Accept: "application/json",
          },
          credentials: "same-origin",
        },
      );

      const data = await response.json().catch(() => ({}));

      if (!response.ok) {
        if (response.status === 401 || response.status === 403) {
          throw new Error("Please log in again to view this contact.");
        }

        if (response.status === 404) {
          throw new Error("Contact not found or you don't have access to it.");
        }

        throw new Error(data.message || "Unable to load contact details.");
      }

      renderContact(data);
      details.classList.remove("hidden");
    } catch (exception) {
      errorBox.textContent =
        exception.message || "Something went wrong. Please try again.";

      errorBox.classList.remove("hidden");
    } finally {
      loading.classList.add("hidden");
    }
  }

  // Works for both desktop table and mobile cards.
  document.querySelectorAll("[data-view-contact]").forEach((button) => {
    button.addEventListener("click", () => {
      const contactId = button.dataset.contactId;

      if (!contactId) {
        return;
      }

      previousFocusedElement = button;
      loadContact(contactId);
    });
  });

  closeButton.addEventListener("click", closeModal);
  closeFooterButton.addEventListener("click", closeModal);

  // Close when clicking outside the modal content.
  modal.addEventListener("click", (event) => {
    if (event.target === modal) {
      closeModal();
    }
  });

  // Close with Escape key.
  document.addEventListener("keydown", (event) => {
    if (event.key === "Escape" && !modal.classList.contains("hidden")) {
      closeModal();
    }
  });
});
