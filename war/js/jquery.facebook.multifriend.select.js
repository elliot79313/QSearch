﻿// Copyright 2010 Mike Brevoort http://mike.brevoort.com @mbrevoort
// 
// v5.0 jquery-facebook-multi-friend-selector
// 
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
// 
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
   
(function ($) {
    var JFMFS = function (element, options) {
        var elem = $(element),
            obj = this,
            uninitializedImagefriendElements = [], // for images that are initialized
            keyUpTimer,
            friends_per_row = 0,
            friend_height_px = 0,
            first_element_offset_px;

        var settings = $.extend({
            max_selected: 1,
            max_selected_message: "{0} of {1} selected",
            pre_selected_friends: [],
            exclude_friends: [],
            friend_fields: "id,name",
            sorter: function (a, b) {
                var x = a.name.toLowerCase();
                var y = b.name.toLowerCase();
                return ((x < y) ? -1 : ((x > y) ? 1 : 0));
            },
            frienddata: {},
            mode: "fr",
            labels: {
                selected: "已選擇",
                filter_default: "",
                filter_title: "搜尋目標:",
                all: "所有朋友",
                max_selected_message: ""
            }
        }, options || {});
        var lastSelected;  // used when shift-click is performed to know where to start from to select multiple elements

        var arrayToObjectGraph = function (a) {
            var o = {};
            for (var i = 0, l = a.length; i < l; i++) {
                o[a[i]] = '';
            }
            return o;
        };

        // ----------+----------+----------+----------+----------+----------+----------+
        // Initialization of container
        // ----------+----------+----------+----------+----------+----------+----------+
        elem.html(
            "<div id='jfmfs-friend-selector'>" +
            "    <div id='jfmfs-inner-header'>" +
            "        <span class='jfmfs-title'><input type='text' id='jfmfs-friend-filter-text' value='" + "" + "'  placeholder='輸入搜尋的目標對象...'/> </span>" +
            "        <span class='jfmfs-expand' style='margin-left:10px;cursor:pointer;'><img src='./images/up_tip.png'/></span>" +
            "        <a class='filter-link selected' id='jfmfs-filter-all' href='#'>" + settings.labels.all + "</a>" +
            "        <a class='filter-link' id='jfmfs-filter-selected' href='#'>" + settings.labels.selected + " (<span id='jfmfs-selected-count'>0</span>)</a>" +
            ((settings.max_selected > 0) ? "<div id='jfmfs-max-selected-wrapper'></div>" : "") +
            "    </div>" +
            "    <div id='jfmfs-friend-container'></div>" +
            "</div>"
        );

        var friend_container = $("#jfmfs-friend-container", elem),
            container = $("#jfmfs-friend-selector", elem),
			preselected_friends_graph = arrayToObjectGraph(settings.pre_selected_friends),
			excluded_friends_graph = arrayToObjectGraph(settings.exclude_friends),
            all_friends;

        this.start = function () {
            var sortedFriendData = settings.frienddata.data.sort(settings.sorter),
                preselectedFriends = {},
			    selectedClass = "";
            $.each(sortedFriendData, function (i, friend) {
                if (!(friend.id in excluded_friends_graph)) {
                    selectedClass = (friend.id in preselected_friends_graph) ? "selected" : "";
                    $d = $("<div>").addClass("jfmfs-friend").attr("id", friend.id).data("fid", friend.id);
                    $d.append($("<img>"));
                    $d.append($("<div>").addClass("friend-name").html(friend.name));
                    friend_container.append($d);
                }
            });


            uninitializedImagefriendElements = $(".jfmfs-friend", elem);
            uninitializedImagefriendElements.bind('inview', function (event, visible) {
                if ($(this).attr('src') === undefined || $(this).attr('src') == "" || $(this).attr('src') == "undefined" || $(this).attr('src').indexOf("facebook") < 0) {
                    $("img", $(this)).error(function () { console.log("error1") });
                    $("img", $(this)).attr("src", "//graph.facebook.com/" + this.id + "/picture");
                    $(this).unbind('inview');
                }

            });

            init();
        };

        // ----------+----------+----------+----------+----------+----------+----------+
        // Public functions
        // ----------+----------+----------+----------+----------+----------+----------+

        this.getSelectedIds = function () {
            var ids = [];
            $.each(elem.find(".jfmfs-friend.selected"), function (i, friend) {
                ids.push($(friend).attr("id"));
            });
            return ids;
        };

        this.getSelectedIdsAndNames = function () {
            var selected = [];
            $.each(elem.find(".jfmfs-friend.selected"), function (i, friend) {
                selected.push({ id: $(friend).attr("id"), name: $(friend).find(".friend-name").text() });
            });
            return selected;
        };

        this.clearSelected = function () {
            all_friends.removeClass("selected");
        };

        // ----------+----------+----------+----------+----------+----------+----------+
        // Private functions
        // ----------+----------+----------+----------+----------+----------+----------+

        var init = function () {
            all_friends = $(".jfmfs-friend", elem);

            // calculate friends per row
            first_element_offset_px = all_friends.first().offset().top;
            for (var i = 0, l = all_friends.length; i < l; i++) {
                if ($(all_friends[i]).offset().top === first_element_offset_px) {
                    friends_per_row++;
                } else {
                    friend_height_px = $(all_friends[i]).offset().top - first_element_offset_px;
                    break;
                }
            }
            elem.find("span.jfmfs-expand").click(function () {
                $("#jfmfs-friend-container", elem).toggle();
                if ($("#jfmfs-friend-container", elem).css("display") == "none") {
                    $("img", this).attr("src", "./images/up_tip.png");
                } else {
                    $("img", this).attr("src", "./images/down_tip.png");
                }
            });
            // handle when a friend is clicked for selection
            elem.delegate(".jfmfs-friend", 'click', function (event) {
                var onlyOne = settings.max_selected === 1,
                    isSelected = $(this).hasClass("selected"),
                    isMaxSelected = $(".jfmfs-friend.selected").length >= settings.max_selected,
                    alreadySelected = friend_container.find(".selected").attr('id') === $(this).attr('id');

                // if the element is being selected, test if the max number of items have
                // already been selected, if so, just return
                if (!onlyOne && !isSelected && maxSelectedEnabled() && isMaxSelected)
                    return

                // if the max is 1 then unselect the current and select the new    
                if (onlyOne && !alreadySelected) {
                    friend_container.find(".selected").removeClass("selected");

                }

                $(this).toggleClass("selected");
                $(this).removeClass("hover");

                // support shift-click operations to select multiple items at a time
                if ($(this).hasClass("selected")) {
                    elem.find("#jfmfs-friend-filter-text").val($("div.friend-name", this).html());
                    $("#searchtext").focus();
                    if (settings.mode == "fr") {
                        fr_search.register($(this).data("fid"));
                    } else if (settings.mode == "pg") {
                        pg_search.register($(this).data("fid"));
                    }
                    if (!lastSelected) {
                        lastSelected = $(this);
                    }
                    else {
                        if (event.shiftKey) {
                            var selIndex = $(this).index(),
                                lastIndex = lastSelected.index(),
                                end = Math.max(selIndex, lastIndex),
                                start = Math.min(selIndex, lastIndex);

                            for (var i = start; i <= end; i++) {
                                var aFriend = $(all_friends[i]);
                                if (!aFriend.hasClass("hide-non-selected") && !aFriend.hasClass("hide-filtered")) {
                                    if (maxSelectedEnabled() && $(".jfmfs-friend.selected").length < settings.max_selected) {
                                        $(all_friends[i]).addClass("selected");
                                    }
                                }
                            }
                        }
                    }
                } else {
                    elem.find("#jfmfs-friend-filter-text").val("");
                }

                // keep track of last selected, this is used for the shift-select functionality
                lastSelected = $(this);

                // update the count of the total number selected
                updateSelectedCount();

                if (maxSelectedEnabled()) {
                    updateMaxSelectedMessage();
                }
                elem.trigger("jfmfs.selection.changed", [obj.getSelectedIdsAndNames()]);
            });

            // filter by selected, hide all non-selected
            $("#jfmfs-filter-selected", elem).click(function (event) {
                event.preventDefault();
                all_friends.not(".selected").addClass("hide-non-selected");
                $(".filter-link").removeClass("selected");
                $(this).addClass("selected");
                if ($("#jfmfs-friend-container", elem).css("display") == "none") {
                    $("#jfmfs-friend-container", elem).show();
                    $("#jfmfs-inner-header img", elem).attr("src", "./images/down_tip.png");
                }
                $("#jfmfs-friend-container", elem).css("height", 100);
            });

            // remove filter, show all
            $("#jfmfs-filter-all", elem).click(function (event) {
                event.preventDefault();
                all_friends.removeClass("hide-non-selected");
                $(".filter-link").removeClass("selected");
                $(this).addClass("selected");
                if ($("#jfmfs-friend-container", elem).css("display") == "none") {
                    $("#jfmfs-friend-container", elem).show();
                    $("#jfmfs-inner-header img", elem).attr("src", "./images/down_tip.png");
                }
                $("#jfmfs-friend-container", elem).css("height", "");
            });

            // hover effect on friends
            elem.find(".jfmfs-friend:not(.selected)").live(
                'hover', function (ev) {
                    if (ev.type == 'mouseover') {
                        $(this).addClass("hover");
                    }
                    if (ev.type == 'mouseout') {
                        $(this).removeClass("hover");
                    }
                });

            // filter as you type 
            elem.find("#jfmfs-friend-filter-text")
                .keyup(function () {
                    var filter = $(this).val();
                    clearTimeout(keyUpTimer);
                    if ($("#jfmfs-friend-container").css("display") == "none") {
                        $("#jfmfs-friend-container").css("display", "block");
                    }
                    keyUpTimer = setTimeout(function () {
                        if (filter == '') {
                            all_friends.removeClass("hide-filtered");
                        }
                        else {
                            container.find(".friend-name:not(:Contains(" + filter + "))").parent().addClass("hide-filtered");
                            container.find(".friend-name:Contains(" + filter + ")").parent().removeClass("hide-filtered");
                        }
                        showImagesInViewPort();
                    }, 400);
                })
                .focus(function () {
                    if ($.trim($(this).val()) == 'Start typing a name') {
                        $(this).val('');
                    }
                })
                .blur(function () {
                    if ($.trim($(this).val()) == '') {
                        $(this).val('');
                    }
                });

            // hover states on the buttons        
            elem.find(".jfmfs-button").hover(
                function () { $(this).addClass("jfmfs-button-hover"); },
                function () { $(this).removeClass("jfmfs-button-hover"); }
            );

            // manages lazy loading of images
            var getViewportHeight = function () {
                var height = window.innerHeight; // Safari, Opera
                var mode = document.compatMode;

                if ((mode || !$.support.boxModel)) { // IE, Gecko
                    height = (mode == 'CSS1Compat') ?
                    document.documentElement.clientHeight : // Standards
                    document.body.clientHeight; // Quirks
                }

                return height;
            };

            var showImagesInViewPort = function (k) {
                var container_height_px = friend_container.innerHeight(),
                    scroll_top_px = friend_container.scrollTop(),
                    container_offset_px = friend_container.offset().top,
                    $el, top_px,
                    elementVisitedCount = 0,
                    foundVisible = false,
                    allVisibleFriends = $(".jfmfs-friend:not(.hide-filtered )", elem);
                $.each(allVisibleFriends, function (i, $el) {
                    elementVisitedCount++;
                    if (k != null) {
                        if (i > k) return;
                    }
                    if ($el !== null) {
                        $el = $(allVisibleFriends[i]);
                        var el_top = $el[0].offsetTop;                        
                        top_px = (scroll_top_px + container_offset_px);
                        if (el_top >= top_px - 80 && el_top <= top_px + container_height_px + 160) {
                            $el.data('inview', true);
                            $el.trigger('inview');
                            foundVisible = true;
                        }
                        else {
                            if (foundVisible) {
                                return false;
                            }
                        }
                    }
                });
            };

            var updateSelectedCount = function () {
                $("#jfmfs-selected-count", elem).html(selectedCount());
            };

            friend_container.bind('scroll', $.debounce(250, showImagesInViewPort));

            updateMaxSelectedMessage();
            showImagesInViewPort(40);
            updateSelectedCount();
            elem.trigger("jfmfs.friendload.finished");
        };

        var selectedCount = function () {
            return $(".jfmfs-friend.selected", elem).length;
        };

        var maxSelectedEnabled = function () {
            return settings.max_selected > 0;
        };

        var updateMaxSelectedMessage = function () {
            var message = settings.labels.max_selected_message.replace("{0}", selectedCount()).replace("{1}", settings.max_selected);
            $("#jfmfs-max-selected-wrapper", elem).html(message);
        };

    };



    $.fn.jfmfs = function (options) {
        return this.each(function () {
            var element = $(this);

            // Return early if this element already has a plugin instance
            if (element.data('jfmfs')) { return; }

            // pass options to plugin constructor
            var jfmfs = new JFMFS(this, options);
            jfmfs.start();
            // Store plugin object in this element's data
            element.data('jfmfs', jfmfs);

        });
    };

    // todo, make this more ambiguous
    $.expr[':'].Contains = function (a, i, m) {
        return $(a).text().toUpperCase().indexOf(m[3].toUpperCase()) >= 0;
    };


})(jQuery);

if($.debounce === undefined) {
    /*
     * jQuery throttle / debounce - v1.1 - 3/7/2010
     * http://benalman.com/projects/jquery-throttle-debounce-plugin/
     * 
     * Copyright (c) 2010 "Cowboy" Ben Alman
     * Dual licensed under the MIT and GPL licenses.
     * http://benalman.com/about/license/
     */
    (function(b,c){var $=b.jQuery||b.Cowboy||(b.Cowboy={}),a;$.throttle=a=function(e,f,j,i){var h,d=0;if(typeof f!=="boolean"){i=j;j=f;f=c}function g(){var o=this,m=+new Date()-d,n=arguments;function l(){d=+new Date();j.apply(o,n)}function k(){h=c}if(i&&!h){l()}h&&clearTimeout(h);if(i===c&&m>e){l()}else{if(f!==true){h=setTimeout(i?k:l,i===c?e-m:e)}}}if($.guid){g.guid=j.guid=j.guid||$.guid++}return g};$.debounce=function(d,e,f){return f===c?a(d,e,false):a(d,f,e!==false)}})(this);
}
