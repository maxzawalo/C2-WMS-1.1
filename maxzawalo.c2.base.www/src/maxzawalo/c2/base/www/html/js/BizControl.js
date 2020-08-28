function BizControl(id, field_name) {
    this.id = id;// html id
    this.field_name = field_name;
    this.type = "double";
    this.format = "0.000";
    this.ctrl;

    this.__construct = function() {
	this.ctrl = $("#" + this.id);
    };
    this.__construct();

    // TODO: double,contractor,contract,product
    // формат по модели
    // click - enter - edit - enter - send to server (update cell/field)
    this.setValue = function(value) {
	if (this.type == "double") {
	    var zeros = parseInt(this.format.split('.')[1].length);
	    // str.replace(/тест/g,"прошел")
	    value = value.toString().replace(new RegExp(",", 'g'), ".");
	    value = number_format(value, zeros, '.', '');
	}
	this.ctrl.val(value);
	// return value;
    }

    this.getValue = function() {
	return this.ctrl.val();
    }

    this.getJsonValue = function() {
	var json = '"' + this.field_name + '":';
	if (this.type == "string")
	    json += '"' + this.ctrl.val() + '"';
	else
	    json += this.ctrl.val();
	return json;
    }

    this.setEvents = function() {
	this.ctrl.on('keydown', function(e) {
	    if (e.which == 13) {
		// alert('enter');
		var ctrl_obj;
		var value;
		for (var i = 0; i < controls.length; i++)
		    if (controls[i].id == this.id) {
			ctrl_obj = controls[i];
			break;
		    }
		if (this.type == "double") {
		    value = this.value;
		} else {
		    value = this.value;
		    // send to server
		    console.log("send to server: " + value);
		}

		ctrl_obj.afterPressEnter(value);

		// this.obj.setValue(this.obj.ctrl.val());
		e.preventDefault();
	    }
	});
    }

    // кнопка для выбора из списка(в ТЧ 2 очередь)
    this.SetData = function(obj) {
	this.ctrl.attr("obj", this);
	var value = obj[this.field_name];
	console.log(value);

	this.setValue(value);
	this.setEvents();
    };

    this.afterPressEnter = function(value) {
	console.log("afterPressEnter: " + value);
	this.setValue(value);
    };

}