var AVAIL_PHONE_TYPES = [
    { value: 'WORK_PHONE', description: 'Work' },
    { value: 'HOME_PHONE', description: 'Home' },
    { value: 'MOBILE_PHONE', description: 'Mobile' }
];

var AVAIL_EMAIL_TYPES = [
    { value: 'WORK_EMAIL', description: 'Work' },
    { value: 'PERSONAL_EMAIL', description: 'Personal' }
];

var AVAIL_ADDR_TYPES = [
    { value: 'VISITING_ADDRESS', description: 'Visiting' },
    { value: 'CORPORATE_ADDRESS', description: 'Corporate' }
];

headerView = Backbone.View.extend({

    initialize: function () {
        this.render();
    },

    render: function () {
        $(this.el).html(this.template());
        return this;
    },

    selectMenuItem: function (menuItem) {
        $('.nav li').removeClass('active');
        if (menuItem) {
            $('.' + menuItem).addClass('active');
        }
    }

});

typeValueModel = Backbone.Model.extend({
    availableTypes: [{value:'TYPE',description:''}],
    initialize: function() {
	this.set({type: this.get('type') || this.availableTypes[0].value});
    }
});

typeValueView = Backbone.View.extend({
    initialize: function() {
	this.render();
	this.listenTo(this.model, "change", this.render);
	this.listenTo(this.model, "destroy", this.remove);
    },
    
    events: {
	"click .dropdown-menu a": "chooseType",
	"click .removeValue": "removeValue",
	"change .valueInput": "changeValue"
    },
    
    render: function() {
	var avail = this.model.availableTypes;
	var type = this.model.get('type'); 
	
	$(this.el).html(this.template({
	    availTypes: avail,
	    type: type || avail[0].value,
	    typeDescription: (_.findWhere(avail, { value: type }) || avail[0]).description,
	    value: this.model.get(this.modelValueField)
	}));
	return this;
    },
    
    chooseType: function(event) {
	this.model.set({type: $(event.target).attr('href')});
	event.preventDefault();
    },
    
    changeValue: function(event) {
	this.model.set(this.modelValueField, $.trim(event.target.value));
    },
    
    removeValue: function() {
	//TODO: save last version to the model prototype for new creation
	this.model.destroy();
    }
});

phoneModel = typeValueModel.extend({
    defaults: { phone: "" }, availableTypes: AVAIL_PHONE_TYPES
});

phoneView = typeValueView.extend({
    modelValueField: 'phone'
});

emailModel = typeValueModel.extend({
    defaults: { email: "" }, availableTypes: AVAIL_EMAIL_TYPES
});

emailView = typeValueView.extend({
    modelValueField: 'email'
});

addressModel = Backbone.DeepModel.extend({
    availableTypes: AVAIL_ADDR_TYPES,
    defaults: { street: [], town: "", postalCode: "" },
    initialize: function() {
	this.set({type: this.get('type') || this.availableTypes[0].value});
    }
});

addressView = Backbone.View.extend({
    initialize: function() {
	this.listenTo(this.model, "change", this.render);
	this.listenTo(this.model, "destroy", this.remove);
	this.render();
    },
    
    events: {
	"click .dropdown-menu a": "chooseType",
	"click .removeAddress": "removeAddress",
	"click .removeStreet": "removeStreet",
	"click .addStreet": "addStreet",
	"change input[type='text'],select": "changeValue"
    },
    
    render: function() {
	var vars = this.model.toJSON();
	vars.typeDescription = (_.findWhere(this.model.availableTypes, { value: vars.type }) || this.model.availableTypes[0]).description;
	vars.availableTypes = this.model.availableTypes;
	if(vars.street.length == 0) {
	    vars.street = [ "" ];
	}
	$(this.el).html(this.template(vars));
	return this;
    },
    
    chooseType: function(e) {
	this.model.set({type: $(e.target).attr('href')});
	e.preventDefault();
    },
    
    changeValue: function(event) {
	this.model.set(event.target.name, $.trim(event.target.value));
    },
    
    removeAddress: function(e) {
	this.model.destroy();
	e.preventDefault();
    },
    
    addStreet: function(e) {
	var st = this.model.get('street');
	if(st.length == 0)
	    st = [""];
	this.model.set({street: st.concat([""])});
	e.preventDefault();
    },
    
    removeStreet: function(e) {
	var i = parseInt($(e.target).attr('data-num'));
	var st = this.model.get('street');
	this.model.set({street: st.slice(0, i).concat(st.slice(i + 1))});
	e.preventDefault();
    }
    
});

