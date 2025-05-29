// Initialize all charts
const initCharts = function (data) {
    if (!data) return;

    // Parts Orders Trend (Line Chart)
    createLineChart("partsTrendChart", {
        data: data.partsOrders.monthlyTrend,
        xField: "month",
        yFields: ["orders", "fulfilled"],
        seriesNames: ["Total Orders", "Fulfilled"],
        colors: ["#0066B3", "#00A0E1"],
        title: "Monthly Parts Order Trend"
    });

    // Top Parts (Bar Chart)
    createBarChart("topPartsChart", {
        data: data.partsOrders.topParts,
        categoryField: "part",
        valueField: "orders",
        title: "Top Ordered Parts",
        color: "#0066B3"
    });

    // Service Appointments (Column Chart)
    createColumnChart("serviceAppointmentsChart", {
        data: data.service.appointments,
        xField: "day",
        yFields: ["scheduled", "completed"],
        seriesNames: ["Scheduled", "Completed"],
        colors: ["#0066B3", "#00A0E1"],
        title: "Weekly Appointments"
    });

    // Service Types (Pie Chart)
    createPieChart("serviceTypesChart", {
        data: data.service.serviceTypes,
        categoryField: "type",
        valueField: "count",
        title: "Service Type Distribution",
        colors: ["#0066B3", "#00A0E1", "#7FC4FD", "#003366", "#6699CC"]
    });

    // CSI Trend (Line Chart)
    createLineChart("csiTrendChart", {
        data: data.feedback.csiScores,
        xField: "month",
        yFields: ["score"],
        seriesNames: ["CSI Score"],
        colors: ["#00A859"],
        title: "Customer Satisfaction Index Trend"
    });

    // Feedback Categories (Stacked Bar Chart)
    createStackedBarChart("feedbackCategoriesChart", {
        data: data.feedback.feedbackCategories,
        categoryField: "category",
        valueFields: ["positive", "negative"],
        seriesNames: ["Positive", "Negative"],
        colors: ["#00A859", "#E31937"],
        title: "Feedback by Category"
    });

    // Sales Target (Radar Chart)
    /*createRadarChart("salesTargetChart", {
        data: data.dealerPerformance.salesTarget,
        categoryField: "model",
        valueFields: ["target", "actual"],
        seriesNames: ["Target", "Actual"],
        colors: ["#0066B3", "#00A0E1"],
        title: "Sales Performance vs Target"
    });*/
    createGroupedColumnChart("salesTargetChart", {
        data: data.dealerPerformance.salesTarget,
        categoryField: "model",
        valueFields: ["target", "actual"],
        seriesNames: ["Target", "Actual"],
        colors: ["#0066B3", "#00A0E1"],
        title: "Sales Performance vs Target"
    });
    
    createDonutChart("userStatusChart", {
    data: [
        { category: "Active Users", value: data.dealerNetwork.activeUsers },
        { category: "Inactive Users", value: data.dealerNetwork.inactiveUsers }
    ],
    categoryField: "category",
    valueField: "value",
    title: "User Status Distribution"
});

};

