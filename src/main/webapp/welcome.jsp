<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
<meta name="description" content="">
<meta name="author" content="">

<title>Intuiter</title>

<link href="${contextPath}/resources/css/bootstrap.min.css"
	rel="stylesheet">

<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->

<style>
p {outline-color:black;}
p.solid {outline-style: solid;}
</style>
</head>
<body>
	<div style="width: 100%;">
		<c:if test="${pageContext.request.userPrincipal.name != null}">
			<div style="float: left; width: 70%;" id="div00">
				<h2>&nbsp;&nbsp;Hello, ${pageContext.request.userPrincipal.name}</h2>

			</div>
			<div style="float: right; width: 20%;" id="div01">

				<form id="logoutForm" method="POST" action="${contextPath}/logout">
					<input type="hidden" name="${_csrf.parameterName}"
						value="${_csrf.token}" />
				</form>

				<h5>
					<a onclick="document.forms['logoutForm'].submit()">Logout</a>
				</h5>

			</div>
			<div style="float: left; width: 80%;" class="container" id="div1">

				<br> <br>
				<h4 id="followingUsersList"></h4>
				<br> 
				<textarea rows=4 cols=80 name="tweetBox" id="tweetBox"
					onfocus="(this.value == 'Write tweet here') && (this.value = '')"
					onblur="(this.value == '') && (this.value = 'Write tweet here')">Write tweet here</textarea>
					<br>
				<input type="button" id="tweetButton" value="Post Tweet"> <br>
				<br>


			</div>
			<div style="float: right; width: 20%;" id="div2">
				<br> <input type="text" name="userNameToFollow"
					id="userNameToFollow" value="Enter UserName here"
					onfocus="(this.value == 'Enter UserName here') && (this.value = '')"
					onblur="(this.value == '') && (this.value = 'Enter UserName here')">
				 <input type="button" id="followUser"
					value="Follow"> <br> <br>
				<h5>All users list:</h5>
			</div>
		</c:if>
	</div>

	<!-- /container -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
	<script src="${contextPath}/resources/js/bootstrap.min.js"></script>
	<script src="${contextPath}/resources/js/welcomeboot.js"></script>
</body>
</html>
