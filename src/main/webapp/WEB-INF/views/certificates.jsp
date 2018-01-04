<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title><spring:message code="title" /></title>
<c:set var="app" value="${pageContext.request.contextPath}" />
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
				<h1>Certificates</h1>
				<ol class="breadcrumb">
					<li><i class="fa fa-dashboard"></i> Home</li>
					<li class="active"><a href="#"> Certificates</a></li>
				</ol>
			</section>

			<!-- Main content -->
			<section class="content">

				<!-- Default box -->
				<div class="box">
					<div class="box-header with-border">
						<h3 class="box-title">Certificates</h3>
						<div class="box-tools">
							<form method="get"
								action="${pageContext.request.contextPath}/certificates">
								<div class="input-group input-group-sm" style="width: 250px;">
									<input type="text" name="query" value="${param.query}"
										class="form-control pull-right"
										placeholder="Find certificate by subject">
									<div class="input-group-btn">
										<button type="submit" class="btn btn-default">
											<i class="fa fa-search"></i>
										</button>
									</div>
								</div>
							</form>
						</div>
					</div>
					<div class="box-body">
						<c:if test="${param.query != null && not empty param.query}">
							<div class="info-box">
								<span class="info-box-icon bg-aqua"><i
									class="fa fa-search"></i></span>
								<div class="info-box-content">
									<h3 class="widget-user-username">
										Finded certificates by: <span class="text-aqua">${param.query}</span>
									</h3>
								</div>
								<!-- /.info-box-content -->
							</div>
						</c:if>
						<c:if test="${not empty error}">
							<div class="alert alert-warning alert-dismissible">
								<button type="button" class="close" data-dismiss="alert"
									aria-hidden="true">×</button>
								<h4>
									<i class="icon fa fa-ban"></i> Warning!
								</h4>
								${error}
							</div>
						</c:if>
						<c:if test="${param.deleted eq true}">
							<div class="alert alert-success alert-dismissible">
								<button type="button" class="close" data-dismiss="alert"
									aria-hidden="true">×</button>
								<h4>
									<i class="icon fa fa-check"></i> Success!
								</h4>
								The certificate was deleted successfully.
							</div>
						</c:if>
						<c:if test="${param.deleted eq false}">
							<div class="alert alert-warning alert-dismissible">
								<button type="button" class="close" data-dismiss="alert"
									aria-hidden="true">×</button>
								<h4>
									<i class="icon fa fa-warning"></i> Warning!
								</h4>
								The certificate wasn't deleted.
							</div>
						</c:if>
						<c:choose>
							<c:when test="${found == false}">
								<div class="alert alert-warning alert-dismissible">
									<button type="button" class="close" data-dismiss="alert"
										aria-hidden="true">×</button>
									<h4>
										<i class="icon fa fa-warning"></i> Warning!
									</h4>
									No certificate was found for this account. To generate one
									please access the following link: <a class="btn btn-primary"
										href="${app}/generate_certificate">Generate certificate</a>
								</div>
							</c:when>
							<c:otherwise>
								<div class="table-responsive no-padding">
									<table class="table table-hover">
										<tbody>
											<tr>
												<th>Issuer</th>
												<th>Creation date</th>
												<th>Public key</th>
												<th>Private key</th>
												<th>Details</th>
												<th>Generate CSR</th>
											</tr>
											<c:forEach items="${keystores}" var="keyStore">
												<tr>
													<td><c:out value="${keyStore.certificateSubject}" /></td>
													<td><c:out value="${keyStore.creationDate}" /></td>
													<td>
														<form method="POST"
															action="${app}/export_certificate/${keyStore.id}">
															<input type="hidden" name="${_csrf.parameterName}"
																value="${_csrf.token}" />
															<button type="submit" class="btn btn-default">Export
																certificate</button>
														</form>
													</td>
													<td>
														<form method="POST"
															action="${app}/export_privateKey/${keyStore.id}">
															<input type="hidden" name="${_csrf.parameterName}"
																value="${_csrf.token}" />
															<button type="submit" class="btn btn-default">Export
																private key</button>
														</form>
													</td>
													<td><a class="btn btn-default"
														href="${app}/certificate_details/${keyStore.id}"><i
															class="fa fa-eye"></i>View</a></td>
													<td><form method="POST"
															action="${app}/generate_csr/${keyStore.id}">
															<input type="hidden" name="${_csrf.parameterName}"
																value="${_csrf.token}" />
															<button type="submit" class="btn btn-default">Generate</button>
														</form></td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</c:otherwise>
						</c:choose>
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

	<!-- Modal -->
	<div class="modal fade" id="deleteModal" role="dialog">
		<div class="modal-dialog">
			<form action="${pageContext.request.contextPath}/delete_certificate"
				method="post">
				<!-- Modal content-->
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">&times;</button>
						<h4 class="modal-title">Delete certificate</h4>
					</div>
					<div class="modal-body">
						<p>Are you sure you want to delete this certificate?</p>
						<input type="hidden" name="id" /> <input type="hidden"
							name="${_csrf.parameterName}" value="${_csrf.token}" />
					</div>
					<div class="modal-footer">
						<button type="submit" class="btn btn-danger">Delete</button>
						<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					</div>
				</div>
			</form>
		</div>
	</div>

</body>
</html>