// Chart creation functions
const createLineChart = function (containerId, config) {
    am5.ready(function () {
        const root = am5.Root.new(containerId);
        root.setThemes([am5themes_Animated.new(root)]);

        const chart = root.container.children.push(
            am5xy.XYChart.new(root, {
                panX: false,
                panY: false,
                wheelX: "panX",
                wheelY: "zoomX",
                layout: root.verticalLayout
            })
        );

        // Add legend
        const legend = chart.children.push(
            am5.Legend.new(root, {
                centerX: am5.p50,
                x: am5.p50
            })
        );

        // Create axes
        const xAxis = chart.xAxes.push(
            am5xy.CategoryAxis.new(root, {
                categoryField: config.xField,
                renderer: am5xy.AxisRendererX.new(root, {}),
                tooltip: am5.Tooltip.new(root, {})
            })
        );

        xAxis.data.setAll(config.data);

        const yAxis = chart.yAxes.push(
            am5xy.ValueAxis.new(root, {
                renderer: am5xy.AxisRendererY.new(root, {})
            })
        );

        // Add series
        config.yFields.forEach((field, i) => {
            const series = chart.series.push(
                am5xy.LineSeries.new(root, {
                    name: config.seriesNames[i],
                    xAxis: xAxis,
                    yAxis: yAxis,
                    valueYField: field,
                    categoryXField: config.xField,
                    stroke: am5.color(config.colors[i]),
                    tooltip: am5.Tooltip.new(root, {
                        labelText: "{name}: {valueY}"
                    })
                })
            );

            series.data.setAll(config.data);
            series.bullets.push(function () {
                return am5.Bullet.new(root, {
                    sprite: am5.Circle.new(root, {
                        radius: 4,
                        fill: series.get("stroke"),
                        stroke: root.interfaceColors.get("background"),
                        strokeWidth: 2
                    })
                });
            });

            legend.data.push(series);
        });

        // Add cursor
        chart.set("cursor", am5xy.XYCursor.new(root, {
            behavior: "zoomX"
        }));

        // Add title
        if (config.title) {
            chart.children.unshift(am5.Label.new(root, {
                text: config.title,
                fontSize: 14,
                fontWeight: "bold",
                textAlign: "center",
                x: am5.p50,
                centerX: am5.p50,
                paddingTop: 0,
                paddingBottom: 10
            }));
        }
    });
};

const createBarChart = function createBarChart(containerId, config) {
    am5.ready(function () {
        let root = am5.Root.new(containerId);

        root.setThemes([
            am5themes_Animated.new(root)
        ]);

        let chart = root.container.children.push(
            am5xy.XYChart.new(root, {
                panX: false,
                panY: false,
                wheelX: "none",
                wheelY: "none",
                layout: root.verticalLayout
            })
        );

        let xAxis = chart.xAxes.push(
            am5xy.CategoryAxis.new(root, {
                categoryField: config.categoryField,
                renderer: am5xy.AxisRendererX.new(root, {
                    minGridDistance: 30
                }),
                tooltip: am5.Tooltip.new(root, {})
            })
        );

        let yAxis = chart.yAxes.push(
            am5xy.ValueAxis.new(root, {
                renderer: am5xy.AxisRendererY.new(root, {})
            })
        );

        xAxis.data.setAll(config.data);

        let series = chart.series.push(
            am5xy.ColumnSeries.new(root, {
                name: config.title,
                xAxis: xAxis,
                yAxis: yAxis,
                valueYField: config.valueField,
                categoryXField: config.categoryField,
                fill: am5.color(config.color),
                stroke: am5.color(config.color)
            })
        );

        series.columns.template.setAll({
            tooltipText: "{categoryX}: {valueY}",
            width: am5.percent(80),
            fillOpacity: 0.8
        });

        series.data.setAll(config.data);

        // Add title
        chart.children.unshift(am5.Label.new(root, {
            text: config.title,
            fontSize: 20,
            fontWeight: "500",
            textAlign: "center",
            x: am5.percent(50),
            centerX: am5.percent(50)
        }));
    });
};
const createPieChart = function (containerId, config) {
    am5.ready(function () {
        const root = am5.Root.new(containerId);
        root.setThemes([am5themes_Animated.new(root)]);

        const chart = root.container.children.push(
            am5percent.PieChart.new(root, {
                layout: root.verticalLayout
            })
        );

        const series = chart.series.push(
            am5percent.PieSeries.new(root, {
                valueField: config.valueField,
                categoryField: config.categoryField
            })
        );

        series.data.setAll(config.data);
        series.slices.template.setAll({
            tooltipText: "{category}: {value}",
        });

        if (config.colors) {
            series.slices.template.adapters.add("fill", (fill, target) => {
                return am5.color(config.colors[target.dataItem.index % config.colors.length]);
            });
        }

        chart.children.unshift(am5.Label.new(root, {
            text: config.title,
            fontSize: 16,
            fontWeight: "bold",
            x: am5.percent(50),
            centerX: am5.percent(50),
            paddingBottom: 10
        }));
    });
};

