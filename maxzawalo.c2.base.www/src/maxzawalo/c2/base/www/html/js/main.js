Array.prototype.contains = function (obj) {
    var i = this.length;
    while (i--) {
        if (this[i] === obj) {
            return true;
        }
    }
    return false;
}

function resolve(path, obj) {
    return path.split('.').reduce(function (prev, curr) {
        return prev ? prev[curr] : null
    }, obj || self)
}

function number_format(number, decimals, dec_point, thousands_sep) { // Format
    // a
    // number
    // with
    // grouped
    // thousands
    //
    // + original by: Jonas Raoni Soares Silva (http://www.jsfromhell.com)
    // + improved by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    // + bugfix by: Michael White (http://crestidg.com)

    var i, j, kw, kd, km;

    // input sanitation & defaults
    if (isNaN(decimals = Math.abs(decimals))) {
        decimals = 2;
    }
    if (dec_point == undefined) {
        dec_point = ",";
    }
    if (thousands_sep == undefined) {
        thousands_sep = ".";
    }

    i = parseInt(number = (+number || 0).toFixed(decimals)) + "";

    if ((j = i.length) > 3) {
        j = j % 3;
    } else {
        j = 0;
    }

    km = (j ? i.substr(0, j) + thousands_sep : "");
    kw = i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousands_sep);
    // kd = (decimals ? dec_point + Math.abs(number -
    // i).toFixed(decimals).slice(2) : "");
    kd = (decimals ? dec_point
        + Math.abs(number - i).toFixed(decimals).replace(/-/, 0).slice(2)
        : "");

    return km + kw + kd;
}

$(document).ready(
    function () {

        $("body").click(
            function (evt) {
                console.log(evt.target);
                var except = ["menu", "btn_select_image",
                    "btn_show_menu", "contractors", "tr_td_name"];
                if (!except.contains(evt.target.id)
                    && !except.contains(evt.target.parentNode.id)
                    && !except.contains(evt.target
                        .getAttribute("name")))
                // if ($('#menu').css('display') == 'block')
                    HideMenu();
            });

        $('form').submit(function (event) {
            var focused = $(document.activeElement);
            var name = focused.attr("name");
            // var id = focused.attr("id");

            if (name == "submit_store_record") {
                var who_recieve = $('[name=who_recieve]').val().trim();
                if (who_recieve == "") {
                    alert("Поле 'Кто взял' не заполнено");
                    event.preventDefault();
                }
            } else if (name == "btn_select_image") {
                // event.preventDefault();
            }
        });

        $("#btn_select_image").click(function () {
            $('#image').click();
        });

        $("#image").change(function () {
            if (this.value != "") {
                $("#btn_select_image").val($(this).val());
                $('form').btn_select_image = "";
                $('form').submit();
            } else
                $("#btn_select_image").val("Выберите изображение");
        });
    });

function getPositionTop(element) {
    var e = document.getElementById(element);
    var left = 0;
    var top = 0;

    do {
        left += e.offsetLeft;
        top += e.offsetTop;
    } while (e = e.offsetParent);

    // return [left, top];
    return top;
}

function jumpTo(id) {
    if (id == "")
        return;
    window.scrollTo(0, getPositionTop(id));
}

function Select(id) {
    var jqxhr = $.ajax("contractor.php?mode=select&id=" + id).done(
        function (data) {
            alert("Выбран " + data);
            $("#contractor_name").html(data);
            $("#contractor_name_bottom").html(data);

            HideMenu();
        }).fail(function () {
        // alert( "error" );
    }).always(function () {
        // alert( "complete" );
    });
}

function on_row_dblclick(id) {
    alert(id);
    return false;
}

