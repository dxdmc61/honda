$(document).ready(function () {
    $(document).on("click", "#select-all", function () {
		$("input[type=checkbox]").prop('checked', $(this).prop('checked'));
    });
    $(document).on("click", "#btn-del-order", function () {
		$("#tns-table input[type=checkbox]:checked").each(function () {
                $(this).closest("tr").hide();
                
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
            if (txtValue.indexOf(input) > -1 || input==="all") {
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

    $(document).on("click", "#sales", function () {


        filterTable("sales");

    });
    $(document).on("click", "#service", function () {


        filterTable("service");

    });
    $(document).on("click", "#cpo", function () {


        filterTable("cpo");

    });
    $(document).on("click", "#parts", function () {


        filterTable("parts");

    });
    $(document).on("click", "#hfs", function () {


        filterTable("hfs");

    });
    $(document).on("click", "#misc", function () {


        filterTable("misc");

    });
    $(document).on("click", "#all", function () {


        filterTable("all");

    });
});
