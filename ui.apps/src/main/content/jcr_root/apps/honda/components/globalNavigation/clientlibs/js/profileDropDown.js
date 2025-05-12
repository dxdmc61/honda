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
  });
  