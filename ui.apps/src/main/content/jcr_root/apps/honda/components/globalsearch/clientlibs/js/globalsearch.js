document.addEventListener('DOMContentLoaded', function () {
  const searchBtn = document.getElementById('search-btn');
  const input = document.getElementById('search-input');
  const resultsContainer = document.getElementById('search-results');

  searchBtn.addEventListener('click', () => {
      const query = input.value;
      fetch(`/bin/globalSearch?q=${encodeURIComponent(query)}`)
          .then(res => res.json())
          .then(data => {
              resultsContainer.innerHTML = '';
              if (data.results.length === 0) {
                  resultsContainer.innerHTML = '<p>No results found.</p>';
                  return;
              }
              data.results.forEach(item => {
                  const div = document.createElement('div');
                  div.classList.add('search-result');
                  div.innerHTML = `<a href="${item.path}">${item.title}</a>`;
                  resultsContainer.appendChild(div);
              });
          });
  });
});