customerModel = Backbone.DeepModel.extend({

    localStorage: new Backbone.LocalStorage("customer"),
    
    initialize: function() {
	this.childCollection('addresses', addressModel);
	this.childCollection('phones', phoneModel);
	this.childCollection('emails', emailModel);
    },
    
    childCollection: function(fieldName, modelType) {
	var collection = this[fieldName + 'Collection'] = new Backbone.Collection([], {model: modelType});
	var listenerFrom = this[fieldName + 'CollectionUpdated'] = function() {
	    this.off('change:' + fieldName, listenerTo);
	    this.set(fieldName, _.map(collection.models, function(m) {
		return m.toJSON();
	    }));
	    this.on('change:' + fieldName, listenerTo);
	};
	var listenerTo = this[fieldName + 'ModelUpdated'] = function() {
	    collection.reset(this.get(fieldName));
	}
	
	this.listenTo(collection, "add", listenerFrom);
	this.listenTo(collection, "remove", listenerFrom);
	this.listenTo(collection, "change", listenerFrom);
	this.on('change:' + fieldName, listenerTo);
	
	return collection;
    }
});

customerView = Backbone.View.extend({
    
    initialize: function() {
	this.childRenders = [];
	
	this.listenTo(this.model, "change:name", function () {
	    $("input[name='name']", this.el).val(this.model.get('name'));
	}); 
	this.listenTo(this.model, "change:notes", function () {
	    $("textarea", this.el).val(this.model.get('notes'));
	}); 
	
	this.childCollection(this.model.addressesCollection, '.addressesList', addressView, '.addAddress');
	this.childCollection(this.model.emailsCollection, '.emailsList', emailView, '.addEmail');
	this.childCollection(this.model.phonesCollection, '.phonesList', phoneView, '.addPhone');
	
	this.render();
    },
    
    childCollection: function(collection, container, viewType, addButton) {
	var redrawAll = function() {
	    $(container, this.el).empty();
	    for(var i = 0; i < collection.length; ++i) {
		$(container, this.el).append(new viewType({model: collection.at(i)}).$el);
	    }
	}
	
	this.listenTo(collection, "add", function (model) {
	    $(container, this.el).append(new viewType({model: model}).$el);
	}); 
	
	this.listenTo(collection, "reset", redrawAll); 
	this.childRenders.push(redrawAll);
	
	this["click" + addButton] = function(e) {
	    collection.add(new collection.model({}));
	    e.preventDefault();
	};
	
	this.events["click " + addButton] = "click" + addButton;
    },
    
    events: {
	"change input[name='name'],textarea": "changeValue",
	"click .saveCustomer": "saveCustomer",
	"click .deleteCustomer": "deleteCustomer"
    },
    
    saveCustomer: function(e) {
	var self = this;
	
        var check = { isValid: true };//this.model.validateAll();
        if (check.isValid === false) {
            utils.displayValidationErrors(check.messages);
            return false;
        }
	
        this.model.save(null, {
            success: function (model) {
                self.render();
                app.navigate('edit/' + model.id, false);
                utils.showAlert('Success!', 'Customer saved successfully', 'alert-success');
            },
            error: function () {
                utils.showAlert('Error', 'An error occurred while trying to delete this item', 'alert-error');
            }
        });
	
	return false;
    },
    
    deleteCustomer: function(e) {
	if(confirm('Are you sure you want to delete customer?')) {
	    this.model.destroy({
		success: function () {
                    alert('Customer was deleted successfully');
                    window.history.back();
		}
            });
	}
	return false;
    },
    
    changeValue: function(event) {
	this.model.set(event.target.name, $.trim(event.target.value));
    },
	
    render: function() {
        $(this.el).html(this.template({name: this.model.get('name'), notes: this.model.get('notes')}));
	
	this.model.get('id') ? $('.deleteCustomer', this.el).show() : $('.deleteCustomer', this.el).hide();

	var self = this;
	_.each(this.childRenders, function(f) { f.call(self); });
	
	return this;
    }
    
});

var appRouter = Backbone.Router.extend({

    routes: {
        ""                  : "list",
        "page/:page"	: "list",
        "add"         : "addCustomer",
        "edit/:id"    : "editCustomer"
    },

    initialize: function () {
        this.headerView = new headerView();
        $('.header').html(this.headerView.el);
    },

    list: function(page) {

	$('#content').empty();
	
        // var p = page ? parseInt(page, 10) : 1;
        // var customerList = new CustomerCollection();
        // customerList.fetch({success: function(){
        //     $("#content").html(new CustomerListView({model: customerList, page: p}).el);
        // }});
        this.headerView.selectMenuItem('home-menu');
    },

    editCustomer: function (id) {
	var customer = new customerModel({id: id});
        customer.fetch({success: function(){
	    console.dir(customer);
	    $('#content').empty().append(new customerView({model: customer}).$el);
        }});
	
        this.headerView.selectMenuItem();
    },

    addCustomer: function() {
	var customer = new customerModel({});
	$('#content').empty().append(new customerView({model: customer}).$el);
	
        this.headerView.selectMenuItem('add-menu');
    }

});

utils.loadTemplate(['typeValueView','headerView','addressView','customerView'], function() {
    app = new appRouter();
    Backbone.history.start();
});