package aviralgupta.site.miko_assignment;

import aviralgupta.site.miko_assignment.verticles.HttpServerVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class TestMain {

  Vertx vertx;

  @BeforeEach
  void init(VertxTestContext ctx){
    vertx = Vertx.vertx();
    ctx.completeNow();
  }

 @Test
  void DeployHttpServerVerticle_ReturnsFutureString(VertxTestContext ctx){

   Future<String> stringFuture = vertx.deployVerticle(new HttpServerVerticle());

   stringFuture
     .onSuccess(id -> {

       vertx.undeploy(id)
         .onSuccess(v -> ctx.completeNow())
         .onFailure(err -> System.err.println("Failed to undeploy: " + err));
     })
     .onFailure(fail -> {
       ctx.failNow("Unable to deploy HttpServerVerticle");
     });
 }
}
