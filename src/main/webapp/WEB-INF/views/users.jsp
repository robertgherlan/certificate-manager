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
				<h1>Users</h1>
				<ol class="breadcrumb">
					<li><i class="fa fa-dashboard"></i> Home</li>
					<li class="active"><a href="#">Users</a></li>
				</ol>
			</section>

			<!-- Main content -->
			<section class="content">

				<!-- Default box -->
				<div class="box">
					<div class="box-header with-border">
						<h3 class="box-title">Users</h3>
						<div class="box-tools">
							<form method="get"
								action="${pageContext.request.contextPath}/users/search">
								<div class="input-group input-group-sm" style="width: 250px;">
									<input type="text" name="query" value="${param.query}"
										class="form-control pull-right"
										placeholder="Find users by email or username">
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
										Users founded by: <span class="text-aqua">${param.query}</span>
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
									No user was found.
								</div>
							</c:when>
							<c:otherwise>
								<div class="table-responsive no-padding">
									<table class="table table-hover">
										<tbody>
											<tr>
												<th>(<c:out value="${users.totalElements}" />) First
													name
												</th>
												<th>Last name</th>
												<th>User name</th>
												<th>Email</th>

												<th>Action</th>
											</tr>
											<c:forEach items="${users.content}" var="user">
												<tr>
													<td><c:out value="${user.firstname}" /></td>
													<td><c:out value="${user.lastname}" /></td>
													<td><c:out value="${user.username}" /></td>
													<td><c:out value="${user.email}" /></td>
													<td>
														<div class="btn-group">
															<c:if
																test="${user.validEmail eq true and user.enabled eq true}">
																<form method="POST"
																	action="${app}/users/disable_user/${user.id}">
																	<input type="hidden" name="${_csrf.parameterName}"
																		value="${_csrf.token}" />
																	<button type="submit" class="btn btn-danger">Disable
																		user</button>
																</form>
															</c:if>
															<c:if
																test="${user.validEmail eq true and user.enabled eq false}">
																<form method="POST"
																	action="${app}/users/enable_user/${user.id}">
																	<input type="hidden" name="${_csrf.parameterName}"
																		value="${_csrf.token}" />
																	<button type="submit" class="btn btn-info">Enable
																		user</button>
																</form>
															</c:if>
															<c:if test="${user.validEmail eq false}">
																<form method="POST"
																	action="${app}/users/resend_validation_email/${user.id}">
																	<input type="hidden" name="${_csrf.parameterName}"
																		value="${_csrf.token}" />
																	<button type="submit" class="btn btn-danger">Resend
																		validation email</button>
																</form>
															</c:if>
															<form method="DELETE" action="${app}/users/${user.id}">
																<input type="hidden" name="${_csrf.parameterName}"
																	value="${_csrf.token}" />
																<button type="submit" class="btn btn-danger">Delete
																	user account</button>
															</form>
														</div>
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