function LoadTable(table_id, url, model, afterLoadTable) {
    var tbody = $('#' + table_id).find('tbody');
    tbody.empty();
    // Then if no tbody just select your table
    var table = tbody.length ? tbody : $('#' + table_id);

    var jqxhr = $
        .getJSON(url)
        .done(
            function (json) {
                // console.log( "JSON Data: " + json );

                var row = "<tr>";
                for (var i = 0; i < model.length; ++i) {
                    row += "<th name='" + model[i].name + "'>";
                    row += model[i].caption;
                    row += "</th>"
                }
                row += "</tr>";
                table.append(row);
                var posTP = 1;

                $
                    .each(
                        json,
                        function (row_pos, item) {
                            // Add row
                            // TODO: toString на сервере (в
                            // json) или как ф-я js

                            var row = "<tr bo_id='" + item.id
                                + "'>";
                            for (var mi = 0; mi < model.length; ++mi) {

                                row += "<td name='"
                                    + model[mi].name + "'>";
                                if (model[mi].name == "pos")
                                    row += posTP;// TODO:
                                // enumTP
                                else {
                                    if (model[mi].to_string_js == "") {
                                        var cell = "<input id='' type='text' value='" + item[model[mi].name] + "'/>";
                                        row += cell;
                                    }
                                    else {
                                        var cell_id = table_id + "_" + model[mi].name + "_" + row_pos;
                                        // var item = model[i];
                                        if (model[mi].to_string_js == "number_format") {
                                            // var zeros = parseInt(model[mi].format
                                            //     .split('.')[1].length);
                                            // var cell = number_format(
                                            //     item[model[mi].name],
                                            //     zeros, '.',
                                            //     '');

                                            var cell = "<input id='" + cell_id +
                                                "' class='biz_control' format='" + model[mi].format +
                                                "' field_name='" + model[mi].name +
                                                "' type='text' value='" + item[model[mi].name] + "'/>";
                                            row += cell;

                                        } else if (model[mi].to_string_js == "date") {
                                            var date = new Date();
                                            date
                                                .setTime(item[model[mi].name]);
                                            row += date
                                                .format("dd.mm.yyyy");
                                        } else if (model[mi].to_string_js == "doc_state_render") {
                                            if (item[model[mi].name] == "v")
                                                row += "<img height='20' src='img/commited.png'/>";
                                            else if (item[model[mi].name] == "x")
                                                row += "<img height='20' src='img/deleted.png'/>";
                                            else
                                                row += "";

                                        } else if (model[mi].to_string_js == "locked_by_render") {
                                            var user = resolve(
                                                "locked_by.coworker.name",
                                                item);
                                            if (user == "")
                                                row += "";
                                            else
                                                row += "<img height='20' src='img/lock.gif'/ title='"
                                                    + "Объект заблокирован пользователем: "
                                                    + user
                                                    + "'>";
                                        }
                                        else
                                            row += resolve(
                                                model[mi].to_string_js,
                                                item);// ,
                                        // model[i]
                                    }
                                }
                                row += "</td>"
                            }
                            row += "</tr>";
                            table.append(row);

                            // table.append("<tr>" + "<td
                            // name='product'>"
                            // + item.product.name + "</td>" +
                            // "<td>" +
                            // number_format(item.count, 3, '.',
                            // '')
                            // + "</td>" + "<td name='units'>"
                            // + item.product.units.name +
                            // "</td>" + "<td>"
                            // + item.price + "</td>" + "<td>" +
                            // item.sum
                            // + "</td>" + "<td>" + item.rateVat
                            // + "</td>"
                            // + "<td>" + item.sumVat + "</td>"
                            // + "<td>"
                            // + item.discount + "</td>" +
                            // "<td>" + item.total
                            // + "</td>" + "</tr>");
                            posTP++;
                        })

                if (afterLoadTable != null)
                    afterLoadTable();
            }).fail(function (jqxhr, textStatus, error) {
            var err = textStatus + ", " + error;
            console.log("Request Failed: " + err);
        });
}

function LoadBO(url, afterLoad) {

    var jqxhr = $.getJSON(url).done(function (json) {
        console.log("JSON Data: " + json);
        if (afterLoad != null)
            afterLoad(json);
    }).fail(function (jqxhr, textStatus, error) {
        var err = textStatus + ", " + error;
        console.log("Request Failed: " + err);
    });
}

function RunFunc(url, afterRun) {
    var jqxhr = $.get(url).done(function (data) {
        console.log("Run Data: " + data);
        if (afterRun != null)
            afterRun(data);
    }).fail(function (jqxhr, textStatus, error) {
        var err = textStatus + ", " + error;
        console.log("Request Failed: " + err);
    });
}

function Search() {
    LoadTable("contractors", "contractor.php?mode=search&value="
        + $('#search_data').val());
}

function ShowMenu() {
    $('#menu').css('display', 'block');
}

function HideMenu() {
    $('#menu').css('display', 'none');
}

function ClearSelection() {
    var images = document.getElementsByClassName('image_mini');
    for (index = 0; index < images.length; ++index) {
        // console.log(images[index]);
        images[index].style.border = "none";
    }
}

function HideBig(el) {
    el.style.height = "0pt";
    ClearSelection();
}

function Change(el) {
    var big_img = document.getElementById("big_img");
    big_img.style.height = "500px";
    big_img.src = el.src;

    ClearSelection();
    el.style.border = "5px solid red";
}

function submit_store_recordF() {
    var form = $("#store_record_form");
    form.submit();
    // document.store_record_form.submit();
}

function redirect (url) {
    var ua        = navigator.userAgent.toLowerCase(),
        isIE      = ua.indexOf('msie') !== -1,
        version   = parseInt(ua.substr(4, 2), 10);

    // Internet Explorer 8 and lower
    if (isIE && version < 9) {
        var link = document.createElement('a');
        link.href = url;
        document.body.appendChild(link);
        link.click();
    }

    // All other browsers can use the standard window.location.href (they don't lose HTTP_REFERER like Internet Explorer 8 & lower does)
    else { 
        window.location.href = url; 
    }
}