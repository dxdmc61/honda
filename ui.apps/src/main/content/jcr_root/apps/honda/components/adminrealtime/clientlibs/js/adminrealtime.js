$(document).ready(function () {
    $(document).on("click", "#btn-submit", function () {
		$(".success-message").removeClass("hidden");
		setTimeout(function() {
			$(".success-message").addClass("hidden");
		}, 3000);
});
});