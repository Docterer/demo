package ${targetPackageController}.api;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.Page;
import com.jiatu.wms.core.common.CommonPage;
import com.jiatu.wms.core.common.PageResponse;
import com.jiatu.wms.core.common.Response;
import com.jiatu.wms.entity.wms.${classSimpleName};
import com.jiatu.wms.service.${classSimpleName}Service;
import com.jiatu.wms.web.util.HttpRequestPageUitls;

import io.swagger.annotations.ApiOperation;
import tk.mybatis.mapper.entity.Example;

@Controller
@RequestMapping("/api/${classSimpleNameUncapitalize}")
public class ${classSimpleName}ApiController {

    @Autowired
    private ${classSimpleName}Service ${classSimpleNameUncapitalize}Service;

    @ApiOperation(value = "显示页面")
    @RequestMapping(value = "/list", method = { RequestMethod.POST })
    @ResponseBody
    public Response list() {
        CommonPage commonPage = HttpRequestPageUitls.getPage();
        Example example = ${classSimpleNameUncapitalize}Service.buildExample();
        example.orderBy(${classSimpleName}.PROP_CREATE_TIME).desc();
        Page<${classSimpleName}> menus = ${classSimpleNameUncapitalize}Service.selectByExample(example, commonPage);
        return PageResponse.pageResult(commonPage, menus);
    }

    @RequestMapping(value = "/add", method = { RequestMethod.POST })
    @ResponseBody
    public Response add(${classSimpleName} record) {
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        ${classSimpleNameUncapitalize}Service.insertSelective(record);
        Response res = Response.success("查询成功");
        return res;
    }

    @RequestMapping(value = "/update", method = { RequestMethod.POST })
    @ResponseBody
    public Response update(${classSimpleName} record) {
        record.setUpdateTime(new Date());
        ${classSimpleNameUncapitalize}Service.updateByPrimaryKeySelective(record);
        Response res = Response.success("查询成功");
        return res;
    }

    @ApiOperation(value = "显示页面")
    @RequestMapping(value = "/list/all", method = { RequestMethod.POST })
    @ResponseBody
    public Response listAll() {
        Example example = ${classSimpleNameUncapitalize}Service.buildExample();
        example.orderBy(${classSimpleName}.PROP_CREATE_TIME).desc();
        List<${classSimpleName}> rows = ${classSimpleNameUncapitalize}Service.selectByExample(example);
        Response res = Response.success("查询成功", rows);
        return res;
    }
}
