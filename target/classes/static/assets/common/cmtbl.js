var CMTBL = {
	pageLength : 10,
	lengthMenu : [ 10, 20, 30 ],
	dom:
       "<'row'<'col-sm-12 table-responsive'tr>>"
	   + "<'row'<'col-sm-12 col-md-12 d-flex justify-content-center'p>>",
    checkBoxDom: '<div class="form-check form-check-sm form-check-custom d-inline-flex"> <input class="form-check-input" type="checkbox" value=""></div>',
	fnc_renderIndex: function(meta, isAsc) {
        var serverSide = meta.settings.oInit.serverSide;
        if(serverSide) {
            return this.fnc_renderIndexLazy(meta, isAsc);
        }
        return this.fnc_renderIndexPre(meta, isAsc);
    },
    fnc_renderIndexPre: function(meta, isAsc) {
        var ascIndex = meta.row+1;
        if(isAsc) {
            return ascIndex;
        }
        var total = meta.settings.json.recordsTotal;
		var descIndex = total-(ascIndex-1);
        return descIndex;
	},
	fnc_renderIndexLazy: function(meta, isAsc) {
		var rowStartIndex = meta.settings.oAjaxData.start;
		var rowPageIndex = (meta.row);
		var ascIndex = (rowStartIndex + rowPageIndex)+1;
        if(isAsc) {
            return ascIndex;
        }
        var total = meta.settings._iRecordsTotal;
		var descIndex = total-(ascIndex-1);
		return descIndex;
	},
	fnc_createPageRequest: function(data,settings) {
		// page
		var pageSize = this.pageLength;
		var page = (data.start / pageSize) + 1;
		// sort
		var sortSqls = data.order.map(function(e) {
		    var columnName = data.columns[e.column].data;
		    var sortType = e.dir;
            return columnName+" "+sortType;
		});
		var sortSql = sortSqls.join(", ");
		// page request
		var pageRequest = {
			draw : data.draw,
			page : page,
			pageSize : pageSize,
			sortSql : sortSql
		}
		return pageRequest;
	},
    fnc_getCheckedRows: function(tableId, chkColIdxPrm) {
        chkColIdx = 1;
        if(chkColIdxPrm) {
            chkColIdx = chkColIdxPrm;
        }
        var aoData = $('#'+tableId).DataTable().data().context[0].aoData;
        if(aoData && aoData.length > 0) {
            var checkedRows = [];
            aoData.forEach(function(rowData) {
                var chk = $(rowData.anCells).find('input[type="checkbox"]')[chkColIdx-1];
                if(chk.checked) {
                    checkedRows.push(rowData._aData);
                }
            });
            return checkedRows;
        }
        return [];
    },
    fnc_checkRowsPreload: function(tableId, colIdxPrm) {
        var colIdx = 1;
        if(colIdxPrm) {
            colIdx = colIdxPrm;
        }
        var chkAll = $($('#'+tableId+' thead tr th:nth-child('+colIdx+')').find('input[type="checkbox"]')[0]);
        var isCheckAll = chkAll.prop('checked');

        var chkRows = [];
        var aoData = $('#'+tableId).DataTable().data().context[0].aoData;
        if(aoData && aoData.length > 0) {
            aoData.forEach(function(rowData) {
                var chk = $(rowData.anCells).find('input[type="checkbox"]')[colIdx-1];
                chkRows.push(chk)
            })

            if(isCheckAll) {
                chkRows.forEach(function(chk) {
                    chk.checked = true;
                });
            }

            chkAll.click(function(e) {
                var isChecked = $(e.target).prop('checked');
                chkRows.forEach(function(chk) {
                    chk.checked = isChecked;
                });
            });
            $(chkRows).click(function(e) {
                var checkedLength = chkRows.filter(e1 => e1.checked).length;
                chkAll[0].checked = (checkedLength === chkRows.length);
            });
        }
    },
    fnc_checkRowsLazyload: function(tableId, colIdxPrm) {
        var colIdx = 1;
        if(colIdxPrm) {
            colIdx = colIdxPrm;
        }
        var chkAll = $($('#'+tableId+' thead tr th:nth-child('+colIdx+')').find('input[type="checkbox"]')[0]);
        chkAll[0].checked = false;
        var chkRows = $('#'+tableId+' tbody tr td:nth-child('+colIdx+')').find('input[type="checkbox"]');
        chkAll.click(function(e) {
            var isChecked = $(e.target).prop('checked');
            chkRows.each(function(index) {
                chkRows[index].checked = isChecked;
            });
        });
        chkRows.click(function(e) {
            var checkedLength = chkRows.filter(e1 => chkRows[e1].checked).length;
            chkAll[0].checked = (checkedLength === chkRows.length);
        });
    },
    fnc_lazyLoad: function(tableId, columns, pagingUrl, loadSearchReq, drawCallback, initComplete) {
        var searchReq = {};
        if(loadSearchReq) {
            searchReq = loadSearchReq();
        }

        // init datatable
        $('#'+tableId).DataTable({
            responsive : false,
            processing : true,
            serverSide : true,
            paging : true,
            lengthMenu : CMTBL.lengthMenu,
            lengthChange : true,
            pageLength : CMTBL.pageLength,
            order : [],
            stateSave : false,
            dom : CMTBL.dom,
            language: {
                url: "/assets/common/datatable_language_ko.json"
            },
            columns : columns,
            ajax : function(data, callback, settings) {
                // page request
                var pageRequest = CMTBL.fnc_createPageRequest(data,settings);
                if (!searchReq) {
                    searchReq = {};
                }
                var requestPrm = Object.assign(pageRequest,searchReq);
                $.get(
                    pagingUrl,
                    requestPrm,
                    function(response) {
                        callback({
                            draw : response.draw,
                            recordsTotal : response.total,
                            recordsFiltered : response.total,
                            data : response.content
                        });
                    });
            },
            "drawCallback" : function(settings) {
                KTMenu.createInstances();
                drawCallback&&drawCallback(settings);
            },
            "initComplete": function(settings, json) {
                initComplete&&initComplete(settings, json);
            }
        });

        // regist trigger reload
        $('#'+tableId).on('CMTBL_reload', function(e, arg){
            if(loadSearchReq) {
                searchReq = loadSearchReq();
            };
            $('#'+tableId).DataTable().draw();
        });
    },
    fnc_preLoad: function(tableId, columns, listUrl, loadSearchReq, drawCallback, initComplete) {
        var searchReq = {};
        if(loadSearchReq) {
            searchReq = loadSearchReq();
        }
        // init datatable
        $('#'+tableId).DataTable({
            destroy: true,
            responsive : false,
            processing : true,
            serverSide : false,
            paging : true,
            lengthMenu : CMTBL.lengthMenu,
            lengthChange : true,
            pageLength : CMTBL.pageLength,
            order : [],
            stateSave : false,
            dom : CMTBL.dom,
            language: {
                url: "/assets/common/datatable_language_ko.json"
            },
            columns : columns,
            ajax : function(data, callback, settings) {
                if (!searchReq) {
                    searchReq = {};
                }
                $.get(
                    listUrl,
                    searchReq,
                    function(response) {
                        callback({
                            recordsTotal : response.length,
                            data : response
                        });
                    });
            },
            "drawCallback" : function(settings) {
                KTMenu.createInstances();
                drawCallback&&drawCallback(settings);
            },
            "initComplete": function(settings, json) {
                initComplete&&initComplete(settings, json);
            }
        });

        // regist trigger reload
        $('#'+tableId).on('CMTBL_reload', function(e, arg){
            if(loadSearchReq) {
                searchReq = loadSearchReq();
            };
            $('#'+tableId).DataTable().ajax.reload();
        });
    },
    fnc_dummyLoad: function(tableId, columns, dummyData, drawCallback, initComplete) {
        $('#'+tableId).DataTable({
            destroy: true,
            responsive : false,
            processing : true,
            serverSide : false,
            paging : true,
            lengthMenu : CMTBL.lengthMenu,
            lengthChange : true,
            pageLength : CMTBL.pageLength,
            order : [],
            stateSave : false,
            dom : CMTBL.dom,
            data: dummyData,
            language: {
                url: "/assets/common/datatable_language_ko.json"
            },
            columns : columns,
            "drawCallback" : function(settings) {
                KTMenu.createInstances();
                drawCallback&&drawCallback(settings);
            },
            "initComplete": function(settings, json) {
                initComplete&&initComplete(settings, json);
            }
        });
    },
    fnc_addRow: function(tableId, rowObj, isAddFirst) {
        var table = $('#'+tableId).DataTable();
        if(isAddFirst) {
            var rowData = table.rows().data().toArray();
            rowData.unshift(rowObj);
            table.clear().rows.add(rowData).draw();
            return;
        }
        table.row.add(rowObj).draw();
    }
}