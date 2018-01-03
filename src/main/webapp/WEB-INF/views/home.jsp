<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title><spring:message code="title" /></title>
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
				<h1>Home</h1>
				<ol class="breadcrumb">
					<li class="active"><i class="fa fa-home"></i> Home</li>
				</ol>
			</section>

			<!-- Main content -->
			<section class="content">

				<!-- Default box -->
				<div class="box">
					<div class="box-header with-border">
						<h3 class="box-title">Home</h3>

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
						<ul>
							<li class="text-aqua">
								<h3>Flexible and reliable system for digital certificate
									issuance and lifecycle management</h3>
							</li>
							<li class="text-aqua"><h3>Centralizes and made
									management of cryptographic keys and digital certificates</h3></li>
							<li class="text-aqua"><h3>Know when your website's SSL
									certificates will expire</h3></li>
							<li class="text-aqua"><h3>More details about your
									certificates</h3></li>
							<li class="text-aqua"><h3>Our tool offer support for
									retrieving certificates details from a specific URL</h3></li>
						</ul>
					</div>
					<!-- /.box-body -->
					<div class="box-footer">
						<blockquote>
							<p>Securing a computer system has traditionally been a battle
								of wits: the penetrator tries to find the holes, and the
								designer tries to close them.</p>
						</blockquote>
					</div>
					<!-- /.box-footer-->
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
