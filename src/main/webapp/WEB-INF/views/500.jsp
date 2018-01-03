<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title><spring:message code="title" /> | Oops! Something went
	wrong</title>
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
				<h1>Oops! Something went wrong.</h1>
				<ol class="breadcrumb">
					<li class="active"><i class="fa fa-home"></i> <a
						href="${pageContext.request.contextPath}/">Home</a></li>
				</ol>
			</section>

			<!-- Main content -->
			<section class="content">

				<div class="error-page">
					<h2 class="headline text-red">500</h2>

					<div class="error-content">
						<h3>
							<i class="fa fa-warning text-red"></i> Oops! Something went
							wrong.
						</h3>

						<c:if test="${not empty error}">
							<h4 class="text-red">${error}</h4>
						</c:if>

						<p>
							We will work on fixing that right away. Meanwhile, you may <a
								href="${pageContext.request.contextPath}/">return to
								home page</a> or try using the search form.
						</p>

						<form class="search-form">
							<div class="input-group">
								<input type="text" name="search" class="form-control"
									placeholder="Search">

								<div class="input-group-btn">
									<button type="submit" name="submit"
										class="btn btn-danger btn-flat">
										<i class="fa fa-search"></i>
									</button>
								</div>
							</div>
							<!-- /.input-group -->
						</form>
					</div>
				</div>

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
