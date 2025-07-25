package aviralgupta.site.miko_assignment.verticles;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(VertxExtension.class)
public class HttpServerVerticleTest {

  static String deployId;

  @BeforeAll
  static void startServer(Vertx vertx, VertxTestContext ctx){
    vertx.deployVerticle(new HttpServerVerticle())
      .onSuccess(id -> {
        ctx.completeNow();
        deployId = id;
      })
      .onFailure(ctx::failNow);
  }

  @AfterAll
  static void stopServer(Vertx vertx, VertxTestContext ctx){
    vertx.undeploy(deployId)
      .onSuccess(v -> ctx.completeNow())
      .onFailure(error -> ctx.failNow("Unable to stop server"));
  }


  @Test
  void TestHomePageRoute_Returns404(Vertx vertx, VertxTestContext testContext) {
    WebClient client = WebClient.create(vertx);

    client.get(8080, "localhost", "/404")
      .send()
      .onSuccess(response -> testContext.verify(() -> {
        assertEquals(404, response.statusCode());
        testContext.completeNow();
      }))
      .onFailure(testContext::failNow);
  }

  @Test
  void TestAggregateRoute_Returns200AndJsonData(Vertx vertx, VertxTestContext testContext) {
    WebClient client = WebClient.create(vertx);

    client.get(8080, "localhost", "/aggregate")
      .send()
      .onSuccess(response -> testContext.verify(() -> {
        assertEquals(200, response.statusCode());
        JsonObject json = response.bodyAsJsonObject();

        // ✅ Just checking if keys exist
        assertTrue(json.containsKey("author_name"));
        assertTrue(json.containsKey("post_title"));
        testContext.completeNow();
      }))
      .onFailure(testContext::failNow);
  }



}
