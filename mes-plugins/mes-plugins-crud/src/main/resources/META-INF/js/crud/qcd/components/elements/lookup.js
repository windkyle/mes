/*
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo MES
 * Version: 0.2.0
 *
 * This file is part of Qcadoo.
 *
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */

var QCD = QCD || {};
QCD.components = QCD.components || {};
QCD.components.elements = QCD.components.elements || {};

QCD.components.elements.Lookup = function(_element, _mainController) {
	$.extend(this, new QCD.components.elements.FormComponent(_element, _mainController));
	
	var elementPath = this.elementPath;
	var element = _element;
	
	var mainController = _mainController;
	
	var lookupWindow;
	
	var inputElement = this.input;
	var valueDivElement = $("#"+this.elementSearchName+"_valueDiv");
	var loadingElement = $("#"+this.elementSearchName+"_loadingDiv");
	var labelElement = $("#"+this.elementSearchName+"_labelDiv");
	var openLookupButtonElement = $("#"+this.elementSearchName+"_openLookupButton");
	
	var labelNormal = labelElement.html();
	var labelFocus = "<span class='focusedLabel'>"+this.options.translations.labelOnFocus+"</span>";
	
	var currentData = new Object();
	currentData.value = null;
	currentData.selectedEntityValue = null;
	currentData.selectedEntityCode = null;
	
	var isFocused = false;
	
	var baseValue;
	
	var listeners = this.options.listeners;
	var hasListeners = (this.options.listeners.length > 0) ? true : false;
	
	var _this = this;
	
	var constructor = function(_this) {
		openLookupButtonElement.click(openLookup);
		inputElement.focus(onInputFocus).blur(onInputBlur);
		inputElement.keypress(function(e) {
			var key=e.keyCode || e.which;
			if (key==13) {
				performSearch();
			}
		});
		valueDivElement.click(function() {
			if (openLookupButtonElement.hasClass("enabled")) {
				inputElement.focus();
			}
		});
	}
	
	this.setComponentData = function(data) {
		currentData.value = data.value ? data.value : null;
		currentData.selectedEntityValue = data.selectedEntityValue;
		currentData.selectedEntityCode = data.selectedEntityCode;
		currentData.contextEntityId = data.contextEntityId;
		if (currentData.value == null && currentData.selectedEntityCode != null && $.trim(currentData.selectedEntityCode) != "") {
			currentData.isError = true;
		} else {
			currentData.isError = false;
		}
		updateData();
	}
	
	this.getComponentData = function() {
		if (isFocused) {
			currentData.selectedEntityCode = $.trim(inputElement.val());
		}
		//currentData.selectedEntityCode = $.trim(inputElement.val());
		//if (baseValue && (currentData.selectedEntityCode != baseValue.selectedEntityCode)) {
		//	QCD.info(elementPath + " clear");
		//}
		return currentData;
	}
	
	this.setFormComponentEnabled = function(isEnabled) {
		if (isEnabled) {
			openLookupButtonElement.addClass("enabled")
			document.getElementById(this.elementPath+"_valueDiv").removeAttribute("disabled");
		} else {
			openLookupButtonElement.removeClass("enabled")
			document.getElementById(this.elementPath+"_valueDiv").setAttribute("disabled", true);
		}
	}
	
	this.performUpdateState = function() {
		baseValue = new Object();
		baseValue.value = currentData.value;
		baseValue.selectedEntityCode = currentData.selectedEntityCode;
	}
	
	this.isComponentChanged = function() {
		return ! (currentData.selectedEntityCode == baseValue.selectedEntityCode);
	}
	
	function updateData() {
		loadingElement.hide();
		if (! currentData.isError) {
			element.removeClass("error");
			valueDivElement.html(currentData.selectedEntityValue);
			if (currentData.selectedEntityCode) {
				valueDivElement.attr('title', currentData.selectedEntityValue);
				inputElement.attr('title', currentData.selectedEntityValue);
				if (! isFocused) {
					valueDivElement.show();
					inputElement.val("");
					labelElement.html(labelNormal);
				} else {
					inputElement.val(currentData.selectedEntityCode);
				}
			} else {
				valueDivElement.attr('title', "");
				inputElement.attr('title', "");
				inputElement.val("");
				if (! isFocused) {
					labelElement.html(labelNormal);
				}
			}
		}
	}
	
	//TODO: mady 429
//	function setSelectionRange(input, selectionStart, selectionEnd) {
//		input = document.getElementsByTagName("input")[0];
//		input.value = input.value;
//		if (input.createTextRange) {
//			var range = input.createTextRange();
//			range.collapse(true);
//			range.moveEnd('character', selectionEnd);
//			range.moveStart('character', selectionStart);
//			range.select();
//		}
//	}

//	function setCaretToPos(input, pos) {
//		setSelectionRange(input, pos, pos);
//	}
	
	function onInputFocus() {
		isFocused = true;
		openLookupButtonElement.addClass("lightHover");
		valueDivElement.hide();
		labelElement.html(labelFocus);
		if (currentData.selectedEntityCode) {
			inputElement.val(currentData.selectedEntityCode);
			inputElement.attr('title', currentData.selectedEntityCode);
		} else {
			
		}

		//TODO: mady 429
//		setCaretToPos(inputElement,2);
//		var input = $("input:first");
//		input.val(input.val());
//		inputElement.val(inputElement.val());
//		input.focus();
//		input.value = input.value;

	}
	
	function onInputBlur() {
		isFocused = false;
		openLookupButtonElement.removeClass("lightHover");
		performSearch();
	}
	
	function performSearch() {
		var newCode = $.trim(inputElement.val());
		if (newCode != currentData.selectedEntityCode) {
			currentData.selectedEntityCode = $.trim(inputElement.val());
			currentData.selectedEntityValue = null;
			currentData.value = null;
			if (currentData.selectedEntityCode == "") {
				if (hasListeners) {
					loadingElement.show();
					mainController.callEvent("search", elementPath, null, null, null);
				} else {
					currentData.isError = false;
					updateData();
					element.removeClass("error");
				}
			} else {
				loadingElement.show();
				mainController.callEvent("search", elementPath, null, null, null);
			}
		} else {
			updateData();
		}
	}
	
	
	function openLookup() {
		if (! openLookupButtonElement.hasClass("enabled")) {
			return;
		}
		var url = _this.options.viewName+".html";
		if (currentData.contextEntityId) {
			var params = new Object();
			params["window.grid.belongsToEntityId"] = currentData.contextEntityId;
			url += "?context="+JSON.stringify(params);
	}		
		lookupWindow = mainController.openPopup(url, _this, "lookup");
	}
	
	this.onPopupInit = function() {
		var grid = lookupWindow.getComponent("window.grid");
		grid.setLinkListener(this);
		if (currentData.selectedEntityCode) {
			grid.setFilterState("lookupCode", currentData.selectedEntityCode);	
		}
		lookupWindow.init();
	}
	
	this.onPopupClose = function() {
		lookupWindow = null;
	}
	
	this.onGridLinkClicked = function(entityId) {
		var grid = lookupWindow.getComponent("window.grid");
		var lookupData = grid.getLookupData(entityId);
		currentData.value = lookupData.entityId;
		currentData.selectedEntityValue = lookupData.lookupValue;
		currentData.selectedEntityCode = lookupData.lookupCode;
		currentData.isError = false;
		updateData();
		if (hasListeners) {
			mainController.callEvent("search", elementPath, null, null, null);
		}
		mainController.closePopup();
	}
	
	this.updateSize = function(_width, _height) {
		var height = _height ? _height-10 : 40;
		this.input.parent().parent().parent().parent().parent().height(height);
	}
	
	constructor(this);
}