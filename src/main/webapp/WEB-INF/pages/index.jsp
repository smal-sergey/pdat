<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="false"%>
<html>
<body>
<h1>Title : ${title}</h1>
<h1>Message : ${body}</h1>
<c:if test="${pageContext.request.userPrincipal.name != null}">
    Hello, ${pageContext.request.userPrincipal.name} !
</c:if>
</body>
</html>