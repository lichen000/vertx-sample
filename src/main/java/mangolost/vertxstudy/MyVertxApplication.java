package mangolost.vertxstudy;

import com.sun.istack.internal.NotNull;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import mangolost.vertxstudy.common.CommonResult;
import mangolost.vertxstudy.entity.Student;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 */
public class MyVertxApplication extends AbstractVerticle {

    private JDBCClient client;

    /**
     * @throws Exception
     */
    @Override
    public void start() throws Exception {

        // Create a JDBC client with a test database
        client = JDBCClient.createShared(vertx, new JsonObject()
                .put("url", "jdbc:mysql://localhost:3306/db_test1?useSSL=true&useUnicode=true&characterEncoding=UTF-8")
                .put("driver_class", "com.mysql.jdbc.Driver")
                .put("user", "root")
                .put("password", "bstek")
        );

        Router router = Router.router(vertx);

        // in order to minimize the nesting of call backs we can put the JDBC connection on the context for all routes
        // that match /products
        // this should really be encapsulated in a reusable JDBC handler that uses can just add to their app
        router.route("/api/student*").handler(routingContext -> client.getConnection(res -> {
//            System.out.println("00000");
            if (res.failed()) {
                routingContext.fail(res.cause());
            } else {
                SQLConnection conn = res.result();

                // save the connection on the context
                routingContext.put("conn", conn);

                // we need to return the connection back to the jdbc pool. In order to do that we need to close it, to keep
                // the remaining code readable one can add a headers end handler to close the connection.
                routingContext.addHeadersEndHandler(done -> conn.close(v -> { }));

                routingContext.next();
            }
        })).failureHandler(routingContext -> {
            SQLConnection conn = routingContext.get("conn");
            if (conn != null) {
                conn.close(v -> {
                });
            }
        });

        router.route("/api/student/get").handler(this::get);
        router.route("/api/student/getall").handler(this::getAll);
        router.route().path("/api/student/add").handler(this::add);
        router.route().path("/api/student/update").handler(this::update);
        router.route().path("/api/student/delete").handler(this::delete);

        vertx.createHttpServer().requestHandler(router::accept).listen(9991);
        System.out.println("Server is running on port 9991");
    }

    /**
     *
     * @param routingContext
     */
    private void get(@NotNull RoutingContext routingContext) {
//        System.out.println("11111");
        String id = routingContext.request().getParam("id");
        HttpServerResponse response = routingContext.response();
        if (StringUtils.isBlank(id)) {
            response.setStatusCode(430).end();
        } else {
            SQLConnection conn = routingContext.get("conn");

            conn.queryWithParams("SELECT * FROM t_student where id = ?", new JsonArray().add(Integer.parseInt(id)), query -> {
                if (query.failed()) {
                    response.setStatusCode(500).end();
                } else {
                    if (query.result().getNumRows() == 0) {
                        response.setStatusCode(404).end();
                    } else {
                        Student student = query.result().getRows().get(0).mapTo(Student.class);
                        CommonResult commonResult = new CommonResult();
                        commonResult.setData(student);
                        response.putHeader("content-type", "application/json").end(Json.encode(commonResult));
                    }
                }
            });
        }
    }

    /**
     *
     * @param routingContext
     */
    private void getAll(@NotNull RoutingContext routingContext) {
//        System.out.println("22222");
        HttpServerResponse response = routingContext.response();
        SQLConnection conn = routingContext.get("conn");

        conn.query("SELECT * FROM t_student", query -> {
            if (query.failed()) {
                response.setStatusCode(500).end();
            } else {
                List<Student> students = new ArrayList<>();
                query.result().getRows().forEach(x -> students.add(x.mapTo(Student.class)));
                CommonResult commonResult = new CommonResult();
                commonResult.setData(students);
                response.putHeader("content-type", "application/json").end(Json.encode(commonResult));
            }
        });
    }

    /**
     *
     * @param routingContext
     */
    private void add(@NotNull RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        SQLConnection conn = routingContext.get("conn");
        String note = routingContext.request().getParam("note");
        String number = routingContext.request().getParam("number");
        String name = routingContext.request().getParam("name");
        Integer age = Integer.parseInt(routingContext.request().getParam("age"));

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(note).add(number).add(name).add(age);

        conn.queryWithParams("insert into t_student (note, number, name, age) values (?, ?, ?, ?)", jsonArray, query -> {
            if (query.failed()) {
                response.setStatusCode(500).end();
            } else {
                CommonResult commonResult = new CommonResult();
                response.putHeader("content-type", "application/json").end(Json.encode(commonResult));
            }
        });
    }

    /**
     *
     * @param routingContext
     */
    private void update(@NotNull RoutingContext routingContext) {
        String idStr = routingContext.request().getParam("id");
        HttpServerResponse response = routingContext.response();

        if (StringUtils.isBlank(idStr)) {
            response.setStatusCode(430).end();
        } else {
            Integer id = Integer.parseInt(idStr);
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
            if (jsonObject.isEmpty()) {
                response.setStatusCode(500).end();
            } else {
                SQLConnection conn = routingContext.get("conn");
                String sql = "update t_student set ";
                String sql2 = "";
                if (jsonObject.containsKey("note")) {
                    sql2 += ", note = '" + jsonObject.getValue("note").toString() + "'";
                }
                if (jsonObject.containsKey("number")) {
                    sql2 += ", number = '" + jsonObject.getValue("number").toString() + "'";
                }
                if (jsonObject.containsKey("name")) {
                    sql2 += ", name = '" + jsonObject.getValue("name").toString() + "'";
                }
                if (jsonObject.containsKey("age")) {
                    sql2 += ", age = " + jsonObject.getValue("age");
                }

                sql2 = sql2.substring(1);

                sql += sql2 + " where id = " + id;

                conn.query(sql, query -> {
                    if (query.failed()) {
                        response.setStatusCode(500).end();
                    } else {
                        CommonResult commonResult = new CommonResult();
                        response.putHeader("content-type", "application/json").end(Json.encode(commonResult));
                    }
                });
            }


        }
    }

    /**
     *
     * @param routingContext
     */
    private void delete(@NotNull RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        SQLConnection conn = routingContext.get("conn");
        String id = routingContext.request().getParam("id");
        if (StringUtils.isBlank(id)) {
            response.setStatusCode(430).end();
        } else {
            conn.queryWithParams("delete from t_student where id = ?", new JsonArray().add(id), query -> {
                if (query.failed()) {
                    response.setStatusCode(500).end();
                } else {
                    CommonResult commonResult = new CommonResult();
                    response.putHeader("content-type", "application/json").end(Json.encode(commonResult));
                }
            });
        }
    }

    public static void main(String[] args) {
        VertxOptions options = new VertxOptions();
        Consumer<Vertx> runner = vertx -> vertx.deployVerticle(MyVertxApplication.class.getName());
        // Vert.x实例是vert.x api的入口点，我们调用vert.x中的核心服务时，均要先获取vert.x实例，
        // 通过该实例来调用相应的服务，例如部署verticle、创建http server
        Vertx vertx = Vertx.vertx(options);
        runner.accept(vertx);
    }
}