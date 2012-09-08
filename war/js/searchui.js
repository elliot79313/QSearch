
$(function () {
    $("div.menu > div").bind("click", function () {
        $("div.menu > div").removeClass("active");
        $(this).addClass("active");
        $("div.feed ").hide();
        searchlist[$(this).index()].open();
        if ($(this).index() == 0) current.mode = "user";
        else if ($(this).index() == 1) current.mode = "fr";
        else if ($(this).index() == 2) current.mode = "pg";
    });
    $(window).resize(function () {
        $(".feed_wrapper").width($("#container").width() - 105);
    });
    
});
$(document).ready(function () {
    $(".feed_wrapper").width($("#container").width() - 105);
    $("a.scrolltop").click(function (event) {
        event.preventDefault();
        $("body").stop().animate({ scrollTop: 0 });
    });
    $(window).bind("scroll", function () {
        var scrolltop = $("body")[0].scrollTop;        
        if (scrolltop > 100) {
            $("a.scrolltop").show();
        } else {
            $("a.scrolltop").hide();
        }
    });
});
$(document).ready(function () {    
    $.get("./fbapi/meta", null, function (resp) {
        if (resp.error) return;
        $("#fr-container").addClass("font_zh").jfmfs({ max_selected_message: "", frienddata: resp.friend });
        $("#pg-container").addClass("font_zh").jfmfs({ max_selected_message: "", frienddata: resp.likes, mode: "pg", labels: { all: "所有粉絲頁", selected: "已選擇", max_selected_message: "", filter_default: ""} });

    }, "json");
});
JSON.stringify = JSON.stringify || function (obj) {
    var t = typeof (obj);
    if (t != "object" || obj === null) {
        // simple data type
        if (t == "string") obj = '"' + obj + '"';
        return String(obj);
    }
    else {
        // recurse array or object
        var n, v, json = [], arr = (obj && obj.constructor == Array);
        for (n in obj) {
            v = obj[n]; t = typeof (v);
            if (t == "string") v = '"' + v + '"';
            else if (t == "object" && v !== null) v = JSON.stringify(v);
            json.push((arr ? "" : '"' + n + '":') + String(v));
        }
        return (arr ? "[" : "{") + String(json) + (arr ? "]" : "}");
    }
};
function download(str) {
    $.post("./fbapi/trigger", { result: str }, function (resp) {
        if (resp == "error") return;
        $("#frame_download").attr("src", "./fbapi/download?str=" + resp);
    }, "text");
}
