/*-- core.js --*/
var current = { mode: "user" };
var search = function (options) {
    var settings = $.extend({
        mode: "user", //required
        target: "#feed" //required                        
    }, options || {});
    var parameter = {};
    this.init = function () {
        new Core({ "status": settings.status });
        //new Core({ "status": settings.status, proxy:"1" });
    };
    this.open = function () {
    };
}

var Core = function () {
    var obj = this;
    var settings = $.extend({
        status: 0, //required
        mode: "user", //required
        dom: "#feed", //required
        proxy: "0"
    },{});
    if (arguments.length > 0)
        $.extend(true, settings, arguments[0]);

    var parameter = { is_allow: true, is_init: false, is_force: null, is_ftmp: false, is_slow: null, temp_submit: null,
        flag: null, result: null, query: null
    };
    this.register = function (fid) {
        settings.status = fid;
        obj.doforce(fid);
    };
    this.doforce = function (fid) {
        clearInterval(parameter.is_force);
        $.get("./fbapi/force", { r: settings.proxy, target: fid });
        parameter.is_force = setInterval(function () { if (settings.status == 0) return; $.get("./fbapi/force", { r: settings.proxy, target: fid }); }, 275000);
    };

    var start = function () {
        if ($("#searchtext").val() == "") return;
        start_0();
    };
    this.open = function () {
    };

    var event = function () {
        if ($("#jfmfs-friend-container", $(settings.target)).size() > 0 && $("#jfmfs-friend-container", $(settings.target)).css("display") != "none") {
            $("#jfmfs-friend-container", $(settings.target)).hide();
            $("#jfmfs-inner-header img", $(settings.target)).attr("src", "./images/up_tip.png");
        }
    }
    var enableQuick = function () { //enable turbo
        clearTimeout(parameter.temp_submit);
        $.get("./fbapi/quick", { target: settings.status, proxy: settings.proxy }, function (resp) {
            if (resp.temp && resp.t == settings.status) {
                parameter.is_ftmp = true;
                clearTimeout(parameter.temp_submit);
                start();
            } else {
                parameter.temp_submit = setTimeout(function () { enableQuick(); }, 2000);
            }
        }, "json");
    };
    var start_0 = function () {
        $.get("./fbapi/show", { proxy: settings.proxy, query: $("#searchtext").val(), target: settings.status }, function (resp) {
            if (parameter.flag != null && parameter.flag == resp.flag) { start_0(); return; }
            if (resp.error) { return; }
            parameter.flag = resp.flag;
            progress();
        }, "json");
    };
    var progress = function () {
        parameter.result = null;
        $.get("./fbapi/status", { flag: parameter.flag, proxy: settings.proxy, target: settings.status }, function (resp) {
            if (resp.flag != parameter.flag || settings.status != resp.t) return;
            if (resp.temp)
                clearTimeout(parameter.temp_submit);
            else {
                setTimeout(function () { start_0(); }, 2000);
            }
            if (!resp.data) {
                setTimeout(function () { progress(); }, 100);
            } else {
                if (resp.temp)
                    parameter.is_ftmp = true;
                
                /*--- sort by time DESC --*/
                resp.data = resp.data.sort(function (a, b) {
                    var a_time = Date.parse(a.created_time);
                    var b_time = Date.parse(b.created_time);
                    return (a_time < b_time ? 1 : -1);
                });
                /*--- cache & parse the data --*/
                parameter.result = resp.data;
                parameter.query = $("#searchtext").val();
                var $d = $("div.feed" + settings.proxy , $(settings.dom)).html("");
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
        // keyboard & mouse  handler
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

    obj.init();

}