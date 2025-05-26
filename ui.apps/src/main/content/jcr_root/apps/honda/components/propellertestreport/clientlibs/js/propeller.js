
(function($, window, document) {
    "use strict";
    
    var PropellerTestReport = function(config) {
        this.config = config;
        this.apiPath = config.apiPath || '/api/propeller-test-reports';
        this.refreshTime = config.refreshTime || 30; // in minutes
        this.init();
    };
    
    PropellerTestReport.prototype = {
        init: function() {
            var self = this;
            
            // Initialize component
            this.$component = $('#' + this.config.id);
            this.$searchForm = this.$component.find('#search-form');
            this.$resultsBody = this.$component.find('#results-body');
            this.$newReportBtn = this.$component.find('#new-report-btn');
            
            // Bind event handlers
            this.$searchForm.on('submit', function(e) {
                e.preventDefault();
                self.searchReports();
            });
            
            this.$newReportBtn.on('click', function() {
                self.openNewReportDialog();
            });
            
            // Load initial dummy data
            this.loadDummyData();
            
            // Set up refresh timer if needed
            if (this.refreshTime > 0) {
                setInterval(function() {
                    self.refreshData();
                }, this.refreshTime * 60 * 1000);
            }
        },
        
        searchReports: function() {
            var self = this;
            
            // Get form values
            var hullMaterial = this.$component.find('#hull-material').val();
            var hp = this.$component.find('#hp').val();
            var boatType = this.$component.find('#boat-type').val();
            var model = this.$component.find('#model').val();
            
            // In a real implementation, you would make an AJAX call to the API
            // For now, we'll just simulate it with a timeout
            this.$resultsBody.html('<tr><td colspan="5" class="text-center">Loading...</td></tr>');
            
            setTimeout(function() {
                // For demo, just reload dummy data 
                // In real implementation, this would use the search parameters
                self.loadDummyData();
            }, 500);
        },
        
        loadDummyData: function() {
            var dummyData = [
                { hp: '115A', boatType: 'MONOHULL', model: 'APEX/CREST LSR19 GEN II TRITOON', length: '19\'-4"', propeller: '13.5 x 15 x 3' },
                { hp: '100A', boatType: 'MONOHULL', model: 'ALUMACRAFT VOYAGER 175', length: '17\'-10"', propeller: 'Solas Titan 13.75 x 17 x 3' },
                { hp: '100A', boatType: 'MONOHULL', model: 'HIGHFIELD DELUXE 540', length: '17\'-8"', propeller: '13.75 x 15 x 3' },
                { hp: '100A', boatType: 'MONOHULL', model: 'HIGHFIELD DELUXE 540', length: '17\'-8"', propeller: '13.25 x 15 x 4' },
                { hp: '100A', boatType: 'INFLATABLE', model: 'INVENTECH 18\' RIB', length: '18\'-0"', propeller: 'Solas Titan 13.25 x 17 x 3' },
                { hp: '115A', boatType: 'MONOHULL', model: 'KEY WEST 1720 SPORTSMAN', length: '17\'-0"', propeller: '13.5 x 17 x 3' },
                { hp: '100A', boatType: 'MONOHULL', model: 'ALUMACRAFT VOYAGER 175', length: '17\'-10"', propeller: '13.25 x 17 x 3' },
                { hp: '115A', boatType: 'MONOHULL', model: 'JAVELIN/OLD STYLE 17ft FM 75 SKI', length: '18\'-7"', propeller: 'Honda 13 1/4 x 17 x 3' },
                { hp: '115A', boatType: 'MONOHULL', model: 'CAROLINA SKIFF 195 SEA CHASER', length: '17\'-10"', propeller: 'Various' }
            ];
            
            var html = '';
            
            dummyData.forEach(function(item) {
                html += '<tr>';
                html += '<td>' + item.hp + '</td>';
                html += '<td>' + item.boatType + '</td>';
                html += '<td> <a href="#">' + item.model + '</a></td>';
                html += '<td>' + item.length + '</td>';
                html += '<td>' + item.propeller + '</td>';
                html += '</tr>';
            });
            
            this.$resultsBody.html(html);
        },
        
        refreshData: function() {
            console.log('Refreshing data...');
            this.loadDummyData();
        },
        
        openNewReportDialog: function() {
            // In a real implementation, this would open a dialog to create a new report
            alert('New Report functionality would open a dialog here.');
        }
    };
    
    // Initialize component when document is ready
    $(document).ready(function() {
        $('.honda-propeller-test-report').each(function() {
            var $component = $(this);
            var config = {
                id: $component.attr('id'),
                apiPath: $component.data('api-path'),
                refreshTime: parseInt($component.data('refresh-time'), 10)
            };
            
            new PropellerTestReport(config);
        });
    });
    function TableComparer(index) {
        return function (a, b) {
            let val_a = TableCellValue(a, index).replace(/\$\,/g, "");
            let val_b = TableCellValue(b, index).replace(/\$\,/g, "");
            let result = val_a.toString().localeCompare(val_b);

            if ($.isNumeric(val_a) && $.isNumeric(val_b)) {
                result = val_a - val_b;
            }

            if (isDate(val_a) && isDate(val_b)) {
                let date_a = new Date(val_a);
                let date_b = new Date(val_b);
                result = date_a - date_b;
            }

            return result;
        }
    }
    function TableCellValue(row, index) {
        return $(row).children("td").eq(index).text();
    }
    function isDate(val) {
        let d = new Date(val);

        return !isNaN(d.valueOf());
    }
    function filterTable(input) {


        let table = document.getElementById('tns-table');
        let rows = table.getElementsByTagName('tr');

        // Iterate over the rows.
        for (let i = 1; i < rows.length; i++) {

            // Get the cell in the iterated row.
            let td = rows[i].getElementsByTagName('td')[0];
            let match = false;

            var txtValue = td.getAttribute("data-filter");
            if (txtValue.indexOf(input) > -1 || input === "all") {
                rows[i].style.display = "";
            } else {
                rows[i].style.display = "none";
            }

        }
    }
    function displayRadioValue() {
        var ele = document.getElementsByName('filterTns');
        var value;
        for (i = 0; i < ele.length; i++) {
            if (ele[i].checked) {
                value = ele[i].value;
            }
        }
        return
    }
    $(document).on("click", "table thead tr th:not(.no-sort)", function () {
        let table = $(this).parents("table");
        let rows = $(this).parents("table").find("tbody tr").toArray().sort(TableComparer($(this).index()));
        let dir = ($(this).hasClass("sort-asc")) ? "desc" : "asc";

        if (dir == "desc") {
            rows = rows.reverse();
        }

        for (let i = 0; i < rows.length; i++) {
            table.append(rows[i]);
        }

        table.find("thead tr th").removeClass("sort-asc").removeClass("sort-desc");
        $(this).removeClass("sort-asc").removeClass("sort-desc").addClass("sort-" + dir);
    });
    
})(jQuery, window, document);
