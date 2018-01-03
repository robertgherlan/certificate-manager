<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="app" value="${pageContext.request.contextPath}" />
<c:choose>
	<c:when test="${not empty error}">
		<div class="alert alert-warning alert-dismissible">
			<button type="button" class="close" data-dismiss="alert"
				aria-hidden="true">×</button>
			<h4>
				<i class="icon fa fa-ban"></i> Warning!
			</h4>
			${error}
		</div>
	</c:when>
	<c:otherwise>


		<c:forEach items="${certificatesDetails}" var="certificateDetails">
			<div class="box-solid">
				<div class="box-header with-border">
					<h3 class="box-title text-aqua">${certificateDetails.commonName}</h3>
				</div>
				<!-- /.box-header -->
				<div class="box-body text-center">
					<div class="row">
						<div class="col-md-6">
							<dl class="dl-horizontal">
								<dt>Valid:</dt>
								<c:choose>
									<c:when test="${certificateDetails.expired eq true}">
										<dd class="text-red">
											<img src="${app}/resources/admin/img/uncheck.png"
												class="img-circle">
										</dd>
									</c:when>
									<c:otherwise>
										<dd class="text-green">
											<img src="${app}/resources/admin/img/check.png"
												class="img-circle">
										</dd>
									</c:otherwise>
								</c:choose>
								<dt>Self signed:</dt>
								<c:choose>
									<c:when test="${certificateDetails.selfSigned eq true}">
										<dd class="text-red">
											<img src="${app}/resources/admin/img/check.png"
												class="img-circle">
										</dd>
									</c:when>
									<c:otherwise>
										<dd class="text-green">
											<img src="${app}/resources/admin/img/uncheck.png"
												class="img-circle">
										</dd>
									</c:otherwise>
								</c:choose>
								<dt>Common name:</dt>
								<dd>${certificateDetails.commonName}</dd>
								<dt>Subject name:</dt>
								<dd>${certificateDetails.subject}</dd>
								<dt>Issuer name:</dt>
								<dd>${certificateDetails.issuer}</dd>
								<dt>Alias:</dt>
								<dd>${certificateDetails.alias}</dd>
								<dt>Format:</dt>
								<dd>${certificateDetails.format}</dd>
							</dl>
							<br />
							<dl class="dl-horizontal">
								<dt>Signature algorithm:</dt>
								<dd>${certificateDetails.signatureAlgorithm}</dd>
								<dt>Key size:</dt>
								<dd>${certificateDetails.keySize}</dd>
								<dt>Creation date:</dt>
								<dd>${certificateDetails.notBefore}</dd>
								<dt>Expiration date:</dt>
								<dd>${certificateDetails.notAfter}</dd>
							</dl>
						</div>

						<div class="col-md-6">
							<div class="form-group">
								<label>PEM</label>
								<textarea class="form-control" rows="20" readonly="readonly">${certificateDetails.pem}</textarea>
							</div>
						</div>
					</div>
				</div>
				<!-- /.box-body -->
			</div>
		</c:forEach>
	</c:otherwise>
</c:choose>
