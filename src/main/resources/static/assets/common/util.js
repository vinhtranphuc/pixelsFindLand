var UTL = {
	fnc_getDateFormat: function(date,format) {
		return $.format.date(date, format)
	},
	dateFormat: 'YYYY.MM.DD',
	daysOfWeek: ["일", "월", "화", "수", "목", "금", "토"],
	monthNames: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
    fnc_registDateRangePicker: function() {
        // date range picker
        $("input[data-ranger-picker='date-ranger-picker']").on('apply.daterangepicker', function(ev, picker) {
            $(this).val(picker.startDate.format(UTL.dateFormat));
        });
        $("input[data-ranger-picker='date-ranger-picker']").on('cancel.daterangepicker', function(ev, picker) {
            $(this).val("");
        });
        $("input[data-ranger-picker='date-ranger-picker']").on('change',function(ev, picker) {
            if($(this).data('daterangepicker')) {
                var format = $(this).data('daterangepicker').locale.format;
                var val = $(this).val();
                var check = moment(val, format, true);
                if(!check._isValid) {
                    $(this).val("")
                }
            }
        });
    },
    fnc_getMasterVal: function(type, code) {
        var rs = CM_masterJsonArray.filter(e => (e.type === type && e.code === code));
        if(rs.length < 1) {
            return '';
        }
        return rs[0].value;
    },
    fnc_camelToUnderscore : function(key) {
       return key.replace(/[A-Z]/g, letter => `_${letter.toLowerCase()}`);
    }
}