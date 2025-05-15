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

  // Show search bar
  searchTrigger.addEventListener('click', () => {
    searchOverlay.classList.remove('hidden');
    searchInput.focus();
  });

  // Hide on outside click or Esc
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
      searchOverlay.classList.add('hidden');
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

  document.addEventListener('click', (e) => {
    // Don't hide if the click is inside the overlay or on the search trigger
    if (
        !searchOverlay.contains(e.target) &&
        !searchTrigger.contains(e.target) &&  // in case trigger has child elements
        e.target.id !== 'search-input' &&      // allow typing
        !document.getElementById('search-results')?.contains(e.target) // allow clicking results
    ) {
        searchOverlay.classList.add('hidden');
    }
});

});