const createColumnChart = function (containerId, config) {
    am5.ready(function () {
        const root = am5.Root.new(containerId);
        root.setThemes([am5themes_Animated.new(root)]);

        const chart = root.container.children.push(
            am5xy.XYChart.new(root, {
                layout: root.verticalLayout
            })
        );

        const xAxis = chart.xAxes.push(
            am5xy.CategoryAxis.new(root, {
                categoryField: config.xField,
                renderer: am5xy.AxisRendererX.new(root, {}),
                tooltip: am5.Tooltip.new(root, {})
            })
        );
        xAxis.data.setAll(config.data);

        const yAxis = chart.yAxes.push(
            am5xy.ValueAxis.new(root, {
                renderer: am5xy.AxisRendererY.new(root, {})
            })
        );

        const legend = chart.children.push(am5.Legend.new(root, {
            centerX: am5.p50,
            x: am5.p50
        }));

        config.yFields.forEach((field, i) => {
            const series = chart.series.push(
                am5xy.ColumnSeries.new(root, {
                    name: config.seriesNames[i],
                    xAxis: xAxis,
                    yAxis: yAxis,
                    valueYField: field,
                    categoryXField: config.xField,
                    fill: am5.color(config.colors[i]),
                    stroke: am5.color(config.colors[i])
                })
            );
            series.data.setAll(config.data);
            series.columns.template.setAll({
                tooltipText: "{name}: {valueY}",
                width: am5.percent(80),
                fillOpacity: 0.8
            });
            legend.data.push(series);
        });

        chart.children.unshift(am5.Label.new(root, {
            text: config.title,
            fontSize: 16,
            fontWeight: "bold",
            x: am5.percent(50),
            centerX: am5.percent(50),
            paddingBottom: 10
        }));
    });
};
const createStackedBarChart = function (containerId, config) {
    am5.ready(function () {
        const root = am5.Root.new(containerId);
        root.setThemes([am5themes_Animated.new(root)]);

        const chart = root.container.children.push(
            am5xy.XYChart.new(root, {
                layout: root.verticalLayout
            })
        );

        const xAxis = chart.xAxes.push(
            am5xy.CategoryAxis.new(root, {
                categoryField: config.categoryField,
                renderer: am5xy.AxisRendererX.new(root, {}),
                tooltip: am5.Tooltip.new(root, {})
            })
        );
        xAxis.data.setAll(config.data);

        const yAxis = chart.yAxes.push(
            am5xy.ValueAxis.new(root, {
                renderer: am5xy.AxisRendererY.new(root, {})
            })
        );

        const legend = chart.children.push(am5.Legend.new(root, {
            centerX: am5.p50,
            x: am5.p50
        }));

        config.valueFields.forEach((field, i) => {
            const series = chart.series.push(
                am5xy.ColumnSeries.new(root, {
                    name: config.seriesNames[i],
                    stacked: true,
                    xAxis: xAxis,
                    yAxis: yAxis,
                    valueYField: field,
                    categoryXField: config.categoryField,
                    fill: am5.color(config.colors[i]),
                    stroke: am5.color(config.colors[i])
                })
            );
            series.data.setAll(config.data);
            series.columns.template.setAll({
                tooltipText: "{name}: {valueY}",
                width: am5.percent(80)
            });
            legend.data.push(series);
        });

        chart.children.unshift(am5.Label.new(root, {
            text: config.title,
            fontSize: 16,
            fontWeight: "bold",
            x: am5.percent(50),
            centerX: am5.percent(50),
            paddingBottom: 10
        }));
    });
};
const createRadarChart = function (containerId, config) {
    am5.ready(function () {
        const root = am5.Root.new(containerId);
        root.setThemes([am5themes_Animated.new(root)]);

        const chart = root.container.children.push(
            am5radar.RadarChart.new(root, {
                panX: false,
                panY: false,
                wheelX: "none",
                wheelY: "none"
            })
        );

        const xAxis = chart.xAxes.push(
            am5.CategoryAxis.new(root, {
                categoryField: config.categoryField,
                renderer: am5radar.AxisRendererCircular.new(root, {})
            })
        );
        xAxis.data.setAll(config.data);

        const yAxis = chart.yAxes.push(
            am5radar.ValueAxis.new(root, {
                renderer: am5radar.AxisRendererRadial.new(root, {})
            })
        );

        const legend = chart.children.push(am5.Legend.new(root, {
            centerX: am5.p50,
            x: am5.p50
        }));

        config.valueFields.forEach((field, i) => {
            const series = chart.series.push(
                am5radar.RadarLineSeries.new(root, {
                    name: config.seriesNames[i],
                    xAxis: xAxis,
                    yAxis: yAxis,
                    valueYField: field,
                    categoryXField: config.categoryField,
                    stroke: am5.color(config.colors[i]),
                    fill: am5.color(config.colors[i]),
                    tooltip: am5.Tooltip.new(root, {
                        labelText: "{name}: {valueY}"
                    })
                })
            );

            series.strokes.template.setAll({ strokeWidth: 2 });
            series.data.setAll(config.data);
            legend.data.push(series);
        });

        chart.children.unshift(am5.Label.new(root, {
            text: config.title,
            fontSize: 16,
            fontWeight: "bold",
            x: am5.percent(50),
            centerX: am5.percent(50),
            paddingBottom: 10
        }));
    });
};

