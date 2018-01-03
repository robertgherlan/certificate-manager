<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
<title><spring:message code="title" /> Certificate retriever</title>
<!-- Tell the browser to be responsive to screen width -->
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">

<%@ include file="include/script_files.jsp"%>

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
  <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
  <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
  <![endif]-->

<c:set var="app" value="${app }" />
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
				<h1>Certificate retriever</h1>
				<ol class="breadcrumb">
					<li><i class="fa fa-home"></i> <a href="${app}/">Home</a></li>
					<li class="active"><a href="#">Certificate retriever</a></li>
				</ol>
			</section>

			<!-- Main content -->
			<section class="content">
				<!-- Default box -->
				<div class="box">
					<div class="box-header with-border">
						<h3 class="box-title">Certificate retriever</h3>

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
						<form id="retrieve-certificate-form"
							action="${app}/retrieve_certificate">
							<div class="box-body">
								<div class="row">
									<div class="form-group col-xs-3">
										<label><input type="radio" name="type"
											checked="checked" value="byURL" /> URL(HTTPS): </label>
									</div>
									<div class="form-group col-xs-9">
										<input type="text" name="URL" class="form-control" />
									</div>
								</div>
								<div class="row">
									<div class="form-group col-xs-3">
										<label><input type="radio" name="type" value="byHost" />
											Host: </label>
									</div>
									<div class="form-group col-xs-6">
										<input type="text" name="host" disabled="disabled"
											class="form-control" />
									</div>
									<div class="form-group col-xs-3">
										<input type="text" name="port" disabled="disabled"
											class="form-control" />
									</div>
								</div>
							</div>
							<!-- /.box-body -->

							<div class="box-footer">
								<button type="button" class="btn btn-primary"
									id="retrieve-certificate-button">Retrieve
									certificate(s)</button>
							</div>
						</form>

						<br />
						<div id="extracted-certificates" class="col-md-12">
							<div id="loader"></div>
						</div>
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
