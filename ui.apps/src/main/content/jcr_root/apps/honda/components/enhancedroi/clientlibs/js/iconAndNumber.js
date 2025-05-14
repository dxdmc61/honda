(function (document, $) {
    "use strict";

    var NS = "cmp-enhanced-roi";
    var IS = "enhancedROI";
    var selectors = {
        self: "[data-cmp-is='" + IS + "']",
        form: ".cmp-enhanced-roi__form",
        searchType: "[data-field='searchType']",
        searchValue: "[data-field='searchValue']",
        dateFrom: "[data-field='dateFrom']",
        dateTo: "[data-field='dateTo']",
        clearBtn: "[data-action='clear']",
        searchBtn: "[data-action='search']",
        spinner: ".cmp-enhanced-roi__spinner",
        alertSuccess: "[data-alert-success]",
        alertError: "[data-alert-error]",
        alertNoResults: "[data-alert-no-results]",
        errorMessage: "[data-error-message]",
        console: ".cmp-enhanced-roi__console",
        resultsBody: ".cmp-enhanced-roi__table-body",
        pagination: {
            info: "[data-pagination-info]",
            controls: "[data-pagination-controls]",
            prev: "[data-pagination-prev]",
            next: "[data-pagination-next]",
            page: "[data-pagination-page]"
        }
    };

    /**
     * Enhanced ROI Interface Component
     * @constructor
     * @param {HTMLElement} element The HTML element representing the component
     */
    function EnhancedROI(element) {
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
            that.resultsBody = element.querySelector(selectors.resultsBody) || element.querySelector("#" + that.id + "-results");

            // Initialize pagination elements
            that.paginationInfo = element.querySelector(selectors.pagination.info);
            that.paginationControls = element.querySelector(selectors.pagination.controls);
            that.paginationPrev = element.querySelector(selectors.pagination.prev);
            that.paginationNext = element.querySelector(selectors.pagination.next);
            that.paginationPages = element.querySelectorAll(selectors.pagination.page);

            // Initialize datepickers
            initDatepickers();

            // Bind events
            bindEvents();

            // Log initialization
            logToConsole("Enhanced ROI Interface Component initialized", "info");
            
            // Load initial data
            loadDummyData();
        }

        /**
         * Initialize datepickers
         */
        function initDatepickers() {
            // Initialize datepicker on date fields if jQuery UI datepicker is available
            if ($.fn.datepicker) {
                $(that.dateFromInput).datepicker({
                    dateFormat: "dd/mm/yy",
                    changeMonth: true,
                    changeYear: true,
                    showOtherMonths: true,
                    selectOtherMonths: true
                });
                
                $(that.dateToInput).datepicker({
                    dateFormat: "dd/mm/yy",
                    changeMonth: true,
                    changeYear: true,
                    showOtherMonths: true,
                    selectOtherMonths: true
                });
            }
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
            
            // Pagination events
            if (that.paginationPrev) {
                that.paginationPrev.addEventListener("click", function(event) {
                    event.preventDefault();
                    prevPage();
                });
            }
            
            if (that.paginationNext) {
                that.paginationNext.addEventListener("click", function(event) {
                    event.preventDefault();
                    nextPage();
                });
            }
            
            if (that.paginationPages) {
                that.paginationPages.forEach(function(pageLink) {
                    pageLink.addEventListener("click", function(event) {
                        event.preventDefault();
                        goToPage(parseInt(pageLink.dataset.paginationPage, 10));
                    });
                });
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
                var dateFrom = new Date(that.dateFromInput.value.split('/').reverse().join('-'));
                var dateTo = new Date(that.dateToInput.value.split('/').reverse().join('-'));

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
                // Add error highlight class to the field
                var inputField = that.element.querySelector("[data-field='" + field + "']");
                if (inputField) {
                    inputField.classList.add("cmp-enhanced-roi__field--error");
                }
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
                // Remove error highlight class from the field
                var inputField = that.element.querySelector("[data-field='" + field + "']");
                if (inputField) {
                    inputField.classList.remove("cmp-enhanced-roi__field--error");
                }
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
            
            // Remove error highlight class from all fields
            var inputFields = that.element.querySelectorAll("[data-field]");
            inputFields.forEach(function(field) {
                field.classList.remove("cmp-enhanced-roi__field--error");
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
            that.searchBtn.classList.add("cmp-enhanced-roi__button--loading");
            if (that.spinner) {
                that.spinner.style.display = "inline-block";
            }

            logToConsole("Starting search with criteria:", "info");
            logToConsole("Search Type: " + searchParams.searchType, "info");
            logToConsole("Search Value: " + searchParams.searchValue, "info");
            logToConsole("Date Range: " + (searchParams.dateFrom || "N/A") + " to " + (searchParams.dateTo || "N/A"), "info");

            // For demo purposes, we'll just load dummy data
            // In production, this would be an actual API call
            setTimeout(function() {
                loadDummyData();
                that.searchBtn.disabled = false;
                that.searchBtn.classList.remove("cmp-enhanced-roi__button--loading");
                if (that.spinner) {
                    that.spinner.style.display = "none";
                }
                
                if (that.alertSuccess) {
                    that.alertSuccess.style.display = "block";
                }
                
                // Update pagination info
                updatePaginationInfo(1, 3, 15);
            }, 1000);

            // In a real implementation, you would use fetch or XMLHttpRequest to call the API
            /*
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
                that.searchBtn.classList.remove("cmp-enhanced-roi__button--loading");
                if (that.spinner) {
                    that.spinner.style.display = "none";
                }
            });
            */
        }

        /**
         * Handle successful search response
         * @param {Object} response API response
         */
        function handleSearchSuccess(response) {
            logToConsole("Search successful!", "success");

            // Check if there are results
            var results = response.results || [];
            var totalCount = response.totalCount || 0;

            // Show appropriate alert
            if (results.length === 0) {
                // No results
                if (that.alertNoResults) {
                    that.alertNoResults.style.display = "block";
                }
            } else {
                // Success with results
                if (that.alertSuccess) {
                    that.alertSuccess.style.display = "block";
                }
                
                // Render results
                renderResults(results);
                
                // Update pagination info
                updatePaginationInfo(response.currentPage || 1, response.totalPages || 1, totalCount);
            }
        }

        /**
         * Handle search error
         * @param {Error} error Error object
         */
        function handleSearchError(error) {
            if (that.alertError) {
                that.alertError.style.display = "block";
                that.alertError.classList.add("cmp-enhanced-roi__alert--shake");
                // Remove animation class after animation completes
                setTimeout(function() {
                    that.alertError.classList.remove("cmp-enhanced-roi__alert--shake");
                }, 820);
            }
            logToConsole("Search failed: " + error.message, "error");
        }

        /**
         * Load dummy data for demonstration
         */
        function loadDummyData() {
            // Using demo data that matches the image format
            var dummyData = [
                {
                    id: '570624',
                    date: '04/23/2016',
                    completed: '04/23/2016',
                    partNumber: '1HGCR2F31FA242364',
                    serviceCode: '36920-T2A-A04',
                    type: 'SAFETY RECALL',
                    typeClass: 'safety-recall',
                    description: 'INSPECT AND REPLACE BATTERY SENSOR',
                    price: '$100.89'
                },
                {
                    id: '570625',
                    date: '04/23/2016',
                    completed: '04/23/2016',
                    partNumber: '1HGCR2F76CA236218',
                    serviceCode: '36920-T2A-A04',
                    type: 'SAFETY RECALL',
                    typeClass: 'safety-recall',
                    description: 'INSPECT AND REPLACE BATTERY SENSOR',
                    price: '$100.89'
                },
                {
                    id: '570002',
                    date: '04/23/2016',
                    completed: '04/23/2016',
                    partNumber: '5FPYK3F59HB032621',
                    serviceCode: '81310-T2A-305',
                    type: 'SERVICE BULLETIN',
                    typeClass: 'service-bulletin',
                    description: 'INSTALL BRACKET TO STRIKER OF SECOND SEAT S/B 16-021',
                    price: '$72.91'
                }
            ];
            
            renderResults(dummyData);
        }

        /**
         * Render results in the table
         * @param {Array} results Results array
         */
        function renderResults(results) {
            if (!that.resultsBody) {
                logToConsole("Results body element not found", "error");
                return;
            }
            
            var html = '';
            
            if (results.length === 0) {
                html = '<tr><td colspan="8" class="cmp-enhanced-roi__no-results">No results found</td></tr>';
            } else {
                results.forEach(function(item, index) {
                    html += '<tr class="cmp-enhanced-roi__table-row ' + (index % 2 === 0 ? 'cmp-enhanced-roi__table-row--even' : 'cmp-enhanced-roi__table-row--odd') + '">';
                    
                    // Checkbox cell
                    html += '<td class="cmp-enhanced-roi__table-cell cmp-enhanced-roi__table-cell--checkbox">';
                    html += '<input type="checkbox" class="cmp-enhanced-roi__checkbox">';
                    html += '</td>';
                    
                    // ID cell
                    html += '<td class="cmp-enhanced-roi__table-cell cmp-enhanced-roi__table-cell--id">';
                    html += '<span class="cmp-enhanced-roi__id">' + item.id + '</span>';
                    html += '</td>';
                    
                    // Date cell
                    html += '<td class="cmp-enhanced-roi__table-cell cmp-enhanced-roi__table-cell--date">';
                    html += '<div class="cmp-enhanced-roi__date">' + item.date + '</div>';
                    html += '<div class="cmp-enhanced-roi__completed">Completed: ' + item.completed + '</div>';
                    html += '</td>';
                    
                    // Part number cell
                    html += '<td class="cmp-enhanced-roi__table-cell cmp-enhanced-roi__table-cell--part-number">';
                    html += item.partNumber;
                    html += '</td>';
                    
                    // Service code cell
                    html += '<td class="cmp-enhanced-roi__table-cell cmp-enhanced-roi__table-cell--service-code">';
                    html += item.serviceCode;
                    html += '</td>';
                    
                    // Service type and description cell
                    html += '<td class="cmp-enhanced-roi__table-cell cmp-enhanced-roi__table-cell--service">';
                    html += '<div class="cmp-enhanced-roi__type cmp-enhanced-roi__type--' + item.typeClass + '">' + item.type + '</div>';
                    html += '<div class="cmp-enhanced-roi__description">' + item.description + '</div>';
                    html += '</td>';
                    
                    // Price cell
                    html += '<td class="cmp-enhanced-roi__table-cell cmp-enhanced-roi__table-cell--price">';
                    html += '<span class="cmp-enhanced-roi__price">' + item.price + '</span>';
                    html += '</td>';
                    
                    // Action cell (right arrow)
                    html += '<td class="cmp-enhanced-roi__table-cell cmp-enhanced-roi__table-cell--action">';
                    html += '<span class="cmp-enhanced-roi__action">&#10095;</span>';
                    html += '</td>';
                    
                    html += '</tr>';
                });
            }
            
            that.resultsBody.innerHTML = html;
            
            // Add hover effect to table rows
            var tableRows = that.resultsBody.querySelectorAll('.cmp-enhanced-roi__table-row');
            tableRows.forEach(function(row) {
                row.addEventListener('mouseenter', function() {
                    this.classList.add('cmp-enhanced-roi__table-row--hover');
                });
                row.addEventListener('mouseleave', function() {
                    this.classList.remove('cmp-enhanced-roi__table-row--hover');
                });
                
                // Add click event to expand row details or navigate
                row.addEventListener('click', function(event) {
                    // Ignore clicks on checkbox
                    if (event.target.type === 'checkbox') {
                        return;
                    }
                    
                    // Toggle selected state
                    this.classList.toggle('cmp-enhanced-roi__table-row--selected');
                    
                    // In a real implementation, you might navigate to a detail page or expand the row
                    logToConsole("Row selected: " + this.querySelector('.cmp-enhanced-roi__id').textContent, "info");
                });
            });
            
            // Style specific elements
            var safetyRecalls = that.resultsBody.querySelectorAll('.cmp-enhanced-roi__type--safety-recall');
            safetyRecalls.forEach(function(element) {
                element.style.backgroundColor = '#f8d7da';
                element.style.color = '#721c24';
                element.style.padding = '2px 8px';
                element.style.borderRadius = '4px';
                element.style.display = 'inline-block';
                element.style.fontSize = '12px';
                element.style.fontWeight = '500';
            });
            
            var serviceBulletins = that.resultsBody.querySelectorAll('.cmp-enhanced-roi__type--service-bulletin');
            serviceBulletins.forEach(function(element) {
                element.style.backgroundColor = '#cce5ff';
                element.style.color = '#004085';
                element.style.padding = '2px 8px';
                element.style.borderRadius = '4px';
                element.style.display = 'inline-block';
                element.style.fontSize = '12px';
                element.style.fontWeight = '500';
            });
            
            var idElements = that.resultsBody.querySelectorAll('.cmp-enhanced-roi__id');
            idElements.forEach(function(element) {
                element.style.color = '#dc3545';
                element.style.fontWeight = '500';
            });
            
            var dateElements = that.resultsBody.querySelectorAll('.cmp-enhanced-roi__date');
            dateElements.forEach(function(element) {
                element.style.fontWeight = '500';
            });
            
            var completedElements = that.resultsBody.querySelectorAll('.cmp-enhanced-roi__completed');
            completedElements.forEach(function(element) {
                element.style.fontSize = '12px';
                element.style.color = '#6c757d';
            });
            
            var descriptionElements = that.resultsBody.querySelectorAll('.cmp-enhanced-roi__description');
            descriptionElements.forEach(function(element) {
                element.style.fontWeight = '500';
                element.style.marginTop = '4px';
            });
            
            var priceElements = that.resultsBody.querySelectorAll('.cmp-enhanced-roi__price');
            priceElements.forEach(function(element) {
                element.style.fontWeight = '500';
                element.style.fontSize = '16px';
            });
            
            var actionElements = that.resultsBody.querySelectorAll('.cmp-enhanced-roi__action');
            actionElements.forEach(function(element) {
                element.style.color = '#6c757d';
                element.style.fontSize = '18px';
                element.style.cursor = 'pointer';
            });
        }

        /**
         * Update pagination information
         * @param {number} currentPage Current page number
         * @param {number} totalPages Total number of pages
         * @param {number} totalItems Total number of items
         */
        function updatePaginationInfo(currentPage, totalPages, totalItems) {
            var startItem = (currentPage - 1) * 5 + 1;
            var endItem = Math.min(currentPage * 5, totalItems);
            
            if (that.paginationInfo) {
                that.paginationInfo.textContent = "Showing " + startItem + " to " + endItem + " of " + totalItems + " entries";
                that.paginationInfo.classList.add("cmp-enhanced-roi__pagination-info--updated");
                setTimeout(function() {
                    that.paginationInfo.classList.remove("cmp-enhanced-roi__pagination-info--updated");
                }, 1000);
            }
            
            // Update active page
            if (that.paginationPages) {
                that.paginationPages.forEach(function(pageLink) {
                    var pageNum = parseInt(pageLink.dataset.paginationPage, 10);
                    if (pageNum === currentPage) {
                        pageLink.classList.add("cmp-enhanced-roi__pagination-page--active");
                    } else {
                        pageLink.classList.remove("cmp-enhanced-roi__pagination-page--active");
                    }
                });
            }
            
            // Disable/enable prev/next buttons
            if (that.paginationPrev) {
                that.paginationPrev.classList.toggle("cmp-enhanced-roi__pagination-disabled", currentPage <= 1);
                that.paginationPrev.setAttribute("aria-disabled", currentPage <= 1);
            }
            
            if (that.paginationNext) {
                that.paginationNext.classList.toggle("cmp-enhanced-roi__pagination-disabled", currentPage >= totalPages);
                that.paginationNext.setAttribute("aria-disabled", currentPage >= totalPages);
            }
        }

        /**
         * Go to the previous page
         */
        function prevPage() {
            // This would be handled with the real API
            // For demo purposes, we'll just show a message
            logToConsole("Going to previous page", "info");
        }

        /**
         * Go to the next page
         */
        function nextPage() {
            // This would be handled with the real API
            // For demo purposes, we'll just show a message
            logToConsole("Going to next page", "info");
        }

        /**
         * Go to a specific page
         * @param {number} pageNumber Page number
         */
        function goToPage(pageNumber) {
            // This would be handled with the real API
            // For demo purposes, we'll just show a message
            logToConsole("Going to page " + pageNumber, "info");
        }

        /**
         * Hide all alert messages
         */
        function hideAllAlerts() {
            if (that.alertSuccess) {
                that.alertSuccess.style.display = "none";
                that.alertSuccess.classList.remove("cmp-enhanced-roi__alert--fade-in");
            }
            if (that.alertError) {
                that.alertError.style.display = "none";
                that.alertError.classList.remove("cmp-enhanced-roi__alert--fade-in");
            }
            if (that.alertNoResults) {
                that.alertNoResults.style.display = "none";
                that.alertNoResults.classList.remove("cmp-enhanced-roi__alert--fade-in");
            }
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

            // Add animation class to form
            that.form.classList.add("cmp-enhanced-roi__form--cleared");
            setTimeout(function() {
                that.form.classList.remove("cmp-enhanced-roi__form--cleared");
            }, 500);

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
            line.className = "cmp-enhanced-roi__console-line cmp-enhanced-roi__console-" + (type || "info");
            line.classList.add("cmp-enhanced-roi__console-line--new");

            // Create log line with appropriate formatting based on type
            if (type === "info") {
                line.innerHTML = "[" + timestamp + "] <span class='cmp-enhanced-roi__console-info'>INFO:</span> " + message;
            } else if (type === "success") {
                line.innerHTML = "[" + timestamp + "] <span class='cmp-enhanced-roi__console-success'>SUCCESS:</span> " + message;
            } else if (type === "error") {
                line.innerHTML = "[" + timestamp + "] <span class='cmp-enhanced-roi__console-error'>ERROR:</span> " + message;
            } else if (type === "debug") {
                line.innerHTML = "[" + timestamp + "] <span class='cmp-enhanced-roi__console-debug'>DEBUG:</span> " + message;
            }

            // Add to display
            that.consoleEl.appendChild(line);
            that.consoleEl.scrollTop = that.consoleEl.scrollHeight;
            
            // Remove new line indicator after animation
            setTimeout(function() {
                line.classList.remove("cmp-enhanced-roi__console-line--new");
            }, 1000);
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
            new EnhancedROI(this);
        });
    });

    // Initialize when Granite UI adds the component
    $(document).on("foundation-contentloaded", function() {
        $(selectors.self).each(function() {
            if (!this.hasEnhancedROI) {
                new EnhancedROI(this);
                this.hasEnhancedROI = true;
            }
        });
    });
})(document, Granite.$);