const createGroupedColumnChart = function (containerId, config) {
    am5.ready(function () {
        const root = am5.Root.new(containerId);
        root.setThemes([am5themes_Animated.new(root)]);

        const chart = root.container.children.push(
            am5xy.XYChart.new(root, {
                panX: false,
                panY: false,
                layout: root.verticalLayout
            })
        );

        const xAxis = chart.xAxes.push(
            am5xy.CategoryAxis.new(root, {
                categoryField: config.categoryField,
                renderer: am5xy.AxisRendererX.new(root, {}),
                tooltip: am5.Tooltip.new(root, {})
            })
        );

        xAxis.data.setAll(config.data);

        const yAxis = chart.yAxes.push(
            am5xy.ValueAxis.new(root, {
                renderer: am5xy.AxisRendererY.new(root, {})
            })
        );

        config.valueFields.forEach((field, i) => {
            const series = chart.series.push(
                am5xy.ColumnSeries.new(root, {
                    name: config.seriesNames[i],
                    xAxis: xAxis,
                    yAxis: yAxis,
                    valueYField: field,
                    categoryXField: config.categoryField,
                    fill: am5.color(config.colors[i]),
                    stroke: am5.color(config.colors[i])
                })
            );

            series.columns.template.setAll({
                tooltipText: "{name}: {valueY}",
                width: am5.percent(40),
                fillOpacity: 0.9
            });

            series.data.setAll(config.data);
        });

        // Add legend
        chart.children.push(
            am5.Legend.new(root, {
                centerX: am5.p50,
                x: am5.p50
            })
        );

        // Add title
        if (config.title) {
            chart.children.unshift(am5.Label.new(root, {
                text: config.title,
                fontSize: 18,
                fontWeight: "500",
                textAlign: "center",
                x: am5.percent(50),
                centerX: am5.percent(50)
            }));
        }
    });
};

const createDonutChart = function(containerId, config) {
    am5.ready(function () {
        const root = am5.Root.new(containerId);
        root.setThemes([am5themes_Animated.new(root)]);

        const chart = root.container.children.push(
            am5percent.PieChart.new(root, {
                layout: root.verticalLayout,
                innerRadius: am5.percent(50)
            })
        );

        const series = chart.series.push(
            am5percent.PieSeries.new(root, {
                valueField: config.valueField,
                categoryField: config.categoryField,
                alignLabels: false
            })
        );

        series.data.setAll(config.data);

        series.slices.template.setAll({
            stroke: am5.color(0xffffff),
            strokeWidth: 2
        });

        series.labels.template.setAll({
            text: "{category}: {valuePercentTotal.formatNumber('#.#')}%",
            radius: 10,
            inside: true,
            fill: am5.color(0xffffff)
        });

        chart.children.unshift(am5.Label.new(root, {
            text: config.title,
            fontSize: 18,
            fontWeight: "500",
            textAlign: "center",
            x: am5.percent(50),
            centerX: am5.percent(50)
        }));
    });
};




// Similar functions for createBarChart, createColumnChart, createPieChart, 
// createStackedBarChart, and createRadarChart would be implemented here
// (Implementation omitted for brevity but follows similar pattern)

