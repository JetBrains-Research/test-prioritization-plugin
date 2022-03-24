<%@ page import="org.jetbrains.teamcity.PrioritizationConstants" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<c:set var="testsFolderId" value="<%=PrioritizationConstants.TESTS_FOLDER_KEY%>"/>

<div class="parameter">
    Tests root folder: <props:displayValue name="${testsFolderId}" emptyValue=""/>
</div>
