<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:if test="${invalidCaptcha eq true}">
	<div class="alert alert-danger alert-dismissible">
		<button type="button" class="close" data-dismiss="alert"
			aria-hidden="true">×</button>
		<h4>
			<i class="icon fa fa-ban"></i> Warning!
		</h4>
		Invalid captcha.
	</div>
</c:if>
<c:if test="${unvailableCaptcha eq true}">
	<div class="alert alert-danger alert-dismissible">
		<button type="button" class="close" data-dismiss="alert"
			aria-hidden="true">×</button>
		<h4>
			<i class="icon fa fa-ban"></i> Warning!
		</h4>
		Captcha can't be accesed.
	</div>
</c:if>