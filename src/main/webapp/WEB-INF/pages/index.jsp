<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="false"%>
<html>
<body>
<h1>Title : ${title}</h1>
<h1>Message : ${body}</h1>

<c:if test="${author eq pageContext.request.userPrincipal.name}">
    <h1>Only author of this page can see this text!</h1>
</c:if>

</body>
</html>