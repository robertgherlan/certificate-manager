<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>Recover password</title>
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

			<%@ include file="include/authenticationError.jsp"%>

			<p class="login-box-msg">Insert a new password.</p>

			<form action="${pageContext.request.contextPath}/recoverPassword"
				method="post">
				<div class="form-group has-feedback">
					<input type="password" class="form-control" name="newPassword"
						placeholder="Insert new password"> <span
						class="glyphicon glyphicon-envelope form-control-feedback"></span>
				</div>

				<div class="form-group has-feedback">
					<input type="password" class="form-control"
						name="retypeNewPassword" placeholder="Retypr password"> <span
						class="glyphicon glyphicon-envelope form-control-feedback"></span>
				</div>

				<input type="hidden" name="recoverToken"
					value="${param.recoverToken}">

				<div class="form-group has-feedback">
					<div class="g-recaptcha" data-sitekey="${captchaSiteKey}"></div>
				</div>

				<div class="row">
					<div class="col-xs-12">
						<button type="submit" class="btn btn-primary btn-block btn-flat">Salveaza</button>
					</div>
				</div>
				<input type="hidden" name="${_csrf.parameterName}"
					value="${_csrf.token}" />
			</form>
			<br /> <a href="./login">Login</a>

		</div>
		<!-- /.login-box-body -->
	</div>
	<!-- /.login-box -->
	<script>
		$(function() {
			$('input').iCheck({
				checkboxClass : 'icheckbox_square-blue',
				radioClass : 'iradio_square-blue',
				increaseArea : '20%' // optional
			});
		});
	</script>
</body>
</html>
