<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!-- LOGIN -->
<c:if test="${param.error != null}">
	<div class="alert alert-danger alert-dismissible">
		<button type="button" class="close" data-dismiss="alert"
			aria-hidden="true">×</button>
		<h4>
			<i class="icon fa fa-ban"></i> Warning!
		</h4>
		<c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}" />
	</div>

</c:if>

<c:if test="${param.validatedEmail == true}">
	<div class="alert alert-success alert-dismissible">
		<button type="button" class="close" data-dismiss="alert"
			aria-hidden="true">×</button>
		<h4>
			<i class="icon fa fa-check"></i> Success!
		</h4>
		Your email was validate successfully. Now you must wait administrator confirmation email to login in application.
	</div>
</c:if>

<c:if test="${param.validatedEmail == false}">
	<div class="alert alert-danger alert-dismissible">
		<button type="button" class="close" data-dismiss="alert"
			aria-hidden="true">×</button>
		<h4>
			<i class="icon fa fa-warning"></i> Warning!
		</h4>
		Error in your request.
	</div>
</c:if>

<c:if test="${userNotFound eq true}">
	<div class="alert alert-danger alert-dismissible">
		<button type="button" class="close" data-dismiss="alert"
			aria-hidden="true">×</button>
		<h4>
			<i class="icon fa fa-warning"></i> Warning!
		</h4>
		Is not registered any user with provided informations.
	</div>
</c:if>

<c:if test="${param.recoverPassword == true}">
	<div class="alert alert-success alert-dismissible">
		<button type="button" class="close" data-dismiss="alert"
			aria-hidden="true">×</button>
		<h4>
			<i class="icon fa fa-check"></i> Success!
		</h4>
		Your password was successfully changed.
	</div>
</c:if>

<c:if test="${param.logout == true}">
	<div class="alert alert-success alert-dismissible">
		<button type="button" class="close" data-dismiss="alert"
			aria-hidden="true">×</button>
		<h4>
			<i class="icon fa fa-check"></i> Success!
		</h4>
		You have successfully logout.
	</div>
</c:if>



<!-- RECOVER -->
<c:if test="${recover eq true}">
	<div class="alert alert-success alert-dismissible">
		<button type="button" class="close" data-dismiss="alert"
			aria-hidden="true">×</button>
		<h4>
			<i class="icon fa fa-check"></i> Success!
		</h4>
		One email was send to your email for validating your account.
	</div>
</c:if>

<c:if test="${recover eq false}">
	<div class="alert alert-danger alert-dismissible">
		<button type="button" class="close" data-dismiss="alert"
			aria-hidden="true">×</button>
		<h4>
			<i class="icon fa fa-ban"></i> Warning!
		</h4>
		Invalid data in your request.
	</div>
</c:if>
