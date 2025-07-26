package aviralgupta.site.miko_assignment.verticles;

import aviralgupta.site.miko_assignment.routes.MyRoute;
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class HttpServerVerticle extends VerticleBase {

  @Override
  public Future<?> start(){

    CircuitBreaker circuitBreaker = CircuitBreaker.create("myapp-cb", vertx,
      new CircuitBreakerOptions()
        .setMaxFailures(3)
        .setTimeout(2000)
        .setFallbackOnFailure(true)
        .setResetTimeout(5000)
    );

    Router router = Router.router(vertx);

    MyRoute route = new MyRoute(circuitBreaker);

    route.registerRoute(router);

    handleRouteFailure(router);

    return createHttpServer(router);
  }

  private Future<?> createHttpServer(Router router){

    final int PORT = 8080;

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

  private void handleRouteFailure(Router router){
    router.route().failureHandler(ctx -> {
      int statusCode = ctx.statusCode() != -1 ? ctx.statusCode() : 500;
      String failedMessage = ctx.failure().getMessage();

      ctx.response().setStatusCode(statusCode).end(
        new JsonObject().put("message", failedMessage).encode()
      );
    });
  }

}
