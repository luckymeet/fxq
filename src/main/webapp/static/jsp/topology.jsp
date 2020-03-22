<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>银行数据分析网络</title>
<link rel="stylesheet" type="text/css" href="${path}/css/bootstrap.css">
<link href="${path}/css/component.css" rel="stylesheet">
<link href="${path}/js/bstable/css/bootstrap.min.css" rel="stylesheet"
	type="text/css">
<link href="${path}/js/bstable/css/bootstrap-table.css" rel="stylesheet"
	type="text/css">
<link href="${path}/css/zTreeStyle/zTreeStyle.css" rel="stylesheet"
	type="text/css" />
<link href="${path}/css/table.css" rel="stylesheet" type="text/css" />
<script src="${path}/js/d3.v3.min.js"></script>
<script src="${path}/js/jquery-2.2.0.min.js"></script>
<style type="text/css">
.link {
	stroke: black;
	stroke-linejoin: bevel;
	stroke-width: 2;
}

.link_zjhl {
	stroke: #86f307;
	stroke-linejoin: bevel;
	stroke-width: 5;
}

.link_error {
	stroke: red;
	stroke-linejoin: bevel;
	stroke-width: 5;
}

.link_scuess {
	stroke: blue;
	stroke-linejoin: bevel;
}

.link_right {
	stroke: green;
	stroke-linejoin: bevel;
}

.link_5 {
	stroke: #f1aa0a;
	stroke-linejoin: bevel;
	stroke-width: 2;
}

.link_4 {
	stroke: #6cca06;
	stroke-linejoin: bevel;
	stroke-width: 2;
}

.link_3 {
	stroke: #04a268;
	stroke-linejoin: bevel;
	stroke-width: 2;
}

.link_2 {
	stroke: #049ba2;
	stroke-linejoin: bevel;
	stroke-width: 2;
}

.link_1 {
	stroke: #0575b7;
	stroke-linejoin: bevel;
	stroke-width: 2;
}

.nodetext {
	font: 16px sans-serif;
	-webkit-user-select: none;
	-moze-user-select: none;
	stroke-linejoin: bevel;
	fill: #fff;
}

.nodetext-click {
	font: 16px sans-serif;
	-webkit-user-select: none;
	-moze-user-select: none;
	stroke-linejoin: bevel;
	fill: #ff9800;
}

#container {
	width: 70%;
	border: 1px solid gray;
	border-radius: 5px;
	position: relative;
	float: left;
	background-color: #022a50;
	overflow-y: auto;
	overflow-x: auto;
}

.leftdiv {
	width: 14%;
	height: 700px;
	float: left;
}

.rightdiv {
	width: 15%;
	height: 700px;
	float: right;
}

#divInfo-node {
	position: absolute;
	background-color: aquamarine;
	padding: 20px;
}

#divInfo-link {
	position: absolute;
	background-color: cornflowerblue;
	padding: 20px;
}

.nodeinfo {
	width: 100%;
	height: 20%;
	padding: 10px;
	background-color: #eeeeee;
}

.linkinfo {
	background-color: #eeeeee;
	padding: 10px;
	width: 100%;
	height: 40%;
	margin-top: 10px;
}

.zzstart {
	width: 80%;
	height: 50px;
	background-color: #FF5722;
	color: #f8f8f5;
	font-weight: 800;
	border: #FF5722;
	font-size: x-large;
	margin-left: 6%;
	margin-bottom: 4px;
}

.zzstart:hover {
	background-color: #FFC107;
}

.info {
	font-size: x-large;
	font-weight: 600;
}

.info-moeny {
	font-size: xx-large;
	font-weight: 600;
	color: darkred;
}

