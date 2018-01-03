<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>Register</title>
<!-- Tell the browser to be responsive to screen width -->
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">

<%@ include file="include/script_files.jsp"%>

<script src="https://www.google.com/recaptcha/api.js" async defer></script>

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
  <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
  <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
  <![endif]-->
</head>
<body class="hold-transition login-page">
	<div class="login-box">
		<div class="login-logo">
			<a href="${pageContext.request.contextPath}/"><b><spring:message
						code="title" /></b></a>
		</div>
		<!-- /.login-logo -->
		<div class="login-box-body">
			<%@ include file="include/captchaError.jsp"%>

			<c:if test="${success eq true}">
				<div class="alert alert-success alert-dismissible">
					<button type="button" class="close" data-dismiss="alert"
						aria-hidden="true">×</button>
					<h4>
						<i class="icon fa fa-check"></i> Success!
					</h4>
					Your account was registered successfully.
				</div>
			</c:if>

			<c:if test="${param.register == false}">
				<div class="alert alert-danger alert-dismissible">
					<button type="button" class="close" data-dismiss="alert"
						aria-hidden="true">×</button>
					<h4>
						<i class="icon fa fa-warning"></i> Warning!
					</h4>
					Error create your account.
				</div>
			</c:if>

			<p class="login-box-msg">Register page.</p>

			<form:form commandName="registerUser" method="post">
				<div class="form-group has-feedback">
					<form:input cssClass="form-control" path="firstName"
						placeholder="Firts name" />
					<span class="glyphicon glyphicon-envelope form-control-feedback"></span>
					<p class="text-red">
						<form:errors path="firstName" />
					</p>
				</div>
				<div class="form-group has-feedback">
					<form:input cssClass="form-control" path="lastName"
						placeholder="Last name" />
					<span class="glyphicon glyphicon-envelope form-control-feedback"></span>
					<p class="text-red">
						<form:errors path="lastName" />
					</p>
				</div>
				<div class="form-group has-feedback">
					<form:input cssClass="form-control" path="username"
						placeholder="Username" />
					<span class="glyphicon glyphicon-envelope form-control-feedback"></span>
					<p class="text-red">
						<form:errors path="username" />
					</p>
				</div>
				<div class="form-group has-feedback">
					<form:input cssClass="form-control" path="email"
						placeholder="Email" />
					<span class="glyphicon glyphicon-envelope form-control-feedback"></span>
					<p class="text-red">
						<form:errors path="email" />
					</p>
				</div>
				<div class="form-group has-feedback">
					<form:input cssClass="form-control" path="confirmEmail"
						placeholder="Confirmn email" />
					<span class="glyphicon glyphicon-envelope form-control-feedback"></span>
					<p class="text-red">
						<form:errors path="confirmEmail" />
					</p>
				</div>
				<div class="form-group has-feedback">
					<form:password cssClass="form-control" path="password"
						placeholder="Password" />
					<span class="glyphicon glyphicon-envelope form-control-feedback"></span>
					<p class="text-red">
						<form:errors path="password" />
					</p>
				</div>
				<div class="form-group has-feedback">
					<form:password cssClass="form-control" path="confirmPassword"
						placeholder="Confirm password" />
					<span class="glyphicon glyphicon-envelope form-control-feedback"></span>
					<p class="text-red">
						<form:errors path="confirmPassword" />
					</p>
				</div>
				<div class="form-group has-feedback">
					<div class="g-recaptcha" data-sitekey="${captchaSiteKey}"></div>
				</div>

				<div class="row">
					<div class="col-xs-8">
						<a href="${pageContext.request.contextPath}/login">Login page</a>
					</div>
					<!-- /.col -->
					<div class="col-xs-4">
						<button type="submit" class="btn btn-primary btn-block btn-flat">Register</button>
					</div>
					<!-- /.col -->
				</div>
				<input type="hidden" name="${_csrf.parameterName}"
					value="${_csrf.token}" />
			</form:form>
		</div>
		<!-- /.login-box-body -->
	</div>
</body>
</html>
