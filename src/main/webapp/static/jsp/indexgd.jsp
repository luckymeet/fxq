<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>银行数据分析网络</title>
</head>
<body>

<script src="${pageContext.request.contextPath}/js/d3.v3.min.js"></script>
<script>

var graph ={
  "nodes": [
    {"x": 469, "y": 410},
    {"x": 493, "y": 364},
    {"x": 442, "y": 365},
    {"x": 467, "y": 314},
    {"x": 477, "y": 248},
    {"x": 425, "y": 207},
    {"x": 402, "y": 155},
    {"x": 369, "y": 196},
    {"x": 350, "y": 148},
    {"x": 539, "y": 222},
    {"x": 594, "y": 235},
    {"x": 582, "y": 185},
    {"x": 633, "y": 200}
  ],
  "links": [
    {"source":  0, "target":  1},
    {"source":  1, "target":  2},
    {"source":  2, "target":  0},
    {"source":  1, "target":  3},
    {"source":  3, "target":  2},
    {"source":  3, "target":  4},
    {"source":  4, "target":  5},
    {"source":  5, "target":  6},
    {"source":  5, "target":  7},
    {"source":  6, "target":  7},
    {"source":  6, "target":  8},
    {"source":  7, "target":  8},
    {"source":  9, "target":  4},
    {"source":  9, "target": 11},
    {"source":  9, "target": 10},
    {"source": 10, "target": 11},
    {"source": 11, "target": 12},
    {"source": 12, "target": 10}
  ]
}
  
var width = 960,
    height = 500;

var force = d3.layout.force() //初始力导向布局
    .size([width, height])   //指定作用域范围
    .charge(-400)            //指定相互之间的作用力
    .linkDistance(40)        //指定连线数组
    .on("tick", tick);       //tick事件，用于更新force布局

var drag = force.drag()
    .on("dragstart", dragstart);

var svg = d3.select("body").append("svg")
    .attr("width", width)
    .attr("height", height);

var link = svg.selectAll(".link"),
    node = svg.selectAll(".node");



  force
      .nodes(graph.nodes)
      .links(graph.links)
      .start();

  link = link.data(graph.links)
    .enter().append("line")
      .attr("class", "link");

  node = node.data(graph.nodes)
    .enter().append("circle")
      .attr("class", "node")
      .attr("r", 12)
      .on("dblclick", dblclick)
      .call(drag);


function tick() {
  link.attr("x1", function(d) { return d.source.x; })
      .attr("y1", function(d) { return d.source.y; })
      .attr("x2", function(d) { return d.target.x; })
      .attr("y2", function(d) { return d.target.y; });

  node.attr("cx", function(d) { return d.x; })
      .attr("cy", function(d) { return d.y; });
}

function dblclick(d) {
  d3.select(this).classed("fixed", d.fixed = false);
}

function dragstart(d) {
  d3.select(this).classed("fixed", d.fixed = true);
}

</script>
</body>
</html>