document.addEventListener("DOMContentLoaded", function () {
  const toast = document.getElementById("successToast");
  const closeButton = document.getElementById("closeSuccessToast");

  if (!toast) {
    return;
  }

  let hideTimer;

  function hideToast() {
    toast.classList.add("opacity-0", "translate-x-4");

    setTimeout(function () {
      toast.remove();
    }, 300);
  }

  hideTimer = setTimeout(hideToast, 4000);

  if (closeButton) {
    closeButton.addEventListener("click", function () {
      clearTimeout(hideTimer);
      hideToast();
    });
  }
});
