package aviralgupta.site.miko_assignment.verticles;

import aviralgupta.site.miko_assignment.routes.MyRoute;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class HttpServerVerticle extends VerticleBase {

  @Override
  public Future<?> start() throws Exception {

    final int PORT = 8080;

    Router router = Router.router(vertx);

    MyRoute route = new MyRoute();

    route.registerRoute(router);

    router.route().failureHandler(ctx -> {
      int statusCode = ctx.statusCode() != -1 ? ctx.statusCode() : 500;
      String failedMessage = ctx.failure().getMessage();

      ctx.response().setStatusCode(statusCode).end(
        new JsonObject().put("message", failedMessage).encode()
      );
    });

    return vertx.createHttpServer()
      .requestHandler(router)
      .listen(PORT)
      .onSuccess(server -> {
        System.out.println("Server started on http://localhost:" + PORT);
      })
      .onFailure(throwable -> {
        System.out.println("Unable to start the server");
      });
  }

}
