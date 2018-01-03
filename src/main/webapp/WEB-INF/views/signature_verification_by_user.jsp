<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title><spring:message code="title" /> Signature verification
	by user</title>
<!-- Tell the browser to be responsive to screen width -->
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">
<style type="text/css">
p .help-block {
	color: red;
}
</style>

<%@ include file="include/script_files.jsp"%>

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
  <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
  <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
  <![endif]-->

<c:set var="app" value="${pageContext.request.contextPath}" />
</head>
<body class="hold-transition skin-blue sidebar-mini">
	<!-- Site wrapper -->
	<div class="wrapper">

		<!--START HEADER =============================================== -->
		<%@ include file="include/header.jsp"%>
		<!--END HEADER =============================================== -->

		<!--START SIDEBAR =============================================== -->
		<%@ include file="include/sidebar.jsp"%>
		<!--END SIDEBAR =============================================== -->


		<!--START CONTENT =============================================== -->
		<div class="content-wrapper">
			<!-- Content Header (Page header) -->
			<section class="content-header">
				<h1>Signature verification by user</h1>
				<ol class="breadcrumb">
					<li><i class="fa fa-home"></i> <a href="${app}/">Home</a></li>
					<li class="active"><a href="#">Signature verification by
							user</a></li>
				</ol>
			</section>

			<!-- Main content -->
			<section class="content">

				<!-- Default box -->
				<div class="box">
					<div class="box-header with-border">
						<h3 class="box-title">Signature verification by user</h3>
						<div class="box-tools pull-right">
							<button type="button" class="btn btn-box-tool"
								data-widget="collapse" data-toggle="tooltip" title="Collapse">
								<i class="fa fa-minus"></i>
							</button>
							<button type="button" class="btn btn-box-tool"
								data-widget="remove" data-toggle="tooltip" title="Remove">
								<i class="fa fa-times"></i>
							</button>
						</div>
					</div>
					<div class="box-body">
						<c:if test="${success == true}">
							<div class="alert alert-success alert-dismissible">
								<button type="button" class="close" data-dismiss="alert"
									aria-hidden="true">×</button>
								<h4>
									<i class="icon fa fa-check"></i> Success!
								</h4>
								Your signature was successfully verified!
							</div>
						</c:if>
						<c:if test="${success == false}">
							<div class="alert alert-warning alert-dismissible">
								<button type="button" class="close" data-dismiss="alert"
									aria-hidden="true">×</button>
								<h4>
									<i class="icon fa fa-ban"></i> Warning!
								</h4>
								The signature was not verified.
							</div>
						</c:if>

						<form id="sign_text" method="POST"
							action="${app}/signature_verification_by_user?${_csrf.parameterName}=${_csrf.token}"
							enctype="multipart/form-data">
							<div class="box-body">
								<div class="form-group">
									<label>Signed document: </label> <input type="file"
										name="signedDocument" class="form-control" />
								</div>
								<div class="form-group">
									<label>User: </label> <select name="user" class="form-control">
										<c:forEach items="${users}" var="user">
											<option value="${user.id}">${user.firstname}
												${user.lastname}</option>
										</c:forEach>
									</select>
								</div>
							</div>
							<!-- /.box-body -->
							<input type="hidden" name="${_csrf.parameterName}"
								value="${_csrf.token}" />

							<div class="box-footer">
								<button type="submit" class="btn btn-primary">Check</button>
							</div>
						</form>
					</div>
					<!-- /.box-body -->
				</div>
				<!-- /.box -->

			</section>
			<!-- /.content -->
		</div>
		<!--END CONTENT=============================================== -->

		<!--START FOOTER=============================================== -->
		<%@ include file="include/footer.jsp"%>
		<!--END FOOTER=============================================== -->
	</div>
	<!-- ./wrapper -->

</body>
</html>
