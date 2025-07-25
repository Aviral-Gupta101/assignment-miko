package aviralgupta.site.miko_assignment;

import aviralgupta.site.miko_assignment.verticles.HttpServerVerticle;
import io.vertx.core.Vertx;

public class Main {

  public static void main(String[] args) {

    Vertx vertx = Vertx.vertx();

    vertx.deployVerticle(new HttpServerVerticle());

  }
}
