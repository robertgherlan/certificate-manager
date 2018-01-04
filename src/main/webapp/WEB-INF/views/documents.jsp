<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@  taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="app" value="${pageContext.request.contextPath}" />
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
				<h1>Documents</h1>
				<ol class="breadcrumb">
					<li><i class="fa fa-dashboard"></i> Home</li>
					<li class="active"><a href="#">Documents</a></li>
				</ol>
			</section>

			<!-- Main content -->
			<section class="content">

				<!-- Default box -->
				<div class="box">
					<div class="box-header with-border">
						<h3 class="box-title">Documents</h3>
						<div class="box-tools">
							<form method="get"
								action="${pageContext.request.contextPath}/documents/search">
								<div class="input-group input-group-sm" style="width: 250px;">
									<input type="text" name="query" value="${param.query}"
										class="form-control pull-right"
										placeholder="Find document by name">
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
										Documents founded by: <span class="text-aqua">${param.query}</span>
									</h3>
								</div>
								<!-- /.info-box-content -->
							</div>
						</c:if>
						<c:choose>
							<c:when test="${exist == false}">
								<div class="alert alert-warning alert-dismissible">
									<button type="button" class="close" data-dismiss="alert"
										aria-hidden="true">×</button>
									<h4>
										<i class="icon fa fa-warning"></i> Warning!
									</h4>
									No document was found for this account. To add one please
									access the following link: <a class="btn btn-primary"
										href="${app}/sign_document">Sign document</a>
								</div>
							</c:when>
							<c:otherwise>
								<div class="table-responsive no-padding">
									<table class="table table-hover">
										<tbody>
											<tr>
												<th>#</th>
												<th>Name</th>
												<th>Creation date</th>
												<th>User</th>
												<th>Signature</th>
												<th>Certificate id</th>
												<th>Signature</th>
												<th>Document</th>
											</tr>
											<c:forEach items="${documents.content}" var="document">
												<tr>
													<td><c:out value="${document.id}" /></td>
													<td><c:out value="${document.name}" /></td>
													<td><c:out value="${document.creationDate}" /></td>
													<td><c:out value="${document.user.firstname}" /> <c:out
															value="${document.user.lastname}" /></td>
													<td><c:out value="${document.signature.name}" /></td>
													<td><c:out value="${document.keystore.id}" /></td>
													<td>
														<form method="POST"
															action="${app}/documents/download_signature">
															<input type="hidden" name="${_csrf.parameterName}"
																value="${_csrf.token}" /> <input type="hidden"
																name="documentID" value="${document.id}" /> <input
																type="hidden" name="signatureID"
																value="${document.signature.id}" />
															<button type="submit" class="btn btn-primary">Signature</button>
														</form>

													</td>
													<td>
														<form method="POST"
															action="${app}/documents/download_document">
															<input type="hidden" name="${_csrf.parameterName}"
																value="${_csrf.token}" /> <input type="hidden"
																name="documentID" value="${document.id}" /> <input
																type="hidden" name="userID" value="${document.user.id}" />
															<button type="submit" class="btn btn-success">Document</button>
														</form>
													</td>
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
</body>
</html>
