$(document)
		.ready(
				function() {

					var path = window.location.pathname;
					$('#main-menu li ul li a').each(
							function() {
								if (path === "/") {
									$("#home-page").addClass('active');
								} else if (path === $(this).attr('href')
										&& path !== "/") {
									$(this).parent().parent().parent()
											.addClass('active');
									$(this).parent().addClass('active');
								}
							});

					$("#encryptionAlgorithm").change(function() {
						var selected = $(this).find(":selected").val();
						if (selected === "RSA") {
							updateAlgorithmRSA();
						}
						if (selected === "EC") {
							updateAlgorithmEC();
						}
					});

					if ($("#encryptionAlgorithm option:selected").text() === "EC") {
						updateAlgorithmEC();
					}

					function updateAlgorithmEC() {
						$("#keySize")
								.empty()
								.html(
										"<option value='128'>128</option><option value='256'>256</option><option value='384'>384</option><option value='571'>571</option>");
						$("#signatureAlgorithm")
								.empty()
								.html(
										"<option value='SHA224withECDSA'>SHA224withECDSA</option><option value='SHA256withECDSA'>SHA256withECDSA</option><option value='SHA384withECDSA'>SHA384withECDSA</option><option value='SHA512withECDSA'>SHA512withECDSA</option>");
					}

					function updateAlgorithmRSA() {
						$("#keySize")
								.empty()
								.html(
										"<option value='2048'>2048</option><option value='4096'>4096</option><option value='8192'>8192</option><option value='16384'>16384</option>");
						$("#signatureAlgorithm")
								.empty()
								.html(
										"<option value='SHA224withRSA'>SHA224withRSA</option><option value='SHA256withRSA'>SHA256withRSA</option><option value='SHA384withRSA'>SHA384withRSA</option><option value='SHA512withRSA'>SHA512withRSA</option><!--<option value='SHA224withRSAandMGF1'>SHA224withRSAandMGF1</option><option value='SHA256withRSAandMGF1'>SHA256withRSAandMGF1</option><option value='SHA384withRSAandMGF1'>SHA384withRSAandMGF1</option><option value='SHA512withRSAandMGF1'>SHA512withRSAandMGF1</option>-->");

					}

					$(".delete-keyStore").click(function() {
						var id = $(this).attr("id");
						$("#deleteModal").find("input[name='id']").val(id);
						$("#deleteModal").modal()
					});

					$("input[name='type']").click(function() {
						var value = $(this).val();
						var hostInput = $("input[name='host']");
						var urlInput = $("input[name='URL']");
						var portInput = $("input[name='port']");
						if (value === "byURL") {
							$(urlInput).attr("disabled", false);
							$(hostInput).attr("disabled", true);
							$(portInput).attr("disabled", true);
							$(hostInput).val("");
							$(portInput).val("");
						} else {
							$(urlInput).attr("disabled", true);
							$(urlInput).val("");
							$(hostInput).attr("disabled", false);
							$(portInput).attr("disabled", false);
						}
					});

					function setCrsfHeaderName(xhr) {
						var token = $("meta[name='_csrf']").attr("content");
						var header = $("meta[name='_csrf_header']").attr(
								"content");
						xhr.setRequestHeader(header, token);
					}

					/**
					 * Function to made ajax calls
					 * 
					 * @param type
					 *            type of request
					 * @param cache
					 * @param dataType
					 * @param contentType
					 * @param processData
					 * @param async
					 * @param url
					 * @param data
					 * @param success
					 * @param error
					 * @returns
					 */
					function ajaxCall(type, cache, dataType, contentType,
							processData, async, url, data, success, error) {
						$.ajax({
							type : type,
							cache : cache,
							dataType : dataType,
							contentType : contentType,
							processData : processData,
							async : async,
							url : url,
							data : data,
							beforeSend : function(xhr) {
								setCrsfHeaderName(xhr);
							},
							success : function(response) {
								success(response);
							},
							error : function(jqxhr) {
								error(jqxhr);
							}
						});
					}

					$(document).on("click", "#retrieve-certificate-button",
							retrieveCertificates);
					
					function retrieveCertificates() {
						var data = $("#retrieve-certificate-form").serialize();
						$("#extracted-certificates").html("");
						success = function(object) {
							$("#extracted-certificates").html(object);
						}
						error = function(object) {
							$("#extracted-certificates").html(
									object.responseText);
						}
						ajaxCall('POST', false, undefined, undefined, false,
								undefined, './retrieve_certificates', data,
								success, error);
					}

					$(document).ajaxStart(function() {
						$("#wait").css("display", "block");
					});
					$(document).ajaxComplete(function() {
						$("#wait").css("display", "none");
					});
				});
