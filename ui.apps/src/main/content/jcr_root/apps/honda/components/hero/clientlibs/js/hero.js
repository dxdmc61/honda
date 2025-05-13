// /apps/your-site/components/hero/clientlibs/js/hero.js
document.addEventListener("DOMContentLoaded", function () {
    const dateElement = document.getElementById("todayDate");
    if (dateElement) {
      const today = new Date();
      const formatted = today.toLocaleDateString("en-US", {
        month: "2-digit",
        day: "2-digit",
        year: "numeric",
      });
      dateElement.textContent = `Today : ${formatted}`;
    }
  });
  