toastr.options = {
    "closeButton": true,
    "debug": false,
    "newestOnTop": false,
    "progressBar": true,
    "positionClass": "toast-top-right",
    "preventDuplicates": true,
    "onclick": null,
    "showDuration": "300",
    "hideDuration": "1000",
    "timeOut": "1000",
    "extendedTimeOut": "1000",
    "showEasing": "swing",
    "hideEasing": "linear",
    "showMethod": "fadeIn",
    "hideMethod": "fadeOut"
}

var ALERT = {
	noti_success : function(message) {
	    toastr["success"](message);
	},
	noti_error : function(message) {
	    toastr["error"](message);
	},
	noti_warning : function(message) {
	    toastr["warning"](message);
	},
	pop_confirm : function(message, ok, cancel) {
		const Pop = Swal.mixin({
            toast: true,
            position: 'center',
            showCancelButton: true,
            confirmButtonText: ok?ok:'확인',
            cancelButtonText: cancel?cancel:'취소',
            timerProgressBar: true,
            customClass: {
                confirmButton: 'btn btn-primary btn-sm mt-4',
                cancelButton: 'btn btn-secondary btn-sm mr-2 mt-4',
                actions: 'd-flex justify-content-center mt-0',
                icon: 'd-none',
                title: 'fs-1 border-bottom mb-0 mt-12 mx-2 pb-15 text-center'
            },
            buttonsStyling: false,
            width:450,
            showClass: {
                popup: 'animate__animated animate__fadeInDown',
                backdrop: 'swal2-noanimation'
            },
            hideClass: {
                popup: 'animate__animated animate__fadeOutUp'
              },
		  didOpen: (toast) => {
		    toast.addEventListener('mouseenter', Swal.stopTimer)
		    toast.addEventListener('mouseleave', Swal.resumeTimer)
		  }
		})
		return Pop.fire({
		  icon: 'warning',
		  title: message
		});
	},
    pop_message : function(message, ok) {
        const Pop = Swal.mixin({
            toast: true,
            position: 'center',
            showCancelButton: false,
            confirmButtonText: ok?ok:'확인',
            timerProgressBar: true,
            customClass: {
                confirmButton: 'btn btn-primary btn-sm mt-4',
                actions: 'd-flex justify-content-center mt-0',
                icon: 'd-none',
                title: 'fs-1 border-bottom mb-0 mt-12 mx-2 pb-15 text-center'
            },
            buttonsStyling: false,
            width:450,
            showClass: {
                popup: 'animate__animated animate__fadeInDown',
                backdrop: 'swal2-noanimation'
            },
            hideClass: {
                popup: 'animate__animated animate__fadeOutUp'
              },
          didOpen: (toast) => {
            toast.addEventListener('mouseenter', Swal.stopTimer)
            toast.addEventListener('mouseleave', Swal.resumeTimer)
          }
        })
        return Pop.fire({
          icon: 'warning',
          title: message
        });
    }
}