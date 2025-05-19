document.addEventListener('DOMContentLoaded', function () {
    const input = document.getElementById('search-input');
    const resultsContainer = document.getElementById('search-results');
    const searchBtn = document.getElementById('search-btn');
    let debounce;

    if (!input || !resultsContainer || !searchBtn) {
        return;
    }

    function performSearch(query) {
        if (!query || query.length < 3) {
            resultsContainer.innerHTML = '';
            return;
        }

        resultsContainer.innerHTML = '<p>Searching...</p>';

        fetch(`/bin/globalSearch?q=${encodeURIComponent(query)}`)
            .then(res => res.json())
            .then(data => {
                resultsContainer.innerHTML = '';

                if (!data.results || data.results.length === 0) {
                    resultsContainer.innerHTML = '<p>No suggestions found.</p>';
                    return;
                }

                data.results.forEach(item => {
                    const div = document.createElement('div');
                    div.classList.add('search-suggestion');
                    div.innerHTML = `
                        <a href="${item.path}.html">
                            <strong>${item.title}</strong><br>
                            <small>${item.description || ''}</small>
                        </a>
                    `;
                    resultsContainer.appendChild(div);
                });
            })
            .catch(err => {
                console.error('Search error:', err);
                resultsContainer.innerHTML = '<p>Error fetching results.</p>';
            });
    }

    // Auto-suggestions
    input.addEventListener('input', () => {
        const query = input.value.trim();

        if (debounce) clearTimeout(debounce);

        if (query.length < 3) {
            resultsContainer.innerHTML = '';
            return;
        }

        debounce = setTimeout(() => performSearch(query), 300);
    });

    // Clear suggestions when clicking outside
    document.addEventListener('click', (e) => {
        if (!resultsContainer.contains(e.target) && e.target !== input) {
            resultsContainer.innerHTML = '';
        }
    });

    // Full search button redirect
    searchBtn.addEventListener('click', () => {
        const query = input.value.trim();
        if (query.length >= 3) {
            window.location.href = `/content/honda/us/en/search.html?q=${encodeURIComponent(query)}`;
        }
    });

    // Run search if query param exists
    const urlParams = new URLSearchParams(window.location.search);
    const queryFromURL = urlParams.get('q');
    if (queryFromURL && queryFromURL.length >= 3) {
        input.value = queryFromURL;
        performSearch(queryFromURL);
    }
});