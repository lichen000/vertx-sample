package mangolost.vertxstudy.service;

import io.reactivex.Single;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.ext.sql.SQLClient;
import io.vertx.reactivex.ext.sql.SQLConnection;
import mangolost.vertxstudy.entity.Employee;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class EmployeeService {

//    /**
//     *
//     * @param id
//     * @return
//     */
//    public Optional<Employee> get(int id, SQLConnection conn) {
//        List<Optional<Employee>> list = new ArrayList<>();
//
//        //queryWithParams是个异步方法
//        String sql = "select * from t_employee where id = ?";
//        JsonArray params = new JsonArray().add(id);
//        conn.queryWithParams(sql, params, query -> {
//            if (query.failed()) {
//                throw new RuntimeException("query fail");
//            } else if (query.result().getNumRows() == 1) {
//                list.add(Optional.of(query.result().getRows().get(0).mapTo(Employee.class)));
//                System.out.println("aaaa");
//
//                //如何在异步的lambda回掉里面返回list？
//            }
//        });
//        System.out.println("bbbb"); //这个会先于aaaa输出
//        return list.get(0); //这里返回的是个空的
//    }

    /**
     *
     * @param id
     * @return
     */
    public Employee get(int id, SQLClient client) {
        List<Employee> employeeList = new ArrayList<>();
        Handler<AsyncResult<ResultSet>> handler = resultSetAsyncResult -> {
            Employee employee1 = new Employee(resultSetAsyncResult.result());
            employeeList.add(employee1);
        };
        Future<ResultSet> composeFuture = Future.future();
        composeFuture.setHandler(handler);
        String sql = "select * from t_employee where id = ? limit 1";
        JsonArray params = new JsonArray().add(id);
        client.queryWithParams(sql, params, composeFuture.completer());
        composeFuture.compose(rs -> {
            Employee employee1 = new Employee(rs);
            employeeList.add(employee1);
            return null;
        });
        return employeeList.get(0);
    }

}
