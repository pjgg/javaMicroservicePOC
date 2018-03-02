package http2;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.JksOptions;

public class Server extends AbstractVerticle {


	private static final Logger LOG = LoggerFactory.getLogger(Server.class);

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new Server(), ar -> {
			if (ar.failed()) {
				ar.cause().printStackTrace();
			}
		});
	}

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		LOG.info("Vertx Up!");
		HttpServer server = vertx.createHttpServer(
				new HttpServerOptions()
					//	.setUseAlpn(true)
					//	.setKeyCertOptions(new JksOptions().setPath("server-keystore.jks").setPassword("wibble"))
					//	.setSsl(true)
		);
		server.requestHandler(req -> {
			LOG.info("Path "+req.path() +" -> " +req.response().getStatusMessage());
			req.response().end("Hello " + req.version());
		}).listen(8080, ar -> startFuture.handle(ar.mapEmpty()));

	}
}