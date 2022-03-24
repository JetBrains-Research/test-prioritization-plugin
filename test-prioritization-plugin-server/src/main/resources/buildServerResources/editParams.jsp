<%@ page import="org.jetbrains.teamcity.PrioritizationConstants" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<c:set var="testsFolderId" value="<%=PrioritizationConstants.TESTS_FOLDER_KEY%>"/>

<l:settingsGroup title="Test prioritization settings">
    <tr>
        <th><label for="${testsFolderId}">Tests root folder: <l:star/></label></th>
        <td>
            <div class="posRel">
                <props:textProperty name="${testsFolderId}" size="56" maxlength="100"/>
                <span class="error" id="error_${testsFolderId}"></span>
            </div>
        </td>
    </tr>
</l:settingsGroup>
