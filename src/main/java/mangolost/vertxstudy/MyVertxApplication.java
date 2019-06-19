package mangolost.vertxstudy;

import io.reactivex.Completable;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import io.vertx.reactivex.ext.sql.SQLClient;
import io.vertx.reactivex.ext.web.Router;
import mangolost.vertxstudy.config.CommonConfig;
import mangolost.vertxstudy.router.RouterConfig;

import java.util.function.Consumer;

/**
 *
 */
public class MyVertxApplication extends AbstractVerticle {

    @Override
    public void start() {
        CommonConfig.init();

        // Create a JDBC client with a test database
        JDBCClient client = JDBCClient.createShared(vertx, new JsonObject()
                .put("url", CommonConfig.url)
                .put("driver_class", CommonConfig.driverClassName)
                .put("user", CommonConfig.username)
                .put("password", CommonConfig.password)
        );
        Router router = RouterConfig.registerRouter(vertx, client);
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(CommonConfig.port);
        System.out.println("server started on port " + CommonConfig.port);
    }

    public static void main(String[] args) {
        Consumer<Vertx> runner = vertx -> vertx.deployVerticle(MyVertxApplication.class.getName());
        // Vert.x实例是vert.x api的入口点，我们调用vert.x中的核心服务时，均要先获取vert.x实例，
        // 通过该实例来调用相应的服务，例如部署verticle、创建http server
        Vertx vertx = Vertx.vertx();
        runner.accept(vertx);
    }
}