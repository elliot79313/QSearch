/*-- core.js --*/
var current = { mode: "user" };
var search = function (options) {
    var obj = this;
    var settings = $.extend({
        status: 0, //required
        mode: "user", //required
        target: "#feed" //required                        
    }, options || {});
    var parameter = {
        is_allow: true,
        is_init: false,
        is_force: null,
        is_ftmp: false,
        is_slow: null,
        temp_submit: null,
        flag: [],
        result: [],
        query: null
    };
    this.register = function (fid) {
        settings.status = fid;
        obj.doforce(fid);
    };
    this.doforce = function (fid) {
        clearInterval(parameter.is_force);
        $.get("./fbapi/force", { r: "2", target: fid });
        parameter.is_force = setInterval(function () { if (settings.status == 0) return; $.get("./fbapi/force", { r: "1", target: fid }); }, 275000);
    };
    var start = function () {
        if ($("#searchtext").val() == "") return;
        obj.open("clear");
        start_0();
        start_1();
    };

    this.open = function () {
        if (arguments.length > 0) {
            if (arguments[0] == "clear") {
                $("button.btn_download").hide();
                return;
            }
            if ($("div" + settings.target).css("display") != "none") {
                if (parameter.result[0] == null && parameter.result[1] == null) $("button.btn_download").hide();
                else {
                    $("button.btn_download").show();
                    $("button.btn_download").unbind("click");
                    $("button.btn_download").bind("click", function () { download(JSON.stringify($.merge($.merge([], parameter.result[0]), parameter.result[1]))); });
                }
            } else {
            }
            return;
        }
        $("div" + settings.target).show();
        if (parameter.result[0] == null && parameter.result[1] == null) $("button.btn_download").hide();
        else { $("button.btn_download").show(); $("button.btn_download").bind("click", function () { download(JSON.stringify($.merge($.merge([], parameter.result[0]), parameter.result[1]))); }); }
    };

    var event = function () {
        if ($("#jfmfs-friend-container", $(settings.target)).size() > 0 && $("#jfmfs-friend-container", $(settings.target)).css("display") != "none") {
            $("#jfmfs-friend-container", $(settings.target)).hide();
            $("#jfmfs-inner-header img", $(settings.target)).attr("src", "./images/up_tip.png");
        }
    }

    var enableQuick = function () {
        clearTimeout(parameter.temp_submit);
        $.get("./fbapi/quick", { target: settings.status, proxy: "0" }, function (resp) {
            if (resp.temp && resp.t == settings.status) {
                parameter.is_ftmp = true;
                clearTimeout(parameter.temp_submit);
                start();
            } else {
                parameter.temp_submit = setTimeout(function () { if (console.log) { console.log("slow") } enableQuick(); }, 2000);
            }
        }, "json");
    };
    var start_0 = function () {
        $.get("./fbapi/show", { proxy: "0", query: $("#searchtext").val(), target: settings.status }, function (resp) {
            if (parameter.flag[0] != null && parameter.flag[0] == resp.flag) { start_0(); return; }
            if (resp.error) { return; }
            parameter.flag[0] = resp.flag;
            if (current.mode == settings.mode) {
                $("#progresstitle span.count").html("0%");
                $("#progressbar div").animate({ width: 0 + "%" }, 100);
            }
            progress();
        }, "json");
    };
    var start_1 = function () {
        $.get("./fbapi/show", { proxy: "1", query: $("#searchtext").val(), target: settings.status }, function (resp) {
            if (parameter.flag[1] != null && parameter.flag[1] == resp.flag) { start_1(); return; }
            if (resp.error) { return; }
            parameter.flag[1] = resp.flag;
            progress1();
        }, "json");
    };
    var progress = function () {
        parameter.result[0] = null;
        $.get("./fbapi/status", { flag: parameter.flag[0], proxy: "0", target: settings.status }, function (resp) {
            if (resp.flag != parameter.flag[0] || settings.status != resp.t) return;
            if (resp.temp)
                clearTimeout(parameter.temp_submit);
            else {
                setTimeout(function () { if (console.log) { console.log("slow") } start_0(); }, 2000);
            }
            if (!resp.data) {
                var prog = parseInt(parseFloat(resp.th_c) / parseFloat(resp.th_all) * 100);
                if (prog >= 100) prog = 100;
                $("#progressbar div").animate({ width: prog + "%" }, 10);
                $("#progresstitle span.count").html(prog + "%");
                setTimeout(function () { progress(); }, 100);
            } else {
                if (resp.temp)
                    parameter.is_ftmp = true;
                $("#progresstitle span.count").html("完成        在" + (parseInt(resp.endtime) - parseInt(resp.starttime)) + "秒內");
                $("#progressbar div").animate({ width: 0 + "%" }, 10);

                resp.data = resp.data.sort(function (a, b) {
                    var a_time = Date.parse(a.created_time);
                    var b_time = Date.parse(b.created_time);
                    return (a_time < b_time ? 1 : -1);
                });

                parameter.result[0] = resp.data;
                parameter.query = $("#searchtext").val();
                obj.open("show");

                var $d = $("div.feed0", $(settings.target)).html("");
                updatePost(resp.data, $d);
            }
        }, "json");
    };
    var progress1 = function () {
        parameter.result[1] = null;
        $.get("./fbapi/status", { flag: parameter.flag[1], proxy: "1", target: settings.status }, function (resp) {
            if (resp.flag != parameter.flag[1]) return;
            if (resp.temp)
                clearTimeout(parameter.temp_submit);
            else {
                setTimeout(function () { if (console.log) { console.log("slow") } start_1(); }, 2000);
            }
            if (!resp.data) {
                progress1();
            } else {
                resp.data = resp.data.sort(function (a, b) {
                    var a_time = Date.parse(a.created_time);
                    var b_time = Date.parse(b.created_time);
                    return (a_time < b_time ? 1 : -1);
                });
                parameter.result[1] = resp.data;

                obj.open("show");
                var $d = $("div.feed1", $(settings.target)).html("");
                updatePost(resp.data, $d);
            }
        }, "json");
    };


    this.init = function () {
        if (parameter.is_init == true) return;
        parameter.is_init = true;
        if (settings.status != 0) {
            obj.doforce("me");
        }
        $("#btn_search").click(function () {
            if (current.mode != settings.mode) return;
            if (current.mode == settings.mode)
                event();
            clearTimeout(parameter.temp_submit);
            _gaq.push(['_trackEvent', 'buttonsearch', "button"]);
            if (parameter.is_ftmp == true)
                start();
            else
                enableQuick();
        });
        $("#searchtext").keypress(function (e) {
            if (current.mode != settings.mode) return;
            if (e.which == 13) {
                event();
                if (parameter.is_ftmp == false)
                    enableQuick();
                else
                    start();
            }
        });
        $("#searchtext").keyup(function (e) {
            if (current.mode != settings.mode) return;
            if (e.which == 27 || e.which == 46) return;
            if (e.which == 16 || e.which == 17 || e.which == 18 || e.which == 91 || e.which == 20) return;
            if (e.which >= 37 && e.which <= 40) return;
            if (parameter.is_ftmp == false) { enableQuick(); return; }
            if (e.which == 8) {
                clearTimeout(parameter.is_slow);
                parameter.is_slow = setTimeout(function () { event(); start(); }, 400);
                _gaq.push(['_trackEvent', 'keyboardsearch', "keyboard"]);
                return;
            }
            clearTimeout(parameter.is_slow);
            parameter.is_slow = setTimeout(function () { event(); start(); }, 200);
            _gaq.push(['_trackEvent', 'keyboardsearch', "keyboard"]);
        });
    };
}

