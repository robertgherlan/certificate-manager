<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title><spring:message code="title" /> Generate Certificate</title>
<!-- Tell the browser to be responsive to screen width -->
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">
<style type="text/css">
p .help-block {
	color: red;
}
</style>
<c:set var="app" value="${pageContext.request.contextPath}" />
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
				<h1>Generate certificate</h1>
				<ol class="breadcrumb">
					<li><i class="fa fa-home"></i> <a href="${app}/">Home</a></li>
					<li class="active"><a href="#">Generate certificate</a></li>
				</ol>
			</section>

			<!-- Main content -->
			<section class="content">

				<!-- Default box -->
				<div class="box">
					<div class="box-header with-border">
						<h3 class="box-title">Generate certificate</h3>

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
								The certificate was generated successfully!
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

						<form:form id="generate-certificate-form" method="POST"
							action="${app}/generate_certificate" modelAttribute="certificate">
							<div class="box-body">
								<div class="form-group">
									<label>Common name <span class="text-red">*</span>:
									</label>
									<form:input path="commonName" cssClass="form-control" />
									<p class="text-red">
										<form:errors path="commonName" />
									</p>
								</div>
								<div class="form-group">
									<label>Organization: </label>
									<form:input path="organization" cssClass="form-control" />
									<p class="text-red">
										<form:errors path="organization" />
									</p>
								</div>
								<div class="form-group">
									<label>Organization unit: </label>
									<form:input path="organizationUnit" cssClass="form-control" />
									<p class="text-red">
										<form:errors path="organizationUnit" />
									</p>
								</div>
								<div class="form-group">
									<label>Country: </label>
									<form:input path="country" cssClass="form-control" />
									<p class="text-red">
										<form:errors path="country" />
									</p>
								</div>
								<div class="form-group">
									<label>State: </label>
									<form:input path="state" cssClass="form-control" />
									<p class="text-red">
										<form:errors path="state" />
									</p>
								</div>
								<div class="form-group">
									<label>Locality: </label>
									<form:input path="locality" cssClass="form-control" />
									<p class="text-red">
										<form:errors path="locality" />
									</p>
								</div>
								<div class="form-group">
									<label>Email: </label>
									<form:input path="email" cssClass="form-control" />
									<p class="text-red">
										<form:errors path="email" />
									</p>
								</div>
								<div class="row">
									<div class="form-group col-xs-6">
										<label>Validity number <span class="text-red">*</span>:
										</label>
										<form:input path="validityNumber" value="1"
											cssClass="form-control" />
										<p class="text-red">
											<form:errors path="validityNumber" />
										</p>
									</div>
									<div class="form-group col-xs-6">
										<label>Validity type <span class="text-red">*</span>:
										</label>
										<form:select path="validityType" cssClass="form-control">
											<form:option value="YEARS">YEARS</form:option>
											<form:option value="MONTHS">MONTHS</form:option>
											<form:option value="DAYS">DAYS</form:option>
										</form:select>
										<p class="text-red">
											<form:errors path="validityType" />
										</p>
									</div>
								</div>
								<div class="row">
									<div class="form-group col-xs-4">
										<label>Encryption algorithm <span class="text-red">*</span>:
										</label>
										<form:select path="encryptionAlgorithm"
											cssClass="form-control">
											<form:option value="RSA">RSA</form:option>
											<form:option value="EC">EC</form:option>
										</form:select>
										<p class="text-red">
											<form:errors path="encryptionAlgorithm" />
										</p>
									</div>
									<div class="form-group col-xs-4">
										<label>Key size <span class="text-red">*</span>:
										</label>
										<form:select path="keySize" cssClass="form-control">
											<form:option value="2048">2048</form:option>
											<form:option value="4096">4096</form:option>
											<form:option value="8192">8192</form:option>
											<form:option value="16384">16384</form:option>
										</form:select>
										<p class="text-red">
											<form:errors path="keySize" />
										</p>
									</div>
									<div class="form-group col-xs-4">
										<label>Signature algorithm <span class="text-red">*</span>:
										</label>
										<form:select path="signatureAlgorithm" cssClass="form-control">
											<form:option value="SHA224withRSA">SHA224withRSA</form:option>
											<form:option value="SHA256withRSA">SHA256withRSA</form:option>
											<form:option value="SHA384withRSA">SHA384withRSA</form:option>
											<form:option value="SHA512withRSA">SHA512withRSA</form:option>
											<!-- 
											<form:option value="SHA224withRSAandMGF1">SHA224withRSAandMGF1</form:option>
											<form:option value="SHA256withRSAandMGF1">SHA256withRSAandMGF1</form:option>
											<form:option value="SHA384withRSAandMGF1">SHA384withRSAandMGF1</form:option>
											<form:option value="SHA512withRSAandMGF1">SHA512withRSAandMGF1</form:option> -->
										</form:select>
										<p class="text-red">
											<form:errors path="signatureAlgorithm" />
										</p>
									</div>
								</div>
							</div>

							<!-- /.box-body -->

							<input type="hidden" name="${_csrf.parameterName}"
								value="${_csrf.token}" />

							<div class="box-footer">
								<button type="submit" class="btn btn-primary" id="generate">Generate</button>
							</div>
						</form:form>

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
