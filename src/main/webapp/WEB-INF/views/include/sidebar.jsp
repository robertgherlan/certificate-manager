<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="app" value="${pageContext.request.contextPath}" />
<!-- Left side column. contains the sidebar -->
<aside class="main-sidebar">
	<!-- sidebar: style can be found in sidebar.less -->
	<section class="sidebar">
		<!-- Sidebar user panel -->
		<div class="user-panel">
			<div class="pull-left image">
				<img src="${app}/resources/admin/img/user2-160x160.jpg"
					class="img-circle" alt="User Image">
			</div>
			<div class="pull-left info">
				<p>
					<security:authorize access="isAuthenticated()">
						<security:authentication property="principal.username" />
					</security:authorize>
				</p>
				<a href="#"><i class="fa fa-circle text-success"></i> Online</a>
			</div>
		</div>
		<!-- search form -->
		<form action="${app}/certificates" method="get" class="sidebar-form">
			<div class="input-group">
				<input type="text" name="query" class="form-control"
					placeholder="Search..." value="${param.query}"> <span
					class="input-group-btn">
					<button type="submit" id="search-btn" class="btn btn-flat">
						<i class="fa fa-search"></i>
					</button>
				</span>
			</div>
		</form>
		<!-- /.search form -->
		<!-- sidebar menu: : style can be found in sidebar.less -->
		<ul class="sidebar-menu" id="main-menu">
			<li class="header">Navigation</li>
			<li id="home-page"><a href="${app}/"> <i class="fa fa-home"></i>
					<span>Home</span>
			</a></li>
			<li id="sign-document-page"><a href="${app}/sign_document">
					<i class="fa fa-file"></i> <span>Sign document</span>
			</a></li>
			<li class="treeview"><a href="#"><i class="fa fa-edit"></i>
					<span>Signature verification</span> <span
					class="pull-right-container"> <i
						class="fa fa-angle-left pull-right"></i>
				</span> </a>
				<ul class="treeview-menu">
					<li id="documents-page"><a
						href="${app}/signature_verification_by_signature"> <i
							class="fa fa-file"></i> <span>By user signature</span>
					</a></li>
					<li id="my-documents-page"><a
						href="${app}/signature_verification_by_user"> <i
							class="fa fa-user"></i> <span>By user</span>
					</a></li>
				</ul></li>
			<li class="treeview"><a href="${app}/documents"><i
					class="fa fa-edit"></i> <span>Documents</span> <span
					class="pull-right-container"> <i
						class="fa fa-angle-left pull-right"></i>
				</span> </a>
				<ul class="treeview-menu">
					<li id="documents-page"><a href="${app}/documents"> <i
							class="fa fa-check"></i> <span>Documents</span>
					</a></li>
					<li id="my-documents-page"><a href="${app}/my_documents">
							<i class="fa fa-file-pdf-o"></i> <span>My documents</span>
					</a></li>
				</ul></li>
			<li class="treeview"><a href="#"><i class="fa  fa-file-text"></i>
					<span>Certificates</span> <span class="pull-right-container">
						<i class="fa fa-angle-left pull-right"></i>
				</span> </a>
				<ul class="treeview-menu">
					<li><a href="${app}/certificates"><i class="fa fa-list-ul"></i>
							Certificates</a></li>
					<li><a href="${app}/generate_certificate"><i
							class="fa fa-plus-square"></i> Generate certificate</a></li>
					<li><a href="${app}/import_certificate"><i
							class="fa fa-plus-square"></i> Import certificate</a></li>
					<li><a href="${app}/upload_certificate"><i
							class="fa fa-upload"></i> Upload certificate</a></li>
				</ul></li>
			<security:authorize access="hasAuthority('ADMIN')">
				<li class="treeview"><a href="#"><i
						class="fa  fa-file-text"></i> <span>Users</span> <span
						class="pull-right-container"> <i
							class="fa fa-angle-left pull-right"></i>
					</span> </a>
					<ul class="treeview-menu">
						<li><a href="${app}/users"><i class="fa fa-list-ul"></i>
								All users</a></li>
						<li><a href="${app}/users/requests"><i
								class="fa fa-plus-square"></i> User requests</a></li>
						<li><a href="${app}/users/accepted"><i
								class="fa fa-upload"></i> Accepted users</a></li>
						<li><a href="${app}/users/invalidated_emails"><i
								class="fa fa-upload"></i> Invalidated emails</a></li>
					</ul></li>
			</security:authorize>
			<li class="treeview"><a href="#"><i class="fa fa-edit"></i>
					<span>Tools</span> <span class="pull-right-container"> <i
						class="fa fa-angle-left pull-right"></i>
				</span> </a>
				<ul class="treeview-menu">
					<!--li><a href="${app}/sign_document"><i
							class="fa fa-pencil-square-o"></i> Sign documents</a></li-->
					<li><a href="${app}/retrieve_certificates"><i
							class="fa fa-upload"></i> Certificate viewer</a></li>
				</ul></li>
		</ul>
	</section>
	<!-- /.sidebar -->
</aside>
<div id="wait"
	style="display: none; width: 69px; height: 89px; position: absolute; top: 50%; left: 50%; padding: 2px;">
	<img src='${app}/resources/admin/img/demo_wait.gif' width="128"
		height="128" /><br>Loading...
</div>