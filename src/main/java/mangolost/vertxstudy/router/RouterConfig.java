package mangolost.vertxstudy.router;

import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.sql.SQLClient;
import io.vertx.reactivex.ext.sql.SQLConnection;
import io.vertx.reactivex.ext.web.Router;
import mangolost.vertxstudy.controller.EmployeeController;

public class RouterConfig {

    private static final EmployeeController employeeController = new EmployeeController();

    /**
     *
     * @param vertx
     * @return
     */
    public static Router registerRouter(Vertx vertx, SQLClient client) {
        Router router = Router.router(vertx);

        // in order to minimize the nesting of call backs we can put the JDBC connection on the context for all routes
        // that match /products
        // this should really be encapsulated in a reusable JDBC handler that uses can just add to their app

//        router.route("/api/employee*").handler(routingContext -> client.getConnection(res -> {
//            if (res.failed()) {
//                routingContext.fail(res.cause());
//            } else {
//                SQLConnection conn = res.result();
//
//                // save the connection on the context
//                routingContext.put("client", client);
//                routingContext.put("conn", conn);
//
//                // we need to return the connection back to the jdbc pool. In order to do that we need to close it, to keep
//                // the remaining code readable one can add a headers end handler to close the connection.
//                routingContext.addHeadersEndHandler(done -> conn.close(v -> { }));
//                routingContext.next();
//            }
//        })).failureHandler(routingContext -> {
//            SQLConnection conn = routingContext.get("conn");
//            if (conn != null) {
//                conn.close(v -> {
//                });
//            }
//        });

        router.route("/api/employee*").handler(routingContext -> {
            routingContext.put("client", client);
            routingContext.addHeadersEndHandler(done -> {});
            routingContext.next();
        }).failureHandler(routingContext -> {
            SQLConnection conn = routingContext.get("conn");
            if (conn != null) {
                conn.close(v -> {
                });
            }
        });

        router.route("/api/employee/get").handler(employeeController::get);
        router.route("/api/employee/getall").handler(employeeController::getAll);
        router.route("/api/employee/add").handler(employeeController::add);
        router.route("/api/employee/update").handler(employeeController::update);
        router.route("/api/employee/delete").handler(employeeController::delete);

        return router;
    }
}