document.addEventListener("DOMContentLoaded", function () {
    const component = document.querySelector("[data-component='honda-dealer-dashboard']");

    const dashboardData = {
        "partsOrders": {
            "monthlyTrend": [
                { "month": "Jan", "orders": 1250, "fulfilled": 1150 },
                { "month": "Feb", "orders": 1380, "fulfilled": 1270 },
                { "month": "Mar", "orders": 1520, "fulfilled": 1420 },
                { "month": "Apr", "orders": 1450, "fulfilled": 1350 },
                { "month": "May", "orders": 1680, "fulfilled": 1580 },
                { "month": "Jun", "orders": 1720, "fulfilled": 1620 }
            ],
            "topParts": [
                { "part": "Oil Filter", "orders": 420 },
                { "part": "Air Filter", "orders": 380 },
                { "part": "Brake Pads", "orders": 350 },
                { "part": "Spark Plugs", "orders": 310 },
                { "part": "Battery", "orders": 290 }
            ],
            "fulfillmentRate": 92.5
        },
        "service": {
            "appointments": [
                { "day": "Mon", "scheduled": 45, "completed": 42 },
                { "day": "Tue", "scheduled": 52, "completed": 48 },
                { "day": "Wed", "scheduled": 48, "completed": 45 },
                { "day": "Thu", "scheduled": 55, "completed": 52 },
                { "day": "Fri", "scheduled": 60, "completed": 56 },
                { "day": "Sat", "scheduled": 35, "completed": 32 }
            ],
            "serviceTypes": [
                { "type": "Oil Change", "count": 120 },
                { "type": "Tire Rotation", "count": 85 },
                { "type": "Brake Service", "count": 65 },
                { "type": "Battery Check", "count": 45 },
                { "type": "Other", "count": 55 }
            ],
            "averageCompletionTime": 2.3
        },
        "feedback": {
            "csiScores": [
                { "month": "Jan", "score": 88 },
                { "month": "Feb", "score": 86 },
                { "month": "Mar", "score": 89 },
                { "month": "Apr", "score": 91 },
                { "month": "May", "score": 90 },
                { "month": "Jun", "score": 92 }
            ],
            "feedbackCategories": [
                { "category": "Service Quality", "positive": 85, "negative": 15 },
                { "category": "Timeliness", "positive": 78, "negative": 22 },
                { "category": "Communication", "positive": 82, "negative": 18 },
                { "category": "Facility", "positive": 91, "negative": 9 }
            ]
        },
        "dealerPerformance": {
            "salesTarget": [
                { "model": "Civic", "target": 45, "actual": 48 },
                { "model": "Accord", "target": 35, "actual": 32 },
                { "model": "CR-V", "target": 50, "actual": 55 },
                { "model": "Pilot", "target": 25, "actual": 22 },
                { "model": "HR-V", "target": 30, "actual": 28 }
            ],
            "customerRetention": 78.5
        },
        "dealerNetwork": {
            "totalDealers": 1120,
            "totalUsers": 11450,
            "activeUsers": 11000,
            "inactiveUsers": 450
        }

    };

    // Set KPI values
    document.getElementById("totalDealers").textContent = dashboardData.dealerNetwork.totalDealers;
    document.getElementById("totalUsers").textContent = dashboardData.dealerNetwork.totalUsers;
    document.getElementById("activeUsers").textContent = dashboardData.dealerNetwork.activeUsers;
    document.getElementById("inactiveUsers").textContent = dashboardData.dealerNetwork.inactiveUsers;

    if (component) {
        // Load AMCharts and initialize
        const scripts = [
            "https://cdn.amcharts.com/lib/5/index.js",
            "https://cdn.amcharts.com/lib/5/xy.js",
            "https://cdn.amcharts.com/lib/5/percent.js",
            "https://cdn.amcharts.com/lib/5/radar.js",     // <-- Add this line
            "https://cdn.amcharts.com/lib/5/themes/Animated.js"
        ];

        const loadScript = (src) => new Promise((resolve) => {
            const script = document.createElement('script');
            script.src = src;
            script.onload = resolve;
            document.head.appendChild(script);
        });

        Promise.all(scripts.map(loadScript)).then(() => {
            // Initialize charts with inline data
            initCharts(dashboardData);
        });
    }
});