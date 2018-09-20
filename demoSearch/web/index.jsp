<%--
  Created by IntelliJ IDEA.
  User: yitinwang
  Date: 7/30/18
  Time: 15:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>
    <title>$Title$</title>
  </head>
  <body>

    <div id="box">
      <form action="index.jsp" method="post">
        <p class="main">
          <label>Input Key</label>
          <input type="text" name="key" id="searchkey" value="">
        </p>
        <p class="space">
          <input type="submit" value="Search" class="search" style="cursor: pointer;"/>
        </p>
      </form>
    </div>




  $END$
  </body>
</html>
