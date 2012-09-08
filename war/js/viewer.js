function updatePost(items, container) {
    var $ul = $("<ul>");
    $.map(items, function (elem, i) {
        var $l = $("<li>");
        if (elem.message) {
            var max = 90;
            var msg = elem.message;
            if (msg.length > max)
                for (j = 0; j < msg.length; j += max)
                    msg = msg.substr(0, j + max) + '<wbr/>' + msg.substr(j + max);
            elem.message = msg;
        }
        var $d = $("<div>");
        var $ai = $("<a>").addClass("profile").attr("rel", "external").attr("target", "_blank").attr("href", "http://www.facebook.com/" + elem.from.id);
        var $i = $("<img>").attr("src", "https://graph.facebook.com/" + elem.from.id + "/picture");
        $ai.append($i);
        $d.append($ai);
        switch (elem.type) {
            case 'status':
                var $c = $("<div>").addClass("content");
                var $an = $("<a>").addClass("name").attr("rel", "external").attr("target", "_blank").attr("href", "http://www.facebook.com/" + elem.from.id);
                $an.html(elem.from.name);
                var $msg = $("<span>").addClass("msg").html(elem.message == null ? elem.story : elem.message);
                if (elem.story_tags) {
                    var queue = [];
                    $.map(elem.story_tags, function (tags) {
                        queue.push(tags[0]);
                    });
                    queue.sort(function (a, b) { return a["offset"] > b["offset"] ? 1 : -1; });
                    while (queue.length) {
                        var current = queue.pop();
                        var target = "<a style='color:#3B5998;'target='_blank' rel='external'  href='https://www.facebook.com/" + current.id + "' >" + elem.story.substring(current["offset"], current["offset"] + current["length"]) + "</a>";
                        var prefix = elem.story.substring(0, current["offset"]);
                        var suffix = elem.story.substring(current["offset"] + current["length"], elem.story.length);
                        elem.story = prefix + target + suffix;
                    }
                    $msg.html(elem.story);
                }
                var $p = $("<a>").addClass("time").attr("rel", "external").attr("target", "_blank").attr("href", "https://www.facebook.com/" + elem.id.split("_")[0] + "/posts/" + elem.id.split("_")[1]);
                update_time($p, elem.created_time, { icon: elem.icon });
                $c.append($an).append($msg).append($("<p>").append($p));
                $d.append($c);
                break;
            case 'video':
            case 'photo':
            case 'swf':
            case 'link':
                var $c = $("<div>").addClass("content");
                var $an = $("<a>").addClass("name").attr("rel", "external").attr("target", "_blank").attr("href", "http://www.facebook.com/" + elem.from.id);
                var $msg = $("<span>").addClass("msg").html(elem.message);
                $an.html(elem.from.name);
                var $p = $("<a>").addClass("time").attr("rel", "external").attr("target", "_blank").attr("href", "https://www.facebook.com/" + elem.id.split("_")[0] + "/posts/" + elem.id.split("_")[1]);
                update_time($p, elem.created_time, { icon: elem.icon });
                if ((elem.type == "video" || elem.type == "swf") && (elem.caption == 'www.youtube.com' || (elem.link).search('facebook'))) {
                    var src = "";
                    var $v = $("<div>").addClass("video");
                    try {
                        var ytpath = elem["link"].match(/http:\/\/(?:www\.)?youtube.*watch\?v=([a-zA-Z0-9\-_]+)/);
                        if (ytpath) {
                            var key = ytpath[1];
                            var src = "http://www.youtube.com/v/" + key + "?autoplay=1&version=3"
                            var $vi = $("<img style='cursor:pointer;' title='播放' src='http://i.ytimg.com/vi/" + key + "/mqdefault.jpg'/>");
                            $vi.click(function () { $(this).remove(); $v.append('<object width="460" height="259" ><param name="allowfullscreen" value="true" /><param name="allowscriptaccess" value="always" /><param name="movie" value="' + src + " " + '" /><embed src="' + src + '" type="application/x-shockwave-flash" allowscriptaccess="always" allowfullscreen="true" width="460" height="259"></embed></object>'); });
                            $v.append($vi);
                            throw "yt";
                        }
                        if (elem.caption == 'www.youtube.com' || elem.source.indexOf("www.youtube.com/v") >= 0) {
                            src = (elem.source).split('?')[0];
                            var key = "1236433433a"
                            try {
                                key = src.split("/")[4];
                            } catch (ex) {
                            }
                            var $vi = $("<img style='cursor:pointer;' title='播放' src='http://i.ytimg.com/vi/" + key + "/mqdefault.jpg'/>");
                            $vi.click(function () { $(this).remove(); $v.append('<object width="460" height="259" ><param name="allowfullscreen" value="true" /><param name="allowscriptaccess" value="always" /><param name="movie" value="' + src + "&autoplay=1" + '" /><embed src="' + src + '?version=3" type="application/x-shockwave-flash" allowscriptaccess="always" allowfullscreen="true" width="460" height="259"></embed></object>'); });
                            $v.append($vi);
                        }
                        else {
                            src = 'http://www.facebook.com/v/' + elem.link.split('?v=')[1];
                            $v.append('<object width="460" height="259" ><param name="allowfullscreen" value="true" /><param name="allowscriptaccess" value="always" /><param name="movie" value="' + src + '" /><embed src="' + src + '" type="application/x-shockwave-flash" allowscriptaccess="always" allowfullscreen="true" width="460" height="259"></embed></object>');
                        }
                    } catch (ex) {
                       
                    }
                    $c.append($an).append($msg).append($v).append($("<p>").append($p));
                }
                if (elem.type == "photo") {
                    //$p.attr("href", elem.link);
                    var $v = $("<div>").addClass("photo");
                    $v.append($("<img>").attr("src", elem.picture).width(130));
                    $c.append($an).append($msg).append($v).append($("<p>").append($p));
                }
                if (elem.type == "link") {
                    var $v = $("<div>").addClass("link");
                    var $ir = $("<div>").addClass("ir");
                    $ir.append($("<img>").attr("src", elem.picture));
                    var $cr = $("<div>").addClass("cr");
                    var $t = $("<div>").addClass("cr_t").append($("<a>").addClass("font_zh").attr("rel", "external").attr("target", "_blank").attr("href", elem.link).html(elem.name));
                    var $g = $("<div>").addClass("cr_g").addClass("font_zh").html(elem.description);
                    $cr.append($t).append($g);
                    $v.append($ir).append($cr);
                    $c.append($an).append($msg).append($v).append($("<p>").append($p));
                }
                $d.append($c);
                break;
            case 'checkin':
                var $c = $("<div>").addClass("content");
                var $an = $("<a>").addClass("name").attr("rel", "external").attr("target", "_blank").attr("href", "http://www.facebook.com/" + elem.from.id);
                $an.html(elem.from.name);
                var $msg = $("<span>").addClass("msg").html(elem.message == null ? elem.story : elem.message);
                var $p = $("<a>").addClass("time").attr("rel", "external").attr("target", "_blank").attr("href", "https://www.facebook.com/" + elem.id.split("_")[0] + "/posts/" + elem.id.split("_")[1]);
                update_time($p, elem.created_time, { icon: elem.icon });
                var $v = $("<div>").addClass("map");
                var $map = $("<div>").css("width", 460).css("height", 260).css("margin-top", 10);
                $v.append($map);
                $c.append($an).append($msg).append($v).append($("<p>").append($p));
                $d.append($c);

                $l.ready(function () {
                    setTimeout(function () {
                        var myLatlng = new google.maps.LatLng(elem.place.location.latitude, elem.place.location.longitude);
                        var myOptions = {
                            zoom: 18,
                            center: myLatlng,
                            mapTypeId: google.maps.MapTypeId.HYBRID
                        }
                        var map = new google.maps.Map($map[0], myOptions);

                        var marker = new google.maps.Marker({
                            position: myLatlng,
                            map: map,
                            title: elem.place.name
                        });
                    },500);
                });
                break;
            default:
                break;

        }
        $l.append($d);
        $ul.append($l);
    });
    container.append($ul);
}
function update_time(item, time, option) {
    if (option.icon == null || option.icon == 'undefined')
        option.icon = "http://static.ak.fbcdn.net/rsrc.php/v2/yq/r/SC2ZmEkfI-X.png";
    var current_time = new Date();
    var item_time = new Date(time);
    var diff = parseInt((Date.parse(current_time) - Date.parse(time)) / 1000);
    var time_status;    
    var str = item_time.getFullYear() + '-' + (item_time.getMonth() + 1) + '-' + item_time.getDate() + ' ';
        str += (item_time.getHours() < 10 ? ("0" + item_time.getHours()) : item_time.getHours()) + ":";
        str += (item_time.getMinutes() < 10 ? ("0" + item_time.getMinutes()) : item_time.getMinutes()) + ":";
        str += (item_time.getSeconds() < 10 ? ("0" + item_time.getSeconds()) : item_time.getSeconds());
    
    if (diff < 60) {
        time_status = "幾秒鐘前";
    } else if (diff < 3600) {
        time_status = "約" + parseInt(diff / 60) + "分鐘前";
    } else if (diff < 3600 * 24) {
        time_status = "約" + parseInt(diff / 3600) + "小時前";
    } else {
        time_status = (item_time.getMonth() + 1) + "月 " + item_time.getDate() + "日";
    }

    if (option.isupdate == null) {
        $(item).html("<img class='first' src='" + option.icon + "'/>" + "<span>"+time_status+"</span>" + "<img class='second' src='./images/page_go.png'/>").attr("title", str);
        return setInterval(function () { update_time(item, time, { isupdate: true, icon:option.icon }); }, 1000);
    } else {
        $("span", $(item)).html(time_status);        
    }
}
function QueryString(name, url) {
    var AllVars = url.split("?")[1];
    var Vars = AllVars.split("&");
    for (i = 0; i < Vars.length; i++) {
        var Var = Vars[i].split("=");
        if (Var[0] == name) return Var[1];
    }
    return "";
}