package aviralgupta.site.miko_assignment.routes;

import aviralgupta.site.miko_assignment.entity.Post;
import aviralgupta.site.miko_assignment.entity.User;
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.core.Future;
import io.vertx.core.impl.future.CompositeFutureImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;

public class MyRoute {

  private final CircuitBreaker circuitBreaker;

  public MyRoute(CircuitBreaker circuitBreaker) {
    this.circuitBreaker = circuitBreaker;
  }

  public void registerRoute(Router router){
    router.get("/aggregate").handler(this::aggregateRouteHandler);
  }

  private  void aggregateRouteHandler(RoutingContext ctx){

    String POST_API = "https://jsonplaceholder.typicode.com/posts/1";
    String USER_API = "https://jsonplaceholder.typicode.com/users/1";

    WebClient client = WebClient.create(ctx.vertx());

    Future<Post> postFuture = circuitBreaker.executeWithFallback(
      promise -> client.getAbs(POST_API)
        .send()
        .map(res -> res.bodyAsJson(Post.class))
        .onSuccess(promise::complete)
        .onFailure(promise::fail),
      err -> {
        System.out.println("Post fallback triggered: " + err);
        throw new RuntimeException("Service fallback");
      }
    );

    Future<User> userFuture = circuitBreaker.executeWithFallback(
      promise -> client.getAbs(USER_API)
        .send()
        .map(res -> res.bodyAsJson(User.class))
        .onSuccess(promise::complete)
        .onFailure(promise::fail),
      err -> {
        System.out.println("User fallback triggered: " + err);
        throw new RuntimeException("Service fallback");
      }
    );

      CompositeFutureImpl.all(postFuture, userFuture)
        .onComplete((result, failure) -> {
          if(result != null && result.succeeded()){
            Post post = postFuture.result();
            User user = userFuture.result();

            ctx.json(new JsonObject()
              .put("post_title", post.getTitle())
              .put("author_name", user.getName())
            );
          }
        })
        .onFailure(failure -> {
          System.out.println(failure);
          ctx.response().setStatusCode(500).end(
            new JsonObject().put("message", failure.getMessage()).encode()
          );
        });
  }
}
