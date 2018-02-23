<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/common/common_taglib.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>请修改</title>
<jsp:include page="/WEB-INF/common/default_js_css.jsp" />
</head>
<body>
	<table id="dg"></table>
	
	<div id="toolbar">
		<a class="easyui-linkbutton" iconCls="icon-add" plain="true" href="javascript:add${classSimpleName}DialogOpen()">新增</a>
		<a class="easyui-linkbutton" iconCls="icon-edit" plain="true" href="javascript:update${classSimpleName}DialogOpen()">编辑</a>
	</div>

	<div id="add${classSimpleName}Dialog" class="dialog-form easyui-dialog" title="新增" closed="true">
		<form id="add${classSimpleName}Form" method="post" action="${ctx}/api/${classSimpleNameUncapitalize}/add">
			<table class="table-form">
			<#assign tableIndex=0/>
			<#list fieldNames as fieldName>
				<#if fieldName != 'id'>
					<#if tableIndex%2==0><tr></#if>
					<td>${fieldName}:${tableIndex}</td> <td><input class="easyui-textbox" name="${fieldName}" style="width: 100%"></td>
					<#assign tableIndex = tableIndex+1>
					<#if tableIndex%2==0></tr></#if>
				</#if>
			</#list>
			<#if (tableIndex%2==1)>
			<td></td><td></td></tr>
			</#if>
			<tr><td colspan="4">
				<a class="easyui-linkbutton" href="javascript:add${classSimpleName}()" data-options="iconCls:'icon-ok',width:80">提交</a>
				<a class="easyui-linkbutton" href="javascript:$('#add${classSimpleName}Dialog').dialog('close')" data-options="iconCls:'icon-cancel',width:80">取消</a>
			</td><tr>
			</table>
		</form>
	</div>
	<div style="text-align: center; padding: 5px 0;display: none;" id="add${classSimpleName}Dialog-buttons">
		
	</div>
	<div id="update${classSimpleName}Dialog" class="dialog-form easyui-dialog" title="编辑" closed="true">
		<form id="update${classSimpleName}Form" method="post" action="${ctx}/api/${classSimpleNameUncapitalize}/update">
			<input name="id" class="easyui-validatebox" type="hidden">
			<table class="table-form">
			<#assign tableIndex=0/>
			<#list fieldNames as fieldName>
				<#if fieldName != 'id'>
					<#if tableIndex%2==0><tr></#if>
					<td>${fieldName}:</td> <td><input class="easyui-textbox" name="${fieldName}" style="width: 100%"></td>
					<#assign tableIndex = tableIndex+1>
					<#if tableIndex%2==0></tr></#if>
				</#if>
			</#list>
			<#if (tableIndex%2==0)>
			<td></td><td></td></tr>
			</#if>
			<tr><td colspan="4">
				<a class="easyui-linkbutton" href="javascript:update${classSimpleName}()" data-options="iconCls:'icon-ok',width:80">提交</a>
				<a class="easyui-linkbutton" href="javascript:$('#update${classSimpleName}Dialog').dialog('close')" data-options="iconCls:'icon-cancel',width:80">取消</a>
			</td><tr>
			</table>
		</form>
	</div>
	<script type="text/javascript">
		$(function() {
			dgLoad();
		});
		function dgLoad(){
			$('#dg').datagrid({
				url:'${ctx}/api/${classSimpleNameUncapitalize}/list',
				queryParams : {},
				singleSelect:true,
				pagination:true,
				rownumbers:true,
				fitColumns:true,
				striped:true,
				loadFilter:webConfig.easyuiLoadFilter,
				toolbar: '#toolbar',
				 columns:[[
				 <#list fieldNames as fieldName>
					{field:'${fieldName}',title:'${fieldName}'}<#if fieldName_index+1!=fieldNames?size>,</#if>
				</#list>
			    ]]
			});
		}
		function delete${classSimpleName}() {
			var row = $('#dg').datagrid('getSelected');
			if (row) {
				$.messager.confirm('Confirm', '确定要删除吗?', function(r) {
					if (r) {
						$.post('${ctx}/api/${classSimpleNameUncapitalize}/delete', {
							id : row.id
						}, function(result) {
							if (result.code == webConfig.SUCCESS_CODE) {
								$.messager.show({
									title : '温馨提示',
									msg : '删除成功！',
									timeout : 3000,
								});
								$('#dg').datagrid('reload');
							} else {
								$.messager.alert('温馨提示', '删除失败，请联系技术人员');
							}// reload the appartment data
						}, 'json');
					}
				});
			}
		}
		function add${classSimpleName}DialogOpen() {
			$('#add${classSimpleName}Dialog').dialog('open');
			$('#add${classSimpleName}Form').form('clear');
		}
		function update${classSimpleName}DialogOpen() {
			var row = $('#dg').datagrid('getSelected');
			if (row) {
				$('#update${classSimpleName}Dialog').dialog('open');
				$('#update${classSimpleName}Form').form('load', row);
			}
		}
		function add${classSimpleName}() {
			$('#add${classSimpleName}Form').form('submit', {
				onSubmit : function() {
					return $(this).form('validate');
				},
				success : function(result) {
					var result = eval('(' + result + ')');
					if (result.code == webConfig.SUCCESS_CODE) {
						$.messager.show({
							title : '温馨提示',
							msg : '新增成功！',
							timeout : 3000,
						});
						$('#add${classSimpleName}Dialog').dialog('close'); // close the dialog
						$('#dg').datagrid('reload');
					} else {
						$.messager.alert('温馨提示', '新增失败，请联系技术人员');
					}
				}
			});
		}
		function update${classSimpleName}() {
			$('#update${classSimpleName}Form').form('submit', {
				onSubmit : function() {
					return $(this).form('validate');
				},
				success : function(result) {
					var result = eval('(' + result + ')');
					if (result.code == webConfig.SUCCESS_CODE) {
						$.messager.show({
							title : '温馨提示',
							msg : '修改成功！',
							timeout : 3000,
						});
						$('#update${classSimpleName}Dialog').dialog('close'); // close the dialog
						$('#dg').datagrid('reload');
					} else {
						$.messager.alert('温馨提示', '更新失败，请联系技术人员');
					}
				}
			});
		}
	</script>

</body>
</html>


