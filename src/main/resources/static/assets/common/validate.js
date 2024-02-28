var VLD = {
	fnc_getErrorWrapper : function(message) {
		return $.parseHTML('<div class="fv-plugins-message-container invalid-feedback">'+message+'</div>');
	},
	fnc_showError: function (element,message) {
		var errorWrp = this.fnc_getErrorWrapper(message);
		element.parent().append(errorWrp);
	},
	fnc_hideError: function (element) {
		element.parent().find('.fv-plugins-message-container.invalid-feedback').remove();
	}
}