.slt {
	background-color: #d3e4f2;
	width: 150px;
	height: 150px;
	z-index: 99999;
	float: left;
}
</style>
</head>
<body>
	<span id="colink" colink=${fn:length(linklist) } style="display: none"></span>
	<span id="conode" conode=${fn:length(nodelist) } style="display: none"></span>
	<span id="maxmon" maxmon=${maxmoeny } style="display: none"></span>
	<span id="minmon" minmon=${minmoeny } style="display: none"></span>
	<c:set var="flag" value="0" />
	<c:forEach items="${linklist }" var="link">
		<span id="${flag}" sourse="${link.name1}" amount="${link.money}"
			target="${link.name2}" style="display: none"></span>
		<c:set var="flag" value="${flag+1}" />
	</c:forEach>
	<c:set var="flag" value="0" />
	<c:forEach items="${nodelist }" var="node">
		<span id="%${flag}" sh="${flag}" name="${node.name}"
			img="${node.imgName}" style="display: none"></span>
		<c:set var="flag" value="${flag+1}" />
	</c:forEach>
	<div>
		<div class="leftdiv">
			<h3 style="margin-top: 17%; margin-left: 28%;">人物信息</h3>
			<div class="nodeinfo">
				<span id="infoid-n" class="info"></span>
			</div>
			<h3 style="margin-left: 28%;">流水信息</h3>
			<div class="linkinfo">
				<span id="infon-l" class="info"></span><br />
				<h4>
					交易金额: <span id="infom-l" class="info-moeny"></span>元
				</h4>
			</div>
			<div id="thumbMap" class="slt" style="display: none"></div>

		</div>
		<div>
			<div id='container' class=""></div>
		</div>
		<!-- 筛选模态框 -->
		<div class="md-modal md-effect-13" id="modal-13">
			<div class="md-content">
				<h3>数据网络筛选</h3>
				<div>
					<p>筛选出你想画出的数据网络：</p>
					<span>总交易金额：</span><input type="text" id="amount"
						class="form-control" /><br /> <span>总交易数：</span><input
						type="text" id="frequency" class="form-control" /><br /> <span>日交易金额：</span><input
						type="text" id="everyDayAmount" class="form-control" /><br /> <span>日交易数：</span><input
						type="text" id="everyDayFrequency" class="form-control" /><br />
					<span>开始时间：</span><input type="text" id="starttime"
						placeholder="格式：yyyy-MM-dd HH:mm:ss" class="form-control" /><br />
					<span>结束时间：</span><input type="text" id="endtime"
						placeholder="格式：yyyy-MM-dd HH:mm:ss" class="form-control" /><br />
					<button class="md-close btn-sm btn-primary">取消</button>
					<button class="md-close btn-sm btn-danger" onclick="filter()">开始筛选</button>
				</div>
			</div>
		</div>
		<div class="md-overlay">
			<!-- the overlay element -->
		</div>
	</div>

	<div class="l_left news_right" style="width: 100%;margin-bottom:20%">
		<h6>案件管理</h6>
		<div class="notice_check"></div>
		<div class="clear"></div>
		<div class="notice_check">
			<div class="notice_nav r_right paddingBotme">
				<P>
					<input id="zzsx" type="button" value="数据筛选"
						class="zzstart md-trigger" data-modal="modal-13" />
				</P>
			</div>
		</div>
		<div class="clear"></div>

		<ul class="news_table department_table">
			<li>
				<table id="table" class="table_style" style="margin: 0 auto"></table>
			</li>
		</ul>

	</div>
	<div class="clear"></div>

	<!-- 筛选模态框end -->
	<script src="${path}/js/jquery/jQuery-2.2.0.min.js"></script>
	<script src="${path}/js/bstable/js/bootstrap.min.js"></script>
	<script src="${path}/js/bstable/js/bootstrap-table.js"></script>
	<script src="${path}/js/bstable/js/bootstrap-table-zh-CN.min.js"></script>
	<script type="text/javascript"
		src="${path}/js/ztree/jquery.ztree.core-3.5.js"></script>
	<script type="text/javascript"
		src="${path}/js/ztree/jquery.ztree.excheck-3.5.js"></script>
	<script type="text/javascript"
		src="${path}/js/ztree/jquery.ztree.exedit-3.5.js"></script>
	<script src="${path}/js/layer_v2.1/layer/layer.js"></script>
	<script src="${path}/js/localLight.js"></script>
	<script type="text/javascript">

    var total = document.documentElement.clientHeight;
    document.getElementById("container").style.height = total+"px";

    function submitsx(){

    }

    function filter(){
    	var amount = $("#amount").val();
    	var frequency = $("#frequency").val();
    	var everyDayAmount = $("#everyDayAmount").val();
    	var everyDayFrequency = $("#everyDayFrequency").val();
    	var starttime = $("#starttime").val();
    	var endtime = $("#endtime").val();
    	window.location.href = "filter?frequency=" + frequency + "&amount=" + amount
    			+ "&everyDayAmount=" + everyDayAmount + "&everyDayFrequency=" + everyDayFrequency
    			+ "&starttime=" + starttime + "&endtime=" + endtime;
    }
    $(function () {
        $('#table').bootstrapTable({
            method: "get",
            striped: true,
            singleSelect: false,
            url: "json/case.json",
            dataType: "json",
            pagination: true, //分页
            pageSize: 10,
            pageNumber: 1,
            search: false, //显示搜索框
            contentType: "application/x-www-form-urlencoded",
            queryParams:null,
            columns: [
                {
                    title: "环路",
                    field: 'id',
                    align: 'center',
                    width: 160,
                    valign: 'middle'
                }
            ]
        });
    });
    function LinkColorSet(Money) {
        if (Maxmoney - Minmoney == 0) {
            return 'link';
        } else {
            var Gap = (Money - Minmoney) / (Maxmoney - Minmoney);
            if (Gap >= 4 / 5) {
                return 'link link_5';
            } else if (Gap >= 3 / 5) {
                return 'link link_4';
            } else if (Gap >= 2 / 5) {
                return 'link link_3';
            } else if (Gap >= 1 / 5) {
                return 'link link_2';
            } else {
                return 'link link_1';
            }
        }
    }

    //定义功能标记，已判别不同功能代码
    var funsign = 0;
    var gdsign = 0;
    function Topology(ele) {
        typeof (ele) == 'string' && (ele = document.getElementById(ele));
        var w = ele.clientWidth,
            h = ele.clientHeight,
            self = this;
        var zoom = d3.behavior.zoom().scaleExtent([-10,5]).on("zoom", function(){self.vis.attr("transform","translate(" + zoom.translate() + ")" +"scale(" + zoom.scale() + ")");});
        this.force = d3.layout.force().gravity(.05).distance(800).charge(-800).size([w, h]);
        this.nodes = this.force.nodes();
        this.links = this.force.links();
        this.clickFn = function () { };
        this.vis = d3.select(ele).append("svg:svg")
            .attr("width", w).attr("height", h).attr("pointer-events", "all").call(zoom).append("g");
        this.force.on("tick", function (x) {
            self.vis.selectAll("g.node")
                .attr("transform", function (d) { return "translate(" + d.x + "," + d.y + ")"; })
                //.call(drag);
            self.vis.selectAll("line.link")
                .attr("x1", function (d) { return d.source.x; })
                .attr("y1", function (d) { return d.source.y; })
                .attr("x2", function (d) { return d.target.x; })
                .attr("y2", function (d) { return d.target.y; });
        });

    }

    Topology.prototype.doZoom = function () {
    	d3.select(this).select('g').attr("transform", "translate(" + d3.event.translate + ")" + " scale(" + d3.event.scale + ")");
    }


    function dragstarted(d){
    	d3.event.sourceEvent.stopPropagation();
    	d3.select(this).classed("dragging",true);
    	d3.layout.force().gravity(.05).distance(200).charge(-800).size([document.getElementById("container").clientWidth, document.getElementById("container").clientHeight]).start();
    }
    function dragged(d){
    	d3.select(this).attr("cx",d.x = d3.event.x).attr("cy",d.y = d3.event.y);
    }
    function dragended(d){
    	d3.select(this).classed("dragging",false);
    }


    function interpolateZoom (translate, scale) {
        var self = this;
        return d3.transition().duration(350).tween("zoom", function () {
            var iTranslate = d3.interpolate(zoom.translate(), translate),
                iScale = d3.interpolate(zoom.scale(), scale);
            return function (t) {
                zoom
                    .scale(iScale(t))
                    .translate(iTranslate(t));
                zoomed();
            };
        });
    }

    function zoomClick() {
        var clicked = d3.event.target,
            direction = 1,
            factor = 0.2,
            target_zoom = 1,
            center = [width / 2, height / 2],
            extent = zoom.scaleExtent(),
            translate = zoom.translate(),
            translate0 = [],
            l = [],
            view = {x: translate[0], y: translate[1], k: zoom.scale()};

        d3.event.preventDefault();
        direction = (this.id === 'zoom_in') ? 1 : -1;
        target_zoom = zoom.scale() * (1 + factor * direction);

        if (target_zoom < extent[0] || target_zoom > extent[1]) { return false; }

        translate0 = [(center[0] - view.x) / view.k, (center[1] - view.y) / view.k];
        view.k = target_zoom;
        l = [translate0[0] * view.k + view.x, translate0[1] * view.k + view.y];

        view.x += center[0] - l[0];
        view.y += center[1] - l[1];

        interpolateZoom([view.x, view.y], view.k);
    }

   // d3.selectAll('button').on('click', zoomClick);

    //增加节点
    Topology.prototype.addNode = function (node) {
        this.nodes.push(node);
    }

    Topology.prototype.addNodes = function (nodes) {
        if (Object.prototype.toString.call(nodes) == '[object Array]') {
            var self = this;
            nodes.forEach(function (node) {
                self.addNode(node);
            });

        }
    }

    //增加连线
    Topology.prototype.addLink = function (source, target,amoun) {
        this.links.push({ source: this.findNode(source), target: this.findNode(target),amount:amoun });
    }

    //增加多个连线
    Topology.prototype.addLinks = function (links) {
        if (Object.prototype.toString.call(links) == '[object Array]') {
            var self = this;
            links.forEach(function (link) {
                self.addLink(link['source'], link['target'],link['amount']);
            });
        }
    }


    //删除节点
    Topology.prototype.removeNode = function (id) {
        var i = 0,
            n = this.findNode(id),
            links = this.links;
        while (i < links.length) {
            links[i]['source'] == n || links[i]['target'] == n ? links.splice(i, 1) : ++i;
        }
        this.nodes.splice(this.findNodeIndex(id), 1);
    }

    //删除节点下的子节点，同时清除link信息
    Topology.prototype.removeChildNodes = function (id) {
        var node = this.findNode(id),
            nodes = this.nodes;
        links = this.links,
            self = this;

        var linksToDelete = [],
            childNodes = [];

        links.forEach(function (link, index) {
            link['source'] == node
                && linksToDelete.push(index)
                && childNodes.push(link['target']);
        });

        linksToDelete.reverse().forEach(function (index) {
            links.splice(index, 1);
        });

        var remove = function (node) {
            var length = links.length;
            for (var i = length - 1; i >= 0; i--) {
                if (links[i]['source'] == node) {
                    var target = links[i]['target'];
                    links.splice(i, 1);
                    nodes.splice(self.findNodeIndex(node.id), 1);
                    remove(target);

                }
            }
        }

        childNodes.forEach(function (node) {
            remove(node);
        });

        //清除没有连线的节点
        for (var i = nodes.length - 1; i >= 0; i--) {
            var haveFoundNode = false;
            for (var j = 0, l = links.length; j < l; j++) {
                (links[j]['source'] == nodes[i] || links[j]['target'] == nodes[i]) && (haveFoundNode = true)
            }
            !haveFoundNode && nodes.splice(i, 1);
        }
    }



    //查找节点
    Topology.prototype.findNode = function (id) {
        var nodes = this.nodes;
        for (var i in nodes) {
            if (nodes[i]['id'] == id) return nodes[i];
        }
        return null;
    }


    //查找节点所在索引号
    Topology.prototype.findNodeIndex = function (id) {
        var nodes = this.nodes;
        for (var i in nodes) {
            if (nodes[i]['id'] == id) return i;
        }
        return -1;
    }

    //节点点击事件
    Topology.prototype.setNodeClickFn = function (callback) {
        this.clickFn = callback;
    }




    //更新拓扑图状态信息
    Topology.prototype.update = function () {
        //画箭头
    var marker=
    this.vis.append("marker")
    //.attr("id", function(d) { return d; })
    .attr("id", "resolved")
    //.attr("markerUnits","strokeWidth")//设置为strokeWidth箭头会随着线的粗细发生变化
    .attr("markerUnits","userSpaceOnUse")
    .attr("viewBox", "0 -5 10 10")//坐标系的区域
    .attr("refX",32)//箭头坐标
    .attr("refY", -1)
    .attr("markerWidth", 12)//标识的大小
    .attr("markerHeight", 12)
    .attr("orient", "auto")//绘制方向，可设定为：auto（自动确认方向）和 角度值
    .attr("stroke-width",2)//箭头宽度
    .append("path")
    .attr("d", "M0,-5L10,0L0,5")//箭头的路径
    .attr('fill','#FF9800');//箭头颜色

        var link = this.vis.selectAll("line.link")
            .data(this.links, function (d) {
               if (funsign == 1) {
                    d.source.status ? true : d.target.status = 0;
                }
                return d.source.id + "-" + d.target.id;
            })
            .attr("class", function (d) {
            	if(funsign == 2){
            		var s=document.getElementsByName(d.source.id);
            		var t=document.getElementsByName(d.target.id);
            		return  queryhl(s[0].getAttribute("sh"),t[0].getAttribute("sh")) ? 'link link_zjhl' : LinkColorSet(d.amount);
            	}
                return d['source']['status'] ? LinkColorSet(d.amount) : 'link link_error';
            });

        link.enter().insert("svg:line", "g.node")
            .attr("marker-end", "url(#resolved)")
            .attr("class", function (d) {
                return d['source']['status'] ? LinkColorSet(d.amount) : 'link link_error';
            })
           .on('mouseover', function (d) {
                document.getElementById("infon-l").innerHTML ="流水方向: "+ d['source']['id'] + " --> " + d['target']['id'];
                document.getElementById("infom-l").innerHTML = d.amount;


            })
            .on('mouseout', function (d) {
               // document.getElementById("divInfo-link").style.visibility = "hidden";
            });


        link.exit().remove();

        var node = this.vis.selectAll("g.node")
            .data(this.nodes, function (d) {
            	if (gdsign == 1)
                {
                    d.fixed = true;
                }
            	return d.id;
            })

        var nodeEnter = node.enter().append("svg:g")
            .attr("class", "node")
            .call(this.force.drag().on("dragstart", dragstart));




        //增加图片，可以根据需要来修改
        var self = this;
        nodeEnter.append("svg:image")
            .attr("class", "circle")
            .attr("xlink:href", function (d) {
                return (d.img == null || d.img == "") ? "${path}/img/node/person.png" : "${pageContext.request.contextPath}//img/node/" + d.img;
            })
            .attr("x", "-32px")
            .attr("y", "-32px")
            .attr("width", "64px")
            .attr("height", "64px")
            .on('click', function (d) {
                self.clickFn(d);
            })
            .on('mouseover', function (d) {
                document.getElementById("infoid-n").innerHTML ="人物名称： "+ d.id;

            })
            .on('mouseout', function (d) {
                //document.getElementById("divInfo-node").style.visibility = "hidden";
            })
            .on("dblclick", dblclick);

        nodeEnter.append("svg:text")
            .attr("class", "nodetext")
            .attr("dx", 15)
            .attr("dy", -35)
            .text(function (d) { return d.id });


        node.exit().remove();

        this.force.start();
    }

    function dblclick(d) {
      d3.select(this).classed("fixed", d.fixed = false);
    }

    function dragstart(d) {
  	  d3.select(this).classed("fixed", d.fixed = true);
  	  d3.event.sourceEvent.stopPropagation();
	  d3.select(this).classed("dragging",true);
	//d3.layout.force().gravity(.05).distance(200).charge(-800).size([document.getElementById("container").clientWidth, document.getElementById("container").clientHeight]).start();
  	}

    var topology = new Topology('container');
    var nodes = [];
    var childNodes = [];
    var links = [];
    var Maxmoney = document.getElementById("maxmon").getAttribute("maxmon");
    var Minmoney = document.getElementById("minmon").getAttribute("minmon");
    var childLinks = [];



    for(var i = 0;i<document.getElementById("colink").getAttribute("colink");i++){
        links.push({ source: document.getElementById(i.toString()).getAttribute("sourse"), amount: document.getElementById(i.toString()).getAttribute("amount"), target: document.getElementById(i.toString()).getAttribute("target") });
    }
    for(var i = 0;i<document.getElementById("conode").getAttribute("conode");i++){
    	 var content = document.getElementById('%'+i);
    	 nodes.push({ id: content.getAttribute("name"), img: content.getAttribute("img"), type: 'switch', status: 1 ,hlstatus:1,expand:false});
    }

    topology.addNodes(nodes);
    topology.addLinks(links);
    //可展开节点的点击事件
    topology.setNodeClickFn(function (node) {
       /* if (!node['_expanded']) {
            expandNode(node.id);
            node['_expanded'] = true;
        } else {
            collapseNode(node.id);
            node['_expanded'] = false;
        }*/
        if(funsign == 1){
           node.status ? node.status = 0 : node.status = 1;
           topology.update();
        }
    });
    topology.update();


    function expandNode(id) {
        topology.addNodes(childNodes);
        topology.addLinks(childLinks);
        topology.update();
    }

    function collapseNode(id) {
        topology.removeChildNodes(id);
        topology.update();
    }

    function gozz() {
        //设置标记为1，设置更新后为追踪模式
        if (funsign != 1) {
            funsign = 1;
            document.getElementById("zz").value = "关闭追踪模式";
            document.getElementById("zz").style.backgroundColor = "#4CAF50";
            alert("追踪模式开启！鼠标单击目标人物，系统将追踪标记资金流向");
            topology.update();
        } else {
            funsign = 0;
            document.getElementById("zz").value = "开启追踪模式";
            document.getElementById("zz").style.backgroundColor = "#FF5722";
            alert("追踪模式关闭！");
        }
    }

    function hljc(Linklength,Nodelength){//资金回流算法
    	funsign = 2;
    	var DFSarr=new Array();  //先声明一维
    	for(var i=0;i<Nodelength;i++){   //一维长度为2
    		 DFSarr[i]=new Array();  //再声明二维
    	   for(var j=0;j<Nodelength;j++){   //二维长度为3
    		 DFSarr[i][j]= 0;   // 赋值，每个数组元素的值为i+j
    	   }
    	 }

    	var soursesh;
    	//alert(soursesh);
    	var targetsh;
    	for(var i =0;i<Linklength;i++){//生成基于边集合的有向DFS树
    		soursesh = document.getElementsByName(document.getElementById(i).getAttribute("sourse"));
    		targetsh = document.getElementsByName(document.getElementById(i).getAttribute("target"));
    		DFSarr[soursesh[0].getAttribute("sh")][targetsh[0].getAttribute("sh")] = 1;
    	}
    	 DFS(DFSarr,Nodelength,0);
    	 topology.update();
    }
	var trace =  new Array();
	var DFSresult = new Array();
	var hasCycle=false;
    function DFS(dfsarr,POINT_NUM,v){
		var j = 0;
	       if((j=trace.indexOf(v))!=-1)
	       {
	                hasCycle=true;
	                var result = new Array();
	                while(j<trace.length)
	                {
	                	result.push(trace[j]);
	                    j++;
	                }
	                DFSresult.push(result);
	                return;
	        }
	        trace.push(v);

	        for(var i=0;i<POINT_NUM;i++)
	        {
	            if(dfsarr[v][i]==1){
	            	DFS(dfsarr,POINT_NUM,i);
	            }
	        }
	        trace.pop();
    }
    function queryhl(s,t){
    	for(var i=0;i<DFSresult.length;i++){
    		var pro = new Array();
    		pro = DFSresult[i];
    		pro.push(pro[0]);
    		for(var j=0;j<pro.length-1;j++){
    			if(s == pro[j]){
    				if(t == pro[j+1]){
    					return true;
    				}
    			}
    		}
    	}
    	return false;
    }

    function gds() {
        if (gdsign == 0)
        {
        	document.getElementById("gd").value = "受力活动";
            gdsign = 1;
            topology.update();

        }
        if (gdsign == 1)
        {
        	document.getElementById("gd").value = "固定静止";
            gdsign = 0;
            topology.update();
        }
    }

    function drawThumb(){

    	//每次绘制前删除之前的图形（这是一种简单有效的动画理论，但是比较消耗资源，之后会介绍如何节省资源完成需求）
    	  d3.select("#thumbSvg").remove();

    	  var thumbSvg = d3.select("#thumbMap").append("svg")
    	            .attr("width",150)
    	            .attr("height",150)
    	            .attr("id","thumbSvg");
    	    var thumbG = thumbSvg.append("g")
    	    .attr("id","thumbGroup");
    	    var thumbLink = thumbG.selectAll(".tlink")
    	    .data(links)
    	    .enter()
    	    .append("line")
    	    .attr("class","link")
    	    .attr("stroke","#ccc")
    	    //缩略图绘制和主图的差异在这，不需要tick，只需要把节点的坐标直接赋予即可
    	    .attr("x1", function (d) {
    	                        return d.source.x/5;//这里的除5是缩略图和主图的比例，thumbWidth/forceWidth
    	                    })
    	                    .attr("y1", function (d) {
    	                        return d.source.y/5;
    	                    })
    	                    .attr("x2", function (d) {
    	                        return d.target.x/5;
    	                    })
    	                    .attr("y2", function (d) {
    	                        return d.target.y/5;
    	                    });
    	var thumbNode = thumbG.selectAll(".tnode")
    	.data(nodes)
    	.enter()
    	.append("circle")
    	.attr("class","tnode")
    	.attr("r",1.2)//图形尺寸都要缩小
    	.attr("fill",function(d){
    	  switch(d.type){
    	    case "switch": return "red";break;
    	    case"phone": return "blue";break;
    	    case "weixin": return "green";break;
    	  }
    	})
    	.attr("cx", function (d) {
    	                    return d.x/5
    	                })
    	                .attr("cy", function(d){
    	                  return d.y/5
    	                });
    	}

    function real(){
         window.location.href = "real";
    }
    function fz(){
    	 window.location.href = "index";
    }


</script>
	<script src="${pageContext.request.contextPath}/js/classie.js"></script>
	<script src="${pageContext.request.contextPath}/js/modalEffects.js"></script>
</body>
</html>