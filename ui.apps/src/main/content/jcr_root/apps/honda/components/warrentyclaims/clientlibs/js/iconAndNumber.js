/**
    * Search Claims Component JavaScript
    */
(function (document, $) {
    "use strict";

    var NS = "cmp-search-claims";
    var IS = "searchClaims";
    var selectors = {
        self: "[data-cmp-is='" + IS + "']",
        form: ".cmp-search-claims__form",
        searchType: "[data-field='searchType']",
        searchValue: "[data-field='searchValue']",
        dateFrom: "[data-field='dateFrom']",
        dateTo: "[data-field='dateTo']",
        clearBtn: "[data-action='clear']",
        searchBtn: "[data-action='search']",
        spinner: ".cmp-search-claims__spinner",
        alertSuccess: "[data-alert-success]",
        alertError: "[data-alert-error]",
        alertNoResults: "[data-alert-no-results]",
        errorMessage: "[data-error-message]",
        console: ".cmp-search-claims__console"
    };

    /**
     * Search Claims Component
     * @constructor
     * @param {HTMLElement} element The HTL element used for Search Claims Component
     */
    function SearchClaims(element) {
        var that = this;

        // Initialize component
        function init() {
            that.element = element;
            that.id = element.id;
            that.apiEndpoint = element.dataset.apiEndpoint;

            // Try to parse search options
            try {
                that.searchOptions = JSON.parse(element.dataset.searchOptions || "[]");
            } catch (e) {
                console.error("Error parsing search options:", e);
                that.searchOptions = [];
            }

            // Cache DOM elements
            that.form = element.querySelector(selectors.form);
            that.searchTypeSelect = element.querySelector(selectors.searchType);
            that.searchValueInput = element.querySelector(selectors.searchValue);
            that.dateFromInput = element.querySelector(selectors.dateFrom);
            that.dateToInput = element.querySelector(selectors.dateTo);
            that.clearBtn = element.querySelector(selectors.clearBtn);
            that.searchBtn = element.querySelector(selectors.searchBtn);
            that.spinner = element.querySelector(selectors.spinner);
            that.alertSuccess = element.querySelector(selectors.alertSuccess);
            that.alertError = element.querySelector(selectors.alertError);
            that.alertNoResults = element.querySelector(selectors.alertNoResults);
            that.consoleEl = element.querySelector(selectors.console);

            // Bind events
            bindEvents();

            // Log initialization
            logToConsole("Search Claims Component initialized", "info");
        }

        /**
         * Bind event listeners
         */
        function bindEvents() {
            // Update search value placeholder based on selected search type
            if (that.searchTypeSelect) {
                that.searchTypeSelect.addEventListener("change", updateSearchValuePlaceholder);
            }

            // Form submission
            if (that.form) {
                that.form.addEventListener("submit", function(event) {
                    event.preventDefault();
                    performSearch();
                });
            }

            // Clear form
            if (that.clearBtn) {
                that.clearBtn.addEventListener("click", clearForm);
            }
        }

        /**
         * Update search value placeholder based on selected search type
         */
        function updateSearchValuePlaceholder() {
            var searchType = that.searchTypeSelect.value;
            var placeholder = "Enter search value...";

            // Find placeholder based on selected search type
            that.searchOptions.forEach(function(option) {
                if (option.value === searchType) {
                    placeholder = "Enter " + option.text.toLowerCase() + "...";
                }
            });

            // Update placeholder
            that.searchValueInput.placeholder = placeholder;

            // Clear any existing error
            resetError("searchType");
        }

        /**
         * Form validation
         * @returns {boolean} True if form is valid, false otherwise
         */
        function validateForm() {
            var isValid = true;

            // Reset all error messages
            resetAllErrors();

            // Validate search type
            if (!that.searchTypeSelect.value) {
                showError("searchType", "Please select a search option");
                isValid = false;
            }

            // Validate search value (required if search type is selected)
            if (that.searchTypeSelect.value && !that.searchValueInput.value.trim()) {
                showError("searchValue", "Please enter a search value");
                isValid = false;
            }

            // Validate date range if both dates are provided
            if (that.dateFromInput.value && that.dateToInput.value) {
                var dateFrom = new Date(that.dateFromInput.value);
                var dateTo = new Date(that.dateToInput.value);

                if (dateFrom > dateTo) {
                    showError("dateFrom", "From date cannot be after To date");
                    isValid = false;
                }
            }

            return isValid;
        }

        /**
         * Show error message for a specific field
         * @param {string} field Field name
         * @param {string} message Error message
         */
        function showError(field, message) {
            var errorEl = that.element.querySelector(selectors.errorMessage + "[data-error-message='" + field + "']");
            if (errorEl) {
                errorEl.textContent = message;
                errorEl.style.display = "block";
            }
        }

        /**
         * Reset error message for a specific field
         * @param {string} field Field name
         */
        function resetError(field) {
            var errorEl = that.element.querySelector(selectors.errorMessage + "[data-error-message='" + field + "']");
            if (errorEl) {
                errorEl.textContent = "";
                errorEl.style.display = "none";
            }
        }

        /**
         * Reset all error messages
         */
        function resetAllErrors() {
            var errorElements = that.element.querySelectorAll(selectors.errorMessage);
            errorElements.forEach(function(el) {
                el.textContent = "";
                el.style.display = "none";
            });
        }

        /**
         * Perform search
         */
        function performSearch() {
            // Hide any existing alerts
            hideAllAlerts();

            // Validate form
            if (!validateForm()) {
                logToConsole("Form validation failed", "error");
                return;
            }

            // Prepare search parameters
            var searchParams = {
                searchType: that.searchTypeSelect.value,
                searchValue: that.searchValueInput.value.trim(),
                dateFrom: that.dateFromInput.value,
                dateTo: that.dateToInput.value
            };

            // Show loading state
            that.searchBtn.disabled = true;
            if (that.spinner) {
                that.spinner.style.display = "inline-block";
            }

            logToConsole("Starting search with criteria:", "info");
            logToConsole("Search Type: " + searchParams.searchType, "info");
            logToConsole("Search Value: " + searchParams.searchValue, "info");
            logToConsole("Date Range: " + (searchParams.dateFrom || "N/A") + " to " + (searchParams.dateTo || "N/A"), "info");

            // Call search API
            fetch(that.apiEndpoint, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                },
                body: JSON.stringify(searchParams)
            })
            .then(function(response) {
                if (!response.ok) {
                    throw new Error("API request failed with status: " + response.status);
                }
                return response.json();
            })
            .then(function(data) {
                handleSearchSuccess(data);
            })
            .catch(function(error) {
                handleSearchError(error);
            })
            .finally(function() {
                // Reset loading state
                that.searchBtn.disabled = false;
                if (that.spinner) {
                    that.spinner.style.display = "none";
                }
            });
        }

        /**
         * Handle successful search response
         * @param {Object} response API response
         */
        function handleSearchSuccess(response) {
            logToConsole("Search successful!", "success");

            // Check if there are results
            var claims = response.claims || [];
            var totalCount = response.totalCount || 0;

            // Show appropriate alert
            if (claims.length === 0) {
                // No results
                if (that.alertNoResults) {
                    that.alertNoResults.style.display = "block";
                }
            } else {
                // Success with results
                if (that.alertSuccess) {
                    that.alertSuccess.style.display = "block";
                }
            }

            // Trigger custom event for table component to listen for
            var event = new CustomEvent("honda.search.completed", {
                detail: {
                    results: claims,
                    totalCount: totalCount
                },
                bubbles: true
            });
            that.element.dispatchEvent(event);
        }

        /**
         * Handle search error
         * @param {Error} error Error object
         */
        function handleSearchError(error) {
            if (that.alertError) {
                that.alertError.style.display = "block";
            }
            logToConsole("Search failed: " + error.message, "error");
        }

        /**
         * Hide all alert messages
         */
        function hideAllAlerts() {
            if (that.alertSuccess) that.alertSuccess.style.display = "none";
            if (that.alertError) that.alertError.style.display = "none";
            if (that.alertNoResults) that.alertNoResults.style.display = "none";
        }

        /**
         * Clear form fields and messages
         */
        function clearForm() {
            // Reset form fields
            if (that.searchTypeSelect) that.searchTypeSelect.value = "";
            if (that.searchValueInput) {
                that.searchValueInput.value = "";
                that.searchValueInput.placeholder = "Enter search value...";
            }
            if (that.dateFromInput) that.dateFromInput.value = "";
            if (that.dateToInput) that.dateToInput.value = "";

            // Reset all error messages
            resetAllErrors();

            // Hide alerts
            hideAllAlerts();

            logToConsole("Form cleared", "info");
        }

        /**
         * Log to console
         * @param {string} message Message to log
         * @param {string} type Log type (info, success, error, debug)
         */
        function logToConsole(message, type) {
            if (!that.consoleEl) {
                // Debug console not available, log to browser console instead
                console.log(message);
                return;
            }

            var now = new Date();
            var timestamp = padZero(now.getHours(), 2) + ":" +
                          padZero(now.getMinutes(), 2) + ":" +
                          padZero(now.getSeconds(), 2) + "." +
                          padZero(now.getMilliseconds(), 3);

            var line = document.createElement("div");
            line.className = "cmp-search-claims__console-line cmp-search-claims__console-" + (type || "info");

            // Create log line with appropriate formatting based on type
            if (type === "info") {
                line.innerHTML = "[" + timestamp + "] <span class='cmp-search-claims__console-info'>INFO:</span> " + message;
            } else if (type === "success") {
                line.innerHTML = "[" + timestamp + "] <span class='cmp-search-claims__console-success'>SUCCESS:</span> " + message;
            } else if (type === "error") {
                line.innerHTML = "[" + timestamp + "] <span class='cmp-search-claims__console-error'>ERROR:</span> " + message;
            } else if (type === "debug") {
                line.innerHTML = "[" + timestamp + "] <span class='cmp-search-claims__console-debug'>DEBUG:</span> " + message;
            }

            // Add to display
            that.consoleEl.appendChild(line);
            that.consoleEl.scrollTop = that.consoleEl.scrollHeight;
        }

        /**
         * Pad number with leading zeros
         * @param {number} num Number to pad
         * @param {number} size Desired size
         * @returns {string} Padded number
         */
        function padZero(num, size) {
            var s = String(num);
            while (s.length < size) {
                s = "0" + s;
            }
            return s;
        }

        // Initialize component
        init();
    }

    // Initialize when document is ready
    $(document).ready(function() {
        $(selectors.self).each(function() {
            new SearchClaims(this);
        });
    });

    // Initialize when Granite UI adds the component
    $(document).on("foundation-contentloaded", function() {
        $(selectors.self).each(function() {
            if (!this.hasSearchClaims) {
                new SearchClaims(this);
                this.hasSearchClaims = true;
            }
        });
    });
})(document, Granite.$);