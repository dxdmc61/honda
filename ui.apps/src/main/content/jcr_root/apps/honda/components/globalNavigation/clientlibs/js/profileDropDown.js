document.addEventListener('DOMContentLoaded', function () {
  const profile = document.getElementById('userProfile');
  const dropdown = document.getElementById('userDropdown');

  if (profile && dropdown) {
    profile.addEventListener('click', function (e) {
      e.stopPropagation();
      dropdown.classList.toggle('hidden');
    });

    document.addEventListener('click', function (e) {
      if (!profile.contains(e.target)) {
        dropdown.classList.add('hidden');
      }
    });
  } else {
    console.warn('User profile or dropdown not found in DOM.');
  }

  const firstName = getCookie('hondaFirstName');
  const lastName = getCookie('hondaLastName');
  const userId = getCookie('hondaUserId');

  const userNameEl = document.querySelector('.user-name');
  const avatarEl = document.querySelector('.avatar');

  if (userNameEl && (firstName || lastName)) {
    userNameEl.textContent = `${firstName || ''} ${lastName || ''}`.trim();
  }

  if (avatarEl) {
    // Initials: e.g. "Tech Manager" => "TM"
    const initials = (firstName && lastName)
      ? firstName.charAt(0) + lastName.charAt(0)
      : userId ? userId.substring(0, 2).toUpperCase() : '??';

    avatarEl.textContent = initials;
  }
});

function getCookie(name) {
  const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
  return match ? decodeURIComponent(match[2]) : null;
}
document.addEventListener('DOMContentLoaded', () => {
  const searchTrigger = document.getElementById('searchTrigger');
  const searchOverlay = document.getElementById('searchOverlay');
  const searchInput = document.getElementById('searchInputOverlay');
  const closeBtn = document.getElementById('closeSearchOverlay');

  // Show overlay
  searchTrigger.addEventListener('click', () => {
    searchOverlay.classList.add('active');
    searchInput.focus();
  });

  // Close overlay
  closeBtn.addEventListener('click', () => {
    searchOverlay.classList.remove('active');
  });

  // Close on Escape key
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
      searchOverlay.classList.remove('active');
    }
  });

  // Redirect on Enter
  searchInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
      const query = searchInput.value.trim();
      if (query.length >= 3) {
        window.location.href = `/content/honda/us/en/search.html?q=${encodeURIComponent(query)}`;
      }
    }
  });
});

