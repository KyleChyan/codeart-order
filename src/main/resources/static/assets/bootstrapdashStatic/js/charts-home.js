/*global $, document, Chart, LINECHART, data, options, window*/
$(document).ready(function () {


    'use strict';

    // ------------------------------------------------------- //
    // Line Chart
    // ------------------------------------------------------ //
    var legendState = true;
    if ($(window).outerWidth() < 576) {
        legendState = false;
    }

    // 从隐藏的HTML元素中获取数据
    var daySalesRevenue = JSON.parse(document.getElementById('daySalesRevenueData').textContent);
    var daySalesCount = JSON.parse(document.getElementById('daySalesCountData').textContent);
    var monthSalesRevenue = JSON.parse(document.getElementById('monthSalesRevenueData').textContent);


    // 获取当前月份和当前日期
    var now = new Date();
    var currentMonth = now.getMonth();
    var currentDate = now.getDate();

    // 生成日期标签
    var dayLabels = [];
    for (var i = 1; i <= currentDate; i++) {
        dayLabels.push(i.toString());
    }

    // 生成月份标签
    var monthLabels = [];
    var monthTamplate = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
    for (var j = 0; j <= currentMonth; j++) {
        monthLabels.push(monthTamplate[j]);
    }

    // 模拟从后端获取的数据
    // 这里假设数据已经从后端获取，并且格式为 { labels: [...], salesData: [...] }
    var dayRevenue = {
        daySalesRevenue: daySalesRevenue.slice(0, currentDate)  // 只取当前日期对应的销售数据
    };
    var dayCount = {
        daySalesCount: daySalesCount.slice(0, currentDate)  // 只取当前日期对应的销售数据
    };
    var monthRevenue = {
        monthSalesRevenue: monthSalesRevenue.slice(0, currentDate)  // 只取当前日期对应的销售数据
    };



    var LINECHART = $('#lineCahrt');
    // 创建图表
    createLineChart(dayLabels, dayRevenue.daySalesRevenue,dayCount.daySalesCount);

    function createLineChart(labels, salesRevenue,salesCount) {
        var myLineChart = new Chart(LINECHART, {
            type: 'line',
            options: {
                scales: {
                    xAxes: [{
                        display: true,
                        gridLines: {
                            display: false
                        },
                        ticks: {
                            autoSkip: true,
                            maxTicksLimit: 10 // 控制最大标签数
                        }
                    }],
                    yAxes: [{
                        id: 'revenue',
                        position: 'left',
                        display: true,
                        gridLines: {
                            display: false
                        }
                    }, {
                        id: 'count',
                        position: 'right',
                        display: true,
                        gridLines: {
                            display: false
                        }
                    }]
                },
                legend: {
                    display: legendState
                },
                tooltips: {
                    mode: 'index', // 设置模式为索引，这样在悬停在任何一个数据点上时都会显示该索引对应的数据点的详细信息
                    intersect: false // 不交叉显示，这样在悬停在数据点时只显示该点的详细信息，而不会同时显示其他数据点的信息
                }

            },
            data: {
                labels: labels,
                datasets: [
                    {
                        label: "日销售额",
                        yAxisID: 'revenue', // 关联到名为 'revenue' 的 y 轴
                        fill: true,
                        lineTension: 0,
                        backgroundColor: "transparent",
                        borderColor: '#f15765',
                        pointBorderColor: '#da4c59',
                        pointHoverBackgroundColor: '#da4c59',
                        borderCapStyle: 'butt',
                        borderDash: [],
                        borderDashOffset: 0.0,
                        borderJoinStyle: 'miter',
                        borderWidth: 1,
                        pointBackgroundColor: "#fff",
                        pointBorderWidth: 1,
                        pointHoverRadius: 5,
                        pointHoverBorderColor: "#fff",
                        pointHoverBorderWidth: 2,
                        pointRadius: 1,
                        pointHitRadius: 0,
                        data: salesRevenue,
                        spanGaps: false
                    },
                    {
                        label: "日订单量",
                        yAxisID: 'count', // 关联到名为 'count' 的 y 轴
                        fill: true,
                        lineTension: 0,
                        backgroundColor: "transparent",
                        borderColor: "#54e69d",
                        pointHoverBackgroundColor: "#44c384",
                        borderCapStyle: 'butt',
                        borderDash: [],
                        borderDashOffset: 0.0,
                        borderJoinStyle: 'miter',
                        borderWidth: 1,
                        pointBorderColor: "#44c384",
                        pointBackgroundColor: "#fff",
                        pointBorderWidth: 1,
                        pointHoverRadius: 5,
                        pointHoverBorderColor: "#fff",
                        pointHoverBorderWidth: 2,
                        pointRadius: 1,
                        pointHitRadius: 10,
                        data: salesCount,
                        spanGaps: false
                    }
                ]
            }
        });
    }





    // ------------------------------------------------------- //
    // Line Chart 1
    // ------------------------------------------------------ //
    var LINECHART1 = $('#lineChart1');
    var myLineChart = new Chart(LINECHART1, {
        type: 'line',
        options: {
            scales: {
                xAxes: [{
                    display: true,
                    gridLines: {
                        display: false
                    }
                }],
                yAxes: [{
                    ticks: {
                        max: 40,
                        min: 0,
                        stepSize: 0.5
                    },
                    display: false,
                    gridLines: {
                        display: false
                    }
                }]
            },
            legend: {
                display: false
            }
        },
        data: {
            labels: ["A", "B", "C", "D", "E", "F", "G"],
            datasets: [
                {
                    label: "Total Overdue",
                    fill: true,
                    lineTension: 0,
                    backgroundColor: "transparent",
                    borderColor: '#6ccef0',
                    pointBorderColor: '#59c2e6',
                    pointHoverBackgroundColor: '#59c2e6',
                    borderCapStyle: 'butt',
                    borderDash: [],
                    borderDashOffset: 0.0,
                    borderJoinStyle: 'miter',
                    borderWidth: 3,
                    pointBackgroundColor: "#59c2e6",
                    pointBorderWidth: 0,
                    pointHoverRadius: 4,
                    pointHoverBorderColor: "#fff",
                    pointHoverBorderWidth: 0,
                    pointRadius: 4,
                    pointHitRadius: 0,
                    data: [20, 28, 30, 22, 24, 10, 7],
                    spanGaps: false
                }
            ]
        }
    });



    // ------------------------------------------------------- //
    // Pie Chart
    // ------------------------------------------------------ //
    var PIECHART = $('#pieChart');
    var myPieChart = new Chart(PIECHART, {
        type: 'doughnut',
        options: {
            cutoutPercentage: 80,
            legend: {
                display: false
            }
        },
        data: {
            labels: [
                "First",
                "Second",
                "Third",
                "Fourth"
            ],
            datasets: [
                {
                    data: [300, 50, 100, 60],
                    borderWidth: [0, 0, 0, 0],
                    backgroundColor: [
                        '#44b2d7',
                        "#59c2e6",
                        "#71d1f2",
                        "#96e5ff"
                    ],
                    hoverBackgroundColor: [
                        '#44b2d7',
                        "#59c2e6",
                        "#71d1f2",
                        "#96e5ff"
                    ]
                }]
        }
    });


    // ------------------------------------------------------- //
    // Bar Chart
    // ------------------------------------------------------ //
    var BARCHARTHOME = $('#barChartHome');
    createBarChart(monthLabels,monthRevenue.monthSalesRevenue);
    function createBarChart(labels, salesRevenue) {

        var colors = labels.map((label, index) => {
            return index === labels.length - 1 ? 'rgb(255, 0, 0)' : 'rgb(121, 106, 238)';
        });

        var barChartHome = new Chart(BARCHARTHOME, {
            type: 'bar',
            options:
                {
                    scales:
                        {
                            xAxes: [{
                                display: false
                            }],
                            yAxes: [{
                                display: true
                            }],
                        },
                    legend: {
                        display: false
                    }
                },
            data: {
                labels: labels,
                datasets: [{
                    label: "Data Set 1",
                    backgroundColor: colors,
                    borderColor: colors,
                    borderWidth: 1,
                    data: salesRevenue
                }]
            }

        });
        console.log('Colors:', colors);

    }

});
