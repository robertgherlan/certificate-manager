<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<header class="main-header">
	<!-- Logo -->
	<a href="${pageContext.request.contextPath}/" class="logo"> <!-- mini logo for sidebar mini 50x50 pixels -->
		<span class="logo-mini">CM</span> <!-- logo for regular state and mobile devices -->
		<span class="logo-lg"><spring:message code="title" /></span>
	</a>
	<!-- Header Navbar: style can be found in header.less -->
	<nav class="navbar navbar-static-top">
		<!-- Sidebar toggle button-->
		<a href="#" class="sidebar-toggle" data-toggle="offcanvas"
			role="button"> <span class="sr-only">Toggle navigation</span> <span
			class="icon-bar"></span> <span class="icon-bar"></span> <span
			class="icon-bar"></span>
		</a>

		<div class="navbar-custom-menu">
			<ul class="nav navbar-nav">
				<!-- User Account: style can be found in dropdown.less -->
				<li class="dropdown user user-menu"><a href="#"
					class="dropdown-toggle" data-toggle="dropdown"> <img
						src="${pageContext.request.contextPath}/resources/admin/img/user2-160x160.jpg"
						class="user-image" alt="User Image"> <span class="hidden-xs"><security:authorize
								access="isAuthenticated()">
								<security:authentication property="principal.username" />
							</security:authorize></span>
				</a>
					<ul class="dropdown-menu">
						<!-- User image -->
						<li class="user-header">
							<p>
								<security:authorize access="isAuthenticated()">
									<security:authentication property="principal.username" />
								</security:authorize>
							</p>
						</li>
						<!-- Menu Footer-->
						<li class="user-footer">
							<div class="pull-right">

								<form method="post"
									action="${pageContext.request.contextPath}/logout">
									<input type="hidden" name="${_csrf.parameterName}"
										value="${_csrf.token}" />
									<button type="submit" class="btn btn-default btn-flat">Logout</button>
								</form>
							</div>
						</li>
					</ul></li>
			</ul>
		</div>
	</nav>
</header>