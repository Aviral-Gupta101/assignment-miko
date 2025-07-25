package aviralgupta.site.miko_assignment.routes;

import aviralgupta.site.miko_assignment.entity.Post;
import aviralgupta.site.miko_assignment.entity.User;
import io.vertx.core.Future;
import io.vertx.core.impl.future.CompositeFutureImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;

public class MyRoute {

  public void registerRoute(Router router){
    router.get("/aggregate").handler(MyRoute::aggregateRouteHandler);
  }

  private static void aggregateRouteHandler(RoutingContext ctx){

    String POST_API = "https://jsonplaceholder.typicode.com/posts/1";
    String USER_API = "https://jsonplaceholder.typicode.com/users/1";

    WebClient client = WebClient.create(ctx.vertx());

    Future<Post> postFuture = client.getAbs(POST_API)
      .send()
      .map(res -> res.bodyAsJson(Post.class));

    Future<User> userFuture = client.getAbs(USER_API)
      .send()
      .map(res -> res.bodyAsJson(User.class));

      CompositeFutureImpl.all(postFuture, userFuture)
        .onComplete((result, failure) -> {
          if(result.succeeded()){
            Post post = postFuture.result();
            User user = userFuture.result();

            ctx.json(new JsonObject()
              .put("post_title", post.getTitle())
              .put("author_name", user.getName()));
          }
        })
        .onFailure(failure -> {
          System.out.println(failure);
          ctx.fail(500, new RuntimeException("Service unavailable"));
        });
  }
}
