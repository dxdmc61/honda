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
    // Set profile picture based on user ID
    let profilePicture = '';
    
    if (userId === 'honda-admin') {
      profilePicture = '<img src="/content/dam/honda/icons/admin-profile.png" alt="Admin Profile">';
    } else if (userId === 'honda-auto') {
      profilePicture = '<img src="/content/dam/honda/icons/auto-profile.png" alt="Auto Profile">';
    } else if (userId === 'honda-mc-pe') {
      profilePicture = '<img src="/content/dam/honda/icons/mcpe-profile.png" alt="MC-PE Profile">';
    } else {
      // Fallback to initials if no specific profile picture
      const initials = (firstName && lastName)
        ? firstName.charAt(0) + lastName.charAt(0)
        : userId ? userId.substring(0, 2).toUpperCase() : '??';
      profilePicture = `<span class="initials">${initials}</span>`;
    }
    
    avatarEl.innerHTML = profilePicture;
  }
});

// Search overlay functionality remains the same
document.addEventListener('DOMContentLoaded', () => {
  const searchTrigger = document.getElementById('searchTrigger');
  const searchOverlay = document.getElementById('searchOverlay');
  const searchInput = document.getElementById('searchInputOverlay');
  const closeBtn = document.getElementById('closeSearchOverlay');
  
  if (window.location.pathname.includes('/search.html')) {
    if (searchTrigger) {
      searchTrigger.style.display = 'none';
    }
  }

  // Show overlay
  if (searchTrigger) {
    searchTrigger.addEventListener('click', () => {
      searchOverlay.classList.add('active');
      searchInput.focus();
    });
  }

  // Close overlay
  if (closeBtn) {
    closeBtn.addEventListener('click', () => {
      searchOverlay.classList.remove('active');
    });
  }

  // Close on Escape key
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
      searchOverlay.classList.remove('active');
    }
  });

  // Redirect on Enter
  if (searchInput) {
    searchInput.addEventListener('keypress', (e) => {
      if (e.key === 'Enter') {
        const query = searchInput.value.trim();
        if (query.length >= 3) {
          window.location.href = `/content/honda/us/en/search.html?q=${encodeURIComponent(query)}`;
        }
      }
    });
  }
});

function getCookie(name) {
  const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
  return match ? decodeURIComponent(match[2]) : null;
}