package mangolost.vertxstudy.controller;

import io.reactivex.Single;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.sql.SQLClient;
import io.vertx.reactivex.ext.web.RoutingContext;
import mangolost.vertxstudy.common.CommonResult;
import mangolost.vertxstudy.entity.Employee;
import mangolost.vertxstudy.service.EmployeeService;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class EmployeeController {

    private final EmployeeService employeeService = new EmployeeService();

    /**
     *
     * @param routingContext
     */
    public void get(RoutingContext routingContext) {
        CommonResult commonResult = new CommonResult();
        String id = routingContext.request().getParam("id");
        if (StringUtils.isBlank(id)) {
            commonResult.setCodeAndMessage(430, "id缺少");
        } else {
//            SQLConnection conn = routingContext.get("conn");
            SQLClient client = routingContext.get("client");
            Employee employee = employeeService.get(Integer.parseInt(id), client);
            commonResult.setData(employee);

        }
        return0(routingContext, commonResult);
    }

    /**
     *
     * @param routingContext
     */
    public void getAll(RoutingContext routingContext) {
        CommonResult commonResult = new CommonResult();
        SQLConnection conn = routingContext.get("conn");
        String sql = "select * from t_employee";
        conn.query(sql, query -> {
            if (query.failed()) {
                commonResult.setCodeAndMessage(500, "查询失败");
            } else {
                List<Employee> employees = new ArrayList<>();
                query.result().getRows().forEach(x -> employees.add(x.mapTo(Employee.class)));
                commonResult.setData(employees);
            }
            return0(routingContext, commonResult);
        });
    }

    /**
     *
     * @param routingContext
     */
    public void add(RoutingContext routingContext) {
        CommonResult commonResult = new CommonResult();
        SQLConnection conn = routingContext.get("conn");
        String employee_no = routingContext.request().getParam("employee_no");
        String employee_name = routingContext.request().getParam("employee_name");
        int age = Integer.parseInt(routingContext.request().getParam("age"));
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(employee_no).add(employee_name).add(age);
        String sql = "insert into t_employee (employee_no, employee_name, age) values (?, ?, ?)";
        conn.queryWithParams(sql, jsonArray, query -> {
            if (query.failed()) {
                commonResult.setCodeAndMessage(500, "新增失败");
            }
            return0(routingContext, commonResult);
        });
    }

    /**
     *
     * @param routingContext
     */
    public void update(RoutingContext routingContext) {
        CommonResult commonResult = new CommonResult();
        String idStr = routingContext.request().getParam("id");
        if (StringUtils.isBlank(idStr)) {
            commonResult.setCodeAndMessage(430, "参数错误");
            return0(routingContext, commonResult);
        } else {
            int id = Integer.parseInt(idStr);
            String updatedParams = routingContext.request().getParam("updatedParams");
            JsonObject jsonObject = new JsonObject(updatedParams);
            while (jsonObject.containsKey("id")) {
                jsonObject.remove("id");
            }
            while (jsonObject.containsKey("create_time")) {
                jsonObject.remove("create_time");
            }
            while (jsonObject.containsKey("update_time")) {
                jsonObject.remove("update_time");
            }
            while (jsonObject.containsKey("record_status")) {
                jsonObject.remove("record_status");
            }
            if (jsonObject.isEmpty()) {
                commonResult.setCodeAndMessage(430, "参数错误");
                return0(routingContext, commonResult);
            } else {
                SQLConnection conn = routingContext.get("conn");
                String sql = "update t_employee set ";
                String sql2 = "";
                if (jsonObject.containsKey("employee_no")) {
                    sql2 += ", employee_no = '" + jsonObject.getValue("employee_no").toString() + "'";
                }
                if (jsonObject.containsKey("employee_name")) {
                    sql2 += ", employee_name = '" + jsonObject.getValue("employee_name").toString() + "'";
                }
                if (jsonObject.containsKey("age")) {
                    sql2 += ", age = " + jsonObject.getValue("age");
                }

                sql2 = sql2.substring(1);

                sql += sql2 + " where id = " + id;

                conn.query(sql, query -> {
                    if (query.failed()) {
                        commonResult.setCodeAndMessage(500, "更新失败");
                    }
                    return0(routingContext, commonResult);
                });
            }
        }
    }

    /**
     *
     * @param routingContext
     */
    public void delete(RoutingContext routingContext) {
        CommonResult commonResult = new CommonResult();
        SQLConnection conn = routingContext.get("conn");
        String id = routingContext.request().getParam("id");
        if (StringUtils.isBlank(id)) {
            commonResult.setCodeAndMessage(430, "id参数错误");
            return0(routingContext, commonResult);
        } else {
            String sql = "delete from t_employee where id = ?";
            conn.queryWithParams(sql, new JsonArray().add(id), query -> {
                if (query.failed()) {
                    commonResult.setCodeAndMessage(500, "删除失败");
                }
                return0(routingContext, commonResult);
            });
        }
    }

    /**
     *
     * @param routingContext
     * @param commonResult
     */
    private void return0(RoutingContext routingContext, CommonResult commonResult) {
        HttpServerResponse response = routingContext.response();
        response.putHeader("content-type", "application/json").end(Json.encode(commonResult));
    }
}
